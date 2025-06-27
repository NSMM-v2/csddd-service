package com.nsmm.esg.csddd_service.dto.request;

import com.nsmm.esg.csddd_service.enums.AssessmentGrade;
import lombok.Getter;
import lombok.Setter;

/**
 * CSDDD 자가진단 제출 요청 DTO
 * 
 * 클라이언트에서 각 문항에 대한 응답을 제출할 때 사용하는 DTO입니다.
 * 프론트엔드의 자가진단 폼에서 백엔드로 전송되는 개별 문항 데이터를 담습니다.
 * 
 * 사용 예시:
 * - 자가진단 폼의 각 질문에 대한 사용자 응답
 * - 점수 계산을 위한 가중치 정보
 * - 중대위반 여부 판단을 위한 플래그
 * 
 * @author ESG Project Team
 * @version 2.0
 * @since 2024
 * @lastModified 2024-12-20
 */
@Getter
@Setter
public class SelfAssessmentRequest {

    // ============================================================================
    // 질문 식별 정보 (Question Identification)
    // ============================================================================

    /**
     * 질문 식별자
     * 예시: "Q1_1", "Q2_3", "Q5_2" 등
     * 프론트엔드와 백엔드 간 질문을 매핑하는 고유 키
     */
    private String questionId;

    /**
     * 질문 카테고리
     * 예시: "인권및노동", "산업안전보건", "환경경영", "공급망및조달", "윤리경영및정보보호"
     * CSDDD 5개 주요 평가 영역별 분류
     */
    private String category;

    // ============================================================================
    // 응답 정보 (Answer Information)
    // ============================================================================

    /**
     * 사용자 응답
     * 가능한 값: "yes", "no"
     * - yes: 요구사항을 충족함
     * - no: 요구사항을 충족하지 않음
     */
    private String answer;

    /**
     * 응답에 대한 부가 설명 및 비고사항
     * 선택 입력 필드로, 답변에 대한 상세 설명이나 추가 정보를 기록
     */
    private String remarks;

    // ============================================================================
    // 평가 기준 정보 (Assessment Criteria)
    // ============================================================================

    /**
     * 질문 가중치
     * 각 질문의 중요도에 따른 가중치 값
     * 점수 계산 시 (답변 비율 * 가중치)로 실제 점수 산출
     * 높은 가중치일수록 전체 점수에 미치는 영향이 큼
     */
    private Double weight;

    /**
     * 중대위반 등급
     * 프론트에서 내려주는 중대위반 문항의 등급 정보 (예: "D", "C" 등)
     */
    private AssessmentGrade criticalGrade;
    /**
     * 중대위반 여부
     * true인 경우 NO 답변 시 전체 등급에 영향을 미치는 중요한 문항
     * 중대위반 항목에서 NO 응답 시 프론트엔드에서 등급 자동 강등 적용
     */
    private Boolean critical;
}