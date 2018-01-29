package com.data_realm.interview.service.impl;

import com.data_realm.interview.model.CountryPopulation;
import com.data_realm.interview.repository.StatRepository;
import com.data_realm.interview.service.StatisticsService;

import com.data_realm.interview.utils.CountryHelper;
import org.cache2k.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class StatisticsServiceImpl implements StatisticsService {

  private final String CacheKey = "CountryCacheKey";
  private final org.cache2k.Cache<String, Map<String, CountryPopulation>> cacheService;

  @Autowired
  StatisticsServiceImpl(@Qualifier("database-stats") StatRepository dbRepo,
                        @Qualifier("api-stats") StatRepository externalRepo) {

      //Issue #1, caching the results
      cacheService = new Cache2kBuilder<String, Map<String, CountryPopulation>>() {}
              .expireAfterWrite(5, TimeUnit.MINUTES)    // expire/refresh after 5 minutes
              .resilienceDuration(30, TimeUnit.SECONDS) // cope with at most 30 seconds
              .refreshAhead(true)                       // keep fresh when expiring
              .loader(key -> {
                  Map<String, CountryPopulation> aggregatedResults = new TreeMap<>();

                  try {
                     dbRepo.getCountryPopulations().stream().forEach(country -> aggregatedResults.put(country.getName(), country));
                  } catch (IOException except) {
                      System.out.println("Oh no something happened loading the countries from the database");
                  }

                  try {
                      externalRepo.getCountryPopulations().stream().forEach(country -> aggregatedResults.put(country.getName(), country));

                  } catch (IOException except) {
                      System.out.println("Oh no something happened loading the countries from the api");
                  }

                  return aggregatedResults;
              })
              .build();
  }

  @Override
  public Optional<CountryPopulation> findCountry(String countryName) {
    for (CountryPopulation countryPopulation : getAllCountries()) {
      if (countryPopulation.getName().toLowerCase().equals(countryName.trim().toLowerCase())) {
        return Optional.of(countryPopulation);
      }
    }

    return Optional.empty();
  }

  @Override
  public List<CountryPopulation> getAllCountries() {
      List<CountryPopulation> aggregatedResults = new ArrayList<>(cacheService.get(CacheKey).values());

      return  CountryHelper.removeDuplicates(aggregatedResults);
  }
}
