package ucf.assignments;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import com.google.gson.Gson;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class GUIController
{
    ArrayList<TODOList> listOfLists;
    ObservableList<TODOList> lists = FXCollections.observableArrayList();

    @FXML
    private ListView<String> list_listView;
    @FXML
    private ListView<String> items_listView;
    @FXML
    private TableView<String> desc_tableView;

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
    protected void RemoveTODOList() // Only supports a single remove atm
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
        dialog.setHeaderText("Title,\nDescription,\nDue Date.");

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
            items_listView.getItems().add(item.title);
        });


        // Add a tableView - Probably easier this way
            // Make columns for description, due date, and if completed

        // Editing a cell:
            // Rows will align with index
            // Use row# as item_arrayList index when editing to arrayList
            // Use selected list and selected

        //Tableview rules:
            // Disable sorting
            // Checkbox for complete would be ideal

    }

    @FXML
    protected void RemoveTODOItem()
    {
        int selectedList = list_listView.getSelectionModel().getSelectedIndex();
        int selectedItem =  items_listView.getSelectionModel().getSelectedIndex();

        if(selectedItem < 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR); // Alert dialog
            alert.setHeaderText("Select a TODO Item.");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }

        lists.get(selectedList).itemsArray.remove(selectedItem);
        items_listView.getItems().remove(selectedItem);
    }

    /************************************************
     *                                              *
     *      AREA TO ADD ITEM CLICK HANDLING         *
     *   UPDATE EACH LISTVIEW ON EACH ITEM CLICK    *
     ************************************************/

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
        //Gson gson = new Gson();
    }

    // Edit functions
    @FXML
    protected void EditTODOListTitle()
    {
        // Dialog to edit List Titles
    }

    @FXML
    protected void EditTODOItem()
    {
        // Same window as Creating TODOItem, just update arrayList item instead of appending to arraylist
    }

    @FXML
    protected void MarkComplete()
    {
        // Set selected item green
        // Toggle
        // Black for incomplete
    }

    // Filter functions
    @FXML
    protected void ShowComplete()
    {
        // Filter item_listView
    }

    @FXML
    protected void ShowIncomplete()
    {
        // Filter item_listView
    }

    @FXML
    protected void ShowAll()
    {
        // Essentially display like you are selecting.
        // Call the code to display list when list is selected
    }

    static TODOList LoadJSON(String filePath)
    {
        //Gson gson = new Gson();
        return null;
    }
}

class TODOList
{
    String title;
    ArrayList<TODOItem> itemsArray = new ArrayList<TODOItem>();

    public TODOList(String title)
    {
        this.title = title;
    }
}

class TODOItem
{
    String title;
    String description;
    LocalDate due_Date; // Change to actual date function?
    boolean complete = false;

    public TODOItem(String title, String description, LocalDate due_Date, boolean complete)
    {
        this.title = title;
        this.description = description;
        this.due_Date = due_Date;
        this.complete = complete;
    }
}