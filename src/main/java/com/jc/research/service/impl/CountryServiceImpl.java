package com.jc.research.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jc.research.entity.Country;
import com.jc.research.mapper.CountryMapper;
import com.jc.research.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @program: constructing-composite-indicators
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 17:57
 **/
@Service
public class CountryServiceImpl extends ServiceImpl<CountryMapper, Country> implements CountryService {

    @Autowired
    private CountryMapper countryMapper;

    @Override
    public List<Country> getAllCountrys() {
        return countryMapper.getAllCountrys();
    }

    @Override
    public Boolean saveCountry(Country country) {
        return save(country);
    }
}
