package com.senijs.mavenproject;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import com.toedter.calendar.JDateChooser;




public class GUI {
    public static final String URL = "jdbc:hsqldb:hsql://localhost/";
    public final Connection conn;

    public GUI() {
        this.conn = createConnection();

        // A shutdown hook to close the connection when the user exits the program 
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));

        JFrame frame = new JFrame("Tvestor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1800, 1000);
        frame.setLayout(new GridLayout(2, 3)); // 2x3 grid layout for panels

        frame.add(createTaskPanel());
        frame.add(createOperationPanel());
        frame.add(createCompletionPanel());
        frame.add(createStatusPanel());
        frame.add(dateRangePickerPanel("finished"));
        frame.add(dateRangePickerPanel("cost"));
        frame.setVisible(true);
    }



    
    Connection createConnection() {
        try {
        	return DriverManager.getConnection(URL, "SA", "");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public class Components {
    	JPanel panel;
        JTextField fieldTaskID;
        JTextField fieldOperationID;
        JTextField fieldDescription;
        JTextField fieldEstimatedQuantity;
        JTextField fieldActualQuantity;
        JTextField fieldCost;
        JTextField fieldPrice;
        JLabel labelOutput;
    }
    
    public JPanel createTaskPanel() {
        JPanel newTaskPanel = new JPanel(new GridBagLayout());
        newTaskPanel.setBorder(BorderFactory.createEtchedBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel newTaskLabel = new JLabel("Add new task's description");
        newTaskPanel.add(newTaskLabel, gbc);
        
        JLabel newTaskLabelOutput = new JLabel();
        gbc.gridy++;
        newTaskPanel.add(newTaskLabelOutput, gbc);

        JTextField newTaskField = new JTextField(20);
        gbc.gridy++;
        newTaskPanel.add(newTaskField, gbc);

        JButton button = new JButton("New Task");
        gbc.gridy++;
        newTaskPanel.add(button, gbc);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {        
                	newTaskHandler(newTaskField, newTaskLabelOutput, newTaskPanel);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(newTaskPanel, "An error occurred while adding the task.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return newTaskPanel;
    }
    
    public void newTaskHandler(JTextField newTaskField, JLabel newTaskLabelOutput, JPanel newTaskPanel) throws SQLException{
    	System.out.println(newTaskField.getText());
    	String description = newTaskField.getText().trim();
    	
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(newTaskPanel, "Please enter a Description!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method without executing the query
        }
        
        String query = "INSERT INTO \"PUBLIC\".\"Task\" (\"DESCRIPTION\", \"COST\", \"STATUS\", \"FINISHED\") "
				+ " VALUES ('" + description + "', 0.0, 'Project', LOCALTIME);"; //note set as parameter using PreparedStatement

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
        	preparedStatement.executeUpdate();
        }
        
        query = "SELECT TASK_ID " +
			    		"FROM \"PUBLIC\".\"Task\" " +
			    		"ORDER BY TASK_ID DESC " +
			    		"LIMIT 1";
        
        //Select the latest Task ID and output it to user
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)){
        	try (ResultSet resultSet = preparedStatement.executeQuery()){
				int newTaskID = -1; // Default value if no record is found
				if (resultSet.next()) {
				    newTaskID = resultSet.getInt("TASK_ID");
				}
				newTaskLabelOutput.setText("New task ID: " + newTaskID);
        	}
        }
		

    }
    

    public JPanel createOperationPanel() {
    	Components operationComponents = new Components();
        operationComponents.panel = new JPanel(new GridBagLayout());
        operationComponents.panel.setBorder(BorderFactory.createEtchedBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        
        JLabel newOperationLabelTaskID = new JLabel("Input task's ID");
        operationComponents.panel.add(newOperationLabelTaskID, gbc);

        operationComponents.fieldTaskID = new JTextField(20);
        gbc.gridy++;
        operationComponents.panel.add(operationComponents.fieldTaskID, gbc);

        
        JLabel newOperationLabelDescription = new JLabel("Enter operation's description");
        gbc.gridy++;
        operationComponents.panel.add(newOperationLabelDescription, gbc);

        operationComponents.fieldDescription = new JTextField(20);
        gbc.gridy++;
        operationComponents.panel.add(operationComponents.fieldDescription, gbc);

        
        JLabel newOperationLabelEstimatedQuantity = new JLabel("Enter estimated operation quantity");
        gbc.gridy++;
        operationComponents.panel.add(newOperationLabelEstimatedQuantity, gbc);

        operationComponents.fieldEstimatedQuantity = new JTextField(20);
        gbc.gridy++;
        operationComponents.panel.add(operationComponents.fieldEstimatedQuantity, gbc);

        
        JLabel newOperationLabelPrice = new JLabel("Enter operation's price");
        gbc.gridy++;
        operationComponents.panel.add(newOperationLabelPrice, gbc);

        operationComponents.fieldPrice = new JTextField(20);
        gbc.gridy++;
        operationComponents.panel.add(operationComponents.fieldPrice, gbc);

        
        JButton newOperationButton = new JButton("New Operation");
        gbc.gridy++;
        operationComponents.panel.add(newOperationButton, gbc);
        
        
        operationComponents.labelOutput = new JLabel ();
        gbc.gridy++;
        operationComponents.panel.add(operationComponents.labelOutput, gbc);

        newOperationButton.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                try {
                    newOperationHandler(operationComponents);
                } catch (Exception e1) {
                    e1.printStackTrace();
//                    JOptionPane.showMessageDialog(AoperationComponents.panel, "An error occurred while adding the operation.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }  
        });

        return operationComponents.panel;
    }

    
    public void newOperationHandler(Components operationComponents) throws SQLException {
        // Get input values from components
        String taskIDText = operationComponents.fieldTaskID.getText().trim();
        String description = operationComponents.fieldDescription.getText().trim();
        String priceText = operationComponents.fieldPrice.getText().trim();
        String estimatedQuantityText = operationComponents.fieldEstimatedQuantity.getText().trim();

        // Validate and parse inputs
        int taskID = parseIntegerInput(taskIDText, "Task ID", operationComponents);
        if (isNullOrWhitespace(description)) {
            showMessageDialog("Please enter a Description!", "Input Error", operationComponents);
            throw new IllegalArgumentException("Invalid Description");
        }
        double price = parseDoubleInput(priceText, "Price", operationComponents);
        int estimatedQuantity = parseIntegerInput(estimatedQuantityText, "Estimated Quantity", operationComponents);

        if (!taskExists(taskID)) {
            showMessageDialog("Task with ID " + taskID + " does not exist!", "Task Not Found", operationComponents);
            return;
        }

        // Create new operation record
        insertNewOperation(taskID, description, estimatedQuantity, price);

        // Calculate combined cost and determine new status for the Task table
        double combinedCost = calculateCombinedCost(taskID);
        String newStatus = calculateNewStatus(taskID);

        // Update the Task table
        updateTaskTable(taskID, combinedCost, newStatus);

        // Display the new Operation ID to the user
        int newOperationID = retrieveNewOperationID();
        operationComponents.labelOutput.setText("New operation ID: " + newOperationID);
    }


    boolean taskExists(int taskID) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM \"PUBLIC\".\"Task\" WHERE TASK_ID = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, taskID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0; // If count is greater than 0, the task exists
            }
        }
        return false; // Default to false if an error occurs or no result is found
    }
    
    public boolean isNullOrWhitespace(String input) {
        return input == null || input.trim().isEmpty();
    }

    int parseIntegerInput(String input, String fieldName, Components operationComponents) {
        if (isNullOrWhitespace(input)) {
            showMessageDialog("Please enter " + fieldName + "!", "Input Error", operationComponents);
            throw new IllegalArgumentException("Invalid " + fieldName);
        }
        return Integer.parseInt(input);
    }

    double parseDoubleInput(String input, String fieldName, Components operationComponents) {
        if (isNullOrWhitespace(input)) {
            showMessageDialog("Please enter " + fieldName + "!", "Input Error", operationComponents);
            throw new IllegalArgumentException("Invalid " + fieldName);
        }
        return Double.parseDouble(input);
    }


    public void showMessageDialog(String message, String title, Components operationComponents) {
        JOptionPane.showMessageDialog(operationComponents.panel, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void insertNewOperation(int taskID, String description, int estimatedQuantity, double price) throws SQLException {
        String query = "INSERT INTO \"PUBLIC\".\"Operation\" (\"TASK_ID\", \"DESCRIPTION\", \"COST\", \"ESTIMATED_QUANTITY\", \"PRICE\", \"STATUS\")"
                + " VALUES (?, ?, ?, ?, ?, 'Project')";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, taskID);
            statement.setString(2, description);
            statement.setDouble(3, price*estimatedQuantity); //cost=price*estimatedQuantity for newly created operations
            statement.setInt(4, estimatedQuantity);
            statement.setDouble(5, price);
            statement.executeUpdate();
        }
    }

    public double calculateCombinedCost(int taskID) throws SQLException {
        String query = "SELECT SUM(COST) AS COMBINED_COST FROM \"PUBLIC\".\"Operation\" WHERE TASK_ID = ?"; //note "select sum(cost) from operation where task_id = ?"
        double combinedCost = 0.0;

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, taskID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                combinedCost = resultSet.getDouble("COMBINED_COST"); // Retrieve the combined cost from the result set
            }
        }

        return combinedCost;
    }

    public class TaskStatus {
        public static final String PROJECT = "Project";
        public static final String FINISHED = "Finished";
        public static final String IN_PROGRESS = "In-Progress";
    }

    public String calculateNewStatus(int taskID) throws SQLException {
        String newStatus = TaskStatus.PROJECT; //note enum or constant
        
        String query = "SELECT STATUS FROM \"PUBLIC\".\"Operation\" WHERE TASK_ID = " + taskID;
        try (PreparedStatement statement = conn.prepareStatement(query)) {
	        try (ResultSet resultSet= statement.executeQuery()) {
	            boolean hasProjectStatus = false;
	            boolean hasFinishedStatus = false;
	
	            while (resultSet.next()) {
	                String status = resultSet.getString("STATUS");
	                if (TaskStatus.PROJECT.equalsIgnoreCase(status)) { //note enum or constant
	                    hasProjectStatus = true;
	                } else if (TaskStatus.FINISHED.equalsIgnoreCase(status)) { //note enum or constant
	                    hasFinishedStatus = true;
	                }
	            }
	
	            if (hasProjectStatus && hasFinishedStatus) {
	                newStatus = TaskStatus.IN_PROGRESS; //note enum or constant
	            } else if (hasProjectStatus) {
	                newStatus = TaskStatus.PROJECT; //note enum or constant
	            } else if (hasFinishedStatus) {
	                newStatus = TaskStatus.FINISHED; //note enum or constant
	            }
	
	            System.out.println("Task status updated successfully: " + newStatus);
	        }
        } catch (SQLException e) {
            System.err.println("Error while calculating new status: " + e.getMessage());
        }

        return newStatus;
    }



    public void updateTaskTable(int taskID, double combinedCost, String newStatus) throws SQLException {
        String query = "UPDATE \"PUBLIC\".\"Task\" SET COST = ?, STATUS = ? WHERE TASK_ID = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setDouble(1, combinedCost);
            statement.setString(2, newStatus);
            statement.setInt(3, taskID);
            statement.executeUpdate();
        }
    }

    public int retrieveNewOperationID() throws SQLException {
        String query = "SELECT OPERATION_ID FROM \"PUBLIC\".\"Operation\" ORDER BY OPERATION_ID DESC LIMIT 1";
        try (Statement statement = conn.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {//note try-with-resource
            	int newOperationID = -1;

                if (resultSet.next()) {
                    newOperationID = resultSet.getInt("OPERATION_ID");
                }
                return newOperationID;
            }

        }
    }

    public JPanel createCompletionPanel() {
    	Components completionComponents = new Components();
    	completionComponents.panel = new JPanel(new GridBagLayout());
    	completionComponents.panel.setBorder(BorderFactory.createEtchedBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel completionOperationIDLabel = new JLabel("Enter Operation's ID");
        completionComponents.panel.add(completionOperationIDLabel, gbc);

        completionComponents.fieldOperationID = new JTextField(20);
        gbc.gridy++;
        completionComponents.panel.add(completionComponents.fieldOperationID, gbc);
        
        JLabel completionActualQuantityLabel = new JLabel("Enter Operation's actual quantity");
        gbc.gridy++;
        completionComponents.panel.add(completionActualQuantityLabel, gbc);
        
        completionComponents.fieldActualQuantity = new JTextField(20);
        gbc.gridy++;
        completionComponents.panel.add(completionComponents.fieldActualQuantity, gbc);
        
        completionComponents.labelOutput = new JLabel();
        gbc.gridy++;
        completionComponents.panel.add(completionComponents.labelOutput, gbc);

        JButton button = new JButton("Mark Operation as Completed");
        gbc.gridy++;
        completionComponents.panel.add(button, gbc);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {        
                	completionHandler(completionComponents);
                	completionComponents.labelOutput.setText("Operation's status updated successfully!");
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(completionComponents.panel, "An error occurred while marking operation as completed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return completionComponents.panel;
    }
    
    
    public void completionHandler(Components completionComponents) throws SQLException {
        String operationIDText = completionComponents.fieldOperationID.getText().trim();
        String actualQuantityText = completionComponents.fieldActualQuantity.getText().trim();
        
        // Validate and parse inputs
        int operationID = parseIntegerInput(operationIDText, "Operation ID", completionComponents);
        int actualQuantity = parseIntegerInput(actualQuantityText, "Actual Quantity", completionComponents);
        int taskID = 0;
        double operationPrice = 0.0;
        
        String query = "SELECT * FROM \"PUBLIC\".\"Operation\" "
                	  + "WHERE OPERATION_ID = " + operationID;
        try (PreparedStatement statement = conn.prepareStatement(query)){ //note set as parameter using PreparedStatement
	        try (ResultSet resultSet = statement.executeQuery()){ //note try-with-resource
	            if (resultSet.next()) {
	            	taskID = resultSet.getInt("TASK_ID");
	            	operationPrice = resultSet.getDouble("PRICE");
	            	}
	        } 
        }

		query = "UPDATE \"PUBLIC\".\"Operation\""
					+ " SET \"COST\" = ?, \"ACTUAL_QUANTITY\" = ?, \"STATUS\" = 'Finished'"
					+ " WHERE \"OPERATION_ID\" = ?";

		try (PreparedStatement statement = conn.prepareStatement(query)) {
		    statement.setDouble(1, actualQuantity * operationPrice);
		    statement.setInt(2, actualQuantity);
		    statement.setInt(3, operationID);
		    statement.executeUpdate();
		}
        

        // Update the Task table
        double combinedCost = calculateCombinedCost(taskID);
        String newStatus = calculateNewStatus(taskID);
        updateTaskTable(taskID, combinedCost, newStatus);

    }
    
    
    public JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());

        JButton showStatusButton = new JButton("Show Unfinished Tasks");
        statusPanel.add(showStatusButton, BorderLayout.NORTH);

        JTextArea statusTextArea = new JTextArea(20, 40);
        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        statusPanel.add(scrollPane, BorderLayout.CENTER);

        showStatusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String statusToFilter = TaskStatus.PROJECT;//note ?
                    List<String> records = fetchRecordsByStatus(statusToFilter);
                    statusTextArea.setText(String.join("\n", records));
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return statusPanel;
    }

    public List<String> fetchRecordsByStatus(String status) throws SQLException {
        List<String> records = new ArrayList<>();

        String query = "SELECT * FROM \"PUBLIC\".\"Task\" WHERE \"STATUS\" = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, status);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String record = "ID: " + resultSet.getInt("TASK_ID") + 
                				", Description: " + resultSet.getString("DESCRIPTION") +
                				", Cost: " + resultSet.getDouble("COST") +
                				", Status: " + resultSet.getString("STATUS");
                records.add(record);
            }
        }

        return records;
    }
    
    
        

    public JPanel dateRangePickerPanel(String finishedOrCost) {
    	JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());
        mainPanel.setBorder(BorderFactory.createEtchedBorder());

        // Create "FROM" date picker
        JDateChooser fromDateChooser = new JDateChooser();
        fromDateChooser.setDateFormatString("yyyy-MM-dd");
        mainPanel.add(new JLabel("FROM Date:"));
        mainPanel.add(fromDateChooser);

        // Create "TO" date picker
        JDateChooser toDateChooser = new JDateChooser();
        toDateChooser.setDateFormatString("yyyy-MM-dd");
        mainPanel.add(new JLabel("TO Date:"));
        mainPanel.add(toDateChooser);

        // Create button to retrieve records
        JButton retrieveButton = new JButton("Retrieve " + finishedOrCost);
        mainPanel.add(retrieveButton);
        
        JTextArea statusTextArea = new JTextArea(20, 40);
        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add action listener to the button
        retrieveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
                Date fromDate = fromDateChooser.getDate();
                Date toDate = toDateChooser.getDate();

	                if (fromDate != null && toDate != null) {
	                    // Retrieve records based on the date range
	                    List <String> records = retrieveRecordsFromDatabase(fromDate, toDate, finishedOrCost);
	                    statusTextArea.setText(String.join("\n", records));
	                } else {
	                    JOptionPane.showMessageDialog(null, "Please select both FROM and TO dates.");
	                }
	                
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "An error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return mainPanel;
    }
        
            
            

    public List <String> retrieveRecordsFromDatabase(Date fromDate, Date toDate, String finishedOrCost) {
        List<String> records = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateString = dateFormat.format(fromDate);
        String toDateString = dateFormat.format(toDate);

        try {

        	switch (finishedOrCost){
        		case "finished":{
        			String query = "SELECT * FROM \"PUBLIC\".\"Task\" WHERE \"STATUS\" = ? AND \"FINISHED\" BETWEEN ? AND ?";
        			try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
        			    preparedStatement.setString(1, TaskStatus.FINISHED);
        			    preparedStatement.setString(2, fromDateString);
        			    preparedStatement.setString(3, toDateString);
        			    
        			    try (ResultSet resultSet = preparedStatement.executeQuery()) { // Nested try-with-resources for ResultSet
        			        while (resultSet.next()) {
        			            String record = "ID: " + resultSet.getInt("TASK_ID") + 
        			                            ", Description: " + resultSet.getString("DESCRIPTION") +
        			                            ", Cost: " + resultSet.getDouble("COST") +
        			                            ", Status: " + resultSet.getString("STATUS");
        			            records.add(record);
        			        }
        			    } // ResultSet is automatically closed here
        			} catch (SQLException e) {
        			    // Handle any SQLException if it occurs
        			    System.err.println("Error while executing the query: " + e.getMessage());
        			}
        		}
        		break;
        		case "cost":{
        			String query = "SELECT * FROM \"PUBLIC\".\"Task\" WHERE \"STATUS\" = ? AND \"FINISHED\" BETWEEN ? AND ?";
                    try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                        preparedStatement.setString(1, TaskStatus.PROJECT);
                    	preparedStatement.setString(2, fromDateString);
                    	preparedStatement.setString(3, toDateString);
                        try (ResultSet resultSet = preparedStatement.executeQuery()){ //note try-with-resource
                            double cost = 0.0;
                            while (resultSet.next()) {
                            	cost += resultSet.getDouble("COST");
                            }
                            
                            records.add("Cost of all tasks from this period = " + Double.toString(cost));
                        }
                    } 
        		}
        		break;
        	}

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
		return records;
    }
            
}





