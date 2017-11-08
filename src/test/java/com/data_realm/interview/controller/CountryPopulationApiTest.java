package com.data_realm.interview.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.data_realm.interview.model.CountryPopulation;
import com.data_realm.interview.service.StatisticsService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CountryPopulationApiTest {
  private StatisticsService service;
  private CountryPopulationApi api;

  @Before
  public void setup() {
    service = mock(StatisticsService.class);
    api = new CountryPopulationApi(service);
  }

  @Test
  public void getACountryGivenAValidName() {
    String countryName = "Canada";
    CountryPopulation countryPopulation = new CountryPopulation(countryName, 1234);
    when(service.findCountry(countryName)).thenReturn(Optional.of(countryPopulation));

    ResponseEntity result = api.getCountry(countryName);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(countryPopulation, result.getBody());
  }

  @Test
  public void getAErrorWhenCountryNameIsBlank() {
    String countryName = "";

    ResponseEntity result = api.getCountry(countryName);

    assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    verify(service, never()).getAllCountries();
  }

  @Test
  public void getErrorWhenCountryDoesNotExist() {
    String countryName = "fake";
    when(service.findCountry(countryName)).thenReturn(Optional.empty());

    ResponseEntity result = api.getCountry(countryName);

    assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
  }

  @Test
  public void getAllCountries() {
    String countryName = "Canada";
    List<CountryPopulation> countryPopulation = new ArrayList<>();
    countryPopulation.add(new CountryPopulation(countryName, 1234));
    when(service.getAllCountries()).thenReturn(countryPopulation);

    ResponseEntity result = api.getAllCountries();

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(countryPopulation, result.getBody());
  }

}