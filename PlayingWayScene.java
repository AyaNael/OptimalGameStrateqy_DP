package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PlayingWayScene {

	private Button withComputerBt, twoPlayerBt, backBt;
	private int[] coins;
	private OptimalGameInterface mainGameScene;

	public PlayingWayScene(int[] coins, OptimalGameInterface mainGameScene) {
		this.coins = coins;
		this.mainGameScene = mainGameScene; // Properly assign mainGameScene
	}

	public PlayingWayScene(int[] coins) {
		this.coins = coins;
	}

	public Scene createScene(Stage primaryStage, OptimalGameInterface mainGameScene) {
		// Header Label
		Label headLb = new Label("How You Want To Play?");
		headLb.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

		// Play Against Computer Button
		withComputerBt = createStyledButton("Play With Computer");
		withComputerBt.setOnAction(e -> startComputerMode(primaryStage));

		// Two-Player Mode Button
		twoPlayerBt = createStyledButton("Two Players");
		twoPlayerBt.setOnAction(e -> startTwoPlayerMode(primaryStage, this)); // Pass this PlayingWayScene

		// Back Button
		backBt = createStyledButton("Back");
		backBt.setOnAction(e -> {
		    if (mainGameScene != null) {
		        primaryStage.setScene(mainGameScene.mainScene());
		    } else {
		        System.err.println("Error: mainGameScene is null!");
		    }
		});
		// Layout Configuration
		VBox layout = new VBox(20, headLb, withComputerBt, twoPlayerBt, backBt);
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.CENTER);
		layout.setStyle("-fx-background-color: #2F4F4F;");

		return new Scene(layout, 400, 300);
	}

	// Method to create and style buttons consistently
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

	// Placeholder method to start computer mode
	private void startComputerMode(Stage primaryStage) {
		System.out.println("Starting game against the computer...");
		PlayWithComputerScene withComputerScene = new PlayWithComputerScene(coins);
		primaryStage.setScene(withComputerScene.createScene(primaryStage,mainGameScene));
	}

	// Method to start two-player mode, passes coins array to PlayerNamesInputScene
	private void startTwoPlayerMode(Stage primaryStage, PlayingWayScene playingWayScene) {
		PlayerNamesInputScene playerNamesInputScene = new PlayerNamesInputScene(coins, mainGameScene);
		primaryStage.setScene(playerNamesInputScene.createScene(primaryStage, mainGameScene, playingWayScene)); // Pass
																												// PlayingWayScene
	}
}
