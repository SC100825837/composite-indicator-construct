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
    private CountryService countryService;

    @Autowired
    private TAIService taiService;

    @GetMapping("/getAllCountrys")
    public R<List<Country>> getAllAlgorithm() {
        List<Country> allCountrys = taiService.getAllCountryList();
        if (allCountrys.isEmpty()) {
            return R.failed(null, "数据为空");
        }
        return R.ok(allCountrys);
    }

    @GetMapping("/save")
    public R save() {
        Country country = new Country();
//        country.setCountryName("芬兰");
//        String baseIndicator = "{\"PATENTS\":187,\"ROYALTIES\":125.6,\"INTERNET\":200.2,\"EXPORTS\":50.7,\"TELEPHONES\":3.08,\"ELECTRICITY\":4.15,\"SCHOOLING\":10,\"UNIVERSITY\":27.4}";

        country.setCountryName("美国");
        String baseIndicator = "{\"PATENTS\":289,\"ROYALTIES\":130,\"INTERNET\":179.1,\"EXPORTS\":66.2,\"TELEPHONES\":3.00,\"ELECTRICITY\":4.07,\"SCHOOLING\":12,\"UNIVERSITY\":13.9}";

        country.setBaseIndicator(baseIndicator);

        boolean saveFlag = countryService.save(country);
        if (saveFlag) {
            return R.ok(null, "保存成功");
        } else {
            return R.failed("保存失败");
        }
    }

}
