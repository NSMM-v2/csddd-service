package com.nsmm.esg.csddd_service.repository;

import com.nsmm.esg.csddd_service.entity.SelfAssessmentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *  SelfAssessmentAnswerRepository
 * - 자가진단 문항(Answer) 엔티티용 JPA 리포지토리
 * - 기본적인 CRUD 메서드는 JpaRepository에서 제공
 */
public interface SelfAssessmentAnswerRepository extends JpaRepository<SelfAssessmentAnswer, Long> {

    /**
     *  특정 결과(resultId)에 해당하는 모든 자가진단 문항 리스트를 조회
     * @param resultId 자가진단 결과 ID (SelfAssessmentResult의 ID)
     * @return 해당 결과에 연결된 모든 문항 리스트
     */
    List<SelfAssessmentAnswer> findByResultId(Long resultId);
}