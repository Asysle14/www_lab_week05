package vn.edu.iuh.fit.week05_nguyenduythanh.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.enums.SkillLevel;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.Candidate;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.Job;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories.CandidateRepository;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories.CompanyRepository;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories.JobRepository;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateServices {
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SkillRepository skillRepository;

    public Page<Candidate> findAll(int page, int size) {
        return candidateRepository.findAll(PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "id")));
    }

    public Page<Candidate> searchCandidates(String keyword, int page, int size) {
        SkillLevel skillLevel = null;
        try {
            skillLevel = SkillLevel.valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Ignore, keyword is not a valid SkillLevel
        }
        Pageable pageable = PageRequest.of(page, size);
        return candidateRepository.findByKeyword(keyword, skillLevel, pageable);
    }

    public List<Candidate> findCandidatesForJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return job.getJobSkills().stream()
                .flatMap(jobSkill -> candidateRepository.findBySkillLevelAndSkillName(
                        jobSkill.getSkillLevel(), jobSkill.getSkill().getSkillName()).stream())
                .collect(Collectors.toList());
    }
}
