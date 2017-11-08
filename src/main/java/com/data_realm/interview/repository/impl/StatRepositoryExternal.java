package com.data_realm.interview.repository.impl;

import com.data_realm.interview.StatsRetrivalError;
import com.data_realm.interview.model.CountryPopulation;
import com.data_realm.interview.repository.StatRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Repository;

@Repository("api-stats")
public class StatRepositoryExternal implements StatRepository {

	@Override
	public List<CountryPopulation> getCountryPopulations() throws IOException {
		List<CountryPopulation> output = new ArrayList<>();

		//Sometimes the external services has a connection error
		if (RandomUtils.nextInt(0, 100) == 2) {
		  throw new StatsRetrivalError();
                }

		// Pretend this calls a REST API somewhere
		output.add(new CountryPopulation("India",1182105000));
		output.add(new CountryPopulation("United Kingdom",62026962));
		output.add(new CountryPopulation("Chile",17094270));
		output.add(new CountryPopulation("Mali",15370000));
		output.add(new CountryPopulation("Greece",11305118));
		output.add(new CountryPopulation("Armenia",3249482));
		output.add(new CountryPopulation("Slovenia",2046976));
		output.add(new CountryPopulation("Saint Vincent and the Grenadines",109284));
		output.add(new CountryPopulation("Bhutan",695822));
		output.add(new CountryPopulation("Aruba (Netherlands)",101484));
		output.add(new CountryPopulation("Maldives",319738));
		output.add(new CountryPopulation("Mayotte (France)",202000));
		output.add(new CountryPopulation("Vietnam",86932500));
		output.add(new CountryPopulation("Germany",81802257));
		output.add(new CountryPopulation("Botswana",2029307));
		output.add(new CountryPopulation("Togo",6191155));
		output.add(new CountryPopulation("Luxembourg",502066));
		output.add(new CountryPopulation("U.S. Virgin Islands (US)",106267));
		output.add(new CountryPopulation("Belarus",9480178));
		output.add(new CountryPopulation("Myanmar",59780000));
		output.add(new CountryPopulation("Mauritania",3217383));
		output.add(new CountryPopulation("Malaysia",28334135));
		output.add(new CountryPopulation("Dominican Republic",9884371));
		output.add(new CountryPopulation("New Caledonia (France)",248000));
		output.add(new CountryPopulation("Slovakia",5424925));
		output.add(new CountryPopulation("Kyrgyzstan",5418300));
		output.add(new CountryPopulation("Lithuania",3329039));
		output.add(new CountryPopulation("United States of America",309349689));
		return output;
	}

}
