package com.data_realm.interview.repository;

import java.sql.Connection;

public interface DBManager extends StatRepository {
    Connection getConnection();
}
