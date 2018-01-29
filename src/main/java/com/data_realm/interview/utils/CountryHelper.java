package com.data_realm.interview.utils;

import com.data_realm.interview.model.CountryPopulation;

import java.util.*;

public class CountryHelper {


    //Issue #2
    public static List<CountryPopulation> removeDuplicates(List<CountryPopulation> countries) {

      List<CountryPopulation> filteredCountries = countries.stream()
              .reduce(new ArrayList<>(), (List<CountryPopulation> acc, CountryPopulation country) -> {
                  if(acc.stream().noneMatch(c -> c.getName().equalsIgnoreCase(country.getName().toLowerCase()))){
                      acc.add(country);
                  }
                  return acc;
              }, (accOld, accNew) -> {
                  accOld.addAll(accNew);
                  return accOld;
                      }
              );

        return filteredCountries;
    }

}
