package ma.enset.tp7_gestion_taches;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.io.IOException;

public class HelloApplication extends Application {

    // List to store tasks
    private ObservableList<Task> tasks = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // UI Elements
        TextField taskNameField = new TextField();
        taskNameField.setPromptText("Nom de la tâche");

        TextArea taskDescriptionArea = new TextArea();
        taskDescriptionArea.setPromptText("Description");

        ComboBox<String> priorityComboBox = new ComboBox<>(FXCollections.observableArrayList("Basse", "Moyenne", "Élevée"));
        priorityComboBox.setPromptText("Priorité");

        DatePicker dueDatePicker = new DatePicker();

        ListView<Task> taskListView = new ListView<>(tasks);

        Button addButton = new Button("Ajouter");
        Button updateButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");

        // Add Task Form
        VBox form = new VBox(10, taskNameField, taskDescriptionArea, priorityComboBox, dueDatePicker, addButton, updateButton, deleteButton);
        form.setPadding(new Insets(10));

        // Filter Options
        ToggleGroup filterGroup = new ToggleGroup();
        RadioButton allTasksFilter = new RadioButton("Toutes les tâches");
        allTasksFilter.setToggleGroup(filterGroup);
        allTasksFilter.setSelected(true);

        RadioButton highPriorityFilter = new RadioButton("Priorité Élevée");
        highPriorityFilter.setToggleGroup(filterGroup);

        RadioButton mediumPriorityFilter = new RadioButton("Priorité Moyenne");
        mediumPriorityFilter.setToggleGroup(filterGroup);

        RadioButton lowPriorityFilter = new RadioButton("Priorité Basse");
        lowPriorityFilter.setToggleGroup(filterGroup);

        RadioButton overdueFilter = new RadioButton("Échéance dépassée");
        overdueFilter.setToggleGroup(filterGroup);

        VBox filters = new VBox(10, allTasksFilter, highPriorityFilter, mediumPriorityFilter, lowPriorityFilter, overdueFilter);
        filters.setPadding(new Insets(10));

        // Search Field
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher par nom ou description");

        // Layout
        BorderPane root = new BorderPane();
        root.setLeft(filters);
        root.setCenter(taskListView);
        root.setRight(form);
        root.setTop(searchField);

        // Scene
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Gestion des Tâches");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Event Handlers
        addButton.setOnAction(e -> {
            String name = taskNameField.getText();
            String description = taskDescriptionArea.getText();
            String priority = priorityComboBox.getValue();
            LocalDate dueDate = dueDatePicker.getValue();

            if (name.isEmpty() || description.isEmpty() || priority == null || dueDate == null) {
                showAlert("Erreur", "Veuillez remplir tous les champs du formulaire.", Alert.AlertType.ERROR);
                return;
            }

            tasks.add(new Task(name, description, priority, dueDate));
            clearForm(taskNameField, taskDescriptionArea, priorityComboBox, dueDatePicker);
        });

        updateButton.setOnAction(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask == null) {
                showAlert("Erreur", "Veuillez sélectionner une tâche à modifier.", Alert.AlertType.ERROR);
                return;
            }

            selectedTask.setName(taskNameField.getText());
            selectedTask.setDescription(taskDescriptionArea.getText());
            selectedTask.setPriority(priorityComboBox.getValue());
            selectedTask.setDueDate(dueDatePicker.getValue());

            taskListView.refresh();
            clearForm(taskNameField, taskDescriptionArea, priorityComboBox, dueDatePicker);
        });

        deleteButton.setOnAction(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask == null) {
                showAlert("Erreur", "Veuillez sélectionner une tâche à supprimer.", Alert.AlertType.ERROR);
                return;
            }

            tasks.remove(selectedTask);
        });

        filterGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) return;

            String filter = ((RadioButton) newToggle).getText();
            taskListView.setItems(getFilteredTasks(filter));
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            taskListView.setItems(getFilteredTasks(newText));
        });
    }

    private ObservableList<Task> getFilteredTasks(String filter) {
        return FXCollections.observableArrayList(tasks.stream()
                .filter(task -> task.matchesFilter(filter))
                .collect(Collectors.toList()));
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm(TextField nameField, TextArea descriptionArea, ComboBox<String> priorityComboBox, DatePicker dueDatePicker) {
        nameField.clear();
        descriptionArea.clear();
        priorityComboBox.setValue(null);
        dueDatePicker.setValue(null);
    }

    // Task Class
    public static class Task {
        private String name;
        private String description;
        private String priority;
        private LocalDate dueDate;

        public Task(String name, String description, String priority, LocalDate dueDate) {
            this.name = name;
            this.description = description;
            this.priority = priority;
            this.dueDate = dueDate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }

        public boolean matchesFilter(String filter) {
            if (filter.equals("Toutes les tâches")) return true;
            if (filter.equals("Priorité Élevée") && priority.equals("Élevée")) return true;
            if (filter.equals("Priorité Moyenne") && priority.equals("Moyenne")) return true;
            if (filter.equals("Priorité Basse") && priority.equals("Basse")) return true;
            if (filter.equals("Échéance dépassée") && dueDate.isBefore(LocalDate.now())) return true;
            if (name.toLowerCase().contains(filter.toLowerCase()) || description.toLowerCase().contains(filter.toLowerCase())) return true;
            return false;
        }

        @Override
        public String toString() {
            return String.format("%s (%s) - %s", name, priority, dueDate);
        }
    }
}