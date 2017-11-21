package com.seckill.dao;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.Seckill;
import com.seckill.service.SeckillService;

/**
 * 配置spring和junit整合,junit启动时加载springIOC容器
 * @author Administrator
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉Junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml",
						"classpath:spring/spring-service.xml"})
public class SeckillDaoTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//注入Dao实现类依赖
	@Autowired
	private SeckillDao seckillDao;
	
	@Autowired
	private SeckillService seckillService;
	

	@Test
	public void testReduceNumber() throws Exception{
		Date killTime = new Date();
		int count = seckillDao.reduceNumber(1000L, killTime);
		System.out.println("count:"+count);
	}
	
	@Test
	public void  testQueryById()throws Exception{
		long id=1000;
		Seckill seckill = seckillDao.queryById(id);
		System.out.println(seckill);
	}
	
	@Test
	public void testQueryAll()throws Exception{
		List<Seckill> list = seckillDao.queryAll(0, 100);
		for (Seckill seckill : list) {
			System.out.println(seckill);
		}
	}
	
	@Test
	public void testExceuteSeckillProcedure(){
		 long seckillId = 1003L;
		 long phone = 13681110100L;
		 Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		 if(exposer.isExposed()){
			 String md5 = exposer.getMd5();
			 SeckillExecution s = seckillService.exceuteSeckillProcedure(seckillId, phone, md5);
			 logger.info(s.getStateInfo());
		 }
		 
	}
}
