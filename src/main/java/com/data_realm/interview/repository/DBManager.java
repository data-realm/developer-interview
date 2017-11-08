package com.data_realm.interview.repository;

import java.sql.Connection;

/**
 * Created by ckeswani on 9/16/15.
 */
public interface DBManager extends StatRepository {
    Connection getConnection();
}
