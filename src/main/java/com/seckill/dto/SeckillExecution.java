package com.seckill.dto;

import com.seckill.entity.SuccessSeckill;
import com.seckill.enums.SeckillStatEnum;

/**
 * ��װ��ɱִ�к���
 * @author Administrator
 *
 */
public class SeckillExecution {

	//��ɱid
	private long seckillId;
	
	//��ɱִ�н��״̬
	private int state;
	
	//״̬��ʾ
	private String stateInfo;
	
	//��ɱ�ɹ�����
	private SuccessSeckill successSeckill;

	public SeckillExecution(long seckillId, SeckillStatEnum statEnum,
			SuccessSeckill successSeckill) {
		this.seckillId = seckillId;
		this.state = statEnum.getState();
		this.stateInfo = statEnum.getStateInfo();
		this.successSeckill = successSeckill;
	}
	

	public SeckillExecution(long seckillId, SeckillStatEnum statEnum) {
		this.seckillId = seckillId;
		this.state = statEnum.getState();
		this.stateInfo = statEnum.getStateInfo();
	}



	public long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}

	public SuccessSeckill getSuccessSeckill() {
		return successSeckill;
	}

	public void setSuccessSeckill(SuccessSeckill successSeckill) {
		this.successSeckill = successSeckill;
	}


	@Override
	public String toString() {
		return "SeckillExecution [seckillId=" + seckillId + ", state=" + state
				+ ", stateInfo=" + stateInfo + ", successSeckill="
				+ successSeckill + "]";
	}
	
	
}
