package com.data_realm.interview.model;

import java.util.Objects;

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

  @Override
  public String toString() {
    return "CountryPopulation{" +
            "name='" + name + '\'' +
            ", population=" + population +
            '}';
  }

  @Override
  public boolean equals(Object obj) {
    if(! (obj instanceof CountryPopulation))
      return false;

    CountryPopulation that = (CountryPopulation) obj;

    return Objects.equals(name.toLowerCase(), that.name.toLowerCase());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.toLowerCase().hashCode());

    return result;
  }
}
