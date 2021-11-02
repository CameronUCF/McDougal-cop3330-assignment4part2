package ucf.assignments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 *  UCF COP3330 Fall 2021 Assignment 4 Solution
 *  Copyright 2021 Cameron McDougal
 */

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
        controller.CreateTODOList();
    }

    @Test
    void removeTODOList()
    {

    }

    @Test
    void createTODOItem()
    {

    }

    @Test
    void removeTODOItem()
    {

    }

    @Test
    void editTODOListTitle()
    {

    }

    @Test
    void editTODOItem()
    {

    }

    @Test
    void markComplete()
    {

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