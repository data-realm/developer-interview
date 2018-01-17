package com.data_realm.interview.helpers;

import com.data_realm.interview.model.CountryPopulation;

import java.util.Comparator;

public class CountryPopulationComparator implements Comparator<CountryPopulation> {
    @Override
    public int compare(CountryPopulation o1, CountryPopulation o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
    }
}
