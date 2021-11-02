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
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
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
    protected void initialize()
    {
        titleCol_tableCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        descCol_tableCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        dueDate_tableCol.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty().asString());
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
        int removeIndex = list_listView.getSelectionModel().getSelectedIndex();

        if(removeIndex >= 0)
        {
            lists.remove(removeIndex);
            list_listView.getItems().remove(removeIndex);
            item_tableView.getItems().clear();
            list_listView.getSelectionModel().clearSelection();
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
        int selectedItem =  item_tableView.getSelectionModel().getSelectedIndex();

        if(selectedList < 0 || selectedItem < 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR); // Alert dialog
            alert.setHeaderText("Select a TODO List and TODO Item.");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }

        lists.get(selectedList).itemsArray.remove(selectedItem);
        item_tableView.getItems().remove(selectedItem);
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
        Gson gson = new Gson();
        try
        {
            FileChooser chooser = new FileChooser();
            File file = chooser.showOpenDialog(null);

            if(file == null)
                return;

            Reader reader = Files.newBufferedReader(Path.of(file.toURI()));
            MainListWrapper mainListWrapper;
            mainListWrapper = gson.fromJson(reader, MainListWrapper.class);

            lists.removeAll();
            list_listView.getItems().clear();
            item_tableView.getItems().clear();
            // Convert back to ObservableList and proper TODO classes
            for(int i = 0; i < mainListWrapper.list.size(); i++)
            {
                TODOList todoList = new TODOList(mainListWrapper.list.get(i).title);
                for(int j = 0; j < mainListWrapper.list.get(i).itemsArray.size(); j++)
                {
                    TODOItem todoItem = new TODOItem(mainListWrapper.list.get(i).itemsArray.get(j).title, mainListWrapper.list.get(i).itemsArray.get(j).description, LocalDate.parse(mainListWrapper.list.get(i).itemsArray.get(j).due_date), mainListWrapper.list.get(i).itemsArray.get(j).complete);
                    todoList.itemsArray.add(todoItem);
                }
                lists.add(todoList);
                list_listView.getItems().add(todoList.title);
            }
            item_tableView.getItems().addAll(lists.get(0).itemsArray);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    protected void SaveCurrentTODOList()
    {
        try (Writer writer = new FileWriter("TODO.json"))
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

            MainListWrapper mainListWrapper = new MainListWrapper();

            TODOListWrapper listWrapper = new TODOListWrapper();
            listWrapper.title = lists.get(selectedList).title;
            for(int j = 0; j < lists.get(selectedList).itemsArray.size(); j++)
            {
                TODOItemWrapper itemWrapper = new TODOItemWrapper();
                itemWrapper.title = lists.get(selectedList).itemsArray.get(j).getTitle();
                itemWrapper.description = lists.get(selectedList).itemsArray.get(j).getDescription();
                itemWrapper.due_date = lists.get(selectedList).itemsArray.get(j).getDue_Date().toString();
                itemWrapper.complete = lists.get(selectedList).itemsArray.get(j).getComplete();
                listWrapper.itemsArray.add(itemWrapper);
            }
            mainListWrapper.list.add(listWrapper);

            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(mainListWrapper, writer);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    protected void SaveAllTODOLists()
    {
        try (Writer writer = new FileWriter("TODO.json"))
        {
            MainListWrapper mainListWrapper = new MainListWrapper();

            for(int i = 0; i < lists.size(); i++)
            {
                TODOListWrapper listWrapper = new TODOListWrapper();
                listWrapper.title = lists.get(i).title;
                for(int j = 0; j < lists.get(i).itemsArray.size(); j++)
                {
                    TODOItemWrapper itemWrapper = new TODOItemWrapper();
                    itemWrapper.title = lists.get(i).itemsArray.get(j).getTitle();
                    itemWrapper.description = lists.get(i).itemsArray.get(j).getDescription();
                    itemWrapper.due_date = lists.get(i).itemsArray.get(j).getDue_Date().toString();
                    itemWrapper.complete = lists.get(i).itemsArray.get(j).getComplete();
                    listWrapper.itemsArray.add(itemWrapper);
                }
                mainListWrapper.list.add(listWrapper);
            }

            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(mainListWrapper, writer);
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

// Wrapper classes needed for Gson as JDK16 does not allow access of internal modules
class MainListWrapper
{
    ArrayList<TODOListWrapper> list;;
}

class TODOListWrapper
{
    String title;
    ArrayList<TODOItemWrapper> itemsArray;
}

class TODOItemWrapper
{
    String title;
    String description;
    String due_date;
    boolean complete;
}