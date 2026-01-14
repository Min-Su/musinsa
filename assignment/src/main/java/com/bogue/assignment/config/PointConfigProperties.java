package com.bogue.assignment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="point")
public class PointConfigProperties {
	
	private long maxEarnOnce;
	private long maxTotalBalance;
	private int defaultExpireDays;
	
	public long getMaxEarnOnce() {
		return this.maxEarnOnce;
	}
	
	public long getMaxTotalBalance() {
		return this.maxTotalBalance;
	}
	
	public long getDefaultExpireDays() {
		return this.defaultExpireDays;
	}
	
}
