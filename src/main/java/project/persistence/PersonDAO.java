package project.persistence;

import project.models.Person;

import java.util.List;

public interface PersonDAO {

    List<Person> getAll();

    Person get(Person person);

    void update(Person person, String newFirstName, String newLastName, int newAge);

    void delete(Person person);

    void add(Person person);
}
