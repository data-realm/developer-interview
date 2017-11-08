package com.data_realm.interview.model;

public class CountryPopulation {
  private String name;
  private int population;

  public CountryPopulation(String name, int population) {
    this.name = name;
    this.population = population;
  }

  public String getName() {
    return name;
  }

  public int getPopulation() {
    return population;
  }
}
