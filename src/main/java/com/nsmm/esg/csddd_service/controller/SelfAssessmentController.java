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

    @PostMapping("/submit")
    public ResponseEntity<Void> submitAssessment(
            @RequestHeader(value = "X-USER-TYPE", required = false) String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerIdStr,
            @Valid @RequestBody List<SelfAssessmentRequest> requestList
    ) {
        try {
            AuthInfo authInfo = validateAndParseHeaders(userType, headquartersIdStr, partnerIdStr);

            log.debug("[POST /submit] userType={}, userId={}, hqId={}, entries={}",
                    authInfo.userType, authInfo.userId, authInfo.headquartersId, requestList.size());

            selfAssessmentService.submitAssessment(
                    authInfo.userId,
                    authInfo.userType,
                    authInfo.headquartersId,
                    authInfo.companyName,
                    requestList
            );

            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            log.error("자가진단 제출 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("자가진단 제출 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<List<ViolationDto>> updateAssessment(
            @RequestHeader(value = "X-USER-TYPE", required = false) String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerIdStr,
            @Valid @RequestBody SelfAssessmentRequest request
    ) {
        try {
            AuthInfo authInfo = validateAndParseHeaders(userType, headquartersIdStr, partnerIdStr);



            selfAssessmentService.submitAssessment(
                    authInfo.userId,
                    authInfo.userType,
                    authInfo.headquartersId,
                    request.getCompanyName(),
                    request.getAnswers()
            );

            List<ViolationDto> violations = selfAssessmentService.getViolations(
                    authInfo.userId,
                    authInfo.userType,
                    authInfo.requesterId,
                    authInfo.requesterType
            );

            return ResponseEntity.ok(violations);

        } catch (IllegalArgumentException e) {
            log.error("자가진단 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("자가진단 수정 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/result")
    public ResponseEntity<SelfAssessmentResponse> getResult(
            @RequestHeader(value = "X-USER-TYPE") String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerIdStr
    ) {
        try {
            AuthInfo authInfo = validateAndParseHeaders(userType, headquartersIdStr, partnerIdStr);

            log.debug("[GET /result] requesterType={}, requesterId={}, targetUserId={}, targetUserType={}",
                    authInfo.requesterType, authInfo.requesterId, authInfo.userId, authInfo.userType);

            SelfAssessmentResponse result = selfAssessmentService.getResult(
                    authInfo.userId,
                    authInfo.userType,
                    authInfo.requesterId,
                    authInfo.requesterType
            );

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("자가진단 결과 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("자가진단 결과 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/result/full")
    public ResponseEntity<SelfAssessmentFullResponse> getFullResult(
            @RequestHeader(value = "X-USER-TYPE") String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerIdStr
    ) {
        try {
            AuthInfo authInfo = validateAndParseHeaders(userType, headquartersIdStr, partnerIdStr);

            log.debug("[GET /result/full] requesterType={}, requesterId={}, targetUserId={}, targetUserType={}",
                    authInfo.requesterType, authInfo.requesterId, authInfo.userId, authInfo.userType);

            SelfAssessmentFullResponse result = selfAssessmentService.getFullResult(
                    authInfo.userId,
                    authInfo.userType,
                    authInfo.requesterId,
                    authInfo.requesterType
            );

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("자가진단 전체 결과 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("자가진단 전체 결과 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/result/violations")
    public ResponseEntity<List<ViolationDto>> getViolations(
            @RequestHeader(value = "X-USER-TYPE") String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerIdStr
    ) {
        try {
            AuthInfo authInfo = validateAndParseHeaders(userType, headquartersIdStr, partnerIdStr);

            log.debug("[GET /result/violations] requesterType={}, requesterId={}, targetUserId={}, targetUserType={}",
                    authInfo.requesterType, authInfo.requesterId, authInfo.userId, authInfo.userType);

            List<ViolationDto> violations = selfAssessmentService.getViolations(
                    authInfo.userId,
                    authInfo.userType,
                    authInfo.requesterId,
                    authInfo.requesterType
            );

            return ResponseEntity.ok(violations);

        } catch (IllegalArgumentException e) {
            log.error("위반 항목 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("위반 항목 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 본사 - 전체 협력사 결과 조회 (1차 + 2차 모두)
     */
    @GetMapping("/partners/results")
    public ResponseEntity<List<SelfAssessmentResponse>> getPartnerResults(
            @RequestHeader(value = "X-USER-TYPE") String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr
    ) {
        try {
            if (!"HEADQUARTERS".equals(userType)) {
                log.error("본사만 접근 가능한 API - userType: {}", userType);
                return ResponseEntity.status(403).build();
            }

            Long headquartersId = parseOrNull(headquartersIdStr);
            if (headquartersId == null) {
                log.error("본사 ID 누락");
                return ResponseEntity.badRequest().build();
            }

            log.debug("[GET /partners/results] headquartersId={}", headquartersId);

            List<SelfAssessmentResponse> results = selfAssessmentService.getPartnerResults(
                    headquartersId, userType
            );

            return ResponseEntity.ok(results);

        } catch (IllegalArgumentException e) {
            log.error("협력사 결과 리스트 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("협력사 결과 리스트 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 1차 협력사 - 소속 2차 협력사 결과 조회
     */
    @GetMapping("/sub-partners/results")
    public ResponseEntity<List<SelfAssessmentResponse>> getSubPartnerResults(
            @RequestHeader(value = "X-USER-TYPE") String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerIdStr
    ) {
        try {
            if (!"FIRST_TIER_PARTNER".equals(userType)) {
                log.error("1차 협력사만 접근 가능한 API - userType: {}", userType);
                return ResponseEntity.status(403).build();
            }

            Long headquartersId = parseOrNull(headquartersIdStr);
            Long partnerId = parseOrNull(partnerIdStr);

            if (headquartersId == null || partnerId == null) {
                log.error("본사 ID 또는 협력사 ID 누락");
                return ResponseEntity.badRequest().build();
            }

            log.debug("[GET /sub-partners/results] 1차협력사ID={}, 본사ID={}", partnerId, headquartersId);

            List<SelfAssessmentResponse> results = selfAssessmentService.getSubPartnerResults(
                    partnerId, headquartersId, userType
            );

            return ResponseEntity.ok(results);

        } catch (IllegalArgumentException e) {
            log.error("하위 협력사 결과 리스트 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("하위 협력사 결과 리스트 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 본사 - 특정 협력사 결과 조회
     */
    @GetMapping("/partner/{partnerId}/result")
    public ResponseEntity<SelfAssessmentResponse> getPartnerResult(
            @PathVariable Long partnerId,
            @RequestHeader(value = "X-USER-TYPE") String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr
    ) {
        try {
            if (!"HEADQUARTERS".equals(userType)) {
                log.error("본사만 접근 가능한 API - userType: {}", userType);
                return ResponseEntity.status(403).build();
            }

            Long headquartersId = parseOrNull(headquartersIdStr);
            if (headquartersId == null) {
                log.error("본사 ID 누락");
                return ResponseEntity.badRequest().build();
            }

            log.debug("[GET /partner/{}/result] headquartersId={}", partnerId, headquartersId);

            SelfAssessmentResponse result = selfAssessmentService.getPartnerResult(
                    partnerId, headquartersId, userType
            );

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("특정 협력사 결과 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("특정 협력사 결과 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 1차 협력사 - 특정 2차 협력사 결과 조회
     */
    @GetMapping("/sub-partner/{subPartnerId}/result")
    public ResponseEntity<SelfAssessmentResponse> getSubPartnerResult(
            @PathVariable Long subPartnerId,
            @RequestHeader(value = "X-USER-TYPE") String userType,
            @RequestHeader(value = "X-HEADQUARTERS-ID", required = false) String headquartersIdStr,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerIdStr
    ) {
        try {
            if (!"FIRST_TIER_PARTNER".equals(userType)) {
                log.error("1차 협력사만 접근 가능한 API - userType: {}", userType);
                return ResponseEntity.status(403).build();
            }

            Long headquartersId = parseOrNull(headquartersIdStr);
            Long firstTierPartnerId = parseOrNull(partnerIdStr);

            if (headquartersId == null || firstTierPartnerId == null) {
                log.error("본사 ID 또는 1차 협력사 ID 누락");
                return ResponseEntity.badRequest().build();
            }

            log.debug("[GET /sub-partner/{}/result] 1차협력사ID={}, 2차협력사ID={}, 본사ID={}",
                    firstTierPartnerId, subPartnerId, headquartersId);

            SelfAssessmentResponse result = selfAssessmentService.getSubPartnerResult(
                    subPartnerId, firstTierPartnerId, headquartersId, userType
            );

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("특정 2차 협력사 결과 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("특정 2차 협력사 결과 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private static class AuthInfo {
        final String userType;
        final String requesterType;
        final Long userId;
        final Long requesterId;
        final Long headquartersId;
        String companyName;

        AuthInfo(String userType, String requesterType, Long userId, Long requesterId, Long headquartersId) {
            this.userType = userType;
            this.requesterType = requesterType;
            this.userId = userId;
            this.requesterId = requesterId;
            this.headquartersId = headquartersId;
            this.companyName = null;
        }
        void setCompanyName(String companyName) {
            this.companyName = companyName;
        }
    }

    private AuthInfo validateAndParseHeaders(String userType, String headquartersIdStr, String partnerIdStr) {
        if (userType == null) {
            throw new IllegalArgumentException("사용자 타입이 누락되었습니다.");
        }

        Long headquartersId = parseOrNull(headquartersIdStr);
        Long partnerId = parseOrNull(partnerIdStr);

        switch (userType) {
            case "HEADQUARTERS":
                if (headquartersId == null) {
                    throw new IllegalArgumentException("본사 ID가 누락되었습니다.");
                }

                Long targetUserId = (partnerId != null) ? partnerId : headquartersId;
                String targetUserType = (partnerId != null) ? "PARTNER" : "HEADQUARTERS";

                return new AuthInfo(targetUserType, "HEADQUARTERS", targetUserId, headquartersId, headquartersId);

            case "FIRST_TIER_PARTNER":
                if (partnerId == null || headquartersId == null) {
                    throw new IllegalArgumentException("1차 협력사는 파트너 ID와 본사 ID가 모두 필요합니다.");
                }

                return new AuthInfo("FIRST_TIER_PARTNER", "FIRST_TIER_PARTNER", partnerId, partnerId, headquartersId);

            case "SECOND_TIER_PARTNER":
                if (partnerId == null || headquartersId == null) {
                    throw new IllegalArgumentException("2차 협력사는 파트너 ID와 본사 ID가 모두 필요합니다.");
                }

                return new AuthInfo("SECOND_TIER_PARTNER", "SECOND_TIER_PARTNER", partnerId, partnerId, headquartersId);

            case "PARTNER": // 하위 호환성을 위해 유지
                if (partnerId == null || headquartersId == null) {
                    throw new IllegalArgumentException("협력사는 파트너 ID와 본사 ID가 모두 필요합니다.");
                }

                return new AuthInfo("PARTNER", "PARTNER", partnerId, partnerId, headquartersId);

            default:
                throw new IllegalArgumentException("유효하지 않은 사용자 타입입니다: " + userType);
        }
    }

    private Long parseOrNull(String value) {
        try {
            return (value != null && !value.isBlank()) ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            log.error("숫자 변환 실패: {}", value);
            return null;
        }
    }
}