package project.persistence;

import project.models.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonDaoImpl implements PersonDAO {

    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String DB_PATH = "src/main/resources/";
    private static final String DB_NAME = "database/persons.db";
    private static final String USER = "root";
    private static final String PASSWORD = "root123";
    private static final String URL = String.format("jdbc:sqlite:%s%s", DB_PATH, DB_NAME);

    public Connection connect() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }

    public void createTable() {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            if (!isTablePresence()) {
                final String QUERY = "CREATE TABLE PERSONS (" +
                        "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "FIRST_NAME CHAR(50) NOT NULL," +
                        "LAST_NAME CHAR(50) NOT NULL," +
                        "AGE INT NOT NULL" +
                        ")";
                statement.executeUpdate(QUERY);
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println("Exception: " + e.getMessage());
            return;
        }
    }

    private boolean isTablePresence() {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            final String QUERY = "SELECT name FROM sqlite_master " +
                    "WHERE type=\'table\' AND name=\'PERSONS\'";
            ResultSet resultSet = statement.executeQuery(QUERY);
            return resultSet.next();
        } catch (SQLException | NullPointerException e) {
            System.out.println("Exception: " + e.getMessage() + " test");
        }
        return false;
    }

    private boolean isPersonPresence(Person person) {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            final String QUERY = String.format("SELECT * FROM PERSONS " +
                            "WHERE FIRST_NAME LIKE \'%s\' AND LAST_NAME LIKE \'%s\'AND AGE = %d",
                    person.getFirstName(), person.getLastName(), person.getAge());
            ResultSet resultSet = statement.executeQuery(QUERY);
            return resultSet.next();
        } catch (SQLException | NullPointerException e) {
            System.out.println("Exception: " + e.getMessage() + " test");
        }
        return false;
    }

    @Override
    public List<Person> getAll() {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            List<Person> result = new ArrayList<>();
            final String QUERY = "SELECT * FROM PERSONS";
            ResultSet resultSet = statement.executeQuery(QUERY);
            while (resultSet.next()) {
                String resFirstName = resultSet.getString("FIRST_NAME");
                String resLastName = resultSet.getString("LAST_NAME");
                int resAge = resultSet.getInt("AGE");
                result.add(new Person(resFirstName, resLastName, resAge));
            }
            return result;
        } catch (SQLException | NullPointerException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Person get(Person person) {
        try {
            if (isPersonPresence(new Person(person.getFirstName(), person.getLastName(), person.getAge())))
                return new Person(person.getFirstName(), person.getLastName(), person.getAge());
        } catch (NullPointerException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void update(Person person, String newFirstName, String newLastName, int newAge) {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            if (isPersonPresence(person)) {
                String firstNameToSave = newFirstName != null && !newFirstName.equals("")
                        ? newFirstName : person.getFirstName();
                String lastNameToSave = newLastName != null && !newLastName.equals("")
                        ? newLastName : person.getLastName();
                int ageToSave = newAge >= 0 ? newAge : person.getAge();

                final String QUERY = String.format(
                        "UPDATE PERSONS SET FIRST_NAME = \'%s\', LAST_NAME = \'%s\', AGE = %d " +
                                "WHERE FIRST_NAME LIKE \'%s\' AND LAST_NAME LIKE \'%s\' AND AGE = %d",
                        firstNameToSave, lastNameToSave, ageToSave, person.getFirstName(),
                        person.getLastName(), person.getAge());
                statement.executeUpdate(QUERY);

                person.setFirstName(firstNameToSave);
                person.setLastName(lastNameToSave);
                person.setAge(ageToSave);
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public void delete(Person person) {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            if (isPersonPresence(person)) {
                final String QUERY = String.format("DELETE FROM PERSONS " +
                                "WHERE FIRST_NAME LIKE '%s' AND LAST_NAME LIKE '%s'AND AGE = %d",
                        person.getFirstName(), person.getLastName(), person.getAge());
                statement.executeUpdate(QUERY);
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public void add(Person person) {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            if (!isPersonPresence(person)) {
                final String QUERY = "INSERT INTO PERSONS (FIRST_NAME, LAST_NAME, AGE)" +
                        "VALUES (" +
                        String.format("\'%s\',", person.getFirstName()) +
                        String.format("\'%s\',", person.getLastName()) +
                        String.format("%d", person.getAge()) +
                        ")";
                statement.executeUpdate(QUERY);
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
