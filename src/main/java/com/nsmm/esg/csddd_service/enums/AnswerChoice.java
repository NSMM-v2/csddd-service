package com.nsmm.esg.csddd_service.enums;

/**
 *  AnswerChoice
 * - 자가진단 문항에 대한 사용자의 답변을 표현하는 Enum
 * - 세 가지 선택지로 구성: YES, NO, PARTIAL
 * - 문자열로부터 안전하게 Enum 값을 변환할 수 있는 `fromString` 유틸 메서드 포함
 */
public enum AnswerChoice {
    YES,      // 예 (해당 기준을 충족함)
    NO,       // 아니오 (해당 기준을 충족하지 못함)
    PARTIAL;  // 부분 충족 (일부 조건만 만족)

    /**
     *  fromString 메서드
     * - 문자열을 받아 Enum 값으로 변환
     * - 대소문자 구분 없이 처리
     * - 유효하지 않은 값 입력 시 IllegalArgumentException 발생
     */
    public static AnswerChoice fromString(String value) {
        return switch (value.toLowerCase()) {
            case "yes" -> YES;
            case "no" -> NO;
            case "partial" -> PARTIAL;
            default -> throw new IllegalArgumentException("Unknown answer: " + value);
        };
    }
}