package ucf.assignments;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.layout.VBox;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.Optional;

/*
 *  UCF COP3330 Fall 2021 Assignment 4 Solution
 *  Copyright 2021 Cameron McDougal
 */

public class GUIController
{
    @FXML
    private final ObservableList<TODOList> lists = FXCollections.observableArrayList();

    private int currentTable;
    private boolean filtered = false;

    @FXML
    private ListView<String> list_listView;
    @FXML
    private ListView<String> items_listView;
    @FXML
    private TableView<TODOItem> item_tableView;
    @FXML
    private TableColumn<TODOItem,String> titleCol_tableCol;
    @FXML
    private TableColumn<TODOItem,String> descCol_tableCol;
    @FXML
    private TableColumn<TODOItem,String> dueDate_tableCol;
    @FXML
    private TableColumn<TODOItem,Boolean> completeCol_tableCol;


    @FXML
    private void initialize()
    {
        titleCol_tableCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        descCol_tableCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        dueDate_tableCol.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty().asString());

        // Checkbox attempt
        //completeCol_tableCol.setCellFactory(CheckBoxTableCell.forTableColumn(completeCol_tableCol));
        //completeCol_tableCol.setCellValueFactory(new PropertyValueFactory("Complete"));

        completeCol_tableCol.setCellValueFactory(cellData -> cellData.getValue().completeProperty());
    }

    @FXML
    protected void CreateTODOList()
    {
        Dialog<TODOList> dialog = new Dialog<>();
        dialog.setTitle("Enter TODO List Title.");
        dialog.setHeaderText("TODO List Title:");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField listTitle = new TextField("Title");

        dialogPane.setContent(new VBox(8, listTitle));
        Platform.runLater(listTitle::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new TODOList(listTitle.getText());
            }
            return null;
        });

        Optional<TODOList> optionalList = dialog.showAndWait();
        optionalList.ifPresent((TODOList list) ->
        {
            lists.add(list);
            list_listView.getItems().add(list.title);
        });
    }

    @FXML
    protected void RemoveTODOList()
    {
        int removeIndices = list_listView.getSelectionModel().getSelectedIndex();

        if(removeIndices >= 0)
        {
            lists.remove(removeIndices);
            list_listView.getItems().remove(removeIndices);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR); // Alert dialog
            alert.setHeaderText("Select a TODO List.");
            alert.setTitle("Error");
            alert.showAndWait();
        }
    }

    @FXML
    protected void CreateTODOItem()
    {
        int selectedList = list_listView.getSelectionModel().getSelectedIndex();
        if(selectedList < 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR); // Alert dialog
            alert.setHeaderText("Select a TODO List.");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }

        Dialog<TODOItem> dialog = new Dialog<>();
        dialog.setTitle("Enter TODO Item details.");
        dialog.setHeaderText("Title, Description, Due Date.");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField itemTitle = new TextField("Title");
        TextField itemDescription = new TextField("Description");
        DatePicker datePicker = new DatePicker(LocalDate.now());

        dialogPane.setContent(new VBox(8, itemTitle, itemDescription, datePicker));
        Platform.runLater(itemTitle::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new TODOItem(itemTitle.getText(), itemDescription.getText(), datePicker.getValue(), false);
            }
            return null;
        });

        Optional<TODOItem> optionalItem = dialog.showAndWait();
        optionalItem.ifPresent((TODOItem item) ->
        {
            lists.get(selectedList).itemsArray.add(item);
            item_tableView.getItems().add(item);
        });
    }

    @FXML
    protected void RemoveTODOItem()
    {
        int selectedList = list_listView.getSelectionModel().getSelectedIndex();
        int selectedItem =  items_listView.getSelectionModel().getSelectedIndex();

        if(selectedList < 0 || selectedItem < 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR); // Alert dialog
            alert.setHeaderText("Select a TODO List and TODO Item.");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }

        lists.get(selectedList).itemsArray.remove(selectedItem);
        items_listView.getItems().remove(selectedItem);
    }

    @FXML
    protected void onTODOListClick()
    {
        int selectedList = list_listView.getSelectionModel().getSelectedIndex();

        if(selectedList >= 0 && selectedList != currentTable)
        {
            currentTable = selectedList;
            item_tableView.getItems().clear();

            item_tableView.getItems().addAll(lists.get(selectedList).itemsArray);
        }
    }

    @FXML
    protected void LoadTODOList()
    {
        //Gson gson = new Gson();
    }

    @FXML
    protected void SaveCurrentTODOList()
    {
        //Gson gson = new Gson();
    }

    @FXML
    protected void SaveAllTODOLists()
    {
        try (Writer writer = new FileWriter("TODO.json"))
        {
            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(lists, writer);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // Edit functions
    @FXML
    protected void EditTODOListTitle()
    {
        int selectedList = list_listView.getSelectionModel().getSelectedIndex();
        if(selectedList < 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR); // Alert dialog
            alert.setHeaderText("Select a TODO List.");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }

        Dialog<TODOList> dialog = new Dialog<>();
        dialog.setTitle("Enter TODO List Title.");
        dialog.setHeaderText("TODO List Title:");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField listTitle = new TextField("Title");

        dialogPane.setContent(new VBox(8, listTitle));
        Platform.runLater(listTitle::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new TODOList(listTitle.getText());
            }
            return null;
        });

        Optional<TODOList> optionalList = dialog.showAndWait();
        optionalList.ifPresent((TODOList list) ->
        {
            lists.get(selectedList).title = list.title;
            list_listView.getItems().set(selectedList, list.title);
        });
    }

    @FXML
    protected void EditTODOItem()
    {
        int selectedList = list_listView.getSelectionModel().getSelectedIndex();
        int selectedIndex = item_tableView.getSelectionModel().getSelectedIndex();
        if(selectedList < 0 || selectedIndex < 0 || filtered)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR); // Alert dialog
            alert.setHeaderText("Select a TODO List & TODO Item and make sure list is not filtered.");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }

        Dialog<TODOItem> dialog = new Dialog<>();
        dialog.setTitle("Enter TODO Item details.");
        dialog.setHeaderText("Title, Description, Due Date.");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField itemTitle = new TextField("Title");
        TextField itemDescription = new TextField("Description");
        DatePicker datePicker = new DatePicker(LocalDate.now());

        dialogPane.setContent(new VBox(8, itemTitle, itemDescription, datePicker));
        Platform.runLater(itemTitle::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new TODOItem(itemTitle.getText(), itemDescription.getText(), datePicker.getValue(), false);
            }
            return null;
        });

        Optional<TODOItem> optionalItem = dialog.showAndWait();
        optionalItem.ifPresent((TODOItem item) ->
        {
            lists.get(selectedList).itemsArray.get(selectedIndex).setTitle(item.getTitle());
            lists.get(selectedList).itemsArray.get(selectedIndex).setDescription(item.getDescription());
            lists.get(selectedList).itemsArray.get(selectedIndex).setDue_Date(item.getDue_Date());
            lists.get(selectedList).itemsArray.get(selectedIndex).setComplete(item.getComplete());
        });
    }

    @FXML
    protected void MarkComplete()
    {
        int selectedList = list_listView.getSelectionModel().getSelectedIndex();
        int selectedIndex = item_tableView.getSelectionModel().getSelectedIndex();
        if(selectedList < 0 || selectedIndex < 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR); // Alert dialog
            alert.setHeaderText("Select a TODO List.");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }

        lists.get(selectedList).itemsArray.get(selectedIndex).setComplete(!lists.get(selectedList).itemsArray.get(selectedIndex).getComplete());
    }

    // Filter functions
    @FXML
    protected void ShowComplete()
    {
        int selectedList = list_listView.getSelectionModel().getSelectedIndex();
        if(selectedList < 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR); // Alert dialog
            alert.setHeaderText("Select a TODO List.");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }

        item_tableView.getItems().clear();
        for(int i = 0; i < lists.get(selectedList).itemsArray.size(); i++)
        {
            if(lists.get(selectedList).itemsArray.get(i).getComplete())
                item_tableView.getItems().add(lists.get(selectedList).itemsArray.get(i));
        }
        filtered = true;
    }

    @FXML
    protected void ShowIncomplete()
    {
        int selectedList = list_listView.getSelectionModel().getSelectedIndex();
        if(selectedList < 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR); // Alert dialog
            alert.setHeaderText("Select a TODO List.");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }

        item_tableView.getItems().clear();
        for(int i = 0; i < lists.get(selectedList).itemsArray.size(); i++)
        {
            if(!lists.get(selectedList).itemsArray.get(i).getComplete())
                item_tableView.getItems().add(lists.get(selectedList).itemsArray.get(i));
        }
        filtered = true;
    }

    @FXML
    protected void ShowAll()
    {
        int selectedList = list_listView.getSelectionModel().getSelectedIndex();
        if(selectedList < 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR); // Alert dialog
            alert.setHeaderText("Select a TODO List.");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }

        item_tableView.getItems().clear();
        item_tableView.getItems().addAll(lists.get(selectedList).itemsArray);
        filtered = false;
    }
}

