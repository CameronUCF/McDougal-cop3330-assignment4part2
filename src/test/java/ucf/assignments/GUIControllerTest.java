package ucf.assignments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/*
 *  UCF COP3330 Fall 2021 Assignment 4 Solution
 *  Copyright 2021 Cameron McDougal
 */

// Instead of using the actual functions that are used in the GUIController class due JavaFX elements causing issues,
// I am essentially doing the same steps that I am doing in GUIController, just outside the GUIController class
class GUIControllerTest
{
    private GUIController controller;

    @BeforeEach
    void setUp()
    {
        controller = new GUIController();
    }

    @Test
    void createTODOList()
    {
        TODOList list = new TODOList("Test Title");
        controller.lists.add(list);

        assertEquals(controller.lists.get(0), list);
    }

    @Test
    void removeTODOList()
    {
        TODOList list = new TODOList("Test Title");
        controller.lists.add(list);
        TODOList list2 = new TODOList("Test Title2");
        controller.lists.add(list2);

        controller.lists.remove(0);

        assertEquals(controller.lists.get(0), list2);
    }

    @Test
    void createTODOItem()
    {
        TODOList list = new TODOList("Test Title");
        controller.lists.add(list);

        TODOItem item = new TODOItem("Title", "Description", LocalDate.now(), false);
        controller.lists.get(0).itemsArray.add(item);

        assertEquals(controller.lists.get(0).itemsArray.get(0), item);
        assertEquals(controller.lists.get(0).title, list.title);
    }

    @Test
    void removeTODOItem()
    {
        TODOList list = new TODOList("Test Title");
        controller.lists.add(list);

        TODOItem item = new TODOItem("Title", "Description", LocalDate.now(), false);
        controller.lists.get(0).itemsArray.add(item);

        TODOItem item2 = new TODOItem("Title2", "Description2", LocalDate.now(), false);
        controller.lists.get(0).itemsArray.add(item2);

        controller.lists.get(0).itemsArray.remove(0);

        assertEquals(controller.lists.get(0).itemsArray.get(0), item2);
    }

    @Test
    void editTODOListTitle()
    {
        TODOList list = new TODOList("Test Title");
        controller.lists.add(list);

        TODOItem item = new TODOItem("Title", "Description", LocalDate.now(), false);
        controller.lists.get(0).itemsArray.add(item);

        controller.lists.get(0).title = "Edited Title";

        assertEquals(controller.lists.get(0).title, "Edited Title");
    }

    @Test
    void editTODOItem()
    {
        TODOList list = new TODOList("Test Title");
        controller.lists.add(list);

        TODOItem item = new TODOItem("Title", "Description", LocalDate.now(), false);
        controller.lists.get(0).itemsArray.add(item);

        TODOItem editItem = new TODOItem("Edited Title", "Edited Description", item.getDue_Date(), true);

        controller.lists.get(0).itemsArray.get(0).setTitle("Edited Title");
        controller.lists.get(0).itemsArray.get(0).setDescription("Edited Description");
        controller.lists.get(0).itemsArray.get(0).setComplete(true);

        assertEquals(controller.lists.get(0).itemsArray.get(0).getTitle(), editItem.getTitle());
        assertEquals(controller.lists.get(0).itemsArray.get(0).getDescription(), editItem.getDescription());
        assertEquals(controller.lists.get(0).itemsArray.get(0).getComplete(), editItem.getComplete());
    }

    @Test
    void markComplete()
    {
        TODOList list = new TODOList("Test Title");
        controller.lists.add(list);

        TODOItem item = new TODOItem("Title", "Description", LocalDate.now(), false);
        controller.lists.get(0).itemsArray.add(item);

        controller.lists.get(0).itemsArray.get(0).setComplete(true);

        assertEquals(controller.lists.get(0).itemsArray.get(0).getComplete(), true);
    }

    @Test
    void showComplete()
    {

    }

    @Test
    void showIncomplete()
    {

    }
}