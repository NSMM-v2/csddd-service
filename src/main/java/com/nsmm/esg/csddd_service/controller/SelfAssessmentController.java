package com.nsmm.esg.csddd_service.controller;

import com.nsmm.esg.csddd_service.dto.request.SelfAssessmentRequest;
import com.nsmm.esg.csddd_service.dto.response.SelfAssessmentFullResponse;
import com.nsmm.esg.csddd_service.dto.response.SelfAssessmentResponse;
import com.nsmm.esg.csddd_service.dto.response.ViolationDto;
import com.nsmm.esg.csddd_service.service.SelfAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/csddd")
@RequiredArgsConstructor
public class SelfAssessmentController {

    private final SelfAssessmentService selfAssessmentService;

    /**
     * 자가진단 제출
     */
    @PostMapping("/submit")
    public ResponseEntity<Void> submitAssessment(
            @RequestHeader(value = "X-USER-TYPE", required = false) String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerIdStr,
            @Valid @RequestBody List<SelfAssessmentRequest> requestList
    ) {
        try {
            // 헤더 검증
            AuthInfo authInfo = validateAndParseHeaders(userType, headquartersIdStr, partnerIdStr);

            log.debug("📥 [POST /submit] userType={}, userId={}, hqId={}, entries={}",
                    authInfo.userType, authInfo.userId, authInfo.headquartersId, requestList.size());

            selfAssessmentService.submitAssessment(
                    authInfo.userId,
                    authInfo.userType,
                    authInfo.headquartersId,
                    requestList
            );

            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            log.error("❌ 자가진단 제출 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ 자가진단 제출 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 자가진단 수정
     */
    @PutMapping("/update")
    public ResponseEntity<List<ViolationDto>> updateAssessment(
            @RequestHeader(value = "X-USER-TYPE", required = false) String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerIdStr,
            @Valid @RequestBody List<SelfAssessmentRequest> requestList
    ) {
        try {
            // 헤더 검증
            AuthInfo authInfo = validateAndParseHeaders(userType, headquartersIdStr, partnerIdStr);

            log.debug("♻️ [PUT /update] userType={}, userId={}, hqId={}, entries={}",
                    authInfo.userType, authInfo.userId, authInfo.headquartersId, requestList.size());

            // 기존 결과 삭제 후 새로 저장
            selfAssessmentService.submitAssessment(
                    authInfo.userId,
                    authInfo.userType,
                    authInfo.headquartersId,
                    requestList
            );

            // 위반 항목 반환 (권한 검증 포함)
            List<ViolationDto> violations = selfAssessmentService.getViolations(
                    authInfo.userId,
                    authInfo.userType,
                    authInfo.requesterId,
                    authInfo.requesterType
            );

            return ResponseEntity.ok(violations);

        } catch (IllegalArgumentException e) {
            log.error("❌ 자가진단 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ 자가진단 수정 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 자가진단 요약 결과 조회
     */
    @GetMapping("/result")
    public ResponseEntity<SelfAssessmentResponse> getResult(
            @RequestHeader(value = "X-USER-TYPE") String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerIdStr
    ) {
        try {
            AuthInfo authInfo = validateAndParseHeaders(userType, headquartersIdStr, partnerIdStr);

            log.debug("📥 [GET /result] requesterType={}, requesterId={}, targetUserId={}, targetUserType={}",
                    authInfo.requesterType, authInfo.requesterId, authInfo.userId, authInfo.userType);

            SelfAssessmentResponse result = selfAssessmentService.getResult(
                    authInfo.userId,
                    authInfo.userType,
                    authInfo.requesterId,
                    authInfo.requesterType
            );

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("❌ 자가진단 결과 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ 자가진단 결과 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 자가진단 전체 결과 + 상세 답변 조회
     */
    @GetMapping("/result/full")
    public ResponseEntity<SelfAssessmentFullResponse> getFullResult(
            @RequestHeader(value = "X-USER-TYPE") String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerIdStr
    ) {
        try {
            AuthInfo authInfo = validateAndParseHeaders(userType, headquartersIdStr, partnerIdStr);

            log.debug("📥 [GET /result/full] requesterType={}, requesterId={}, targetUserId={}, targetUserType={}",
                    authInfo.requesterType, authInfo.requesterId, authInfo.userId, authInfo.userType);

            SelfAssessmentFullResponse result = selfAssessmentService.getFullResult(
                    authInfo.userId,
                    authInfo.userType,
                    authInfo.requesterId,
                    authInfo.requesterType
            );

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("❌ 자가진단 전체 결과 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ 자가진단 전체 결과 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 위반 항목 조회
     */
    @GetMapping("/result/violations")
    public ResponseEntity<List<ViolationDto>> getViolations(
            @RequestHeader(value = "X-USER-TYPE") String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerIdStr
    ) {
        try {
            AuthInfo authInfo = validateAndParseHeaders(userType, headquartersIdStr, partnerIdStr);

            log.debug("📥 [GET /result/violations] requesterType={}, requesterId={}, targetUserId={}, targetUserType={}",
                    authInfo.requesterType, authInfo.requesterId, authInfo.userId, authInfo.userType);

            List<ViolationDto> violations = selfAssessmentService.getViolations(
                    authInfo.userId,
                    authInfo.userType,
                    authInfo.requesterId,
                    authInfo.requesterType
            );

            return ResponseEntity.ok(violations);

        } catch (IllegalArgumentException e) {
            log.error("❌ 위반 항목 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ 위반 항목 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 본사용: 소속 협력사들의 평가 결과 리스트 조회
     */
    @GetMapping("/partners/results")
    public ResponseEntity<List<SelfAssessmentResponse>> getPartnerResults(
            @RequestHeader(value = "X-USER-TYPE") String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr
    ) {
        try {
            if (!"HEADQUARTERS".equals(userType)) {
                log.error("❌ 본사만 접근 가능한 API - userType: {}", userType);
                return ResponseEntity.status(403).build();
            }

            Long headquartersId = parseOrNull(headquartersIdStr);
            if (headquartersId == null) {
                log.error("❌ 본사 ID 누락");
                return ResponseEntity.badRequest().build();
            }

            log.debug("📥 [GET /partners/results] headquartersId={}", headquartersId);

            List<SelfAssessmentResponse> results = selfAssessmentService.getPartnerResults(
                    headquartersId, userType
            );

            return ResponseEntity.ok(results);

        } catch (IllegalArgumentException e) {
            log.error("❌ 협력사 결과 리스트 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ 협력사 결과 리스트 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 본사용: 특정 협력사의 평가 결과 조회
     */
    @GetMapping("/partner/{partnerId}/result")
    public ResponseEntity<SelfAssessmentResponse> getPartnerResult(
            @PathVariable Long partnerId,
            @RequestHeader(value = "X-USER-TYPE") String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr
    ) {
        try {
            if (!"HEADQUARTERS".equals(userType)) {
                log.error("❌ 본사만 접근 가능한 API - userType: {}", userType);
                return ResponseEntity.status(403).build();
            }

            Long headquartersId = parseOrNull(headquartersIdStr);
            if (headquartersId == null) {
                log.error("❌ 본사 ID 누락");
                return ResponseEntity.badRequest().build();
            }

            log.debug("📥 [GET /partner/{}/result] headquartersId={}", partnerId, headquartersId);

            SelfAssessmentResponse result = selfAssessmentService.getPartnerResult(
                    partnerId, headquartersId, userType
            );

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("❌ 특정 협력사 결과 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ 특정 협력사 결과 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // === 내부 유틸리티 클래스 및 메서드 ===

    /**
     * 인증 정보를 담는 내부 클래스
     */
    private static class AuthInfo {
        final String userType;
        final String requesterType;
        final Long userId;           // 대상 사용자 ID
        final Long requesterId;      // 요청자 ID
        final Long headquartersId;

        AuthInfo(String userType, String requesterType, Long userId, Long requesterId, Long headquartersId) {
            this.userType = userType;
            this.requesterType = requesterType;
            this.userId = userId;
            this.requesterId = requesterId;
            this.headquartersId = headquartersId;
        }
    }

    /**
     * 헤더 검증 및 파싱
     */
    private AuthInfo validateAndParseHeaders(String userType, String headquartersIdStr, String partnerIdStr) {
        if (userType == null) {
            throw new IllegalArgumentException("사용자 타입이 누락되었습니다.");
        }

        Long headquartersId = parseOrNull(headquartersIdStr);
        Long partnerId = parseOrNull(partnerIdStr);

        if ("HEADQUARTERS".equals(userType)) {
            if (headquartersId == null) {
                throw new IllegalArgumentException("본사 ID가 누락되었습니다.");
            }

            // 본사인 경우
            Long targetUserId = (partnerId != null) ? partnerId : headquartersId;  // 조회 대상
            String targetUserType = (partnerId != null) ? "PARTNER" : "HEADQUARTERS";  // 대상 타입

            return new AuthInfo(targetUserType, "HEADQUARTERS", targetUserId, headquartersId, headquartersId);

        } else if ("PARTNER".equals(userType)) {
            if (partnerId == null || headquartersId == null) {
                throw new IllegalArgumentException("협력사는 파트너 ID와 본사 ID가 모두 필요합니다.");
            }

            // 협력사인 경우 (자신의 데이터만 조회 가능)
            return new AuthInfo("PARTNER", "PARTNER", partnerId, partnerId, headquartersId);

        } else {
            throw new IllegalArgumentException("유효하지 않은 사용자 타입입니다: " + userType);
        }
    }

    /**
     * 문자열을 Long으로 안전하게 변환
     */
    private Long parseOrNull(String value) {
        try {
            return (value != null && !value.isBlank()) ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            log.error("❌ 숫자 변환 실패: {}", value);
            return null;
        }
    }
}