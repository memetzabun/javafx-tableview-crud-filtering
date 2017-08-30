package project.model_views;

import javafx.collections.ObservableList;
import project.models.Person;
import project.persistence.PersonDaoImpl;

import java.util.List;
import java.util.function.BiConsumer;

public class PersonView {

    private ObservableList<Person> entities;
    private PersonDaoImpl persistence;

    {
        persistence = new PersonDaoImpl();
    }

    public PersonView(ObservableList<Person> entities) {
        this.entities = entities;
        persistence.createTable();
        for (Person person : entities) {
            persistence.add(person);
            if (persistence.get(person) != null)
                entities.add(persistence.get(person));
        }
    }

    public void add(Person person) {
        persistence.add(person);
        entities.add(persistence.get(person));
    }

    public void addAll(Person... persons) {
        for (Person person : persons) {
            persistence.add(person);
            if (persistence.get(person) != null)
                entities.add(persistence.get(person));
        }
    }

    public void delete(Person person) {
        persistence.delete(person);
        System.out.println(persistence.get(person));
        if (persistence.get(person) == null)
            entities.remove(entities.indexOf(person));
    }

    public void update(Person person, BiConsumer<Person, String> setter, String newValue) {
        Person toUpdate = new Person();
        toUpdate.setFirstName(person.getFirstName());
        toUpdate.setLastName(person.getLastName());
        toUpdate.setAge(person.getAge());
        setter.accept(toUpdate, newValue);
        persistence.update(person, toUpdate.getFirstName(), toUpdate.getLastName(), toUpdate.getAge());
    }

    public List<Person> getAll() {
        return persistence.getAll();
    }
}
