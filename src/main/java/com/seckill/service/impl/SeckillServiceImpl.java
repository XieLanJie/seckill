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
	
	//MD5盐值字符串，用于混淆MD5
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
		//优化点：缓存优化
		//1:访问redis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill == null){
			//2:访问数据库
			seckill = seckillDao.queryById(seckillId);
			if(seckill == null){
				return new Exposer(false, seckillId);
			}else{
				//3:放入redis
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
	 * 使用注解控制事务方法的特点:
	 * 1:开发团队达成一致性约定,明确标注事务方法的编程风格。
	 * 2:保证事务方法的执行时间尽可能短,不要穿插其他的网络操作HTTP/RPC请求或者剥离到事务方法的外部
	 * 3:不是所有的方法都需要事务,如果只有一条修改操作,只读操作不需要事务控制
	 */
	public SeckillExecution exceuteSeckill(long seckillId, long userPhone,
			String md5) throws SeckillException, RepeatKillException,
			SeckillCloseException {
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			throw new SeckillException("Seckill data rewrite-----------");
		}
		
		Date nowTime = new Date();
		
		try {
			//减库存
			int updateCount  = seckillDao.reduceNumber(seckillId, nowTime);
			if(updateCount < 0) {
				//没有更新到记录，秒杀结束
				throw new SeckillCloseException("seckill is close");
			}else{
				//记录购买明细
				int insertCount = successSeckillDao.insertSuccessKilled(seckillId, userPhone);
				if(insertCount <= 0){
					//重复秒杀
					throw new RepeatKillException("seckill repeated");
				}else{
					//秒杀成功
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
			//所有编译期异常，转化为运行期异常
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
		//存储过程执行完毕后result被赋值	
		try {
			seckillDao.killByProceure(map);
			//获取result
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
