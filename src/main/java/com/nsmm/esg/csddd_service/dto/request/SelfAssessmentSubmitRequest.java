package com.nsmm.esg.csddd_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * CSDDD 자가진단 전체 제출 요청 DTO
 *
 * - 기업명 + 질문 리스트를 함께 전송
 * - 프론트엔드에서 제출 버튼 클릭 시 이 형식으로 백엔드에 전달
 *
 * @author ESG Project
 * @since 2024
 */
@Getter
@Setter
public class SelfAssessmentSubmitRequest {

    /**
     * 제출한 회사명
     * (선택 사항 - 추후 사용 가능)
     */
    private String companyName;

    /**
     * 자가진단 질문 응답 리스트
     * - 각 항목은 SelfAssessmentRequest로 구성됨
     */
    private List<SelfAssessmentRequest> answers;
}