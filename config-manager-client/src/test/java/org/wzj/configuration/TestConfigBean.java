package org.wzj.configuration;

@Config("classpath:org/wzj/configuration/db-config.properties")
public class TestConfigBean {

	@ConfigKey("driver.class")
	private String driver;
	@ConfigKey("url")
	private String url;
	@ConfigKey("username")
	private String username;
	@ConfigKey("password")
	private String password;
	@ConfigKey("maxActive")
	private int maxActive;
	
	private int initialSize;
	private int maxIdle;
	private int minIdle;
	private Integer maxWait;
	private boolean defaultAutoCommit;
	private Boolean removeAbandoned;

	private double testDouble;

	private Double testDouble2;
	
	private String CDN ;
	
	private float f ;

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getInitialSize() {
		return initialSize;
	}

	@ConfigKey("initialSize")
	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	@ConfigKey("maxIdle")
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	@ConfigKey("minIdle")
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public Integer getMaxWait() {
		return maxWait;
	}

	@ConfigKey("maxWait")
	public void setMaxWait(Integer maxWait) {
		this.maxWait = maxWait;
	}

	
	public boolean isDefaultAutoCommit() {
		return defaultAutoCommit;
	}

	@ConfigKey("defaultAutoCommit")
	public void setDefaultAutoCommit(boolean defaultAutoCommit) {
		this.defaultAutoCommit = defaultAutoCommit;
	}

	public Boolean getRemoveAbandoned() {
		return removeAbandoned;
	}

	@ConfigKey("removeAbandoned")
	public void setRemoveAbandoned(Boolean removeAbandoned) {
		this.removeAbandoned = removeAbandoned;
	}

	public double getTestDouble() {
		return testDouble;
	}

	@ConfigKey("testDouble")
	public void setTestDouble(double testDouble) {
		this.testDouble = testDouble;
	}

	public Double getTestDouble2() {
		return testDouble2;
	}

	@ConfigKey("testDouble2")
	public void setTestDouble2(Double testDouble2) {
		this.testDouble2 = testDouble2;
	}
	
	

	public String getCDN() {
		return CDN;
	}

	@ConfigKey("cdn")
	public void setCDN(String cDN) {
		CDN = cDN;
	}

	
	public float getF() {
		return f;
	}

	@ConfigKey("f")
	public void setF(float f) {
		this.f = f;
	}

	@Override
	public String toString() {
		return "TestConfigBean [driver=" + driver + ", url=" + url
				+ ", username=" + username + ", password=" + password
				+ ", maxActive=" + maxActive + ", initialSize=" + initialSize
				+ ", maxIdle=" + maxIdle + ", minIdle=" + minIdle
				+ ", maxWait=" + maxWait + ", defaultAutoCommit="
				+ defaultAutoCommit + ", removeAbandoned=" + removeAbandoned
				+ ", testDouble=" + testDouble + ", testDouble2=" + testDouble2
				+ ", CDN=" + CDN + ", f=" + f + "]";
	}

	
	

}
