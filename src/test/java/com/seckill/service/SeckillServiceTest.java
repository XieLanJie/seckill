package com.seckill.service;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
					   "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;
	
	@Test
	public void getSeckillList(){
		List<Seckill> seckills = seckillService.getSeckillList();
		logger.info("list={}",seckills);
	}
	
	@Test
	public void getById(){
		long seckillId = 1000L;
		Seckill seckill = seckillService.getById(seckillId);
		logger.info("seckill={}",seckill);
	}
	
	@Test
	public void exportSeckillUrl(){
		long seckillId = 1000L;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		System.out.println(exposer);
		logger.info("exposer={}",exposer);
	}
	
	@Test
	public void exportSeckillLogic(){
		long seckillId = 1000L;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if(exposer.isExposed()){
			logger.info("exposer={}",exposer);
			long userPhone = 15117941852L;
			String md5 = exposer.getMd5();
			SeckillExecution seckill = seckillService.exceuteSeckill(seckillId, userPhone, md5);
			logger.info("seckill={}",seckill);
		}else{
			logger.warn("exposer={}",exposer);
		}
	}
	
	@Test
	public void exceuteSeckill() {
		long seckillId = 1000L;
		long userPhone = 15117941821L;
		String md5 = "68261901d2511ec2fc3c5f381dd48128";
		SeckillExecution seckill = seckillService.exceuteSeckill(seckillId, userPhone, md5);
		logger.info("seckill={}",seckill);
	}
}
