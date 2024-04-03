package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class Util {
    public static Connection connection;
    public static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory != null) {
            return sessionFactory;
        } else {
            return createSessionFactory();
        }
    }

    private static SessionFactory createSessionFactory() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        properties.put(Environment.URL, "jdbc:mysql://localhost:3306/kataWorkDB?createDatabaseIfNotExist=true");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
        properties.put(Environment.SHOW_SQL, "true");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        sessionFactory = new Configuration().addAnnotatedClass(User.class).setProperties(properties).buildSessionFactory();
        return sessionFactory;
    }


    public static Connection getConnection() {
        if (connection != null) {
            return connection;
        } else {
            return createConnection();
        }
    }

    private static Connection createConnection() {
        Properties properties = new Properties();

        try (FileReader fileReader = new FileReader("src\\main\\resources\\database.properties")) {
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
        try (PreparedStatement preparedStatement = getConnection().prepareStatement("CREATE DATABASE IF NOT EXISTS kataWorkDB;")) {
            preparedStatement.addBatch("USE kataWorkDB;");
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
