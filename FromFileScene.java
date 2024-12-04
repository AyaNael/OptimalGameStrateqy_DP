package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FromFileScene {
    private Label numOfCoinsLb, errorLb;
    private TextField numOfCoinsTf, insertedCoinsTf;
    private Button loadFileBt, backBt, nextBt;
    private int[] coins;

    public Scene createScene(Stage primaryStage, OptimalGameInterface mainGameScene) {
        // Header Label
        Label headLb = new Label("Load Coins from File");
        headLb.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        // Error Label
        errorLb = new Label();
        errorLb.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Number of Coins Field
        numOfCoinsLb = new Label("Number of Coins (read from file):");
        numOfCoinsLb.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF;");
        numOfCoinsTf = new TextField();
        numOfCoinsTf.setEditable(false); // Set to read-only since it’s loaded from the file
        numOfCoinsTf.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5px;");

        // Inserted Coins Field
        insertedCoinsTf = new TextField();
        insertedCoinsTf.setPromptText("Coins loaded from file");
        insertedCoinsTf.setEditable(false); // Set to read-only
        insertedCoinsTf.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5px;");

        // Load File Button
        loadFileBt = createStyledButton("Load File");
        loadFileBt.setOnAction(e -> loadFile(primaryStage));

        // OK Button
        nextBt = createStyledButton("Next");
        nextBt.setOnAction(e -> validateInput());
        nextBt.setDisable(true); // Disable until uploaded file successfully
        nextBt.setOnAction(e -> mainGameScene.showPlayMode(coins));

        // Back Button
        backBt = createStyledButton("Back");
        backBt.setOnAction(e -> primaryStage.setScene(mainGameScene.mainScene()));

        // vBox Configuration
        VBox vBox = new VBox(15, headLb, numOfCoinsLb, numOfCoinsTf, insertedCoinsTf, loadFileBt, errorLb, nextBt,
                backBt);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: #2F4F4F;");

        return new Scene(vBox, 500, 400);
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

    private void loadFile(Stage stage) {
        // Clear TextFields before loading a new file
        numOfCoinsTf.clear();
        insertedCoinsTf.clear();
        errorLb.setText(""); // Clear any previous error message
        nextBt.setDisable(true);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Coins File");
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                // Read the first line for the number of coins
                String numOfCoinsLine = reader.readLine();
                if (numOfCoinsLine == null || !numOfCoinsLine.matches("\\d+") || Integer.parseInt(numOfCoinsLine) % 2 != 0) {
                    errorLb.setText("First line must be an even integer which is the number of coins.");
                    return;
                }

                int numOfCoins = Integer.parseInt(numOfCoinsLine);

                // Initialize the coins array based on the number of coins read
                coins = new int[numOfCoins];

                // Initialize StringBuilder to collect all coin values for display
                StringBuilder coinsBuilder = new StringBuilder();

                // Read each line for individual coin values and validate
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null && lineCount < numOfCoins) {
                    if (!line.matches("\\d+")) {
                        errorLb.setText("Invalid format: Each line should contain a number.");
                        return;
                    }

                    // Parse and store the coin value in the array
                    coins[lineCount] = Integer.parseInt(line);
                    coinsBuilder.append(line).append(",");

                    lineCount++;
                }

                // Check if the actual number of coins matches or exceeds the expected count
                if (lineCount < numOfCoins) {
                    errorLb.setText("The number of coins is less than the count specified in the first line.");
                    return;
                }

                // Remove the trailing comma and set TextFields only if all validations pass
                if (coinsBuilder.length() > 0) {
                    coinsBuilder.setLength(coinsBuilder.length() - 1); // Remove last comma
                }
                numOfCoinsTf.setText(numOfCoinsLine);
                insertedCoinsTf.setText(coinsBuilder.toString());

                errorLb.setText(""); // Clear error if the file is correctly loaded
                nextBt.setDisable(false);

            } catch (IOException e) {
                errorLb.setText("Error reading the file. Please try again.");
                nextBt.setDisable(true);

            } catch (NumberFormatException e) {
                errorLb.setText("Invalid format in file. Please ensure it contains only numbers.");
                nextBt.setDisable(true);
            }
        }
    }

    private void validateInput() {
        if (numOfCoinsTf.getText().isEmpty() || insertedCoinsTf.getText().isEmpty()) {
            errorLb.setText("Please load a file containing the coins.");
        } else {
            errorLb.setText(""); // Clear error if input is valid
        }
    }
}
