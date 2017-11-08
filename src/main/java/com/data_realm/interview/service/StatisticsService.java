package com.data_realm.interview.service;

import com.data_realm.interview.model.CountryPopulation;

import java.util.List;
import java.util.Optional;

public interface StatisticsService {
  Optional<CountryPopulation> findCountry(String countryName);
  List<CountryPopulation> getAllCountries();
}
