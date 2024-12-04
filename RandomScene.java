package application;

import java.util.Random;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class RandomScene {
	private Label rangeLb, numOfCoinsLb, errorLabel;
	private TextField rangeFromTf, rangeToTf, numOfCoinsTf, generatedNumbersTf;
	private Button okBt, backBt, nextBt;
	private int[] generatedNumbers; // Array to store generated numbers

	public Scene createScene(Stage primaryStage, OptimalGameInterface mainGameScene) {
		// Header Label
		Label headLb = new Label("Random Coin Generation");
		headLb.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

		// Range Text Fields Styling
		rangeLb = new Label("Enter the Range for Coin Numbers:");
		rangeLb.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF;");

		rangeFromTf = new TextField();
		rangeToTf = new TextField();
		rangeFromTf.setPrefColumnCount(3);
		rangeToTf.setPrefColumnCount(3);
		rangeFromTf.setPromptText("min");
		rangeToTf.setPromptText("max");

		rangeFromTf.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5px;");
		rangeToTf.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5px;");

		// Apply integer-only restriction for range fields
		setIntegerOnly(rangeFromTf);
		setIntegerOnly(rangeToTf);

		// Error Label
		errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		// Number of Coins Field Styling
		numOfCoinsLb = new Label("Enter Number of Coins (Even Integer):");
		numOfCoinsLb.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF;");
		numOfCoinsTf = new TextField();
		numOfCoinsTf.setPrefColumnCount(3);
		numOfCoinsTf.setPromptText("Even #");
		numOfCoinsTf.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5px;");

		// Apply even-integer-only restriction for number of coins field
		setEvenIntegerOnly(numOfCoinsTf);

		// TextField to display generated numbers
		generatedNumbersTf = new TextField();
		generatedNumbersTf.setPromptText("Generated numbers will appear here");
		generatedNumbersTf.setEditable(false);
		generatedNumbersTf
				.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");

		// OK Button Styling
		okBt = new Button("Generate");
		okBt.setStyle(
				"-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px;");
		okBt.setOnAction(e -> generateRandomNumbers()); // Generate numbers on OK button click

		// Next Button (Initially Disabled)
		nextBt = new Button("Next");
		nextBt.setDisable(true); // Disable until random numbers are generated successfully
		nextBt.setStyle(
				"-fx-background-color: #1E90FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px;");
		nextBt.setOnAction(e -> mainGameScene.showPlayMode(generatedNumbers));
		// Back Button Styling
		backBt = new Button("Back");
		backBt.setStyle(
				"-fx-background-color: #FF6347; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px;");
		backBt.setOnAction(e -> primaryStage.setScene(mainGameScene.mainScene()));
		// HBox for Range Input
		HBox rangeHbox = new HBox(10, rangeFromTf, new Label("To"), rangeToTf);
		rangeHbox.setAlignment(Pos.CENTER);

		// GridPane Layout for Inputs
		GridPane inputGrid = new GridPane();
		inputGrid.setAlignment(Pos.CENTER);
		inputGrid.setVgap(15);
		inputGrid.setHgap(10);
		inputGrid.add(rangeLb, 0, 0);
		inputGrid.add(rangeHbox, 1, 0);
		inputGrid.add(numOfCoinsLb, 0, 1);
		inputGrid.add(numOfCoinsTf, 1, 1);
		inputGrid.add(errorLabel, 1, 3);

		// HBox for Buttons
		HBox actionButtons = new HBox(20, okBt, nextBt, backBt);
		actionButtons.setAlignment(Pos.CENTER);
		actionButtons.setPadding(new Insets(20));

		// Validate `rangeToTf` in real-time when the text changes
		rangeToTf.textProperty().addListener((observable, oldValue, newValue) -> {
			validateRange();
		});

		// OK Button Action - Check if all fields are filled and valid
		okBt.setOnAction(e -> {
		    if (checkFilledFields() && validateRange()) {
		        generateRandomNumbers();
		        clearHighlight(rangeFromTf, rangeToTf, numOfCoinsTf);
		        errorLabel.setText("");
		    } else {
		        highlightEmptyFields(rangeFromTf, rangeToTf, numOfCoinsTf);
		        generatedNumbersTf.setText("");
		        nextBt.setDisable(true); // Disable Next button if validation fails
		    }
		});

		// VBox Main Layout
		VBox mainLayout = new VBox(20, headLb, inputGrid, generatedNumbersTf, actionButtons);
		mainLayout.setAlignment(Pos.CENTER);
		mainLayout.setPadding(new Insets(20));
		mainLayout.setStyle("-fx-background-color: #2F4F4F;");

		return new Scene(mainLayout, 600, 400);
	}

	// Method to restrict input to integers only
	private void setIntegerOnly(TextField textField) {
		textField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null,
				change -> change.getControlNewText().matches("\\d*") ? change : null));
	}

	// Method to restrict input to even integers only, and greater than 0
	private void setEvenIntegerOnly(TextField textField) {
	    // Allow any integer input while typing
	    textField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, change -> {
	        String newText = change.getControlNewText();
	        return newText.matches("\\d*") ? change : null;  // Allow only digits
	    }));

	    // Validate the final input when focus is lost or Enter is pressed
	    textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
	        if (!newValue) { // Focus lost
	            validateEvenNumber(textField);
	        }
	    });

	    textField.setOnAction(event -> validateEvenNumber(textField)); // Press Enter
	}

	private void validateEvenNumber(TextField textField) {
	    String text = textField.getText();
	    if (!text.isEmpty()) {
	        try {
	            int number = Integer.parseInt(text);
	            // Check if the final input is a positive even number
	            if (number <= 0 || number % 2 != 0) {
	                textField.setText(""); // Clear if invalid
	                textField.setPromptText("Enter an even number");
	                errorLabel.setText("Number of coins should be even!");
	            }
	        } catch (NumberFormatException e) {
	            textField.setText(""); // Clear if parsing fails
	            textField.setPromptText("Enter an even number");
	        }
	    }
	}


	// Validate that rangeToTf is greater than rangeFromTf
	private boolean validateRange() {
	    try {
	        int fromValue = Integer.parseInt(rangeFromTf.getText());
	        int toValue = Integer.parseInt(rangeToTf.getText());

	        if (toValue <= fromValue) {
	            errorLabel.setText("Max value must be greater than Min value.");
	            rangeToTf.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red;");
	            nextBt.setDisable(true); // Disable Next button if range is invalid
	            return false;
	        } else {
	            errorLabel.setText(""); // Clear error message if valid
	            clearHighlight(rangeToTf);
	            return true;
	        }
	    } catch (NumberFormatException e) {
	        errorLabel.setText("Please enter valid integers.");
	        rangeToTf.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red;");
	        nextBt.setDisable(true); // Disable Next button if input is not an integer
	        return false;
	    }
	}
	// Generate random numbers within the specified range and update the TextField
	private void generateRandomNumbers() {
	    generatedNumbersTf.clear(); // Clear previous generated numbers each time

	    if (checkFilledFields() && validateRange()) {
	        int min = Integer.parseInt(rangeFromTf.getText());
	        int max = Integer.parseInt(rangeToTf.getText());
	        int numOfCoins = Integer.parseInt(numOfCoinsTf.getText());

	        generatedNumbers = new int[numOfCoins];
	        Random random = new Random();
	        StringBuilder generatedText = new StringBuilder();

	        // Generate random numbers and format them into groups of 5 per line
	        for (int i = 0; i < numOfCoins; i++) {
	            generatedNumbers[i] = random.nextInt(max - min + 1) + min;
	            generatedText.append(generatedNumbers[i]);

	            // Add comma between numbers, except after the last number
	            if (i < numOfCoins - 1) {
	                generatedText.append(", ");
	            }
	        }

	        // Display numbers in TextField
	        generatedNumbersTf.setText(generatedText.toString());
	        errorLabel.setText(""); // Clear error
	        nextBt.setDisable(false); // Enable Next button if generation is successful
	    } else {
	        errorLabel.setText("Please fill all fields and ensure the range is valid.");
	        nextBt.setDisable(true); // Disable Next button if there is an error
	    }
	}
	// Method to highlight empty fields
	private void highlightEmptyFields(TextField rangeFromTf, TextField rangeToTf, TextField numOfCoinsTf) {
		if (rangeFromTf.getText().isEmpty()) {
			rangeFromTf.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-text-fill: black;");
		}
		if (rangeToTf.getText().isEmpty()) {
			rangeToTf.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-text-fill: black;");
		}
		if (numOfCoinsTf.getText().isEmpty()) {
			numOfCoinsTf.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red; -fx-text-fill: black;");
		}
	}

	// Clear highlighting on fields
	private void clearHighlight(TextField rangeFromTf, TextField rangeToTf, TextField numOfCoinsTf) {
		rangeFromTf.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5px;");
		rangeToTf.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5px;");
		numOfCoinsTf.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5px;");
	}

	// Method to check if all fields are filled
	private boolean checkFilledFields() {
		return !rangeFromTf.getText().isEmpty() && !rangeToTf.getText().isEmpty() && !numOfCoinsTf.getText().isEmpty();
	}

	// method to handle single TextField as parameter
	private void clearHighlight(TextField textField) {
		textField.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5px;");
	}
}
