package com.seckill.service;

import java.util.List;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.Seckill;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;

/**
 * ҵ��ӿ�:վ��ʹ���ߵĽǶ���ƽӿ�
 * @author Administrator
 *
 */
public interface SeckillService {

	/**
	 * ��ѯ������ɱ��¼
	 * @return
	 */
	List<Seckill> getSeckillList();
	
	/**
	 * ��ѯ������ɱ��¼
	 * @param seckillId
	 * @return
	 */
	Seckill getById(long seckillId);
	
	/**
	 * ��ɱ��ʼ�����ɱ�ӿڵĵ�ַ���������ϵͳʱ�����ɱ�ӿ�
	 * @param seckillId
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	/**
	 * ִ����ɱ
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	SeckillExecution exceuteSeckill(long seckillId,long userPhone,String md5) throws SeckillException,RepeatKillException,SeckillCloseException;

	/**
	 * ִ����ɱ����by �洢����
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 * @throws SeckillException
	 * @throws RepeatKillException
	 * @throws SeckillCloseException
	 */
	SeckillExecution exceuteSeckillProcedure(long seckillId,long userPhone,String md5);
}
