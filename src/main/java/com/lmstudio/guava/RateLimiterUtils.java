/**
 * project name:farm
 * file name:RateLimiterUtils.java
 * package name:com.agroait.farm.util
 * create date:2017年12月11日下午4:26:59
*/
package com.lmstudio.guava;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;

/**
 * TODO
 * RateLimiter是guava提供的基于令牌桶算法的实现类，可以非常简单的完成限流特技，并且根据系统的实际情况来调整生成token的速率
 *@author jason-liu
 *
 */
public class RateLimiterUtils {

	static final RateLimiter limiter = RateLimiter.create(2.0);//速率为每秒2
	
	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();
		
		for(int i=0; i<100; i++) {
			
			/**
		     * tryAcquire(long timeout, TimeUnit unit)
		     * 从RateLimiter 获取许可如果该许可可以在不超过timeout的时间内获取得到的话，
		     * 或者如果无法在timeout 过期之前获取得到许可的话，那么立即返回false（无需等待）
		     * 
		     * limiter.acquire();//该方法会阻塞线程，直到令牌桶中能取到令牌为止才继续向下执行，并返回等待的时间。
		     */
			if(!limiter.tryAcquire(400, TimeUnit.MILLISECONDS)) {
				System.out.println("获取时间超过1s，不再等待，返回，i="+i);
				continue;
			}

			System.out.println("执行"+i);
		}
		
		long endTime = System.currentTimeMillis();
		
		//限速情况下，预计10s左右，不限速，时间可以忽略不到1s
		System.out.println("总共花费时间："+(endTime-startTime)/1000);
	}
	
}
