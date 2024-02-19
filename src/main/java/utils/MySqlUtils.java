package utils;

import aquality.selenium.core.logging.Logger;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import models.AnswerGomelRw;
import models.People;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static databasequeries.ColumnEmployee.*;
import static databasequeries.GomelRwQueries.getInsertPeopleInBase;

public class MySqlUtils {
    protected static final ISettingsFile MYSQL_CONFIG_FILE = new JsonSettingsFile("mysqlConfig.json");
    private static final String DB_HOST = MYSQL_CONFIG_FILE.getValue("/dbHost").toString();
    private static final String DB_PORT = MYSQL_CONFIG_FILE.getValue("/dbPort").toString();
    private static final String DB_USER = MYSQL_CONFIG_FILE.getValue("/dbUser").toString();
    private static final String DB_PASS = MYSQL_CONFIG_FILE.getValue("/dbPass").toString();
    private static final String DB_NAME = MYSQL_CONFIG_FILE.getValue("/dbName").toString();

    public static final String SQL_QUERY_FAILED = "Sql query failed...";
    public static final String CONNECTION_FAILED = "Connection failed...";

    private static Connection connection;

    private static Connection getDbConnection() {
        if (connection != null) {
            return connection;
        } else {
            String connectionString = String.format("jdbc:mysql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME);
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(connectionString, DB_USER, DB_PASS);
                return connection;
            } catch (ClassNotFoundException | SQLException e) {
                Logger.getInstance().error(CONNECTION_FAILED + e);
                throw new IllegalArgumentException(CONNECTION_FAILED, e);
            }
        }
    }

    public static void sendSqlQuery(String sqlQuery) {
        Connection connection = getDbConnection();
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sqlQuery);
        } catch (SQLException e) {
            Logger.getInstance().error(SQL_QUERY_FAILED + e);
        }
    }

    public static ResultSet sendSelectQuery(String sqlQuery) {
        Connection connection = getDbConnection();
        Statement statement;
        try {
            statement = connection.createStatement();
            return statement.executeQuery(sqlQuery);
        } catch (SQLException e) {
            Logger.getInstance().error(SQL_QUERY_FAILED + e);
            throw new IllegalArgumentException(SQL_QUERY_FAILED, e);
        }
    }

    public static int getFirstColumn(String sqlQuery) {
        try {
            ResultSet resultSet = sendSelectQuery(sqlQuery);
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            Logger.getInstance().error(sqlQuery + " " + SQL_QUERY_FAILED + e);
            throw new IllegalArgumentException(sqlQuery + " " + SQL_QUERY_FAILED, e);
        }
    }

    public static int getIdAndAddIfNot(String insertStr, String selectStr) {
        ResultSet resultSet = sendSelectQuery(selectStr);
        try {
            if (!resultSet.next()) {
                sendSqlQuery(insertStr);
                resultSet = sendSelectQuery(selectStr);
                resultSet.next();
            }
            return resultSet.getInt(1);
        } catch (SQLException e) {
            Logger.getInstance().error(SQL_QUERY_FAILED + e);
            throw new IllegalArgumentException(SQL_QUERY_FAILED, e);
        }
    }

    public static int getIdAndAddIfNotPeople(People people, String selectStr) {
        ResultSet resultSet = sendSelectQuery(selectStr);
        try {
            if (!resultSet.next()) {
                sendSqlQuery(getInsertPeopleInBase(people));
                resultSet = sendSelectQuery(selectStr);
                resultSet.next();
            }
            return resultSet.getInt(1);
        } catch (SQLException e) {
            Logger.getInstance().error(SQL_QUERY_FAILED + e);
            throw new IllegalArgumentException(SQL_QUERY_FAILED, e);
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                Logger.getInstance().error(CONNECTION_FAILED + e);
            }
        }
    }

    public static List<AnswerGomelRw> getListEmployee(String selectStr) {
        ResultSet resultSet = sendSelectQuery(selectStr);
        List<AnswerGomelRw> listNewEmployee = new ArrayList<>();
        try {
            while (resultSet.next()) {
                AnswerGomelRw employee = new AnswerGomelRw();
                employee.f = resultSet.getString(firstname.toString());
                employee.i = resultSet.getString(name.toString());
                employee.o = resultSet.getString(middlename.toString());
                employee.dt_birthday = resultSet.getString(dt_birthday.toString());
                employee.sex = resultSet.getString(sex.toString());
                employee.namepred = resultSet.getString(namepred.toString());
                employee.divisionname = resultSet.getString(divisionname.toString());
                employee.namepost = resultSet.getString(namepost.toString());

                listNewEmployee.add(employee);
            }
        } catch (SQLException e) {
            Logger.getInstance().error(SQL_QUERY_FAILED + e);
            throw new IllegalArgumentException(SQL_QUERY_FAILED, e);
        }
        return listNewEmployee;
    }
}