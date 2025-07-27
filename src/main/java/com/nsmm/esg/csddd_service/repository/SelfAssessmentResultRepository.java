package com.nsmm.esg.csddd_service.repository;

import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 자가진단 결과 (SelfAssessmentResult) 레포지터리
 * - 진단 결과 저장, 조회, 수정
 * - 조건 검색을 위한 Specification 기능 지원
 */
public interface SelfAssessmentResultRepository extends JpaRepository<SelfAssessmentResult, Long>,

                JpaSpecificationExecutor<SelfAssessmentResult> {
}