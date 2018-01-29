package com.data_realm.interview.repository.impl;

import com.data_realm.interview.StatsRetrivalError;
import com.data_realm.interview.model.CountryPopulation;
import com.data_realm.interview.repository.StatRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

@Repository("api-stats")
public class StatRepositoryExternal implements StatRepository {

    List<CountryPopulation> output = new ArrayList<>();
     {
        // Pretend this calls a REST API somewhere
        /* Please don't modify this data, you should assume it's the response from an external
         service and you can only manipulate the response from this method. */
        output.add(new CountryPopulation("india",1182105000));
        output.add(new CountryPopulation("united kingdom",62026962));
        output.add(new CountryPopulation("chile",17094270));
        output.add(new CountryPopulation("mali",15370000));
        output.add(new CountryPopulation("greece",11305118));
        output.add(new CountryPopulation("armenia",3249482));
        output.add(new CountryPopulation("slovenia",2046976));
        output.add(new CountryPopulation("saint Vincent and the Grenadines",109284));
        output.add(new CountryPopulation("bhutan",695822));
        output.add(new CountryPopulation("aruba (Netherlands)",101484));
        output.add(new CountryPopulation("maldives",319738));
        output.add(new CountryPopulation("mayotte (France)",202000));
        output.add(new CountryPopulation("vietnam",86932500));
        output.add(new CountryPopulation("germany",81802257));
        output.add(new CountryPopulation("botswana",2029307));
        output.add(new CountryPopulation("togo",6191155));
        output.add(new CountryPopulation("luxembourg",502066));
        output.add(new CountryPopulation("u.s. virgin islands (us)",106267));
        output.add(new CountryPopulation("belarus",9480178));
        output.add(new CountryPopulation("myanmar",59780000));
        output.add(new CountryPopulation("mauritania",3217383));
        output.add(new CountryPopulation("malaysia",28334135));
        output.add(new CountryPopulation("dominican republic",9884371));
        output.add(new CountryPopulation("new caledonia (france)",248000));
        output.add(new CountryPopulation("slovakia",5424925));
        output.add(new CountryPopulation("kyrgyzstan",5418300));
        output.add(new CountryPopulation("lithuania",3329039));
        output.add(new CountryPopulation("united states of america",309349689));
    }


    /**
     * we should keep all of this in a config file
     * maxAttempts
     * backoff
     *
     */
	@Override
    @Retryable(value = {StatsRetrivalError.class }, maxAttempts = 3, backoff = @Backoff(2000))
	public List<CountryPopulation> getCountryPopulations() throws IOException {
		//Sometimes the external services has a connection error
        try {
            if (RandomUtils.nextInt(0, 100) == 2) {
                throw new StatsRetrivalError();
            }

            return output;
        }
        catch (StatsRetrivalError e) {
            System.out.println("Retry");
            throw e;
        }
        catch (Exception e) {
            throw e;
        }

	}

    /**
     * It will be best if the external service will provide a search of their own
     * external-service/population/find?name=countryName
     * so we don't have to filter data on our server
     */
	@Override
    @Retryable(value = {StatsRetrivalError.class }, maxAttempts = 3, backoff = @Backoff(2000))
	public Optional<CountryPopulation> getCountryPopulationByName(String countryName) throws IOException {
        //Sometimes the external services has a connection error
        try {
            if (RandomUtils.nextInt(0, 100) == 2) {
                throw new StatsRetrivalError();
            }

            return output.stream().filter(country -> country.getName().toLowerCase().equals(countryName))
                    .findFirst();
        }
        catch (StatsRetrivalError e) {
            System.out.println("Retry");
            throw e;
        }
        catch (Exception e) {
            throw e;
        }

	}

}
