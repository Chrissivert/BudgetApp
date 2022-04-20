package no.ntnu.idata1002.g08.Budget;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import no.ntnu.idata1002.g08.dao.BudgetPeriodDAO;
import no.ntnu.idata1002.g08.data.Budget;
import no.ntnu.idata1002.g08.data.BudgetInterval;
import no.ntnu.idata1002.g08.data.BudgetPeriod;
import no.ntnu.idata1002.g08.screens.ScreenController;
import no.ntnu.idata1002.g08.screens.Screens;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static no.ntnu.idata1002.g08.utils.Alert.showAlert;

/**
 * The controller to the Budget page.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
public class BudgetController {
    @FXML
    public Button addBudgetButton;
    @FXML
    public VBox content;
    @FXML
    public Label totalAmounts;
    @FXML
    public FlowPane budgetsWrapper;
    @FXML
    public TableView<BudgetInterval> budgetTable;
    public TableColumn<BudgetInterval, String> budgetNameColumn;
    public TableColumn<BudgetInterval, Double> amountSpentColumn;
    @FXML
    private PieChart budgetPiechart;
    @FXML
    private ProgressBar totalProgressBar;
    @FXML
    private ComboBox<BudgetPeriod> budgetSelector;
    private static final double MIN_SCALE = 0.1;
    private static final double MAX_SCALE = 10.0;
    private static final double SCALE_DELTA = 1.1;
    private List<BudgetPeriod> budgetPeriods;
    private BudgetPeriod selectedBudgetPeriod;
    private double totalBudgetedAmount;
    private double totalSpentAmount;

    /**
     * Set up the controller.
     *
     * @param activeBudgetPeriod the active budgetperiode
     */
    public void init(BudgetPeriod activeBudgetPeriod) {

        budgetSelector.setConverter(new StringConverter<BudgetPeriod>() {
            @Override
            public String toString(BudgetPeriod budgetPeriod) {
                if (budgetPeriod == null) {
                    return null;
                } else {
                    return budgetPeriod.getName();
                }
            }

            @Override
            public BudgetPeriod fromString(String string) {
                return null;
            }
        });

        budgetSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedBudgetPeriod = newValue;
            refreshBudgetPeriodsFromDB();
            updateAllVisuals();
        });

        initTable();
        refreshBudgetPeriodsFromDB();
        setActiveBudgetPeriod(activeBudgetPeriod);
        refreshPeriodSelector();
        updateAllVisuals();
    }

    /**
     * Add a new Periode from a dialog.
     */
    @FXML
    public void addPeriod() {
        BudgetPeriod activeBudgetPeriod = new PeriodDialog(null, false).getBudgetPeriod();
        refreshBudgetPeriodsFromDB();
        setActiveBudgetPeriod(activeBudgetPeriod);
        refreshPeriodSelector();
        updateAllVisuals();
    }

    /**
     * Edit Periode from a dialog.
     */
    @FXML
    public void editPeriod() {
        if (selectedBudgetPeriod == null) {
            showAlert(Alert.AlertType.WARNING, content.getScene().getWindow(), "No period selected", "Please select a period to edit\nIf you don't have a period you can add one by clicking the add period button");
            return;
        }

        new PeriodDialog(selectedBudgetPeriod, false);
        refreshBudgetPeriodsFromDB();
        setActiveBudgetPeriod(selectedBudgetPeriod);
        refreshPeriodSelector();
        updateAllVisuals();
    }

    /**
     * Delete periode from dialog.
     */
    @FXML
    public void deletePeriod() {
        if (selectedBudgetPeriod == null) {
            showAlert(Alert.AlertType.WARNING, content.getScene().getWindow(), "No period selected", "Please select a period to delete\nIf you don't have a period you can add one by clicking the add period button");
            return;
        }
        new PeriodDialog(selectedBudgetPeriod, true);
        refreshBudgetPeriodsFromDB();
        setActiveBudgetPeriod(null);
        refreshPeriodSelector();
        updateAllVisuals();
    }

    /**
     * Add a bugdet item.
     */
    @FXML
    public void addBudgetItem() {
        if (selectedBudgetPeriod == null) {
            showAlert(Alert.AlertType.WARNING, content.getScene().getWindow(), "No period selected", "Please select a period to add a budget to.\nIf you don't have a period you can add one by clicking the add period button");
            return;
        }
        BudgetDialog budgetItemDialog = new BudgetDialog(null, selectedBudgetPeriod, false);
        refreshBudgetPeriodsFromDB();
        updateAllVisuals();
    }

    /**
     * Refresh the Periodes from the Database.
     */
    public void refreshBudgetPeriodsFromDB() {
        BudgetPeriodDAO budgetPeriodDAO = new BudgetPeriodDAO();
        budgetPeriods = budgetPeriodDAO.getAllBudgetPeriods();
        if (selectedBudgetPeriod != null) {
            selectedBudgetPeriod = budgetPeriods.stream().filter(budgetPeriod -> budgetPeriod.getId() == selectedBudgetPeriod.getId()).findFirst().orElse(null);
        }
    }

    /**
     * Add all the Budget Items.
     */
    private void addAllBudgetItems() {
        budgetsWrapper.getChildren().clear();
        if (selectedBudgetPeriod != null) {
            Set<Budget> budgets = selectedBudgetPeriod.getBudgets();
            List<Budget> sortedBudgets = budgets.stream().sorted(Comparator.comparing(Budget::getName)).collect(Collectors.toList());
            for (Budget budget : sortedBudgets) {
                BudgetPane budgetPane = new BudgetPane(budget.getLatestInterval(true), false, true);
                Pane budgetItemNode = budgetPane.getRootPane();
                budgetsWrapper.getChildren().add(budgetItemNode);
                budgetItemNode.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        ScreenController.instance().setActiveBudget(budget);
                        ScreenController.instance().setActiveBudgetPeriod(selectedBudgetPeriod);
                        ScreenController.instance().changeScreen(Screens.BUDGETHISTORY);
                    }
                });
            }
        }
    }

    /**
     * Update all the visuals on the page.
     */
    public void updateAllVisuals() {
        calculateAmounts();
        updateTotal();
        addAllBudgetItems();
        updateTable();
        updatePieChart();
    }

    /**
     * Update the table.
     */
    private void updateTable() {
        budgetTable.getItems().clear();

        if (selectedBudgetPeriod == null) {
            return;
        }

        List<BudgetInterval> budgetIntervals = selectedBudgetPeriod.getBudgets().stream()
                .map(budget -> budget.getLatestInterval(true))
                .collect(Collectors.toList());
        if (budgetIntervals.isEmpty()) {
            return;
        }
        budgetTable.getItems().addAll(budgetIntervals);
    }

    /**
     * Update the Pie Chart.
     */
    public void updatePieChart() {
        budgetPiechart.getData().clear();

        if (selectedBudgetPeriod == null) {
            return;
        }

        List<BudgetInterval> budgetIntervals = selectedBudgetPeriod.getBudgets().stream()
                .map(budget -> budget.getLatestInterval(true))
                .collect(Collectors.toList());

        if (budgetIntervals.isEmpty() || totalBudgetedAmount == 0) {
            return;
        }

        budgetIntervals = budgetIntervals.stream()
        .filter(budgetInterval -> budgetInterval.getUsedAmount() > (totalSpentAmount / 25))
        .sorted(Comparator.comparing(BudgetInterval::getUsedAmount).reversed())
        .collect(Collectors.toList());

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(budgetIntervals.stream().map(budgetInterval -> {
            double amountSpent = budgetInterval.getUsedAmount();
            String budgetName = budgetInterval.getBudget().getName();
            return new PieChart.Data(budgetName, amountSpent);
        }).collect(Collectors.toList()));

        budgetPiechart.getData().addAll(pieChartData);

        double remainingAmount = totalBudgetedAmount - totalSpentAmount;
        if (remainingAmount > totalSpentAmount /25) {
            budgetPiechart.getData().add(new PieChart.Data("Remaining", totalBudgetedAmount - totalSpentAmount));
        }

        for (PieChart.Data data : budgetPiechart.getData()) {
            Tooltip tooltip = new Tooltip();
            tooltip.setText(String.format("%s: %d", data.getName(), (int) data.getPieValue()));
            Tooltip.install(data.getNode(), tooltip);
            data.getNode().setOnMouseEntered(event -> tooltip.show(data.getNode().getScene().getWindow()));
            data.getNode().setOnMouseExited(event -> tooltip.hide());
        }
    }

    /**
     *Initiate the table.
     */
    private void initTable(){
        budgetNameColumn = new TableColumn<>("Budget Name");
        budgetNameColumn.setPrefWidth(120);
        budgetNameColumn.setCellValueFactory(param ->{
            BudgetInterval budgetInterval = param.getValue();
            if (budgetInterval == null) return new SimpleStringProperty("");
            Budget budget = budgetInterval.getBudget();
            return new SimpleStringProperty(budget.getName());
        });
        amountSpentColumn = new TableColumn<>("Amount spent");
        amountSpentColumn.setPrefWidth(120);
        amountSpentColumn.setCellValueFactory(param -> {
            BudgetInterval budgetInterval = param.getValue();
            if (budgetInterval == null) return new SimpleDoubleProperty(0).asObject();
            return new SimpleDoubleProperty(budgetInterval.getUsedAmount()).asObject();
        });
        budgetTable.getColumns().clear();
        budgetTable.getColumns().addAll(budgetNameColumn, amountSpentColumn);
    }

    /**
     * Refresh the Periode Selector.
     */
    public void refreshPeriodSelector() {
        budgetSelector.getItems().clear();

        if (budgetPeriods == null || budgetPeriods.size() == 0) {
            budgetSelector.setValue(null);
            budgetSelector.setPromptText("No budget periods");
            return;
        }

        for (BudgetPeriod budgetPeriod : budgetPeriods) {
            budgetSelector.getItems().add(budgetPeriod);
        }
        if (selectedBudgetPeriod != null) {
            budgetSelector.setValue(budgetPeriods.stream().filter(budgetPeriod -> budgetPeriod.getId() == selectedBudgetPeriod.getId()).findFirst().orElse(null));
        } else if (budgetPeriods.size() > 0) {
            budgetSelector.getSelectionModel().selectFirst();
        }
    }

    /**
     * Calculate the amounts.
     */
    private void calculateAmounts() {
        if (selectedBudgetPeriod != null) {
            double totalBudgeted = 0;
            double totalSpent = 0;
            for (Budget budget : selectedBudgetPeriod.getBudgets()) {
                BudgetInterval latestInterval = budget.getLatestInterval(true);
                if (latestInterval == null) {
                    continue;
                }
                totalBudgeted += latestInterval.getBudgetedAmount();
                totalSpent += latestInterval.getUsedAmount();
            }
            totalBudgetedAmount = totalBudgeted;
            totalSpentAmount = totalSpent;
        } else {
            totalBudgetedAmount = 0;
            totalSpentAmount = 0;
        }
    }

    /**
     * Update the total.
     */
    public void updateTotal() {
        String spentBudgeted = String.format("%.2f", totalSpentAmount) + " / " + String.format("%.2f", totalBudgetedAmount);
        totalAmounts.setText(spentBudgeted);
        double spentPercentage = totalBudgetedAmount > 0 ? totalSpentAmount / totalBudgetedAmount : 0;
        if (totalSpentAmount > totalBudgetedAmount) {
            totalProgressBar.setStyle("-fx-accent: red;");
        } else if (totalSpentAmount > totalBudgetedAmount) {
            totalProgressBar.setStyle("-fx-accent: orange;");
        } else {
            totalProgressBar.setStyle("-fx-accent: green;");
        }
        totalProgressBar.setProgress(spentPercentage);
    }

    /**
     * Set the active budget period.
     * @param budgetPeriod the budetPeriode to be set active.
     */
    public void setActiveBudgetPeriod(BudgetPeriod budgetPeriod) {
        if (budgetPeriod != null) {
            selectedBudgetPeriod = budgetPeriods.stream().filter(b -> b.getId() == budgetPeriod.getId()).findFirst().orElse(null);
        } else {
            selectedBudgetPeriod = null;
        }
            budgetSelector.setValue(selectedBudgetPeriod);
    }
}
