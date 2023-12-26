package org.example;

import java.sql.*;

public class JDBCRepository implements AutoCloseable{
    private static Connection connection;
    private static Statement statement;
    public JDBCRepository(String connectionString) throws SQLException {
        connection = DriverManager.getConnection(connectionString);
        statement = connection.createStatement();
    }

    public void runExecute(String sqlExecute) throws SQLException {
        statement.execute(sqlExecute);
    }

    public void runUpdate(String sqlUpdate) throws SQLException {
        statement.executeUpdate(sqlUpdate);
    }

    public ResultSet runQuery(String sqlQuery) throws SQLException {
        return statement.executeQuery(sqlQuery);
    }

    public void close() throws SQLException {
        connection.close();
    }
}
