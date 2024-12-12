package vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.enums.SkillLevel;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.Candidate;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
  @Query("""
            select c from Candidate c inner join c.candidateSkills candidateSkills
            where candidateSkills.skillLevel = ?1 and candidateSkills.skill.skillName = ?2
            """)
  List<Candidate> findBySkillLevelAndSkillName(SkillLevel skillLevel, String skillName);

  @Query("""
    SELECT DISTINCT c FROM Candidate c
    LEFT JOIN c.candidateSkills cs
    LEFT JOIN cs.skill s
    WHERE c.fullName LIKE %:keyword%
    OR c.email LIKE %:keyword%
    OR c.phone LIKE %:keyword%
    OR c.address.street LIKE %:keyword%
    OR c.address.city LIKE %:keyword%
    OR s.skillName LIKE %:keyword%
    OR cs.skillLevel = :skillLevel
    """)
  Page<Candidate> findByKeyword(
          @Param("keyword") String keyword,
          @Param("skillLevel") SkillLevel skillLevel,
          Pageable pageable
  );
}