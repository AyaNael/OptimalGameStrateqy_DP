package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class OptimalGameInterface extends Application {

	private Button randomBT, manualBT, fromFileBT, closeBT;
	private Stage primaryStage;
	private int[] coins;

	@Override
	public void start(Stage stage) {
	    this.primaryStage = stage; // Assign the primary stage
	    primaryStage.setTitle("Optimal Game Strategy - Coin Setup");
	    primaryStage.setScene(mainScene());
	    primaryStage.show();
	}


	public Scene mainScene() {

		// Set Background Color for the Main Scene
		BackgroundFill backgroundFill = new BackgroundFill(javafx.scene.paint.Color.DARKSLATEGRAY, CornerRadii.EMPTY,
				Insets.EMPTY);
		Background background = new Background(backgroundFill);

		// Header
		Image coinILogo = new Image(getClass().getResourceAsStream("coinLogo.png"));
		ImageView imageView = new ImageView(coinILogo);
		imageView.setFitWidth(100);
		imageView.setPreserveRatio(true);

		Label headLb = new Label("Win Maximum Coins!");
		headLb.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
		Label insertWayLb = new Label("Choose your strategy for coins:");
		insertWayLb.setStyle("-fx-font-size: 20px; -fx-text-fill: #FFFFFF;");

		// VBox for Top Contents
		VBox vBox = new VBox(10);
		vBox.setAlignment(Pos.CENTER);
		vBox.getChildren().addAll(imageView, headLb, insertWayLb);

		// Buttons styling
		randomBT = createStyledButton("Random");
		manualBT = createStyledButton("Manual");
		fromFileBT = createStyledButton("From File");

		// Actions for Buttons
		randomBT.setOnAction(e -> openRandomBtScene());
		manualBT.setOnAction(e -> openManualBtScene());
		fromFileBT.setOnAction(e -> openfromFileBtScene());

		// Close Button Styling
		closeBT = new Button("Close");
		closeBT.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white; -fx-font-weight: bold;");
		closeBT.setOnAction(e -> {
		    if (primaryStage != null) {
		        primaryStage.close(); // Safely close the application
		    } else {
		        System.out.println("Primary stage is null!");
		    }
		});

		// Effects for Close Button
		closeBT.setOnMouseEntered(e -> closeBT.setStyle("-fx-background-color: #FF4500; -fx-text-fill: white;"));
		closeBT.setOnMouseExited(e -> closeBT.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white;"));

		// HBox for Game Option Buttons
		HBox hBoxBt = new HBox(20, randomBT, manualBT, fromFileBT);
		hBoxBt.setPadding(new Insets(30));
		hBoxBt.setAlignment(Pos.CENTER);

		// BorderPane Layout
		BorderPane bp = new BorderPane();
		bp.setBackground(background);
		bp.setTop(vBox);
		bp.setCenter(hBoxBt);
		BorderPane.setAlignment(closeBT, Pos.BOTTOM_CENTER);
		BorderPane.setMargin(closeBT, new Insets(10, 0, 20, 0));
		bp.setBottom(closeBT);

		return new Scene(bp, 500, 600);
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

	private void openRandomBtScene() {
		RandomScene randomScene = new RandomScene();
		Scene scene = randomScene.createScene(primaryStage, this);
		primaryStage.setScene(scene);
	}

	private void openManualBtScene() {
		ManualScene manualScene = new ManualScene();
		Scene scene = manualScene.createScene(primaryStage, this);
		primaryStage.setScene(scene);
	}

	private void openfromFileBtScene() {
		FromFileScene fromFileScene = new FromFileScene();
		Scene scene = fromFileScene.createScene(primaryStage, this);
		primaryStage.setScene(scene);
	}

	// showing the Play Mode screen
    public void showPlayMode(int[] coins) {
    	PlayingWayScene playModeScene = new PlayingWayScene(coins,this);
    	primaryStage.setScene(playModeScene.createScene(primaryStage, this)); // `this` refers to OptimalGameInterface

    }
   
    
	public static void main(String[] args) {
		launch(args);
	}
}
