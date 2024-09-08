package com.fastcampus.flow;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
public class FlowApplication {
	public static void main(String[] args) {
		SpringApplication.run(FlowApplication.class, args);
	}
}
/*
http://127.0.0.1:9010/waiting-room?user_id=510&redirect_rul=http://www.naver.com

public class FlowApplication implements ApplicationListener<ApplicationReadyEvent> {
	private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

	public static void main(String[] args) {
		SpringApplication.run(FlowApplication.class, args);
	}

	public void onApplicationEvent(ApplicationReadyEvent event) {
		reactiveRedisTemplate.opsForValue().set("testkey","testvalue").subscribe();
	}

}
*/