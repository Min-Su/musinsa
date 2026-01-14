package com.bogue.assignment.api.dto;

public class PointUseHisDto {

    public long getUseHisId() {
		return useHisId;
	}
	public void setUseHisId(long useHisId) {
		this.useHisId = useHisId;
	}
	public long getUsePointId() {
		return usePointId;
	}
	public void setUsePointId(long usePointId) {
		this.usePointId = usePointId;
	}
	public long getEarnPointId() {
		return earnPointId;
	}
	public void setEarnPointId(long earnPointId) {
		this.earnPointId = earnPointId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	private long useHisId;
    private long usePointId;
    private long earnPointId;
    private String orderNo;
    private long amount;
    
}
