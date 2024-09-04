package com.fastcampus.flow.controller;

import com.fastcampus.flow.dto.AllowUserResponse;
import com.fastcampus.flow.dto.AllowedUserResponse;
import com.fastcampus.flow.dto.RegisterUserResponse;
import com.fastcampus.flow.service.UserQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class UserQueueController {
    private final UserQueueService userQueueService;

    @PostMapping("")
    public Mono<RegisterUserResponse> registerUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                                   @RequestParam(name = "user_id") Long userId) {
        return userQueueService.registerWaitQueue(queue, userId)
                .map(RegisterUserResponse::new);
    }

    @PostMapping("/allow")
    public Mono<AllowUserResponse> allowUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                             @RequestParam(name = "count") Long count) {
        return userQueueService.allowUser(queue, count)
                .map(allowed -> new AllowUserResponse(count, allowed));
    }

    @GetMapping("/allowed")
    public Mono<AllowedUserResponse> isAllowedUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                                   @RequestParam(name = "user_id") Long userId){
        return userQueueService.isAllowed(queue, userId)
                .map(AllowedUserResponse::new);

    }
}

/*
curl -X POST localhost:9010/api/v1/queue/allow\?count=3
curl -X POST localhost:9010/api/v1/queue\?user_id=100
curl -X GET localhost:9010/api/v1/queue/allowed\?user_id=100
 */