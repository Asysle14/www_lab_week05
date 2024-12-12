package vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.ids.CandidateSkillId;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.CandidateSkill;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, CandidateSkillId>, CrudRepository<CandidateSkill, CandidateSkillId> {
}