package com.data_realm.interview.service.impl;

import com.data_realm.interview.model.CountryPopulation;
import com.data_realm.interview.repository.StatRepository;
import com.data_realm.interview.service.StatisticsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StatisticsServiceImpl implements StatisticsService {
  private final StatRepository dbRepo;
  private final StatRepository externalRepo;

  @Autowired
  StatisticsServiceImpl(@Qualifier("database-stats") StatRepository dbRepo,
                        @Qualifier("api-stats") StatRepository externalRepo) {
    this.dbRepo = dbRepo;
    this.externalRepo = externalRepo;
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
      List<CountryPopulation> aggregatedResults = new ArrayList<>();
      try {
        aggregatedResults.addAll(dbRepo.getCountryPopulations());
      } catch (IOException except) {
        System.out.println("Oh no something happened loading the countries from the database");
      }

      try {
        aggregatedResults.addAll(externalRepo.getCountryPopulations());
      } catch (IOException except) {
        System.out.println("Oh no something happened loading the countries from the api");
      }

      return aggregatedResults;
  }
}
