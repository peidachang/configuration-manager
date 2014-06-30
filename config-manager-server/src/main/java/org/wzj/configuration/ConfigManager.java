package org.wzj.configuration;

public interface ConfigManager {
	
	<T> void  register(Class<T> configClass );
	
	<T> T getConfig(Class<T> configClass);
	
	void init();
	
	void destroy() ;

}
