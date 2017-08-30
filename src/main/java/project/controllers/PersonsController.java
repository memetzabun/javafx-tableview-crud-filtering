package project.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import project.model_views.PersonView;
import project.models.Person;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class PersonsController implements Initializable {

    private PersonView personView;
    @FXML
    private TableView<Person> table;
    @FXML
    private TableColumn<Person, String> firstNameColumn;
    @FXML
    private TableColumn<Person, String> lastNameColumn;
    @FXML
    private TableColumn<Person, String> ageColumn;

    @FXML
    private TextField firstNameInput;
    @FXML
    private TextField lastNameInput;
    @FXML
    private TextField ageInput;
    @FXML
    private Button add;

    @FXML
    private Button delete;

    @FXML
    private TextField search;

    @FXML
    private void addOnClick(ActionEvent event) {
        final String FIRST_NAME = firstNameInput.getText();
        final String LAST_NAME = lastNameInput.getText();
        final int AGE_NAME = Integer.parseInt(ageInput.getText());
        Person newPerson = new Person(FIRST_NAME, LAST_NAME, AGE_NAME);
        personView.add(newPerson);
    }

    @FXML
    private void deleteOnClick(ActionEvent event) {
        if (table.getSelectionModel() != null)
            personView.delete(table.getSelectionModel().getSelectedItem());
    }

    public void initialize(URL location, ResourceBundle resources) {
        table.setEditable(true);
        configColumn(firstNameColumn, Person::getFirstName, Person::setFirstName);
        configColumn(lastNameColumn, Person::getLastName, Person::setLastName);
        configColumn(ageColumn, (p) -> p.getAge().toString(), (p, newAge) -> p.setAge(Integer.parseInt(newAge)));

        personView = new PersonView(table.getItems());
        if (personView.getAll().isEmpty()) {
            personView.addAll(
                    new Person("Yevhen", "Zhukov", 35),
                    new Person("Mykola", "Matvienko", 41),
                    new Person("Olena", "Buhachevska", 43)
            );
        } else {
            personView.addAll(personView.getAll().toArray(new Person[]{}));
        }
        addSearch();
    }

    private void addSearch() {
        FilteredList<Person> filteredTable = new FilteredList<Person>(table.getItems(), e -> true);
        search.setOnKeyReleased(e -> {
            search.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredTable.setPredicate(person -> {
                    if (newValue == null || newValue.isEmpty())
                        return true;
                    else if (person.getFirstName().toLowerCase().contains(newValue.toLowerCase()))
                        return  true;
                    else if (person.getLastName().toLowerCase().contains(newValue.toLowerCase()))
                        return  true;
                    else if (person.getAge().toString().toLowerCase().contains(newValue.toLowerCase()))
                        return  true;
                    return false;
                });
            });
            SortedList<Person> sortedTable = new SortedList<>(filteredTable);
            sortedTable.comparatorProperty().bind(table.comparatorProperty());
            table.setItems(sortedTable);
        });
    }

    private void configColumn(final TableColumn<Person, String> COLUMN,
                              Function<Person, String> getter, BiConsumer<Person, String> setter)
    {
        COLUMN.setCellValueFactory(column -> new SimpleStringProperty(getter.apply(column.getValue())));
        COLUMN.setCellFactory(TextFieldTableCell.forTableColumn());
        COLUMN.setOnEditCommit(t -> {
            Person person = t.getTableView().getItems().get(t.getTablePosition().getRow());
            String newValue = t.getNewValue();
            personView.update(person, setter, newValue);
        });
    }
}
