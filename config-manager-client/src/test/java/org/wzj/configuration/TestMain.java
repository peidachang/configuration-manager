package org.wzj.configuration;

import junit.framework.Assert;

import org.junit.Test;

public class TestMain {

	@Test
	public void test_db_config() {

		PropertiesManger propertiesManger = new PropertiesManger();

		propertiesManger.register(TestConfigBean.class);

		propertiesManger.init();

		TestConfigBean config = propertiesManger
				.getConfig(TestConfigBean.class);

		Assert.assertEquals(
				"TestConfigBean [driver=com.mysql.jdbc.Driver, url=jdbc:mysql://localhost:3306/jdbc, username=root, password=123654, maxActive=12, initialSize=2, maxIdle=4, minIdle=3, maxWait=2, defaultAutoCommit=true, removeAbandoned=false, testDouble=0.98, testDouble2=3.14, CDN=YES, f=0.9]",
				config.toString());

		System.out.println(config);

		propertiesManger.destroy();
	}

	@Test
	public void test_auto_reload() {
		PropertiesManger propertiesManger = new PropertiesManger();
		propertiesManger.register(TestConfigBean.class);
		propertiesManger.register(RedisConfig.class);
		
		propertiesManger.init() ;
		
		RedisConfig redisConfig = propertiesManger.getConfig(RedisConfig.class) ;
		
		System.out.println(redisConfig);
		
		try {
			Thread.sleep(2000000) ;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
