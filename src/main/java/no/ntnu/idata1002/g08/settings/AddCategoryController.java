package no.ntnu.idata1002.g08.settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import no.ntnu.idata1002.g08.data.Category;
import no.ntnu.idata1002.g08.data.CategoryRegistry;
import no.ntnu.idata1002.g08.data.TransactionType;
import static no.ntnu.idata1002.g08.utils.Alert.showAlert;

/**
 * Responsible for adding, editing and deleting categories.
 * Shows a window with a form for adding, editing or deleting a category.
 *
 * @author Brage Solem
 * @version 27.04.2023
 */
public class AddCategoryController {

    /**
     * confirm button for add, edit or delete
     */
    @FXML
    private Button addButton;
    /**
     * Name field for category
     */
    @FXML
    private TextField nameField;
    /**
     * Type choice box for category
     */
    @FXML
    private ChoiceBox<TransactionType> typeChoice;

    /**
     * The Invalid inputs.
     */
    private String invalidInputs;
    /**
     * The Category.
     */
    private Category category;


    /**
     * Instantiates a new Add category controller.
     */
    public AddCategoryController() {
        this.init(null,false);
    }

    /**
     * Instantiates a new Add category controller.
     *
     * @param category the category
     */
    public AddCategoryController(Category category) { //for edit
        this.init(category,false);
    }

    /**
     * Instantiates a new Add category controller to edit or delete.
     *
     * @param category the category
     * @param delete to delete or not.
     */
    public AddCategoryController(Category category, boolean delete) { //for delete
        this.init(category,delete);
    }

    /**
     * Initializes the add category controller and window.
     *
     * @param category the category to edit or delete
     * @param delete   to delete or not.
     */
    public void init(Category category,boolean delete) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings/AddCategory.fxml"));
        loader.setController(this);
        try {
            VBox rootVBox = loader.load();

            this.addButton.setOnAction(this::addCategory);

            this.nameField.clear();

            this.typeChoice.getItems().addAll(TransactionType.values());

            if(category != null) {
                this.updateForEdit(category);
                if (delete) {
                    this.updateForDelete(category);
                }
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(rootVBox));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the category for deletion.
     *
     * @param category the category to delete.
     */
    private void updateForDelete(Category category) {
        this.nameField.editableProperty().setValue(false);
        this.typeChoice.setDisable(true);
        this.addButton.setText("Confirm delete");
        this.addButton.setOnAction(e->deleteCategory(category));
    }

    /**
     * Updates the window for editing a category.
     *
     * @param category the category to edit.
     */
    private void updateForEdit(Category category) {
        this.nameField.setText(category.getName());
        this.typeChoice.setValue(category.getType());
        this.addButton.setText("Update");
        this.addButton.setOnAction(e->updateCategory(category));
    }

    /**
     * Adds a category.
     *
     * @param event event that triggers the method.
     */
    private void addCategory(ActionEvent event) {
        if (this.checkInput()) {
            category = new Category(this.nameField.getText(), this.typeChoice.getValue());
            CategoryRegistry.getInstance().addOrUpdateCategory(category);
            Stage stage = (Stage) this.addButton.getScene().getWindow();
            stage.close();
        }else {
            showAlert(Alert.AlertType.INFORMATION, this.addButton.getScene().getWindow(), "Invalid input", "Please enter valid input for: " + invalidInputs);
        }
    }

    /**
     * Updates a category.
     *
     * @param category the category to update.
     */
    private void updateCategory(Category category) {
        if (this.checkInput()) {
            category.setName(this.nameField.getText());
            category.setType(this.typeChoice.getValue());
            CategoryRegistry.getInstance().addOrUpdateCategory(category);
            Stage stage = (Stage) this.addButton.getScene().getWindow();
            stage.close();
        }else {
            showAlert(Alert.AlertType.INFORMATION, this.addButton.getScene().getWindow(), "Invalid input", "Please enter valid input for: " + invalidInputs);
        }
    }

    /**
     * Deletes a category.
     *
     * @param category the category to delete.
     */
    private void deleteCategory(Category category) {
        CategoryRegistry.getInstance().removeCategory(category);
        Stage stage = (Stage) this.addButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    public Category getCategory() {
        return this.category;
    }

    /**
     * Checks the input for the category.
     *
     * @return true if the input is valid, false if not.
     */
    private boolean checkInput() {
        boolean validInput = true;
        invalidInputs = "";
        if ((this.nameField.getText().isEmpty())&&(nameField.getText() == null)){
            validInput = false;
            invalidInputs += "Name, ";
        }

        if(CategoryRegistry.getInstance().getCategoryByName(this.nameField.getText()) != null){
            validInput = false;
            invalidInputs += "Name taken, ";
        }

        if ((this.typeChoice.getValue() != TransactionType.EXPENSE)&&(this.typeChoice.getValue() != TransactionType.INCOME)) {
            validInput = false;
            invalidInputs += "Type";
        }
        return validInput;
    }
}
