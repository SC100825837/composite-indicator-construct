package com.jc.research.controller;

import com.jc.research.entity.Country;
import com.jc.research.service.CountryService;
import com.jc.research.service.TAIService;
import com.jc.research.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 19:45
 **/
@RestController
@RequestMapping("/country")
public class CountryController {

    @Autowired
    private TAIService taiService;

    @GetMapping("/getAllCountrys")
    public R<List<Country>> getAllCountrys() {
        List<Country> allCountrys = taiService.getAllCountryList();
        if (allCountrys.isEmpty()) {
            return R.failed(null, "数据为空");
        }
        return R.ok(allCountrys);
    }

}