class TODOList
{
    String title;
    ObservableList<TODOItem> itemsArray = FXCollections.observableArrayList();

    public TODOList(String title)
    {
        this.title = title;
    }
}

class TODOItem
{
    private final StringProperty title;
    private final StringProperty description;
    private final ObjectProperty<LocalDate> due_Date;
    private final BooleanProperty complete;

    public TODOItem(String title, String description, LocalDate due_Date, boolean complete)
    {
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.due_Date = new SimpleObjectProperty<>(due_Date);
        this.complete = new SimpleBooleanProperty(complete);
    }

    public String getTitle()
    {
        return title.get();
    }

    public StringProperty titleProperty()
    {
        return title;
    }

    public String getDescription()
    {
        return description.get();
    }

    public StringProperty descriptionProperty()
    {
        return description;
    }

    public LocalDate getDue_Date()
    {
        return due_Date.get();
    }

    public ObjectProperty<LocalDate> dueDateProperty()
    {
        return due_Date;
    }

    public boolean getComplete()
    {
        return complete.get();
    }

    public BooleanProperty completeProperty()
    {
        return complete;
    }

    public void setTitle(String _title)
    {
        this.title.set(_title);
    }

    public void setDescription(String _description)
    {
        this.description.set(_description);
    }

    public void setDue_Date(LocalDate date)
    {
        this.due_Date.set(date);
    }

    public void setComplete(boolean status)
    {
        this.complete.set(status);
    }
}