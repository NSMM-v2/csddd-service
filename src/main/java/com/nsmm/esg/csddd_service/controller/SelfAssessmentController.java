package com.nsmm.esg.csddd_service.controller;

import com.nsmm.esg.csddd_service.dto.ApiResponse;
import com.nsmm.esg.csddd_service.dto.request.SelfAssessmentSubmitRequest;
import com.nsmm.esg.csddd_service.dto.response.SelfAssessmentResultResponse;
import com.nsmm.esg.csddd_service.dto.response.ViolationMeta;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import com.nsmm.esg.csddd_service.service.SelfAssessmentService;
import com.nsmm.esg.csddd_service.util.ViolationMetaMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CSDDD 자가진단 API 컨트롤러
 * 
 * 주요 기능:
 * - 자가진단 제출 (점수 계산, 등급 산정)
 * - 자가진단 결과 조회 (단건/목록, 권한별 필터링)
 * - 중대위반 메타데이터 조회
 * - 본사/협력사 권한 기반 접근 제어
 * 
 */
@RestController
@RequestMapping("/api/v1/csddd")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "CSDDD 자가진단", description = "CSDDD 자가진단 제출, 조회, 메타데이터 관리 API")
public class SelfAssessmentController {

    private final SelfAssessmentService selfAssessmentService;

