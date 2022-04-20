package no.ntnu.idata1002.g08.transactions;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import no.ntnu.idata1002.g08.dao.TransactionDAO;
import no.ntnu.idata1002.g08.data.Transaction;
import no.ntnu.idata1002.g08.data.TransactionType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Represents a table view of transactions, with columns for date, amount, category, description and account.
 *
 * @author Brage Solem
 * @version 27.04.2023
 */
public class TransactionTableView {

    /**
     * The Table view.
     */
    private TableView<Transaction> tableView;
    /**
     * Column for date in transaction table view.
     */
    private TableColumn<Transaction, String> dateColumn;
    /**
     * Column for amount in transaction table view.
     */
    private TableColumn<Transaction, Double> amountColumn;
    /**
     * Column for category in transaction table view.
     */
    private TableColumn<Transaction, String> categoryColumn;
    /**
     * Column for description in transaction table view.
     */
    private TableColumn<Transaction, String> descriptionColumn;
    /**
     * Column for account in transaction table view.
     */
    private TableColumn<Transaction, String> accountColumn;

    /**
     * Instantiates a new Transaction table view.
     */
    public TransactionTableView() {
        this.tableView = new TableView<>();
        this.dateColumn = new TableColumn<>("Date");
        this.amountColumn = new TableColumn<>("Amount");
        this.categoryColumn = new TableColumn<>("Category");
        this.descriptionColumn = new TableColumn<>("Description");
        this.accountColumn = new TableColumn<>("Account");

        this.amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        this.amountColumn.setResizable(false);
        this.amountColumn.setMinWidth(90);
        this.amountColumn.setPrefWidth(-1);

        this.dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        this.dateColumn.setResizable(false);
        this.dateColumn.setMinWidth(80);
        this.dateColumn.setPrefWidth(-1);
        this.dateColumn.setSortType(TableColumn.SortType.DESCENDING);
        dateColumn.setComparator((t, t1) -> {
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                Date d1 = format.parse(t);
                Date d2 = format.parse(t1);
                return Long.compare(d1.getTime(), d2.getTime());
            } catch (ParseException p) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                    Date d1 = format.parse(t);
                    Date d2 = format.parse(t1);
                    return Long.compare(d1.getTime(), d2.getTime());
                } catch (ParseException p2) {
                  p2.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return -1;
        });

        this.categoryColumn.setCellValueFactory(param -> {
            if(param.getValue() != null) {
                if (param.getValue().getCategory() == null) {
                    return new SimpleStringProperty("No category");
                } else {
                    return new SimpleStringProperty(param.getValue().getCategory().getName());
                }
            }else {
                return new SimpleStringProperty(null);
            }
        });
        this.categoryColumn.setResizable(false);
        this.categoryColumn.setMinWidth(80);
        this.categoryColumn.setPrefWidth(-1);

        this.accountColumn.setCellValueFactory(param ->{
            if(param.getValue() != null) {
               return new SimpleStringProperty(param.getValue().getAccount().getName());
           }
           else {
               return new SimpleStringProperty(null);
           }
        });

        this.accountColumn.setResizable(false);
        this.accountColumn.setMinWidth(65);
        this.amountColumn.setPrefWidth(-1);

        this.descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        this.descriptionColumn.setMinWidth(145);
        this.descriptionColumn.setPrefWidth(-1);
        this.descriptionColumn.setMaxWidth(290);

        this.amountColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (!(item == null || empty)) {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    if (transaction.getType() == TransactionType.EXPENSE) {
                        setText("-" + item);
                        getStyleClass().add("amountExpense");
                        getStyleClass().removeAll("amountIncome");

                    } else {
                        setText(item.toString());
                        getStyleClass().add("amountIncome");
                        getStyleClass().removeAll("amountExpense");
                    }
                } else {
                    setText(null);
                }
            }
        });

        this.tableView.getColumns().addAll(this.dateColumn, this.amountColumn, this.categoryColumn,this.accountColumn,this.descriptionColumn);
        this.tableView.getItems().addAll(this.dataBaseTransactionList());

        this.tableView.getSortOrder().add(this.dateColumn);
        this.tableView.sort();
        this.tableView.setPlaceholder(new Label("No transactions"));
        this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    /**
     * Get columns observable list.
     *
     * @return the observable list
     */
    public ObservableList<TableColumn<Transaction,?>> getColumns(){
        return this.tableView.getColumns();
    }

    /**
     * Get items observable list.
     *
     * @return the observable list
     */
    public ObservableList<Transaction> getItems(){
        return this.tableView.getItems();
    }

    /**
     * Gets transaction list from database.
     * @return the transaction list
     */
    private List<Transaction> dataBaseTransactionList(){
        TransactionDAO transactionDAO = new TransactionDAO();
        return transactionDAO.getAllTransactions();
    }
}
