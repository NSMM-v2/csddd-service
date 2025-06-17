package com.nsmm.esg.csddd_service.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 📌 클라이언트가 자가진단 제출 시 사용하는 요청 DTO
 * - 각 문항에 대한 응답 데이터를 포함함
 */
@Getter
@Setter
public class SelfAssessmentRequest {

    // 🔢 질문 식별자 (예: "1.1", "2.3" 등 문항 번호)
    private String questionId;

    // 📝 사용자의 응답 (예: "yes", "no", "partial")
    private String answer;

    // ⚖️ 해당 문항의 가중치 (점수 계산에 사용됨)
    private Double weight;

    // 🚨 중대 위반 여부 (true일 경우 심각한 문제로 간주)
    private Boolean critical;

    // 🗂️ 문항이 속한 카테고리 (예: "인권", "환경", "노동")
    private String category;

    // 💬 비고 또는 추가 설명 (선택 입력)
    private String remarks;
}