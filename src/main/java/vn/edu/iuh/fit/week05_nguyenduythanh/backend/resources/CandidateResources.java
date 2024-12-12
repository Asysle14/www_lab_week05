package vn.edu.iuh.fit.week05_nguyenduythanh.backend.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.services.CandidateServices;

@RestController("/candidates")
public class CandidateResources {
    @Autowired
    private CandidateServices candidateServices;
}