    /**
     * 자가진단 결과 제출
     * 답변 데이터 검증, 점수 계산, 등급 산정을 통한 자가진단 완료 처리
     */
    @PostMapping("/submit")
    @Operation(summary = "자가진단 제출", description = "완료된 자가진단 답변을 제출하여 점수 계산 및 등급을 산정합니다")
    public ResponseEntity<ApiResponse<Void>> submitSelfAssessment(
            @Valid @RequestBody SelfAssessmentSubmitRequest request,
            @RequestHeader("X-USER-TYPE") String userType,
            @RequestHeader("X-HEADQUARTERS-ID") String headquartersId,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerId,
            @RequestHeader("X-TREE-PATH") String treePath) {

        log.info("자가진단 제출 요청: 회사={}, 사용자유형={}", request.getCompanyName(), userType);

        try {
            selfAssessmentService.submitSelfAssessment(request, userType, headquartersId, partnerId, treePath);

            log.info("자가진단 제출 성공: 회사={}", request.getCompanyName());
            return ResponseEntity.ok(ApiResponse.success(null, "자가진단이 성공적으로 제출되었습니다."));

        } catch (IllegalArgumentException e) {
            log.warn("자가진단 제출 실패 (잘못된 데이터): {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "INVALID_DATA"));
        } catch (IllegalStateException e) {
            log.warn("자가진단 제출 실패 (상태 오류): {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "INVALID_STATE"));
        } catch (Exception e) {
            log.error("자가진단 제출 중 서버 오류 발생", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("서버 오류가 발생했습니다.", "INTERNAL_ERROR"));
        }
    }

    /**
     * 자가진단 결과 단건 상세 조회
     * 특정 자가진단 결과의 상세 정보 및 문항별 답변을 조회합니다
     */
    @GetMapping("/{resultId}")
    @Operation(summary = "자가진단 결과 상세 조회", description = "특정 자가진단 결과의 상세 정보와 문항별 답변을 조회합니다")
    public ResponseEntity<ApiResponse<SelfAssessmentResultResponse>> getSelfAssessmentResult(
            @PathVariable Long resultId,
            @RequestHeader("X-USER-TYPE") String userType,
            @RequestHeader("X-HEADQUARTERS-ID") Long headquartersId,
            @RequestHeader(value = "X-PARTNER-ID", required = false) Long partnerId,
            @RequestHeader("X-TREE-PATH") String treePath) {

        log.info("자가진단 결과 상세 조회 요청: ID={}, 사용자유형={}", resultId, userType);

        try {
            SelfAssessmentResult result = selfAssessmentService.getSelfAssessmentResult(
                    resultId, userType, headquartersId, partnerId, treePath);

            SelfAssessmentResultResponse response = SelfAssessmentResultResponse.fromDetail(result);

            log.info("자가진단 결과 상세 조회 성공: ID={}", resultId);
            return ResponseEntity.ok(ApiResponse.success(response, "자가진단 결과가 조회되었습니다."));

        } catch (IllegalArgumentException e) {
            log.warn("자가진단 결과 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "RESULT_NOT_FOUND"));
        } catch (SecurityException e) {
            log.warn("자가진단 결과 접근 권한 없음: {}", e.getMessage());
            return ResponseEntity.status(403)
                    .body(ApiResponse.error(e.getMessage(), "ACCESS_DENIED"));
        } catch (Exception e) {
            log.error("자가진단 결과 조회 중 서버 오류 발생", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("서버 오류가 발생했습니다.", "INTERNAL_ERROR"));
        }
    }

    /**
     * 자가진단 결과 목록 조회 (페이징)
     * 조건별 필터링과 권한 기반 접근 제어를 통한 자가진단 결과 목록 조회
     */
    @GetMapping("/results")
    @Operation(summary = "자가진단 결과 목록 조회", description = "조건별 필터링과 페이징을 통한 자가진단 결과 목록을 조회합니다")
    public ResponseEntity<ApiResponse<Page<SelfAssessmentResultResponse>>> getSelfAssessmentResults(
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Boolean onlyPartners,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestHeader("X-USER-TYPE") String userType,
            @RequestHeader("X-HEADQUARTERS-ID") Long headquartersId,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerIdRaw,
            @RequestHeader(value = "X-TREE-PATH", required = false) String treePath) {

        log.info("자가진단 결과 목록 조회 요청: 사용자유형={}, 본사ID={}", userType, headquartersId);

        try {
            // 협력사 ID 안전 파싱
            Long resolvedPartnerId = parsePartnerIdSafely(partnerIdRaw);

            Page<SelfAssessmentResultResponse> resultPage = selfAssessmentService
                    .getSelfAssessmentResults(userType, headquartersId, resolvedPartnerId, treePath,
                            companyName, category, startDate, endDate, pageable, onlyPartners)
                    .map(SelfAssessmentResultResponse::fromSummary);

            log.info("자가진단 결과 목록 조회 성공: 총 {}건", resultPage.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(resultPage, "자가진단 결과 목록이 조회되었습니다."));

        } catch (IllegalArgumentException e) {
            log.warn("자가진단 결과 목록 조회 실패 (잘못된 파라미터): {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "INVALID_PARAMETER"));
        } catch (Exception e) {
            log.error("자가진단 결과 목록 조회 중 서버 오류 발생", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("서버 오류가 발생했습니다.", "INTERNAL_ERROR"));
        }
    }

    /**
     * 중대위반 메타데이터 조회
     * 특정 문항의 중대위반 관련 법적 근거, 처벌 정보 등을 조회합니다
     */
    @GetMapping("/violation-meta/{questionId}")
    @Operation(summary = "중대위반 메타데이터 조회", description = "특정 문항의 중대위반 관련 법적 근거와 처벌 정보를 조회합니다")
    public ResponseEntity<ApiResponse<ViolationMeta>> getViolationMeta(
            @PathVariable String questionId,
            @RequestHeader("X-USER-TYPE") String userType,
            @RequestHeader("X-HEADQUARTERS-ID") String headquartersId,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerId,
            @RequestHeader(value = "X-TREE-PATH", required = false) String treePath) {

        log.info("중대위반 메타데이터 조회 요청: 문항ID={}", questionId);

        try {
            ViolationMeta meta = ViolationMetaMap.get(questionId);

            if (meta == null) {
                log.warn("중대위반 메타데이터를 찾을 수 없음: 문항ID={}", questionId);
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("해당 문항의 메타데이터를 찾을 수 없습니다.", "METADATA_NOT_FOUND"));
            }

            log.info("중대위반 메타데이터 조회 성공: 문항ID={}", questionId);
            return ResponseEntity.ok(ApiResponse.success(meta, "중대위반 메타데이터가 조회되었습니다."));

        } catch (Exception e) {
            log.error("중대위반 메타데이터 조회 중 서버 오류 발생", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("서버 오류가 발생했습니다.", "INTERNAL_ERROR"));
        }
    }

    /**
     * 이메일 중복 확인 (향후 확장용)
     */
    @GetMapping("/check-company")
    @Operation(summary = "회사명 중복 확인", description = "자가진단 제출 시 회사명 중복 여부 확인")
    public ResponseEntity<ApiResponse<Boolean>> checkCompanyName(@RequestParam String companyName) {

        log.info("회사명 중복 확인 요청: {}", companyName);

        try {
            // TODO: 실제 중복 확인 로직 구현
            boolean isDuplicate = false; // 임시로 false 반환
            String message = isDuplicate ? "이미 자가진단을 완료한 회사입니다." : "자가진단 진행 가능한 회사입니다.";

            return ResponseEntity.ok(ApiResponse.success(!isDuplicate, message));

        } catch (Exception e) {
            log.error("회사명 중복 확인 중 오류 발생", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("서버 오류가 발생했습니다.", "INTERNAL_ERROR"));
        }
    }

    /**
     * 협력사 ID 안전 파싱
     * 문자열로 전달된 협력사 ID를 Long으로 안전하게 변환합니다
     */
    private Long parsePartnerIdSafely(String partnerIdRaw) {
        if (partnerIdRaw == null || partnerIdRaw.trim().isEmpty()) {
            return null;
        }

        try {
            return Long.valueOf(partnerIdRaw.trim());
        } catch (NumberFormatException e) {
            log.warn("협력사 ID 파싱 실패: {}", partnerIdRaw);
            return null;
        }
    }
}