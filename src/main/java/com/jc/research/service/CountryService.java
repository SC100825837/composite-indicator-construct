package com.jc.research.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jc.research.entity.Country;
import java.util.List;

/**
 * @program: constructing-composite-indicators
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 18:56
 **/
public interface CountryService extends IService<Country> {

    List<Country> getAllCountrys();

    Boolean saveCountry(Country country);
}
