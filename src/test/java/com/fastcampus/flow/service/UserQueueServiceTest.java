package com.fastcampus.flow.service;

import com.fastcampus.flow.EmbeddedRedis;
import com.fastcampus.flow.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(EmbeddedRedis.class)
@ActiveProfiles("test")
class UserQueueServiceTest {
    @Autowired
    private UserQueueService userQueueService;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    // 독립 테스트를 위함
    @BeforeEach
    public void beforeEach() {
        ReactiveRedisConnection redisConnection = reactiveRedisTemplate.getConnectionFactory().getReactiveConnection();
        redisConnection.serverCommands().flushAll().subscribe();
    }

    @Test
    void registerWaitQueue() {
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(userQueueService.registerWaitQueue("default", 101L))
                .expectNext(2L)
                .verifyComplete();

        StepVerifier.create(userQueueService.registerWaitQueue("default", 102L))
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void alreadyRegisterWaitQueue() {
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L))
                .expectError(ApplicationException.class)
                .verify();

    }

    @Test // 몇명의 유저를 통과시킬지
    void emptyAllowUser(){
        StepVerifier.create(userQueueService.allowUser("default", 100L))
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void allowUser(){
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L)
                    .then(userQueueService.registerWaitQueue("default",101L))
                    .then(userQueueService.registerWaitQueue("default",102L))
                    .then(userQueueService.allowUser("default", 2L)))//2명 통과
                .expectNext(2L) // 통과된 인원 수 2명
                .verifyComplete();
    }

    @Test
    void allowUser2(){
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L)
                        .then(userQueueService.registerWaitQueue("default",101L))
                        .then(userQueueService.registerWaitQueue("default",102L))
                        .then(userQueueService.allowUser("default", 5L)))// 5명 통과
                .expectNext(3L) // 통과된 인원 수 3명
                .verifyComplete();
    }

    @Test
    void allowUserAfterRegisterWaitQueue(){
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L)
                        .then(userQueueService.registerWaitQueue("default",101L))
                        .then(userQueueService.registerWaitQueue("default",102L))
                        .then(userQueueService.allowUser("default", 3L))// 5명 통과
                        .then(userQueueService.registerWaitQueue("default",200L))
                        )
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void isNotAllowed(){
        StepVerifier.create(userQueueService.isAllowed("default", 100L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void isNotAllowed2(){
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L)
                        .then(userQueueService.allowUser("default",3L))
                        .then(userQueueService.isAllowed("default",101L)))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void isAllowed(){
        StepVerifier.create(userQueueService.registerWaitQueue("default", 100L)
                        .then(userQueueService.allowUser("default",3L))
                        .then(userQueueService.isAllowed("default",100L)))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isNotAllowedByToken() {
        StepVerifier.create(userQueueService.isAllowedByToken("default", 100L, ""))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void isAllowedByToken() {
        StepVerifier.create(userQueueService.isAllowedByToken("default", 100L, "d333a5d4eb24f3f5cdd767d79b8c01aad3cd73d3537c70dec430455d37afe4b8"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void generateToken() {
        StepVerifier.create(userQueueService.generateToken("default", 100L))
                .expectNext("d333a5d4eb24f3f5cdd767d79b8c01aad3cd73d3537c70dec430455d37afe4b8")
                .verifyComplete();
    }
}