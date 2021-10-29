package ucf.assignments;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import com.google.gson.Gson;
import java.util.ArrayList;

public class GUIController
{
    ArrayList<TODOList> listOfLists;

    @FXML
    private ListView<String> list_listView;
    @FXML
    private ListView<String> items_listView;
    @FXML
    private ListView<String> description_listView;

    @FXML
    protected void CreateTODOList()
    {
        // Open dialog to get title
        ObservableList<String> items = FXCollections.observableArrayList("test");
        list_listView.setItems(items);
        Gson gson = new Gson();
    }

    @FXML
    protected void RemoveTODOList()
    {
    }

    @FXML
    protected void CreateTODOItem()
    {

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
    TODOItem[] itemsArray;
}

class TODOItem
{
    String title;
    String description;
    String due_Date; // Change to actual date function?
    boolean complete = false;
}