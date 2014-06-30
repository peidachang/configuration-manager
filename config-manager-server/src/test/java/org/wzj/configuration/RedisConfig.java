package org.wzj.configuration;

import java.util.Properties;

@Config("/home/wenzuojing/trunk/fs2/trunk/config-manager/src/test/java/org/wzj/configuration/auto_reload_config.properties")
public class RedisConfig  implements Watch<Properties> {
	
	@ConfigKey("redis.host")
	private String host;
	
	@ConfigKey("redis.port")
	private int port ;
	

	public String getHost() {
		return host;
	}



	public void setHost(String host) {
		this.host = host;
	}



	public int getPort() {
		return port;
	}



	public void setPort(int port) {
		this.port = port;
	}



	@Override
	public String toString() {
		return "RedisConfig [host=" + host + ", port=" + port + "]";
	}



	@Override
	public void onChange(Properties config) {
		
		System.out.println("----------------change------------");
		
	}
	
	

}
