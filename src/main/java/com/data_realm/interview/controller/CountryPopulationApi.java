package com.data_realm.interview.controller;

import com.data_realm.interview.model.CountryPopulation;
import com.data_realm.interview.service.StatisticsService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class CountryPopulationApi {
  private StatisticsService statisticsService;

  @Autowired
  public CountryPopulationApi(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @RequestMapping(method = RequestMethod.GET, path = "/find")
  public ResponseEntity<?> getCountry(@RequestParam("name") String countryName) {
    if (StringUtils.isBlank(countryName)) {
      return ResponseEntity.badRequest().body("countryName cannot be blank");
    }

    Optional<CountryPopulation> country = statisticsService.findCountry(countryName);

    if (country.isPresent()) {
      return ResponseEntity.ok(country.get());
    }

    return ResponseEntity.notFound().build();

  }

  @RequestMapping(method = RequestMethod.GET, path = "/all")
  public ResponseEntity<List<CountryPopulation>> getAllCountries() {
    return ResponseEntity.ok(statisticsService.getAllCountries());
  }
}
