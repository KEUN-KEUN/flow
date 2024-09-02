package com.fastcampus.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserQueueService {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    // 대기열 등록 api

    public Mono<Long> registerWaitQueue(final Long userId){
        // redis sortedest
        // key : userid
        // value : unix timestamp
        var unixTimestamp = Instant.now().getEpochSecond();
        return reactiveRedisTemplate.opsForZSet().add("user-queue", userId.toString(), unixTimestamp)
                .filter(i->i)
                .switchIfEmpty(Mono.error(new Exception("already register error....")))
                .flatMap(i -> reactiveRedisTemplate.opsForZSet().rank("user-queue",userId.toString()));
    }
}
