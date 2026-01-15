package com.bogue.assignment.api.dto;

public class UserPointDto {
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getPointTotal() {
		return pointTotal;
	}

	public void setPointTotal(long pointTotal) {
		this.pointTotal = pointTotal;
	}

	private long userId;
    private long pointTotal;
    
    public void increase(long amount) {
        this.pointTotal += amount;
    }

    public void decrease(long amount) {
        if (this.pointTotal < amount) {
            throw new IllegalStateException("잔액 부족");
        }
        this.pointTotal -= amount;
    }
    
}
