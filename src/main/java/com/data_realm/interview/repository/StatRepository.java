package com.data_realm.interview.repository;

import com.data_realm.interview.model.CountryPopulation;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Repository for retrieving Country population statistics.
 */
public interface StatRepository {

  /**
   * Gets a list of Pairs that are 'country:population'
   *
   * @return Country population list
   * @throws IOException connection problems
   */
  List<CountryPopulation> getCountryPopulations() throws IOException;

  /**
   * Gets a list of Pairs that are 'country:population' filtered by countryName
   *
   * @return Country population list
   * @throws IOException connection problems
   */
  Optional<CountryPopulation> getCountryPopulationByName(String countryName) throws IOException;

}
