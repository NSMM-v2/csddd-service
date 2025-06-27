package com.nsmm.esg.csddd_service.repository;

import com.nsmm.esg.csddd_service.entity.SelfAssessmentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 자가진단 답변 (SelfAssessmentAnswer) 레포지터리
 * - 진단 항목별 개별 응답 저장 및 삭제
 */
public interface SelfAssessmentAnswerRepository extends JpaRepository<SelfAssessmentAnswer, Long> {

}