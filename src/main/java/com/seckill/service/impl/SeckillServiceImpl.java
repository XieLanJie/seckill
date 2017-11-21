package com.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.seckill.cache.RedisDao;
import com.seckill.dao.SeckillDao;
import com.seckill.dao.SuccessSeckillDao;
import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.Seckill;
import com.seckill.entity.SuccessSeckill;
import com.seckill.enums.SeckillStatEnum;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.service.SeckillService;

@Service("seckillService")
public class SeckillServiceImpl implements SeckillService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RedisDao redisDao;

	@Autowired
	private SeckillDao seckillDao;
	
	//MD5��ֵ�ַ��������ڻ���MD5
	private final String salt = "xlj";
	
	@Autowired
	private SuccessSeckillDao successSeckillDao;
	
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	public Exposer exportSeckillUrl(long seckillId) {
		//�Ż��㣺�����Ż�
		//1:����redis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill == null){
			//2:�������ݿ�
			seckill = seckillDao.queryById(seckillId);
			if(seckill == null){
				return new Exposer(false, seckillId);
			}else{
				//3:����redis
				redisDao.putSeckill(seckill);
			}
		}
		
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		Date now = new Date();
		if(now.getTime() < startTime.getTime()
				|| now.getTime() > endTime.getTime()){
			return new Exposer(false, seckillId, now.getTime(),startTime.getTime(), endTime.getTime());
		}
		String md5 = getMD5(seckillId);
		Exposer e = new Exposer(true, md5, seckillId);
		e.setExposed(true);
		return e;
	}
	
	private String getMD5(long seckillId){
		String base = seckillId + "/" +salt;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	@Transactional
	/**
	 * ʹ��ע��������񷽷����ص�:
	 * 1:�����ŶӴ��һ����Լ��,��ȷ��ע���񷽷��ı�̷��
	 * 2:��֤���񷽷���ִ��ʱ�価���ܶ�,��Ҫ�����������������HTTP/RPC������߰��뵽���񷽷����ⲿ
	 * 3:�������еķ�������Ҫ����,���ֻ��һ���޸Ĳ���,ֻ����������Ҫ�������
	 */
	public SeckillExecution exceuteSeckill(long seckillId, long userPhone,
			String md5) throws SeckillException, RepeatKillException,
			SeckillCloseException {
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			throw new SeckillException("Seckill data rewrite-----------");
		}
		
		Date nowTime = new Date();
		
		try {
			//�����
			int updateCount  = seckillDao.reduceNumber(seckillId, nowTime);
			if(updateCount < 0) {
				//û�и��µ���¼����ɱ����
				throw new SeckillCloseException("seckill is close");
			}else{
				//��¼������ϸ
				int insertCount = successSeckillDao.insertSuccessKilled(seckillId, userPhone);
				if(insertCount <= 0){
					//�ظ���ɱ
					throw new RepeatKillException("seckill repeated");
				}else{
					//��ɱ�ɹ�
					SuccessSeckill successSeckilled = successSeckillDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successSeckilled);
				}
			}
		}catch (SeckillCloseException e1){
			throw e1;
		} catch (RepeatKillException e2){
			throw e2;
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			//���б������쳣��ת��Ϊ�������쳣
			throw new SeckillException("seckill inner error:"+e.getMessage());
		}
	}

	public SeckillExecution exceuteSeckillProcedure(long seckillId,
			long userPhone, String md5) {
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			return new SeckillExecution(seckillId, SeckillStatEnum.DATA_REWRITE);
		}
		Date killTime = new Date();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		//�洢����ִ����Ϻ�result����ֵ	
		try {
			seckillDao.killByProceure(map);
			//��ȡresult
			Integer result = MapUtils.getInteger(map, "result",-2);
			if(result == 1){
				SuccessSeckill sk = successSeckillDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,sk);
			}else{
				return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
			}
		} catch (Exception e) {
			// TODO: handle exception
			return  new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
		}
		
	}

}
