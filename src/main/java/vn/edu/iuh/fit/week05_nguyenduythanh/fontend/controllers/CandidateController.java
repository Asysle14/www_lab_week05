package vn.edu.iuh.fit.week05_nguyenduythanh.fontend.controllers;

import com.neovisionaries.i18n.CountryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.Address;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.Candidate;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.CandidateSkill;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.Skill;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories.AddressRepository;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories.CandidateRepository;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories.SkillRepository;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.services.CandidateServices;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/candidates")
public class CandidateController {
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CandidateServices candidateServices;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private SkillRepository skillRepository;

    @GetMapping("/list_no_paging")
    public String showCandidateList(Model model) {
        model.addAttribute("candidates", candidateRepository.findAll());
        return "candidates/list_no_paging";
    }

    @GetMapping("/list")
    public String showCandidateListPaging(Model model,
                                          @RequestParam("page") Optional<Integer> page,
                                          @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<Candidate> candidatePage = candidateServices.findAll(currentPage, pageSize);

        model.addAttribute("candidatePage", candidatePage);

        int totalPages = candidatePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "candidates/list";
    }

    @GetMapping("/search")
    public String searchCandidates(@RequestParam("keyword") String keyword,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   Model model) {
        Page<Candidate> candidatePage = candidateServices.searchCandidates(keyword, page, size);
        model.addAttribute("candidatePage", candidatePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageNumbers", IntStream.rangeClosed(1, candidatePage.getTotalPages()).boxed().collect(Collectors.toList()));
        return "candidates/list";
    }

    @GetMapping("/show-edit-form/{id}")
    public ModelAndView edit(@PathVariable("id") long id) {
        ModelAndView modelAndView = new ModelAndView();
        Optional<Candidate> opt = candidateRepository.findById(id);
        if (opt.isPresent()) {
            Candidate candidate = opt.get();
            modelAndView.addObject("candidate", candidate);
            modelAndView.addObject("countries", CountryCode.values());

            modelAndView.setViewName("candidates/edit");
        } else {
            modelAndView.setViewName("redirect:/candidates?error=candidateNotFound");
        }
        return modelAndView;
    }

    @PostMapping("/edit")
    public String update(
            @ModelAttribute("candidate") Candidate candidate,
            BindingResult result,
            Model model) {

        if (candidate.getAddress() == null || candidate.getAddress().getCountry() == null) {
            model.addAttribute("error", "Country is required.");
            model.addAttribute("candidate", candidate);
            model.addAttribute("countries", CountryCode.values());
            return "candidates/edit";
        }

        Address address = candidate.getAddress();
        if (address.getId() == null) {
            addressRepository.save(address);
        } else {
            addressRepository.save(address);
        }
        candidate.setAddress(address);
        candidateRepository.save(candidate);

        return "redirect:/candidates?success=updateSuccess";
    }

    @GetMapping("/show-add-form")
    public ModelAndView add(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        Candidate candidate = new Candidate();
        candidate.setAddress(new Address());
        modelAndView.addObject("candidate", candidate);
        modelAndView.addObject("address", candidate.getAddress());
        modelAndView.addObject("countries", CountryCode.values());

        List<Skill> allSkills = skillRepository.findAll();
        modelAndView.addObject("allSkills", allSkills);

        modelAndView.setViewName("candidates/add");
        return modelAndView;
    }

    @PostMapping("/add")
    public String addCandidate(
            @ModelAttribute("candidate") Candidate candidate,
            @RequestParam(value = "skills", required = false) List<Long> skillIds,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("countries", CountryCode.values());
            model.addAttribute("allSkills", skillRepository.findAll());
            return "candidates/add";
        }

        addressRepository.save(candidate.getAddress());

        if (skillIds != null && !skillIds.isEmpty()) {
            Set<CandidateSkill> candidateSkills = new LinkedHashSet<>();
            for (Long skillId : skillIds) {
                Skill skill = skillRepository.findById(skillId).orElse(null);
                if (skill != null) {
                    CandidateSkill candidateSkill = new CandidateSkill();
                    candidateSkill.setCan(candidate);
                    candidateSkill.setSkill(skill);
                    candidateSkills.add(candidateSkill);
                }
            }
            candidate.setCandidateSkills(candidateSkills);
        }

        candidateRepository.save(candidate);

        return "redirect:/candidates";
    }

    @GetMapping("/profile/{id}")
    public ModelAndView viewProfile(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        Optional<Candidate> candidate = candidateRepository.findById(id);
        if (candidate.isPresent()) {
            modelAndView.addObject("candidate", candidate.get());
            modelAndView.setViewName("candidates/profile");
        } else {
            modelAndView.setViewName("redirect:/candidates?error=candidateNotFound");
        }
        return modelAndView;
    }
}
