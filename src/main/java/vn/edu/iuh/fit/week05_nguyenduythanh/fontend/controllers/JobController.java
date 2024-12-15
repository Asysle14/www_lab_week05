package vn.edu.iuh.fit.week05_nguyenduythanh.fontend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.enums.SkillLevel;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.ids.JobSkillId;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.*;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories.*;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.services.CandidateServices;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.services.EmailService;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.services.JobServices;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/jobs")
public class JobController {
    @Autowired
    private JobServices jobServices;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobSkillRepository jobSkillRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CandidateServices candidateServices;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/list_no_paging")
    public String showJobList(Model model) {
        model.addAttribute("jobs", jobRepository.findAll());
        return "jobs/list_no_paging";
    }

    @GetMapping("/list")
    public String showJobListPaging(Model model,
                                    @RequestParam("page") Optional<Integer> page,
                                    @RequestParam("size") Optional<Integer> size,
                                    @RequestParam("search") Optional<String> search) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<Job> jobPage = jobServices.findAll(currentPage, pageSize);

        if (search.isPresent()) {
            jobPage = jobServices.searchJobs(currentPage - 1, pageSize, search.get());
        }

        model.addAttribute("jobPage", jobPage);

        int totalPages = jobPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "jobs/list";
    }

    @GetMapping("/show-add-form")
    public ModelAndView showAddForm(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("job", new Job());
        modelAndView.setViewName("jobs/add");
        return modelAndView;
    }

    @GetMapping("/add/{id}")
    public ModelAndView showAddJobForm(@PathVariable("id") Long companyId) {
        ModelAndView modelAndView = new ModelAndView();
        List<Skill> skills = skillRepository.findAll();
        modelAndView.addObject("skills", skills);
        modelAndView.addObject("companyId", companyId);
        modelAndView.setViewName("jobs/add");
        return modelAndView;
    }


    @PostMapping("/add")
    public String saveJob(@RequestParam("jobName") String jobName,
                          @RequestParam("jobDesc") String jobDesc,
                          @RequestParam("skills") List<Long> skills,
                          @RequestParam("skillLevels") List<String> skillLevels,
                          @RequestParam("more_infos") List<String> moreInfos,
                          @RequestParam("companyId") Long companyId) {
        Job job = new Job();
        job.setJobName(jobName);
        job.setJobDesc(jobDesc);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        job.setCompany(company);

        Job savedJob = jobRepository.save(job);

        Set<JobSkill> jobSkills = new HashSet<>();
        for (int i = 0; i < skills.size(); i++) {
            JobSkill jobSkill = new JobSkill();
            JobSkillId jobSkillId = new JobSkillId();
            jobSkillId.setJobId(savedJob.getId());
            jobSkillId.setSkillId(skills.get(i));
            jobSkill.setId(jobSkillId);
            jobSkill.setJob(savedJob);
            jobSkill.setMoreInfos(moreInfos.get(i));
            jobSkill.setSkill(skillRepository.findById(skills.get(i))
                    .orElseThrow(() -> new RuntimeException("Skill not found")));
            jobSkill.setSkillLevel(SkillLevel.valueOf(skillLevels.get(i)));
            jobSkills.add(jobSkill);
            jobSkillRepository.save(jobSkill);
        }

        savedJob.setJobSkills(jobSkills);
        jobRepository.save(savedJob);

        System.out.println("Skills: " + jobSkills);

        return "redirect:/jobs?success=addSuccess";
    }

    @GetMapping("/search")
    public String searchJobs(@RequestParam("keyword") String keyword,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        Page<Job> jobPage = jobServices.searchJobs(page, size, keyword);
        model.addAttribute("jobPage", jobPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageNumbers", IntStream.rangeClosed(1, jobPage.getTotalPages()).boxed().collect(Collectors.toList()));
        return "jobs/list";
    }


    @GetMapping("/view_detail_job/{id}")
    public ModelAndView viewJob(@PathVariable("id") long id) {
        ModelAndView modelAndView = new ModelAndView();
        Optional<Job> job = jobRepository.findById(id);
        if (job.isPresent()) {
            Job job1 = job.get();
            modelAndView.addObject("job", job1);
            modelAndView.setViewName("jobs/view_detail_job");
        } else {
            modelAndView.setViewName("redirect:/jobs?error=jobNotFound");
        }
        return modelAndView;
    }

    @GetMapping("/view/{id}")
    public ModelAndView viewCompany(@PathVariable("id") long id) {
        ModelAndView modelAndView = new ModelAndView();
        Optional<Company> company = companyRepository.findById(id);
        if (company.isPresent()) {
            modelAndView.addObject("company", company.get());
            modelAndView.setViewName("jobs/view_company");
        } else {
            modelAndView.setViewName("redirect:/jobs?error=companyNotFound");
        }
        return modelAndView;
    }

    @GetMapping("/view_candidatebyskill/{id}")
    public ModelAndView viewCandidateBySkill(@PathVariable("id") long jobId) {
        ModelAndView mav = new ModelAndView("jobs/view_candidate");
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        mav.addObject("job", job);
        mav.addObject("listCandidate", candidateServices.findCandidatesForJob(jobId));
        return mav;
    }

    @GetMapping("/delete/{id}")
    public String deleteJob(@PathVariable("id") long id) {
        jobRepository.deleteById(id);
        return "redirect:/jobs?success=deleteSuccess";
    }


    @GetMapping("/{jobId}/{candidateId}/send-email")
    public String sendEmail(@PathVariable("jobId") Long jobId, @PathVariable("candidateId") Long candidateId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        String subject = "Job Application for " + candidate.getFullName();
        String body = "Hello " + candidate.getFullName() + ",\n\nWe are pleased to inform you about the job opportunity at our company. Please visit our website for more details.";

        sendEmail(candidate.getEmail(), subject, body);

        return "redirect:/jobs?success=applySuccess";
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("thanh121224@gmail.com");
        mailSender.send(message);
    }

    @GetMapping("/apply/{id}")
    public String applyJob(@PathVariable("id") long id) {
        // Implement the logic for applying to a job if needed

        return "redirect:/jobs?success=applySuccess";
    }
}
