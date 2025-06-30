package com.nsmm.esg.csddd_service.controller;

import com.nsmm.esg.csddd_service.dto.ApiResponse;
import com.nsmm.esg.csddd_service.dto.request.SelfAssessmentSubmitRequest;
import com.nsmm.esg.csddd_service.dto.response.SelfAssessmentResponse;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import com.nsmm.esg.csddd_service.service.SelfAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/csddd")
@RequiredArgsConstructor
public class SelfAssessmentController {

    private final SelfAssessmentService selfAssessmentService;

    /**
     * 자가진단 제출 (Create)
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<Void>> submitSelfAssessment(
            @RequestBody SelfAssessmentSubmitRequest request,
            @RequestHeader("X-USER-TYPE") String userType,
            @RequestHeader("X-HEADQUARTERS-ID") String headquartersId,
            @RequestHeader(value = "X-PARTNER-ID", required = false) String partnerId,
            @RequestHeader("X-TREE-PATH") String treePath
    ) {
        selfAssessmentService.submitSelfAssessment(request, userType, headquartersId, partnerId, treePath);
        return ResponseEntity.ok(ApiResponse.success(null, "자가진단이 성공적으로 제출되었습니다."));
    }

    /**
     * 자가진단 결과 단건 조회 (Read)
     */
    @GetMapping("/{resultId}")
    public ResponseEntity<ApiResponse<SelfAssessmentResponse>> getSelfAssessmentResult(
            @PathVariable Long resultId,
            @RequestHeader("X-USER-TYPE") String userType,
            @RequestHeader("X-HEADQUARTERS-ID") Long headquartersId,
            @RequestHeader(value = "X-PARTNER-ID", required = false) Long partnerId,
            @RequestHeader("X-TREE-PATH") String treePath
    ) {
        SelfAssessmentResult result = selfAssessmentService.getSelfAssessmentResult(
                resultId, userType, headquartersId, partnerId, treePath
        );
        return ResponseEntity.ok(ApiResponse.success(SelfAssessmentResponse.from(result)));
    }

    /**
     * 자가진단 결과 목록 조회 (Read - 페이징)
     * - 본사: partnerId 없이 조회
     * - 협력사: 본사 + partnerId로 조회
     */
    @GetMapping("/results")
    public ResponseEntity<ApiResponse<Page<SelfAssessmentResponse>>> getSelfAssessmentResults(
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestHeader("X-USER-TYPE") String userType,
            @RequestHeader("X-HEADQUARTERS-ID") Long headquartersId,
            @RequestHeader(value = "X-PARTNER-ID", required = false) Long partnerId,
            @RequestHeader("X-TREE-PATH") String treePath
    ) {
        Long resolvedPartnerId = partnerId;
        String resolvedTreePath = treePath;
        if ("HEADQUARTERS".equalsIgnoreCase(userType)) {
            resolvedPartnerId = null; // 본사는 본인 결과 + 전체 협력사
        } else if ("PARTNER".equalsIgnoreCase(userType)) {
            // 1차 협력사는 자기 자신 + 자식들만 조회 가능
            // 2차 이하 협력사는 자기 자신만
            // 이 로직은 서비스에서 treePath 기반으로 필터링할 것
        }

        Page<SelfAssessmentResponse> resultPage = selfAssessmentService
                .getSelfAssessmentResults(userType, headquartersId, resolvedPartnerId, resolvedTreePath,
                        companyName, category, startDate, endDate, pageable)
                .map(SelfAssessmentResponse::from);

        return ResponseEntity.ok(ApiResponse.success(resultPage));
    }
}