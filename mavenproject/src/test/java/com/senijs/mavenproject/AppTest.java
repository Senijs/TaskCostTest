package com.senijs.mavenproject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class AppTest {
    private GUI gui;

    @Before
    public void setUp() {
        // Initialize GUI and set up any necessary resources
        gui = new GUI();
    }

    @After
    public void tearDown() {
        // Clean up and close any resources used by the GUI
        try {
            Connection conn = gui.createConnection();
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateConnection() {
        // Test that a valid database connection is created
        Connection conn = gui.createConnection();
        assertNotNull("Connection should not be null", conn);
    }

    @Test
    public void testCreateStatement() {
        // Test that a valid statement is created
        Statement stmt = gui.createStatement();
        assertNotNull("Statement should not be null", stmt);
    }

    @Test
    public void testTaskExists() throws SQLException {
        // Assuming there is a task in database with ID 1
        int existingTaskID = 1;
        assertTrue("Task should exist", gui.taskExists(existingTaskID));

        // Assuming there is no task with ID 1000
        int nonExistingTaskID = 1000;
        assertFalse("Task should not exist", gui.taskExists(nonExistingTaskID));
    } 

    @Test
    public void testRetrieveNewOperationID() {
        try {
            int newOperationID = gui.retrieveNewOperationID();

            // Verify that the new operation ID is greater than zero
            assertTrue(newOperationID > 0);
        } catch (Exception e) {
            fail("Exception thrown while retrieving new operation ID: " + e.getMessage());
        }
    }

}
