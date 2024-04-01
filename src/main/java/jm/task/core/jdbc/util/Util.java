package jm.task.core.jdbc.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Util {
    public static Connection connection;

    public static Connection getConnection() {
        if (connection != null) {
            return connection;
        } else {
            return createConnection();
        }
    }

    private static Connection createConnection() {
        Properties properties = new Properties();

        try (FileReader fileReader = new FileReader(new File("src\\main\\resources\\database.properties"))) {
            properties.load(fileReader);

            Class.forName(properties.getProperty("driver")); //Обратнаяя совместимость?

            connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("username"), properties.getProperty("password"));
            initDatabase();  // Инициальзируем базу данных на случай ее отсутствия
            return connection;
        } catch (IOException | SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initDatabase() {
        try (Statement statement = connection.createStatement()) {
            statement.addBatch("CREATE DATABASE IF NOT EXISTS kataWorkDB;");
            statement.addBatch("USE kataWorkDB;");
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
