package vn.edu.iuh.fit.week05_nguyenduythanh.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.Job;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories.JobRepository;

import java.util.Collections;
import java.util.List;

@Service
public class JobServices {
    @Autowired
    private JobRepository jobRepository;

    public Page<Job> findAll(int page, int size) {
        return jobRepository.findAll(PageRequest.of(page, size));
    }

    public Page<Job> searchJobs(int pageNo, int pageSize, String searchTerm) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return jobRepository.findByJobNameContainingOrCompany_CompNameContainingOrJobSkills_Skill_SkillNameContaining(
                searchTerm, searchTerm, searchTerm, pageable);
    }

    public Page<Job> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Job> list;
        List<Job> jobs = jobRepository.findAll();

        if (jobs.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, jobs.size());
            list = jobs.subList(startItem, toIndex);
        }

        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), jobs.size());
    }
}
