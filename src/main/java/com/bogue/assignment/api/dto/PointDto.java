package com.bogue.assignment.api.dto;

import java.time.LocalDateTime;


public class PointDto {

    public long getPointId() {
		return pointId;
	}

	public void setPointId(long pointId) {
		this.pointId = pointId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getPointType() {
		return pointType;
	}

	public void setPointType(String pointType) {
		this.pointType = pointType;
	}

	public String getPointFg() {
		return pointFg;
	}

	public void setPointFg(String pointFg) {
		this.pointFg = pointFg;
	}

	public long getOriginalAmount() {
		return originalAmount;
	}

	public void setOriginalAmount(long originalAmount) {
		this.originalAmount = originalAmount;
	}

	public long getRemainAmount() {
		return remainAmount;
	}

	public void setRemainAmount(long remainAmount) {
		this.remainAmount = remainAmount;
	}

	public LocalDateTime getExpiredDt() {
		return expiredDt;
	}

	public void setExpiredDt(LocalDateTime expiredDt) {
		this.expiredDt = expiredDt;
	}

	public LocalDateTime getCreateDt() {
		return createDt;
	}

	public void setCreateDt(LocalDateTime createDt) {
		this.createDt = createDt;
	}

	private long pointId;
    private long userId;
    private String pointType;
    private String pointFg;
    private long originalAmount;
    private long remainAmount;
    private LocalDateTime expiredDt;
    private LocalDateTime createDt;
    
    public boolean isExpired() {
        return expiredDt.isBefore(LocalDateTime.now());
    }

    public long consume(long amount) {
        long used = Math.min(remainAmount, amount);
        remainAmount -= used;
        return used;
    }

    public void restore(long amount) {
    	remainAmount += amount;
    }
    
}
