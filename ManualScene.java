package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class ManualScene {
	private Label numOfCoinsLb, errorLb, insertCoins;
	private TextField numOfCoinsTf, insertedCoins;
	private Button nextBt, backBt;
	private int[] coins;

	public Scene createScene(Stage primaryStage, OptimalGameInterface mainGameScene) {
		// Header Label
		Label headLb = new Label("Manual Coin Insertion");
		headLb.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

		// Error Label
		errorLb = new Label();
		errorLb.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		// Insert Coins Label
		insertCoins = new Label("Insert the coins (e.g., 1,2,3):");
		insertCoins.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF;");

		// Inserted Coins TextField
		insertedCoins = new TextField();
		insertedCoins.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5px;");

		// Number of Coins Field Styling
		numOfCoinsLb = new Label("Enter Number of Coins (Even Integer):");
		numOfCoinsLb.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF;");
		numOfCoinsTf = new TextField();
		numOfCoinsTf.setPrefColumnCount(3);
		numOfCoinsTf.setPromptText("Even #");
		numOfCoinsTf.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5px;");

		// Validation for Inserted Coins and the number of coins
		setValidatedCommaSeparated(insertedCoins, numOfCoinsTf);
		setEvenIntegerOnly(numOfCoinsTf);

		// Next Button Styling (initially disabled)
		nextBt = createStyledButton("Next");
		nextBt.setDisable(true);

		nextBt.setOnAction(e -> {
		    validateInput(); // Trigger validation on button click
		    if (errorLb.getText().isEmpty()) { // Proceed if there are no errors
		        PlayingWayScene playingWayScene = new PlayingWayScene(coins); // Pass coins to next scene
		        primaryStage.setScene(playingWayScene.createScene(primaryStage, mainGameScene));
		    }
		});

		// Back Button Styling
		backBt = createStyledButton("Back");
		backBt.setOnAction(e -> primaryStage.setScene(mainGameScene.mainScene()));

		// VBox Layout
		VBox vBox = new VBox(15, headLb, numOfCoinsLb, numOfCoinsTf, insertCoins, insertedCoins, errorLb, nextBt,
				backBt);
		vBox.setPadding(new Insets(20));
		vBox.setAlignment(Pos.CENTER);
		vBox.setStyle("-fx-background-color: #2F4F4F;");

		return new Scene(vBox, 500, 400);
	}
	private void setValidatedCommaSeparated(TextField insertedCoins, TextField numOfCoinsTf) {
	    insertedCoins.setTextFormatter(new TextFormatter<>(change -> {
	        String newText = change.getControlNewText();
	        if (newText.isEmpty()) {
	            errorLb.setText("");
	            nextBt.setDisable(true); // Disable next button if field is empty
	            return change;
	        }
	        // Match numbers separated by commas but not requiring a trailing comma
	        if (newText.matches("(\\d+,)*\\d*")) {
	            errorLb.setText("");
	            enableNextButtonIfValid(); // Check if both fields are valid
	            return change;
	        } else {
	            errorLb.setText("Invalid format: Use only numbers separated by commas.");
	            nextBt.setDisable(true); // Disable next button if format is incorrect
	            return null; // Reject change if it doesn't match the allowed pattern
	        }
	    }));
	}

	private void validateInput() {
	    String text = insertedCoins.getText();

	    // Remove any trailing comma before validation
	    if (text.endsWith(",")) {
	        text = text.substring(0, text.length() - 1);
	    }

	    if (numOfCoinsTf.getText().isEmpty() || text.isEmpty()) { // Check if fields are filled
	        errorLb.setText("Please fill all fields.");
	        nextBt.setDisable(true); // Disable Next button if fields are empty
	        return;
	    }

	    String[] coinsArray = text.split(","); // Split the text by commas
	    try {
	        int expectedCoins = Integer.parseInt(numOfCoinsTf.getText()); // Parse the expected coin count

	        // Check if the number of coins matches the entered count
	        if (coinsArray.length != expectedCoins) {
	            errorLb.setText("Please enter exactly " + expectedCoins + " coins.");
	            nextBt.setDisable(true); // Disable Next button if count is incorrect
	            return;
	        }

	        // Parse the coins into an integer array
	        coins = new int[coinsArray.length];
	        for (int i = 0; i < coinsArray.length; i++) {
	            coins[i] = Integer.parseInt(coinsArray[i].trim()); // Parse each coin as an integer
	        }

	        errorLb.setText(""); // Clear any previous error
	        nextBt.setDisable(false); // Enable Next button

	    } catch (NumberFormatException e) {
	        errorLb.setText("Please enter valid integers separated by commas.");
	        nextBt.setDisable(true); // Disable Next button if there is a parsing error
	    }
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
		                errorLb.setText("Number of coins should be even!");
		            }
		        } catch (NumberFormatException e) {
		            textField.setText(""); // Clear if parsing fails
		            textField.setPromptText("Enter an even number");
		        }
		    }
		}

	private void enableNextButtonIfValid() {
		if (!numOfCoinsTf.getText().isEmpty() && !insertedCoins.getText().isEmpty() && errorLb.getText().isEmpty()) {
			validateInput();
		} else {
			nextBt.setDisable(true);
		}
	}

	private Button createStyledButton(String text) {
		Button button = new Button(text);
		button.setStyle(
				"-fx-background-color: #FFD700; -fx-text-fill: #000; -fx-font-size: 16px; -fx-font-weight: bold; "
						+ "-fx-border-color: #FFA500; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; "
						+ "-fx-padding: 10px 20px;");

		button.setOnMouseEntered(
				e -> button.setStyle("-fx-background-color: #FFB700; -fx-text-fill: #000; -fx-font-size: 16px; "
						+ "-fx-font-weight: bold; -fx-border-color: #FFA500; -fx-border-width: 2px; -fx-border-radius: 10px; "
						+ "-fx-background-radius: 10px; -fx-padding: 10px 20px;"));
		button.setOnMouseExited(
				e -> button.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #000; -fx-font-size: 16px; "
						+ "-fx-font-weight: bold; -fx-border-color: #FFA500; -fx-border-width: 2px; -fx-border-radius: 10px; "
						+ "-fx-background-radius: 10px; -fx-padding: 10px 20px;"));

		return button;
	}
}
