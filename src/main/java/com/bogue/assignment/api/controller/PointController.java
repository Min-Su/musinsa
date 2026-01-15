package com.bogue.assignment.api.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bogue.assignment.api.service.PointService;

@RestController
@RequestMapping("/point")
public class PointController {
	
	@Autowired
	PointService pointService;
	
    @PostMapping("/earn")
    public ResponseEntity<Boolean> earn(
            @RequestParam Long userId,
            @RequestParam Long amount,
            @RequestParam(defaultValue = "false") boolean manual
    ) {
        pointService.earn(userId, amount, manual);
        return ResponseEntity.ok(true);
    }
	
    @PostMapping("/use")
    public ResponseEntity<Boolean> use(
            @RequestParam Long userId,
            @RequestParam String orderNo,
            @RequestParam Long amount
    ) {
        pointService.use(userId, orderNo, amount);
        return ResponseEntity.ok(true);
    }
	
    @PostMapping("/use/cancel")
    public ResponseEntity<Boolean> cancelUse(
    		@RequestParam Long userId,
            @RequestParam Long usePointId,
            @RequestParam Long amount
    ) {
        pointService.cancelUse(userId, usePointId, amount);
        return ResponseEntity.ok(true);
    }
	
    @PostMapping("/earn/cancel/{pointId}")
    public ResponseEntity<Boolean> cancelEarn(
            @PathVariable Long pointId,
            @RequestParam Long userId
    ) {
        pointService.cancelEarn(userId, pointId);
        return ResponseEntity.ok(true);
    }
}
