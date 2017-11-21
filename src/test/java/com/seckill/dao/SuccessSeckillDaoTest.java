package com.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.seckill.entity.SuccessSeckill;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessSeckillDaoTest {
	
	@Autowired
	private SuccessSeckillDao successSeckillDao;

	@Test
	public void insertSuccessKilled(){
		long seckillId = 1000L;
		long userPhone = 15117941820L;
		int count = successSeckillDao.insertSuccessKilled(seckillId, userPhone);
		System.out.println("count:"+count);
	}
	
	@Test
	public void queryByIdWithSeckill(){
		long seckillId = 1000L;
		long userPhone = 15117941820L;
		SuccessSeckill s = successSeckillDao.queryByIdWithSeckill(seckillId, userPhone);
		System.out.println(s);
		System.out.println(s.getSeckill());
	}
}
