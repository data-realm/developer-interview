package com.data_realm.interview.service.impl;

import com.data_realm.interview.model.CountryPopulation;
import com.data_realm.interview.repository.StatRepository;
import com.data_realm.interview.service.StatisticsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

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


    /**
     *
     * We are going to rely on our DB since getting data from our DB should be way faster than
     * calling an external service
     *
     */
    @Override
    public Optional<CountryPopulation> findCountry(String countryName) {
        String cleanedCountryName = countryName.trim().toLowerCase();

        return getDbCountryPopulation(cleanedCountryName).map(Optional::of)
                .orElse(getExternalCountryPopulation(cleanedCountryName));
    }

    private Optional<CountryPopulation> getDbCountryPopulation(String countryName) {
        try {
            return dbRepo.getCountryPopulationByName(countryName);
        } catch (IOException except) {
            System.out.println("Oh no something happened loading the countries from the database");
            return Optional.empty();
        }
    }

    private Optional<CountryPopulation> getExternalCountryPopulation(String countryName) {
        try {
            return externalRepo.getCountryPopulationByName(countryName);
        } catch (IOException except) {
            System.out.println("Oh no something happened loading the countries from the database");
            return Optional.empty();
        }
    }


    /**
     * I am going with the solution of trusting our own data over the external one
     * I think we should have a naming standard for the countries
     * We can have a hierarchy or a ranking system of trust. I am a programmer I'll do what the Costumer Success guy says is better :D
     */
  @Override
  public List<CountryPopulation> getAllCountries() {
      Set<CountryPopulation> aggregatedResults = new HashSet<>();
      List<CountryPopulation> toReturn = new ArrayList<>();

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

      toReturn.addAll(aggregatedResults);

      return toReturn;
  }
}
