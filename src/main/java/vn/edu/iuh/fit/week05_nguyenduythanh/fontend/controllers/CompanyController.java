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
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.models.Company;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories.AddressRepository;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.repositories.CompanyRepository;
import vn.edu.iuh.fit.week05_nguyenduythanh.backend.services.CompanyServices;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/companies")
public class CompanyController {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyServices companyServices;

    @Autowired
    private AddressRepository addressRepository;

    @GetMapping("/list_no_paging")
    public String showCompanyList(Model model) {
        model.addAttribute("companies", companyRepository.findAll());
        return "companies/list_no_paging";
    }

    @GetMapping("/list")
    public String showCompanyListPaging(Model model,
                                        @RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Company> companyPage = companyServices.findAll(currentPage, pageSize);

        model.addAttribute("companyPage", companyPage);

        int totalPages = companyPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "companies/list";
    }

    @GetMapping("/search")
    public String searchCompanies(@RequestParam("keyword") String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        Page<Company> companyPage = companyServices.searchCompanies(keyword, page, size);
        model.addAttribute("companyPage", companyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageNumbers", IntStream.rangeClosed(1, companyPage.getTotalPages()).boxed().collect(Collectors.toList()));
        return "companies/list";
    }

    @GetMapping("/show-add-form")
    public ModelAndView add(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        Company company = new Company();
        company.setAddress(new Address());
        modelAndView.addObject("company", company);
        modelAndView.addObject("countries", CountryCode.values());
        modelAndView.setViewName("companies/add");
        return modelAndView;
    }

    @PostMapping("/add")
    public String addCompany(@ModelAttribute("company") Company company,
                             BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("countries", CountryCode.values());
            return "companies/add";
        }

        addressRepository.save(company.getAddress());
        companyRepository.save(company);

        return "redirect:/companies";
    }

    @GetMapping("/show-edit-form/{id}")
    public ModelAndView edit(@PathVariable("id") long id) {
        ModelAndView modelAndView = new ModelAndView();
        Optional<Company> opt = companyRepository.findById(id);
        if (opt.isPresent()) {
            Company company = opt.get();
            modelAndView.addObject("company", company);
            modelAndView.addObject("countries", CountryCode.values());
            modelAndView.setViewName("companies/edit");
        } else {
            modelAndView.setViewName("redirect:/companies?error=companyNotFound");
        }
        return modelAndView;
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute("company") Company company,
                         BindingResult result, Model model) {

        if (company.getAddress() == null || company.getAddress().getCountry() == null) {
            model.addAttribute("error", "Country is required.");
            model.addAttribute("company", company);
            model.addAttribute("countries", CountryCode.values());
            return "companies/edit";
        }

        Address address = company.getAddress();
        addressRepository.save(address);
        company.setAddress(address);
        companyRepository.save(company);

        return "redirect:/companies?success=updateSuccess";
    }
}
