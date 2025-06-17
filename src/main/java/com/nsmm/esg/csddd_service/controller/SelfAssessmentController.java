package com.nsmm.esg.csddd_service.controller;

import com.nsmm.esg.csddd_service.dto.request.SelfAssessmentRequest;
import com.nsmm.esg.csddd_service.dto.response.*;
import com.nsmm.esg.csddd_service.service.SelfAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 📌 CSDDD 자가진단 컨트롤러
 * - 자가진단 제출 및 결과 조회 관련 API를 처리함
 */
@RestController
@RequestMapping("/api/csddd")
@RequiredArgsConstructor
public class SelfAssessmentController {

    // 🔧 서비스 레이어 의존성 주입
    private final SelfAssessmentService selfAssessmentService;

    /**
     * ✅ [POST] 자가진단 결과 제출
     * - 클라이언트로부터 자가진단 응답 리스트를 받아 저장 및 분석 수행
     * @param memberId 헤더에서 전달된 회원 ID
     * @param requestList 자가진단 질문에 대한 응답 목록
     * @return 200 OK 응답
     */
    @PostMapping("/submit")
    public ResponseEntity<Void> submitAssessment(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @Valid @RequestBody(required = true) List<SelfAssessmentRequest> requestList
    ) {
        System.out.println(">> SelfAssessmentController - memberId: " + memberId);
        System.out.println(">> Received answers: " + requestList.size());

        // 서비스에 응답 저장 및 분석 요청
        selfAssessmentService.submitAssessment(memberId, requestList);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ [GET] 자가진단 요약 결과 조회
     * - 점수, 등급, 위반 수, 완료일 등 간단한 정보 반환
     * @param memberId 헤더에서 전달된 회원 ID
     * @return SelfAssessmentResponse (요약 응답 DTO)
     */
    @GetMapping("/result")
    public ResponseEntity<SelfAssessmentResponse> getResult(
            @RequestHeader("X-MEMBER-ID") Long memberId
    ) {
        return ResponseEntity.ok(selfAssessmentService.getResult(memberId));
    }

    /**
     * ✅ [GET] 자가진단 전체 결과 조회
     * - 요약 + 개별 문항에 대한 상세 답변 포함
     * @param memberId 헤더에서 전달된 회원 ID
     * @return SelfAssessmentFullResponse (전체 결과 DTO)
     */
    @GetMapping("/result/full")
    public ResponseEntity<SelfAssessmentFullResponse> getFullResult(
            @RequestHeader("X-MEMBER-ID") Long memberId
    ) {
        return ResponseEntity.ok(selfAssessmentService.getFullResult(memberId));
    }

    /**
     * ✅ [GET] 위반 항목만 필터링 조회
     * - YES가 아닌 항목들 중 중요 위반(CRITICAL) 플래그가 true인 항목만 추출
     * @param memberId 헤더에서 전달된 회원 ID
     * @return List<ViolationDto> (위반 항목 리스트)
     */
    @GetMapping("/result/violations")
    public ResponseEntity<List<ViolationDto>> getViolations(
            @RequestHeader("X-MEMBER-ID") Long memberId
    ) {
        return ResponseEntity.ok(selfAssessmentService.getViolations(memberId));
    }
}