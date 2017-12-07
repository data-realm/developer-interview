package com.data_realm.interview.service.impl;

import com.data_realm.interview.model.CountryPopulation;
import com.data_realm.interview.repository.StatRepository;
import com.data_realm.interview.service.StatisticsService;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@Service
public class StatisticsServiceImpl implements StatisticsService {
  private static final String KEY = "KEY";
  
  private final LoadingCache<String, Map<String, CountryPopulation>> statisticsServiceCache;
  
	@Autowired
	StatisticsServiceImpl(@Qualifier("database-stats") StatRepository dbRepo,
			@Qualifier("api-stats") StatRepository externalRepo) {

		statisticsServiceCache = Caffeine.newBuilder().refreshAfterWrite(1L, TimeUnit.MINUTES).maximumSize(1L)
				.build(key -> {
					Map<String, CountryPopulation> aggregatedResults = new TreeMap<>();

					try {
						dbRepo.getCountryPopulations().stream().forEach(countryPopulation -> aggregatedResults
								.put(StringUtils.lowerCase(countryPopulation.getName()), countryPopulation));
					} catch (IOException except) {
						System.out.println("Oh no something happened loading the countries from the database");
						throw except;
					}

					try {
						externalRepo.getCountryPopulations().stream().forEach(countryPopulation -> aggregatedResults
								.put(StringUtils.lowerCase(countryPopulation.getName()), countryPopulation));
					} catch (IOException except) {
						System.out.println("Oh no something happened loading the countries from the api");
						throw except;
					}

					return aggregatedResults;
				});
	}

  @Override
  public Optional<CountryPopulation> findCountry(String countryName) {
	  
	  Map<String, CountryPopulation> cpMap = statisticsServiceCache.get(KEY);
	  
	  if(cpMap.isEmpty()) {
		  return Optional.empty();
	  }
	  
	  return Optional.ofNullable(cpMap.get(StringUtils.lowerCase(StringUtils.trim(countryName))));
  }

  @Override
  public List<CountryPopulation> getAllCountries() {
      return new ArrayList<>(statisticsServiceCache.get(KEY).values());
  }
}
