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
    final ObservableList<TODOList> lists = FXCollections.observableArrayList();

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

    // File Menu Functions
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
            AddTODOItem(item, selectedList);
            //lists.get(selectedList).itemsArray.add(item);
            //item_tableView.getItems().add(item);
        });
    }

    protected void AddTODOItem(TODOItem item, int selectedList)
    {
        lists.get(selectedList).itemsArray.add(item);
        item_tableView.getItems().add(item);
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

        DeleteTODOItem(selectedList, selectedItem);
        //lists.get(selectedList).itemsArray.remove(selectedItem);
        //item_tableView.getItems().remove(selectedItem);
    }

    protected void DeleteTODOItem(int selectedList, int selectedItem)
    {
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
        /*Gson gson = new Gson();
        try
        {*/
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open TODO List");
            chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(".json", "*.JSON"));
            File file = chooser.showOpenDialog(null);

            if(file == null)
                return;

            LoadTODOList_Handler(file);

            /*Reader reader = Files.newBufferedReader(Path.of(file.toURI()));
            MainListWrapper mainListWrapper = gson.fromJson(reader, MainListWrapper.class);

            lists.clear();
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
        }*/
    }

    protected void LoadTODOList_Handler(File file)
    {
        Gson gson = new Gson();
        try
        {
            if(file == null)
                return;

            Reader reader = Files.newBufferedReader(Path.of(file.toURI()));
            MainListWrapper mainListWrapper = gson.fromJson(reader, MainListWrapper.class);

            lists.clear();
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
        int selectedList = list_listView.getSelectionModel().getSelectedIndex();

        SaveCurrentTODOList_Handler(selectedList);

        /*if(selectedList < 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR); // Alert dialog
            alert.setHeaderText("Select a TODO List.");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open TODO List");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(".json", "*.JSON"));
        File file = chooser.showSaveDialog(null);

        try (Writer writer = new FileWriter(file))
        {
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
        }*/
    }

    protected void SaveCurrentTODOList_Handler(int selectedList)
    {
        if(selectedList < 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR); // Alert dialog
            alert.setHeaderText("Select a TODO List.");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open TODO List");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(".json", "*.JSON"));
        File file = chooser.showSaveDialog(null);

        try (Writer writer = new FileWriter(file))
        {
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
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open TODO List");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(".json", "*.JSON"));
        File file = chooser.showSaveDialog(null);

        SaveAllTODOList_Handler(file);

        /*try (Writer writer = new FileWriter(file))
        {
            MainListWrapper mainListWrapper = new MainListWrapper();

            for (TODOList list : lists)
            {
                TODOListWrapper listWrapper = new TODOListWrapper();
                listWrapper.title = list.title;
                for (int j = 0; j < list.itemsArray.size(); j++)
                {
                    TODOItemWrapper itemWrapper = new TODOItemWrapper();
                    itemWrapper.title = list.itemsArray.get(j).getTitle();
                    itemWrapper.description = list.itemsArray.get(j).getDescription();
                    itemWrapper.due_date = list.itemsArray.get(j).getDue_Date().toString();
                    itemWrapper.complete = list.itemsArray.get(j).getComplete();
                    listWrapper.itemsArray.add(itemWrapper);
                }
                mainListWrapper.list.add(listWrapper);
            }

            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(mainListWrapper, writer);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }*/
    }

    protected void SaveAllTODOList_Handler(File file)
    {
        try (Writer writer = new FileWriter(file))
        {
            MainListWrapper mainListWrapper = new MainListWrapper();

            for (TODOList list : lists)
            {
                TODOListWrapper listWrapper = new TODOListWrapper();
                listWrapper.title = list.title;
                for (int j = 0; j < list.itemsArray.size(); j++)
                {
                    TODOItemWrapper itemWrapper = new TODOItemWrapper();
                    itemWrapper.title = list.itemsArray.get(j).getTitle();
                    itemWrapper.description = list.itemsArray.get(j).getDescription();
                    itemWrapper.due_date = list.itemsArray.get(j).getDue_Date().toString();
                    itemWrapper.complete = list.itemsArray.get(j).getComplete();
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

    // Edit Menu Functions
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

        TextField listTitle = new TextField(lists.get(selectedList).title);

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
            EditTODOList_Handler(list, selectedList);
            //lists.get(selectedList).title = list.title;
            //list_listView.getItems().set(selectedList, list.title);
        });
    }

    protected void EditTODOList_Handler(TODOList list, int selectedList)
    {
        lists.get(selectedList).title = list.title;
        list_listView.getItems().set(selectedList, list.title);
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

        TextField itemTitle = new TextField(lists.get(selectedList).itemsArray.get(selectedIndex).getTitle());
        TextField itemDescription = new TextField(lists.get(selectedList).itemsArray.get(selectedIndex).getDescription());
        DatePicker datePicker = new DatePicker(lists.get(selectedList).itemsArray.get(selectedIndex).getDue_Date());

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
            EditTODOItem_Handler(item, selectedList, selectedIndex);
            /*lists.get(selectedList).itemsArray.get(selectedIndex).setTitle(item.getTitle());
            lists.get(selectedList).itemsArray.get(selectedIndex).setDescription(item.getDescription());
            lists.get(selectedList).itemsArray.get(selectedIndex).setDue_Date(item.getDue_Date());
            lists.get(selectedList).itemsArray.get(selectedIndex).setComplete(item.getComplete());*/
        });
    }

    protected void EditTODOItem_Handler(TODOItem item, int selectedList, int selectedIndex)
    {
        lists.get(selectedList).itemsArray.get(selectedIndex).setTitle(item.getTitle());
        lists.get(selectedList).itemsArray.get(selectedIndex).setDescription(item.getDescription());
        lists.get(selectedList).itemsArray.get(selectedIndex).setDue_Date(item.getDue_Date());
        lists.get(selectedList).itemsArray.get(selectedIndex).setComplete(item.getComplete());
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

        MarkComplete_Handler(selectedList, selectedIndex);
        //lists.get(selectedList).itemsArray.get(selectedIndex).setComplete(!lists.get(selectedList).itemsArray.get(selectedIndex).getComplete());
    }

    protected void MarkComplete_Handler(int selectedList, int selectedIndex)
    {
        lists.get(selectedList).itemsArray.get(selectedIndex).setComplete(!lists.get(selectedList).itemsArray.get(selectedIndex).getComplete());
    }

    // Filter Menu Functions
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

    @FXML
    protected void HelpDialog()
    {
        // Display help dialog with application tutorial
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("TODO List Help");
        alert.setHeaderText("Help");
        alert.setContentText("Load a saved TODO List:\n\tFile>Load TODO List\n\nSave Currently Selected List:\n\tFile>Save Selected List\n\nSave All TODO Lists:\n\tFile>Save All TODO Lists\n\nCreating a new TODO List:\n\tFile>New TODO List\n\nCreating a new TODO Item (Task):\n\tFile>New TODO Item\n(Current TODO List must be selected/highlighted)\n\nRemove TODO List:\n\tFile>Remove TODO List\n(TODO List must be selected/highlighted)\n\nRemove TODO Item (Task):\n\tFile>Remove TODO Item\n(TODO List and item must be selected/highlighted)\n\nEdit TODO List Title:\n\tEdit>Edit TODO List Title\n(TODO List must be selected/highlighted)\n\nEdit TODO Item (Task):\n\tEdit>Edit TODO Item\n(Task you wish to edit must be selected)\n\nMark Task as Complete:\n\tEdit>Toggle Complete\n(Repeat steps to mark the task incomplete)\n\nDisplay only complete items (Tasks):\n\tFilter>Display Complete\n\nDisplay only incomplete items (Tasks):\n\tFilter>Display Incomplete\n\nNo Filter:\n\tFilter>Display All");
        alert.showAndWait();
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
    ArrayList<TODOListWrapper> list = new ArrayList<>();
}

class TODOListWrapper
{
    String title;
    ArrayList<TODOItemWrapper> itemsArray = new ArrayList<>();
}

class TODOItemWrapper
{
    String title;
    String description;
    String due_date;
    boolean complete;
}