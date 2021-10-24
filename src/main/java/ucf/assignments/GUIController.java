package ucf.assignments;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.nio.file.Path;
import java.util.ArrayList;

public class GUIController
{
    @FXML
    private Label welcomeText;

    @FXML
    protected void CreateTODOList()
    {
        //welcomeText.setText("Welcome to JavaFX Application!");
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
}

class TODOList
{
    private String title;
    private ArrayList<TODOItem> itemsArray;
}

class TODOItem
{
    private String title;
    private String description;
    private String due_Date; // Change to actual date function
    private boolean complete = false;
}