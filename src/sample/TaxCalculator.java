package sample;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class TaxCalculator extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tax Calculator");

        // Create a bold heading label
        Label headingLabel = new Label("Tax Calculator");
        headingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 50));

        // Create UI components
        Label nameLabel = new Label("What is your name");
        TextField nameTextField = new TextField();

        Label yearLabel = new Label("Which tax year would you like to calculate?");
        ComboBox<String> yearComboBox = new ComboBox<>();
        yearComboBox.getItems().addAll("2024", "2023", "2022");
        yearComboBox.setPromptText("Year");

        Label salaryLabel = new Label("What is your total salary before deductions?");
        TextField salaryTextField = new TextField();

        Label ageLabel = new Label("Age");
        TextField ageTextField = new TextField();

        Button calculateButton = new Button("Calculate");

        // Set up layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));  
        grid.setVgap(10);
        grid.setHgap(10);

        // Add the heading label to the top of the grid and center it
        grid.add(headingLabel, 0, 0, 2, 1); 
        GridPane.setHalignment(headingLabel, HPos.CENTER); 

        // Add space (empty row) between heading label and other components
        grid.add(new Label(), 0, 1);  // Empty label to add space

        // Add the other components starting from row index 2
        grid.add(nameLabel, 0, 2);
        grid.add(nameTextField, 1, 2);

        grid.add(yearLabel, 0, 3);
        grid.add(yearComboBox, 1, 3);

        grid.add(salaryLabel, 0, 4);
        grid.add(salaryTextField, 1, 4);

        grid.add(ageLabel, 0, 5);
        grid.add(ageTextField, 1, 5);

        grid.add(calculateButton, 0, 6, 2, 1); 
        GridPane.setHalignment(calculateButton, HPos.CENTER); 

        // Customize font and size for labels and buttons
        Font boldFont = Font.font("Arial", FontWeight.BOLD, 16);

        nameLabel.setFont(boldFont);
        yearLabel.setFont(boldFont);
        salaryLabel.setFont(boldFont);
        ageLabel.setFont(boldFont);
        calculateButton.setFont(Font.font("Arial", FontWeight.BOLD, 14)); 

        // Set up button action
        calculateButton.setOnAction(e -> {
            // Extract values from input fields
            String name = nameTextField.getText();
            String year = yearComboBox.getValue();
            String salary = salaryTextField.getText();
            String age = ageTextField.getText();

            // Validate that none of the fields is empty
            if (name.isEmpty() || year == null || salary.isEmpty() || age.isEmpty()) {
                // Show a warning message
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Warning");
                warning.setHeaderText(null);
                warning.setContentText("Fields cannot be empty. Please fill in all the required fields.");
                warning.showAndWait();
                return; 
            }

            // Validate that salary is not negative
            try {
                double newSalary = Double.parseDouble(salary);
                if (newSalary < 0) {
                    // Show a warning message
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("Warning");
                    warning.setHeaderText(null);
                    warning.setContentText("Please enter a valid salary.");
                    warning.showAndWait();
                    return; // Exit the method to prevent further processing
                }
            } catch (NumberFormatException ex) {
                // Show a warning message for invalid salary format
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Warning");
                warning.setHeaderText(null);
                warning.setContentText("Invalid salary format. Please enter a valid numerical value for salary.");
                warning.showAndWait();
                return; // Exit the method to prevent further processing
            }

            
            // Validate age
            try {
                int newAge = Integer.parseInt(age);
                if (newAge < 18) {
                    // Show a warning message for age less than 18
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("Warning");
                    warning.setHeaderText(null);
                    warning.setContentText("Age cannot be less than 18. Please enter a valid age.");
                    warning.showAndWait();
                    return; // Exit the method to prevent further processing
                }
            } catch (NumberFormatException ex) {
                // Show a warning message for invalid age format
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Warning");
                warning.setHeaderText(null);
                warning.setContentText("Please enter a valid numerical value for age.");
                warning.showAndWait();
                return; // Exit the method to prevent further processing
            }


            // Perform the tax calculation (replace this with your actual calculation logic)
            double calculatedTax = performTaxCalculation(name, year, salary, age);

            // Show an information box with the calculated tax
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Calculation Result");
            alert.setHeaderText(null);
            // Set font for the information box text (inline style)
            alert.getDialogPane().setStyle("-fx-font-size: 16px;");
            alert.setContentText("Hi " + name + ", your PAYE tax deduction is R" + calculatedTax);
            alert.showAndWait();

            // Clear all input fields after calculation
            nameTextField.clear();
            yearComboBox.getSelectionModel().clearSelection();
            salaryTextField.clear();
            ageTextField.clear();
        });

        // Set up scene
        Scene scene = new Scene(grid, 545, 325);

        // Set the scene in the stage
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        // Show the stage
        primaryStage.show();
    }


    private double performTaxCalculation(String name, String year, String salary, String age) {
        double taxableIncome = Double.parseDouble(salary);
    
        // Get the tax rates, rebates, and thresholds based on the selected year
        double[] taxRates = getTaxRates(year);
        double[] rebates = getRebates(year);
        double[] thresholds = getThresholds(year);
    
        // Apply the tax calculation based on the tax brackets
        double tax = 0.0;
    
        for (int i = 0; i < taxRates.length; i++) {
            double lowerBound = thresholds[i];
            double upperBound = (i < thresholds.length - 1) ? thresholds[i + 1] : Double.MAX_VALUE;
            double rate = taxRates[i];
    
            if (taxableIncome <= upperBound) {
                tax += (taxableIncome - lowerBound) * rate / 100.0;
                break;
            } else {
                tax += (upperBound - lowerBound) * rate / 100.0;
            }
        }
    
        // Apply rebates based on age
        double rebate = 0.0;
        int ageInt = Integer.parseInt(age);
    
        if (ageInt < 65) {
            rebate = rebates[0];
        } else if (ageInt >= 65 && ageInt < 75) {
            rebate = rebates[1];
        } else {
            rebate = rebates[2];
        }
    
        // Calculate the final tax after applying rebates
        double finalTax = Math.max(0, tax - rebate);
    
        // Round off the final tax to two decimal places
        finalTax = Math.round(finalTax * 100.0) / 100.0;
    
        return finalTax;
    }
    
    
    private double[] getTaxRates(String year) {
        
        switch (year) {
            case "2024":
                return new double[]{18, 26, 31, 36, 39, 41, 45};
            case "2023":
                return new double[]{18, 26, 31, 36, 39, 41, 45};
            case "2022":
                return new double[]{18, 26, 31, 36, 39, 41, 45};
            default:
                throw new IllegalArgumentException("Invalid tax year: " + year);
        }
    }
    
    private double[] getRebates(String year) {
        
        switch (year) {
            case "2024":
                return new double[]{17235, 9444, 3145};
            case "2023":
                return new double[]{16425, 9000, 2997};
            case "2022":
                return new double[]{15714, 8613, 2871};
            default:
                throw new IllegalArgumentException("Invalid tax year: " + year);
        }
    }
    
    private double[] getThresholds(String year) {
        
        switch (year) {
            case "2024":
                return new double[]{0, 237100, 370500, 512800, 673000, 857900, 1817000};
            case "2023":
                return new double[]{0, 226000, 353100, 488700, 641400, 817600, 1731600};
            case "2022":
                return new double[]{0, 216200, 337800, 467500, 613600, 782200, 1656600};
            default:
                throw new IllegalArgumentException("Invalid tax year: " + year);
        }
    }
    
}



/*
Use the following command to compile the program.
-->  javac --module-path "C:/Users/Vuyolwethu.Mabhuleka/Downloads/openjfx-21.0.1_windows-x64_bin-sdk/javafx-sdk-21.0.1/lib" -d out src/module-info.java src/sample/TaxCalculator.java


Use the command below to launch the application
-->  java --module-path "C:/Users/Vuyolwethu.Mabhuleka/Downloads/openjfx-21.0.1_windows-x64_bin-sdk/javafx-sdk-21.0.1/lib" --add-modules javafx.controls,javafx.fxml -cp out sample.TaxCalculator


*/