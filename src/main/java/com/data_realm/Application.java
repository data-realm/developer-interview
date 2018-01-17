package com.data_realm;

import com.data_realm.interview.repository.impl.DbConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main method of the executable JAR generated from this repository. This is to let you
 * execute something from the command-line or IDE for the purposes of demonstration, but you can choose
 * to demonstrate in a different way (e.g. if you're using a framework)
 */
@SpringBootApplication
public class Application {
    public static void main( String args[] ) {
        System.out.println("Starting.");
        SpringApplication.run(Application.class, args);

        // open the DB connection upon application startup
        DbConnection.getInstance();
    }
}