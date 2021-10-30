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
    private ListView<String> description_listView;

    @FXML
    protected void CreateTODOList()
    {
        // TextDialogBox to get TOCDO List Title
        //Gson gson = new Gson();

        TextInputDialog td = new TextInputDialog();
        td.getContentText();
        td.setHeaderText("New TODO List");
        td.showAndWait();
        TODOList list = new TODOList();
        list.title = td.getEditor().getText();

        lists.add(list);
        list_listView.getItems().add(list.title);
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
            // alert
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
        dialog.setHeaderText("Title\nDescription\nDue Date.");

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
    }

    @FXML
    protected void RemoveTODOItem()
    {

    }

    @FXML
    protected void LoadTODOList()
    {

    }

    @FXML
    protected void SaveCurrentTODOList()
    {

    }

    @FXML
    protected void SaveAllTODOLists()
    {

    }

    // Edit functions
    @FXML
    protected void EditTODOListTitle()
    {

    }

    @FXML
    protected void EditTODOItem()
    {

    }

    @FXML
    protected void MarkComplete()
    {

    }

    // Filter functions
    @FXML
    protected void ShowComplete()
    {

    }

    @FXML
    protected void ShowIncomplete()
    {

    }

    @FXML
    protected void ShowAll()
    {

    }

    static TODOList LoadJSON(String filePath)
    {

        return null;
    }
}

class TODOList
{
    String title;
    ArrayList<TODOItem> itemsArray = new ArrayList<TODOItem>();

    public TODOList()
    {

    }

    public TODOList(String title, ArrayList<TODOItem> itemsArray)
    {
        this.title = title;
        this.itemsArray = itemsArray;
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