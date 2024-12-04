package application;

import java.util.Arrays;

import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PlayWithComputerScene {

	private int[] coins;
	private Button[] coinButtons; // Array of buttons for the coins
	private int playerScore = 0, computerScore = 0;
	private Label playerScoreLabel, computerScoreLabel, currentPlayerLabel;
	private VBox playerArea, computerArea; // Areas for chosen coins
	private Button startButton, showResultsButton, finalResultBt, showStepsBt, playAgainButton, showDbTableBt;
	private Move[] steps; // Array to store moves for "Show Steps"
	private int currentStepIndex = 0; // Track the current step
	private int[][][] dp; // 3D DP table for scores
	private boolean isGameStarted = false;

	public PlayWithComputerScene(int[] coins) {
		this.coins = coins;
		this.coinButtons = new Button[coins.length];
		this.steps = new Move[coins.length]; // Array size equals the number of coins
	}

	public Scene createScene(Stage primaryStage, OptimalGameInterface mainGameScene) {
		// Initialize Buttons Early
		startButton = createStyledButton("Start");
		showResultsButton = createStyledButton("Show Results");
		finalResultBt = createStyledButton("Final Result");
		showStepsBt = createStyledButton("Show Steps");
		playAgainButton = createStyledButton("Play Again");
		showDbTableBt = createStyledButton("Show DB Table");

		showResultsButton.setDisable(true);
		finalResultBt.setVisible(false);
		showStepsBt.setVisible(false); // Ensure it's hidden initially
		playAgainButton.setVisible(false);
		showDbTableBt.setVisible(false);

		// Header Label
		Label header = new Label("Optimal Game: Player vs Computer");
		header.setStyle(
				"-fx-font-size: 24px; -fx-font-weight: bold;-fx-text-border-color: White; -fx-text-fill: #FFD700;");

		currentPlayerLabel = new Label("Current Player: Player");
		currentPlayerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #FFD700;");

		playerScoreLabel = new Label("Player Score:");
		playerScoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #800080;"); // Purple for player

		computerScoreLabel = new Label("Computer Score:");
		computerScoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #008000;"); // Green for computer

		// Coin Buttons
		HBox coinHBox = new HBox(10);
		coinHBox.setAlignment(Pos.CENTER);

		// Check if coins exceed the limit
		boolean exceedsLimit = coins.length > 10;
		if (exceedsLimit) {
			// Display warning alert
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Too Many Coins");
			alert.setHeaderText("Coins Limit Exceeded");
			alert.setContentText(
					"The number of coins exceeds the limit for visual display. However, the results will still be calculated.");

			// Prevent the alert from being dismissed until acknowledged
			alert.getButtonTypes().clear();
			alert.getButtonTypes().add(ButtonType.OK);
			alert.showAndWait();
		}

		// Always generate coin buttons
		for (int i = 0; i < coins.length; i++) {
			coinButtons[i] = createCoinButton(coins[i], i);
			if (!exceedsLimit) {
				// Add buttons to the HBox only if within the limit
				coinHBox.getChildren().add(coinButtons[i]);
			}
		}

		// Disable the HBox if exceedsLimit is true (prevents interaction)
		if (exceedsLimit) {
			coinHBox.setVisible(false); // Hide the coin display
			coinHBox.setManaged(false); // Exclude the HBox from layout calculations
		}

		// Ensure "Show Table" and "Play Again" buttons are always visible
		playAgainButton.setVisible(true);
		showDbTableBt.setVisible(true);

		// Player and Computer Areas
		playerArea = new VBox(10);
		playerArea.setAlignment(Pos.CENTER);
		playerArea.setStyle("-fx-background-color: #FFD700; -fx-border-color: #800080; -fx-border-width: 5px;");
		playerArea.setPadding(new Insets(30));
		Label playerAreaLabel = new Label("Player's Coins");
		playerAreaLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");
		playerArea.getChildren().add(playerAreaLabel);

		computerArea = new VBox(10);
		computerArea.setAlignment(Pos.CENTER);
		computerArea.setStyle("-fx-background-color: #FFD700; -fx-border-color: #008000; -fx-border-width: 5px;");
		computerArea.setPadding(new Insets(30));
		Label computerAreaLabel = new Label("Computer's Coins");
		computerAreaLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");
		computerArea.getChildren().add(computerAreaLabel);

		// Button Actions
		startButton.setOnAction(e -> {
			startGame();
			isGameStarted = true; // Set the flag to true
			showDbTableBt.setVisible(true); // Make the Show DB Table button visible
		});
		showResultsButton.setOnAction(e -> {
			finalResultBt.setVisible(true);
			showStepsBt.setVisible(true);
			playAgainButton.setVisible(true);
			showDbTableBt.setVisible(true);
		});
		finalResultBt.setOnAction(e -> showFinalResult(primaryStage));
		showStepsBt.setOnAction(e -> replaySteps()); // Show steps dynamically
		playAgainButton.setOnAction(e -> {
			OptimalGameInterface mainInterface = new OptimalGameInterface();
			primaryStage.setScene(mainInterface.mainScene());
		});
		showDbTableBt.setOnAction(e -> {
			if (isGameStarted) { // Ensure the game has started before showing the table
				showDpTable(primaryStage, dp, coins, primaryStage.getScene());
			}
		});

		// Layout configuration		
		HBox mainLayout = new HBox(50, playerArea, coinHBox, computerArea);
		mainLayout.setAlignment(Pos.CENTER);

		HBox actionButtons = new HBox(10, playAgainButton, showDbTableBt);
		actionButtons.setAlignment(Pos.CENTER);

		VBox controls = new VBox(10, startButton, showResultsButton, finalResultBt, showStepsBt, actionButtons);
		controls.setAlignment(Pos.CENTER);

		VBox layout = new VBox(20, header, currentPlayerLabel, mainLayout, playerScoreLabel, computerScoreLabel,
				controls);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(30));
		layout.setStyle("-fx-background-color: #D3D3D3;"); // Dark gray background

		return new Scene(layout, 1200, 800);
	}

	private Button createCoinButton(int value, int index) {
		Button button = new Button(String.valueOf(value));
		button.setShape(new Circle(25)); // Circular shape
		button.setStyle("-fx-background-color: #FFD700; -fx-font-weight: bold; -fx-border-color: #DAA520; "
				+ "-fx-border-width: 2px; -fx-text-fill: black;"); // Gold color
		button.setPrefSize(50, 50); // Circular size
		button.setDisable(false); // Initially disabled
		return button;
	}

	private void startGame() {
		try {
			System.out.println("Starting new game...");
			resetGameState();
			gameSol(); // Populate the DP table and simulate the game
			showResultsButton.setDisable(false);
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(Alert.AlertType.ERROR, "An error occurred: " + e.getMessage());
			alert.showAndWait();
		}
	}

	private void disableGameButtons() {
		startButton.setDisable(true);
		finalResultBt.setVisible(false);
		showStepsBt.setVisible(false);
		playAgainButton.setVisible(false);
		showDbTableBt.setVisible(false);
	}

	private void gameSol() {
		System.out.println("Coins: " + Arrays.toString(coins));

		computerScoreLabel.setText("Computer Score:");
		playerScoreLabel.setText("Player Score:");
		int n = coins.length;
		dp = new int[n][n][2]; // Initialize the DP table

		// Base case: for each single coin, the maximum amount is the coin itself.
		for (int i = 0; i < n; i++) {
			dp[i][i][0] = coins[i]; // First player gets the coin
			dp[i][i][1] = 0; // Opponent gets nothing
			System.out.println("Base case: dp[" + i + "][" + i + "] = (" + dp[i][i][0] + ", " + dp[i][i][1] + ")");

		}

		// Fill the dp table for subarrays of increasing lengths
		for (int length = 2; length <= n; length++) {
			for (int i = 0; i <= n - length; i++) {
				int j = i + length - 1; // The last element in the array
				System.out.println("Processing dp[" + i + "][" + j + "]...");

				// Case 1: First player picks the left coin
				int pickLeftFirst;
				if (i + 1 <= j) {
					pickLeftFirst = coins[i] + dp[i + 1][j][1];
				} else {
					pickLeftFirst = coins[i];
				}

				int pickLeftSecond;
				if (i + 1 <= j) {
					pickLeftSecond = dp[i + 1][j][0];
				} else {
					pickLeftSecond = 0;
				}

				// Case 2: First player picks the right coin
				int pickRightFirst;
				if (i <= j - 1) {
					pickRightFirst = coins[j] + dp[i][j - 1][1];
				} else {
					pickRightFirst = coins[j];
				}

				int pickRightSecond;
				if (i <= j - 1) {
					pickRightSecond = dp[i][j - 1][0];
				} else {
					pickRightSecond = 0;
				}

				// Choose the better option for the first player
				if (pickLeftFirst > pickRightFirst) {
					dp[i][j][0] = pickLeftFirst; // Update first player's score
					dp[i][j][1] = pickLeftSecond; // Update second player's score
				} else {
					dp[i][j][0] = pickRightFirst; // Update first player's score
					dp[i][j][1] = pickRightSecond; // Update second player's score
				}
			}
		}
		System.out.println(playerScore);

		// Backtrack to find the chosen coins for both players
		int start = 0, end = n - 1;
		boolean firstPlayerTurn = true; // Track whose turn it is
		int moveIndex = 0; // Track steps

		while (start <= end && start < n && end >= 0) {
		    int selectedIndex;
		    if (start + 1 < n && dp[start][end][0] - coins[start] == dp[start + 1][end][1]) {
		        selectedIndex = start;
		        start++;
		    } else if (end - 1 >= 0) {
		        selectedIndex = end;
		        end--;
		    } else {
		        break; // Exit if bounds are exceeded
		    }

		    // Move coin to the respective area
		    moveCoinToArea(selectedIndex, firstPlayerTurn);

		    // Record the step
		    steps[moveIndex++] = new Move(firstPlayerTurn, selectedIndex, coins[selectedIndex]);

		    // Update scores
		    if (firstPlayerTurn) {
		        playerScore += coins[selectedIndex];
		        playerScoreLabel.setText("Player Score: " + playerScore);
		    } else {
		        computerScore += coins[selectedIndex];
		        computerScoreLabel.setText("Computer Score: " + computerScore);
		    }

		    firstPlayerTurn = !firstPlayerTurn; // Switch turns
		}

		currentStepIndex = moveIndex; // Update the total number of moves

	}

	private void replaySteps() {
		playerScore = 0;
		computerScore = 0;

		playerScoreLabel.setText("Player Score: " + playerScore);
		computerScoreLabel.setText("Computer Score: " + computerScore);
		playerArea.getChildren()
				.removeIf(node -> node instanceof Label && !((Label) node).getText().equals("Player's Coins"));
		computerArea.getChildren()
				.removeIf(node -> node instanceof Label && !((Label) node).getText().equals("Computer's Coins"));

		if (currentStepIndex == 0) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("No Steps Available");
			alert.setHeaderText(null);
			alert.setContentText("No steps to show. Please start the game first!");
			alert.showAndWait();
			return;
		}

		PauseTransition pause = new PauseTransition(Duration.seconds(1));
		int[] index = { 0 }; // Use an array to track the current step

		pause.setOnFinished(event -> {
			if (index[0] < currentStepIndex) {
				Move move = steps[index[0]];
				highlightCoin(move.index, move.isPlayer ? "#800080" : "#008000"); // Purple for player, green for
																					// computer
				moveCoinToArea(move.index, move.isPlayer);
				index[0]++;
				pause.playFromStart(); // Continue to the next step
			}
		});

		pause.play();
	}

	private void highlightCoin(int index, String color) {
		Button button = coinButtons[index];
		button.setStyle("-fx-background-color: " + color + "; -fx-font-weight: bold;");
		button.setDisable(true); // Disable after selection
	}

	private void moveCoinToArea(int index, boolean isPlayer) {
		System.out.println(playerScore);

		System.out.println("Moving coin with value: " + coins[index] + " to " + (isPlayer ? "Player" : "Computer"));
		Label coinLabel = new Label(coinButtons[index].getText());
		coinLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");
		if (isPlayer) {
			playerArea.getChildren().add(coinLabel);
			playerScore += coins[index];
			playerScoreLabel.setText("Player Score: " + playerScore);
			System.out.println("Player Score updated: " + playerScore);
		} else {
			computerArea.getChildren().add(coinLabel);
			computerScore += coins[index];
			computerScoreLabel.setText("Computer Score: " + computerScore);
			System.out.println("Computer Score updated: " + computerScore);
		}
	}

	private void resetGameState() {
		playerScore = 0;
		computerScore = 0;

		playerScoreLabel.setText("Player Score: " + playerScore);
		computerScoreLabel.setText("Computer Score: " + computerScore);

		playerArea.getChildren()
				.removeIf(node -> node instanceof Label && !((Label) node).getText().equals("Player's Coins"));
		computerArea.getChildren()
				.removeIf(node -> node instanceof Label && !((Label) node).getText().equals("Computer's Coins"));
	}

	private void showFinalResult(Stage primaryStage) {
		// Create a VBox layout for the final result
		VBox resultLayout = new VBox(20);
		resultLayout.setPadding(new Insets(30));
		resultLayout.setAlignment(Pos.CENTER);
		resultLayout.setStyle("-fx-background-color: #2E2E2E; -fx-border-color: #FFD700; -fx-border-width: 2px;");

		// Add a title label
		Label titleLabel = new Label("Final Game Results");
		titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

		// Add player and computer scores
		Label playerResultLabel = new Label("Player Score: " + playerScore);
		playerResultLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;");

		Label computerResultLabel = new Label("Computer Score: " + computerScore);
		computerResultLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;");

		// Determine the winner
		Label winnerLabel = new Label();
		if (playerScore > computerScore) {
			winnerLabel.setText("Winner: Player!");
			winnerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #00FF00;"); // Green text
		} else if (computerScore > playerScore) {
			winnerLabel.setText("Winner: Computer!");
			winnerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FF4500;"); // Red text
		} else {
			winnerLabel.setText("It's a tie!");
			winnerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FFFF00;"); // Yellow text
		}

		// Add a button to return to the main scene
		Button backButton = new Button("Back to Main Menu");
		backButton.setStyle(
				"-fx-background-color: #FFD700; -fx-font-weight: bold; -fx-text-fill: black; -fx-padding: 10px;");
		backButton.setOnAction(e -> primaryStage.setScene(createScene(primaryStage, null)));

		// Add all elements to the layout
		resultLayout.getChildren().addAll(titleLabel, playerResultLabel, computerResultLabel, winnerLabel, backButton);

		// Create and set the new scene
		Scene resultScene = new Scene(resultLayout, 800, 600);
		primaryStage.setScene(resultScene);
	}

	private void showDpTable(Stage primaryStage, int[][][] dp, int[] coins, Scene currentScene) {
		int n = coins.length;

		// TableView for displaying the DP Table
		TableView<DPRow> tableView = new TableView<>();
		tableView.setStyle(
				"-fx-background-color: #1E1E1E; -fx-border-color: #FFD700; -fx-border-width: 2px; -fx-padding: 10px;");

		// First column: Row indices
		TableColumn<DPRow, Integer> indexColumn = new TableColumn<>("Index");
		indexColumn
				.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIndex()).asObject());
		indexColumn.setMinWidth(70);
		indexColumn
				.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: CENTER;");
		tableView.getColumns().add(indexColumn);

		// Add additional columns dynamically for coins indices
		for (int j = 0; j < n; j++) {
			final int columnIndex = j;
			TableColumn<DPRow, String> column = new TableColumn<>(" " + j);
			column.setCellValueFactory(
					cellData -> new SimpleStringProperty(cellData.getValue().getValueAt(columnIndex)));
			column.setMinWidth(100);
			column.setStyle(
					"-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-alignment: CENTER;");
			tableView.getColumns().add(column);
		}

		// Populate the table data for meaningful rows only
		ObservableList<DPRow> rows = FXCollections.observableArrayList();
		for (int i = 0; i < n; i++) {
			rows.add(createDPRow(i, dp, n)); // Ensure rows match the number of coins

		}
		tableView.setItems(rows);

		// Styling for table rows
		tableView.setRowFactory(tv -> {
			TableRow<DPRow> row = new TableRow<>();
			row.setStyle(
					"-fx-background-color: #3E3E3E; -fx-border-color: #FFD700; -fx-border-width: 1px; -fx-padding: 5px;");
			return row;
		});

		// Header styling
		tableView.setStyle("-fx-table-cell-border-color: #FFD700; " + "-fx-control-inner-background: #1E1E1E; "
				+ "-fx-background-color: #1E1E1E; " + "-fx-border-radius: 5px; -fx-padding: 15px;");

		// back button
		Button backButton = new Button("Back");
		backButton.setStyle(
				"-fx-background-color: #FFD700; -fx-font-weight: bold; -fx-text-fill: black; -fx-padding: 10px;");
		backButton.setOnAction(e -> {
			try {
				primaryStage.setScene(currentScene);
			} catch (Exception ex) {
				ex.printStackTrace(); // Log errors
				Alert alert = new Alert(Alert.AlertType.ERROR, "An error occurred. Please restart the game.");
				alert.showAndWait();
			}
		});

		// Layout for the table and back button
		VBox layout = new VBox(10, tableView, backButton);
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.CENTER);
		layout.setStyle(
				"-fx-background-color: #1E1E1E; -fx-padding: 15px; -fx-border-radius: 10px; -fx-border-color: #FFD700;");

		// Set up the scene and display
		Scene scene = new Scene(layout, 900, 600);
		primaryStage.setScene(scene);

	}

	// Method to create a DPRow object for a given row index
	private DPRow createDPRow(int index, int[][][] dp, int n) {
		DPRow row = new DPRow();
		row.setIndex(index);
		String[] values = new String[n];
		for (int j = 0; j < n; j++) {
			if (dp[index][j][0] == 0 && dp[index][j][1] == 0) {
				values[j] = ""; // Skip cells with no values
			} else {
				values[j] = "(" + dp[index][j][0] + ", " + dp[index][j][1] + ")";
			}
		}
		row.setValues(values);
		return row;
	}

	// Inner class representing a row in the DP table
	public static class DPRow {
		private int index;
		private String[] values;

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getValueAt(int col) {
			if (col >= values.length)
				return "";
			return values[col];
		}

		public void setValues(String[] values) {
			this.values = values;
		}
	}

	private Button createStyledButton(String text) {
		Button button = new Button(text);
		button.setStyle(
				"-fx-background-color: #FFD700; -fx-font-weight: bold; -fx-border-color: #DAA520; -fx-text-fill: black;");
		return button;
	}

	// Helper class to store steps
	private static class Move {
		boolean isPlayer;
		int index;
		int value;

		Move(boolean isPlayer, int index, int value) {
			this.isPlayer = isPlayer;
			this.index = index;
			this.value = value;
		}
	}
}
