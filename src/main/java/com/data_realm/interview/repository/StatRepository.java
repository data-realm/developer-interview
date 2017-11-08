package com.data_realm.interview.repository;

import com.data_realm.interview.model.CountryPopulation;

import java.io.IOException;
import java.util.List;
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

}
