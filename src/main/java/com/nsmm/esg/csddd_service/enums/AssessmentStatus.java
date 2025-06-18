package com.nsmm.esg.csddd_service.enums;

/**
 *  AssessmentStatus
 * - 자가진단 진행 상태를 나타내는 Enum 클래스
 *
 * NOT_STARTED: 자가진단을 아직 시작하지 않음
 * IN_PROGRESS: 자가진단 진행 중
 * COMPLETED: 자가진단 완료됨
 */
public enum AssessmentStatus {
    NOT_STARTED,  // 자가진단 시작 전
    IN_PROGRESS,  // 자가진단 진행 중 (임시 저장 등)
    COMPLETED;    // 자가진단 제출 완료
}