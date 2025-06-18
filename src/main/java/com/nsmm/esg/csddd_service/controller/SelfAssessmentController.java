package com.nsmm.esg.csddd_service.controller;

import com.nsmm.esg.csddd_service.dto.request.SelfAssessmentRequest;
import com.nsmm.esg.csddd_service.dto.response.*;
import com.nsmm.esg.csddd_service.service.SelfAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CSDDD 자가진단 컨트롤러
 * - 자가진단 제출 및 결과 조회 관련 API를 처리함
 */
@RestController
@RequestMapping("/api/v1/csddd")
@RequiredArgsConstructor
public class SelfAssessmentController {

    private final SelfAssessmentService selfAssessmentService;

    /**
     * [POST] 자가진단 결과 제출
     * - 로그인 사용자 기준으로 자가진단 결과 저장
     */
    @PostMapping("/submit")
    public ResponseEntity<Void> submitAssessment(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody List<SelfAssessmentRequest> requestList
    ) {
        Long userId = jwt.getClaim("userId");
        String userType = jwt.getClaim("userType");
        Long headquartersId = jwt.getClaim("headquartersId");

        selfAssessmentService.submitAssessment(userId, userType, headquartersId, requestList);
        return ResponseEntity.ok().build();
    }

    /**
     * [GET] 자가진단 요약 결과 조회
     */
    @GetMapping("/result")
    public ResponseEntity<SelfAssessmentResponse> getResult(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.ok(selfAssessmentService.getResult(userId));
    }

    /**
     * [GET] 자가진단 전체 결과 조회
     */
    @GetMapping("/result/full")
    public ResponseEntity<SelfAssessmentFullResponse> getFullResult(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.ok(selfAssessmentService.getFullResult(userId));
    }

    /**
     * [GET] 위반 항목만 필터링 조회
     */
    @GetMapping("/result/violations")
    public ResponseEntity<List<ViolationDto>> getViolations(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.ok(selfAssessmentService.getViolations(userId));
    }
}