package vn.edu.iuh.fit.week05_nguyenduythanh.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.Candidate;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.Company;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories.CandidateRepository;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories.CompanyRepository;

@Service
public class CompanyServices {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    public Page<Company> findAll(int page, int size) {
        return companyRepository.findAll(PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "id")));
    }

    public Page<Company> searchCompanies(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return companyRepository.findByKeyword(keyword, pageable);
    }

}
