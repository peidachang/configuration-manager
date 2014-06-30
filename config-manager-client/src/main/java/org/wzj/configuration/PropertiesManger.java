package org.wzj.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.beanutils.BeanUtils;

public class PropertiesManger implements ConfigManager {

	private static final String CLASSPATH_PROTOL = "classpath:";
	private static final String FILE_PROTOL = "file:";

	private volatile boolean init = Boolean.FALSE;

	private Set<Class<?>> registers = Collections
			.synchronizedSet(new HashSet<Class<?>>());

	private Map<String, Object> configInstances = new HashMap<String, Object>();

	private Map<String, ExtInfo> configFiles = new HashMap<String, ExtInfo>();

	private ScheduledExecutorService scheduleService = Executors
			.newScheduledThreadPool(1);

	@Override
	public <T> void register(Class<T> configClass) {

		registers.add(configClass);

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getConfig(Class<T> configClass) {
		return (T) configInstances.get(configClass.getCanonicalName());
	}

	@Override
	public void init() {

		if (!init) {

			synchronized (registers) {

				doInit();

			}

		}

	}

	private void doInit() {

		for (Class<?> configClass : registers) {
			if (configClass == null) {
				throw new IllegalArgumentException(
						"configClass can not be null !");
			}
			Object instance = null;

			instance = instanceConfigBean(configClass, instance);

			Config config = instance.getClass().getAnnotation(Config.class);
			
			if(config == null ){
				throw new ConfigurationException("Check "+instance.getClass().getCanonicalName() +" ,whether has @Config annotaction .") ;
			}

			String configLocation = config.value();

			File configFile = null;

			configFile = resolveConifgFile(configLocation);

			try {
				String filePath = configFile.getCanonicalPath();

				ExtInfo extInfo = new ExtInfo();
				extInfo.classes.add(configClass.getCanonicalName());
				extInfo.lastModied = configFile.lastModified();

				configFiles.put(filePath, extInfo);

				configInstances.put(configClass.getCanonicalName(), instance);

			} catch (IOException e) {
				throw new ConfigurationException(e);
			}
		}

		loadConfig();

		scheduleService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				for (String filePath : configFiles.keySet()) {
					ExtInfo extInfo = configFiles.get(filePath);
					File file = new File(filePath);

					if (extInfo.lastModied != file.lastModified()) {
						
						extInfo.lastModied = file.lastModified() ;

						List<String> classes = extInfo.classes;

						Properties properties = loadProperties(filePath);

						for (String clazz : classes) {

							Object bean = configInstances.get(clazz);

							if (bean instanceof Watch) {
								@SuppressWarnings("unchecked")
								Watch<Properties> obj = (Watch<Properties>) bean;

								obj.onChange(properties);
							}

						}

					}

				}

			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	private void loadConfig() {
		for (String configPath : configFiles.keySet()) {
			Properties p = loadProperties(configPath);

			assembleConfigBean(configPath, p, configFiles.get(configPath));
		}

	}

	private Properties loadProperties(String configPath) {
		Properties p = new Properties();
		try {
			FileInputStream in = new FileInputStream(configPath);
			p.load(in);
			in.close();
		} catch (Exception e) {
			throw new ConfigurationException("Can not load " + configPath, e);
		}
		return p;
	}

	private void assembleConfigBean(String configPath, Properties properties,
			ExtInfo extInfo) {

		List<Object> beanList = new ArrayList<Object>(extInfo.classes.size());

		for (String clazz : extInfo.classes) {
			beanList.add(configInstances.get(clazz));
		}

		for (Object bean : beanList) {

			Field[] fields = bean.getClass().getDeclaredFields();
			Method[] methods = bean.getClass().getDeclaredMethods();

			// field
			for (Field f : fields) {
				ConfigKey configKey = f.getAnnotation(ConfigKey.class);
				if (configKey != null) {
					Object value = getValue(configPath, properties, configKey);

					try {
						BeanUtils.setProperty(bean, f.getName(), value);
					} catch (Exception e) {
						throw new ConfigurationException(
								"Can not find set method of " + f.getName()
										+ " in "
										+ bean.getClass().getCanonicalName());
					}
				}
			}

			// methods
			for (Method m : methods) {

				ConfigKey configKey = m.getAnnotation(ConfigKey.class);
				String name = m.getName();
				if (configKey != null && name.startsWith("set")) {

					name = deleteSet(name);
					Object value = getValue(configPath, properties, configKey);

					
					try {
						BeanUtils.setProperty(bean, name, value);
					} catch (Exception e) {
						throw new ConfigurationException(
								"Can not find set method of " + name + " in "
										+ bean.getClass().getCanonicalName());
					}

				}

			}

		}

	}

	private String deleteSet(String methodName) {
		
		if(methodName.startsWith("set")){
			
			methodName = methodName.substring(3) ;
			
			if(methodName.length() >= 2 && Character.isUpperCase(methodName.charAt(1))){
				
			}else{
				methodName = Character.toLowerCase(methodName.charAt(0)) + (methodName.length() == 1 ?  "" : methodName.substring(1)) ;
			}
			
		}
		
		
		return methodName;
	}

	private Object getValue(String configPath, Properties properties,
			ConfigKey configKey) {
		String key = configKey.value();
		Object value = properties.get(key);

		if (value == null) {
			throw new ConfigurationException("Can not find " + key + " in "
					+ configPath);
		}
		return value;
	}

	private File resolveConifgFile(String configLocation) {
		File configFile;
		if (configLocation.startsWith(CLASSPATH_PROTOL)) {

			URL resource = Thread
					.currentThread()
					.getContextClassLoader()
					.getResource(
							configLocation.substring(CLASSPATH_PROTOL.length()));

			configFile = new File(resource.getPath());

		} else if (configLocation.startsWith(FILE_PROTOL)) {

			configFile = new File(
					configLocation.substring(FILE_PROTOL.length()));

		} else {
			configFile = new File(configLocation);
		}

		if (!configFile.exists()) {
			throw new ConfigurationException("Config " + configLocation
					+ " not exist !");
		}
		return configFile;
	}

	private Object instanceConfigBean(Class<?> configClass, Object instance) {
		try {
			instance = configClass.newInstance();
		} catch (InstantiationException e) {
			throw new ConfigurationException("instance "
					+ configClass.getName() + " error", e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationException(configClass.getName()
					+ " must has a no arguments constructor", e);
		}
		return instance;
	}

	@Override
	public void destroy() {
		registers.clear();
		configInstances.clear();
		configFiles.clear();
		scheduleService.shutdown();
		init = Boolean.FALSE;

	}

	private static class ExtInfo {

		List<String> classes = new ArrayList<String>();

		volatile long lastModied = 0;

	}

	public static void main(String[] args) {

		
		
	}

}
