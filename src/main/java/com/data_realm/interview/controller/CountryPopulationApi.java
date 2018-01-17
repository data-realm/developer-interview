package com.data_realm.interview.controller;

import com.data_realm.interview.helpers.CountryPopulationComparator;
import com.data_realm.interview.helpers.CountryPopulationHelper;
import com.data_realm.interview.model.CountryPopulation;
import com.data_realm.interview.service.StatisticsService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
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
    /** ISSUE #2 - part 1/2*/
    //TODO: this is a partial solution. Right now only strict duplicates are removed (same name of the country, case ignored).
    /**There is a need to prioritize data sources according to how trustworthy they are because for some countries these 2 sources
     * are offering different values for the population count so we need to understand which to trust against which. Also the naming
     * of the countries are not standardized (like "u.s.a." and "united states of america"). The naming convention can be easily fixed
     * with a thesaurus table/file but again there is the issue which source to trust, since both are giving different population values.
     *
     * For the moment, the duplicates are discarded only based on the naming. Standardization of the naming is highly recommended.
     */

    //could also use org.apache.commons.lang to correct the spelling of the countries so "IgnoreCase" would not be required anymore

    List<CountryPopulation> allCountries = statisticsService.getAllCountries();
    Collections.sort(allCountries, new CountryPopulationComparator());

    return ResponseEntity.ok(CountryPopulationHelper.removeDuplicatesIgnoringCase(allCountries));
  }
}
