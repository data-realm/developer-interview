package com.data_realm.interview.helpers;

import java.util.*;

import com.data_realm.interview.model.CountryPopulation;

public class CountryPopulationHelper {

    public static List<CountryPopulation> removeDuplicatesIgnoringCase(List<CountryPopulation> countryPopulation) {
        Set set = new TreeSet((Comparator<CountryPopulation>) (c1, c2) -> {
            /** ISSUE #2 - part 2/2*/
            /** since different sources are now giving different values for the same countries, checking the population
             * will still allow duplicates to exist. Since we don't have a trustworthy ranking for the sources of the data
             * and the current issue is not to have duplicates, the naming check seems to be enough.
             * For a better understanding please check the comment from controller/CountryPopulationApi.java */
            if(c1.getName().equalsIgnoreCase(c2.getName()) /* && c1.getPopulation()==c2.getPopulation() */){
                return 0;
            }
            return 1;
        });
        set.addAll(countryPopulation);

        return new ArrayList(set);
    }
}

