package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TwoPlayersScene {

    private int[] coins; // Array of coin values
    private String playerOneName; // Name of Player One
    private String playerTwoName; // Name of Player Two
    private int playerOneScore = 0; // Score of Player One
    private int playerTwoScore = 0; // Score of Player Two
    private boolean isPlayerOneTurn; // Track whose turn it is
    private Label currentPlayerLabel;
    private VBox playerOneScoreBox;
    private VBox playerTwoScoreBox;
    private Label playerOneTotalScore;
    private Label playerTwoTotalScore;
    private VBox playerOneCoinsBox;
    private VBox playerTwoCoinsBox;
    private int startIndex = 0; // Initial edge index
    private int endIndex; // Ending edge index
    private Button playAgainBt;
    private PlayingWayScene playingWayScene; // Reference to PlayingWayScene
    private OptimalGameInterface mainGameScene;

    public TwoPlayersScene(int[] coins, String playerOneName, String playerTwoName, PlayingWayScene playingWayScene) {
        this.coins = coins;
        this.playerOneName = playerOneName;
        this.playerTwoName = playerTwoName;
        this.endIndex = coins.length - 1; // Set initial end index
        this.playingWayScene = playingWayScene;

    }

    public Scene createScene(Stage primaryStage, OptimalGameInterface mainGameScene, String firstPlayer) {
        if (coins == null || coins.length == 0) {
            throw new IllegalStateException("Coins array cannot be null or empty.");
        }

        isPlayerOneTurn = firstPlayer.equals(playerOneName);

        currentPlayerLabel = new Label("Current Player: " + firstPlayer);
        currentPlayerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
        System.out.println("Initial Current Player: " + firstPlayer);

        // Determine the initial turn
        isPlayerOneTurn = firstPlayer.equals(playerOneName);
        System.out.println("Is Player One's Turn? " + isPlayerOneTurn);

        // Add scoreboxes
        Label playerOneTitle = new Label(playerOneName + " Score:");
        playerOneTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #00FF00;");
        playerOneTotalScore = new Label("Total: 0");
        playerOneTotalScore.setStyle("-fx-font-size: 18px; -fx-text-fill: #00FF00;");
        playerOneCoinsBox = new VBox(5);

        Label playerTwoTitle = new Label(playerTwoName + " Score:");
        playerTwoTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #0000FF;");
        playerTwoTotalScore = new Label("Total: 0");
        playerTwoTotalScore.setStyle("-fx-font-size: 18px; -fx-text-fill: #0000FF;");
        playerTwoCoinsBox = new VBox(5);

        playerOneScoreBox = new VBox(10, playerOneTitle, playerOneCoinsBox, playerOneTotalScore);
        playerOneScoreBox.setAlignment(Pos.CENTER);
        playerTwoScoreBox = new VBox(10, playerTwoTitle, playerTwoCoinsBox, playerTwoTotalScore);
        playerTwoScoreBox.setAlignment(Pos.CENTER);

        playerTwoScoreBox.setPadding(new Insets(20));

        // Coin container using GridPane
        GridPane coinGrid = new GridPane();
        coinGrid.setAlignment(Pos.CENTER);
        coinGrid.setHgap(10);
        coinGrid.setVgap(10);

        for (int i = 0; i < coins.length; i++) {
            Button coinButton = createCoinButton(coins[i], i);
            coinGrid.add(coinButton, i % 10, i / 10); // Place buttons in rows of 8
        }

        playAgainBt = new Button("Play Again");
        playAgainBt.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        playAgainBt.setOnAction(e -> {
            PlayerNamesInputScene playerNamesInputScene = new PlayerNamesInputScene(coins, mainGameScene);
            primaryStage.setScene(playerNamesInputScene.createScene(primaryStage, mainGameScene, playingWayScene)); // Pass the PlayingWayScene instance
        });

        playAgainBt.setVisible(false); // Initially hidden until the game ends

        VBox mainCenterLayout = new VBox(20, currentPlayerLabel, coinGrid);
        mainCenterLayout.setAlignment(Pos.CENTER);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setRight(playerTwoScoreBox);
        mainLayout.setLeft(playerOneScoreBox);
        mainLayout.setCenter(mainCenterLayout);
        mainLayout.setBottom(playAgainBt);
        BorderPane.setAlignment(playAgainBt, Pos.CENTER);
        mainLayout.setPadding(new Insets(50));
        mainLayout.setStyle("-fx-background-color: #2F4F4F;");

        return new Scene(mainLayout, 1000, 600);
    }

    private Button createCoinButton(int coinValue, int index) {
        Button coinButton = new Button(String.valueOf(coinValue));
        coinButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-weight: bold;-fx-background-radius: 25; ");
        coinButton.setPrefSize(50, 50);

        coinButton.setOnMouseClicked(event -> handleCoinClick(index, coinButton));
        return coinButton;
    }

    private void handleCoinClick(int index, Button coinButton) {
        if (index != startIndex && index != endIndex) return;

        int selectedValue = coins[index];

        if (isPlayerOneTurn) {
            playerOneScore += selectedValue;
            playerOneTotalScore.setText("Total: " + playerOneScore);
            addCoinToVBox(playerOneCoinsBox, selectedValue, true);
            coinButton.setStyle("-fx-background-color: #00FF00; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            playerTwoScore += selectedValue;
            playerTwoTotalScore.setText("Total: " + playerTwoScore);
            addCoinToVBox(playerTwoCoinsBox, selectedValue, false);
            coinButton.setStyle("-fx-background-color: #0000FF; -fx-text-fill: white; -fx-font-weight: bold;");
        }

        coinButton.setDisable(true);

        if (index == startIndex) {
            startIndex++;
        } else {
            endIndex--;
        }

        isPlayerOneTurn = !isPlayerOneTurn;
        currentPlayerLabel.setText("Current Player: " + (isPlayerOneTurn ? playerOneName : playerTwoName));

        if (startIndex > endIndex) {
            defineTheWinner();
        }
    }

    private void addCoinToVBox(VBox vbox, int coinValue, boolean isPlayerOne) {
        Label coinLabel = new Label(String.valueOf(coinValue));
        String color = isPlayerOne ? "#98FB98" : "#ADD8E6";
        coinLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + color + ";");
        vbox.getChildren().add(coinLabel);
    }

    private void defineTheWinner() {
        String winner;
        if (playerOneScore > playerTwoScore) {
            winner = playerOneName + " 🎉🎉🎉";
            currentPlayerLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #32CD32;");
            currentPlayerLabel.setText("Congratulations! Winner: " + winner);
        } else if (playerTwoScore > playerOneScore) {
            winner = playerTwoName + " 🎉🎉🎉";
            currentPlayerLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #32CD32;");
            currentPlayerLabel.setText("Congratulations! Winner: " + winner);
        } else {
            winner = "No Body Wins!!!";
            currentPlayerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FF6347;");
            currentPlayerLabel.setText("" + winner);
        }

        playAgainBt.setVisible(true); // Show the Play Again button
    }
}
