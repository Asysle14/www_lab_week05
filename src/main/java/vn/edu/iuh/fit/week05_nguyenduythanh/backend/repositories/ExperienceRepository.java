package vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.Experience;

public interface ExperienceRepository extends JpaRepository<Experience, Long>, CrudRepository<Experience, Long> {
}