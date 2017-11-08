package com.data_realm.interview.service.impl;


import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.data_realm.interview.model.CountryPopulation;
import com.data_realm.interview.repository.StatRepository;
import com.data_realm.interview.service.StatisticsService;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StatisticsServiceImplTest {
  private StatRepository dbRepo;
  private StatRepository externalRepo;

  private StatisticsService service;

  @Before
  public void setup() {
    dbRepo = mock(StatRepository.class);
    externalRepo = mock(StatRepository.class);
    service = new StatisticsServiceImpl(dbRepo, externalRepo);
  }

  @Test
  public void findCountryTest() throws Exception {
    List<CountryPopulation> countries = new ArrayList<>();
    countries.add(new CountryPopulation("test", 1));
    when(dbRepo.getCountryPopulations()).thenReturn(countries);
    when(externalRepo.getCountryPopulations()).thenReturn(countries);

    Optional<CountryPopulation> result = service.findCountry("test");

    assertTrue(result.isPresent());
  }

  @Test
  public void getAllTest() throws Exception {
    List<CountryPopulation> countries = new ArrayList<>();
    countries.add(new CountryPopulation("test", 1));
    when(dbRepo.getCountryPopulations()).thenReturn(countries);
    when(externalRepo.getCountryPopulations()).thenReturn(countries);

    List<CountryPopulation> result = service.getAllCountries();

    assertTrue(result.size() == 2);
  }
}