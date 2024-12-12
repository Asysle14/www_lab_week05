package vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.ids.JobSkillId;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.JobSkill;

import java.util.List;

public interface JobSkillRepository extends JpaRepository<JobSkill, JobSkillId> {
  @Query("SELECT js.skill.id FROM JobSkill js WHERE js.job.id = :jobId")
  List<Long> findSkillIdsByJobId(@Param("jobId") Long jobId);

  @Modifying
  @Transactional
  @Query("DELETE FROM JobSkill js WHERE js.job.id = :jobId")
  void deleteByJobId(@Param("jobId") Long jobId);
}