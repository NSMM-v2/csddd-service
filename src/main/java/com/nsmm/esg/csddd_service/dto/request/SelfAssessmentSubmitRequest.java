package com.nsmm.esg.csddd_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * CSDDD 자가진단 제출 요청 DTO
 * 
 * 전체 자가진단 결과를 한 번에 제출할 때 사용하는 요청 객체
 * 회사 정보와 모든 문항에 대한 답변을 포함
 * 
 * @author ESG Project Team
 * @version 2.0
 */
@Schema(description = "CSDDD 자가진단 제출 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SelfAssessmentSubmitRequest {

    /**
     * 자가진단을 수행한 회사명
     * 본사 또는 협력사의 회사명
     */
    @Schema(description = "회사명", example = "삼성전자")
    @NotBlank(message = "회사명은 필수입니다.")
    private String companyName;

    /**
     * 자가진단 문항별 답변 목록
     * 모든 문항에 대한 응답을 포함해야 함
     */
    @Schema(description = "자가진단 답변 목록")
    @NotEmpty(message = "답변 목록은 비어있을 수 없습니다.")
    @Valid
    private List<SelfAssessmentAnswerRequest> answers;
}