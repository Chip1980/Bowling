package application;

import controller.BowlingGameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BowlingGame extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/bowlingGame.fxml"));
			Parent root = loader.load();
			BowlingGameController bowlingGameController = loader.getController();
			Scene scene = new Scene(root, 1280, 768);
			scene.getStylesheets().add(getClass().getResource("/view/bowlingGame.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			primaryStage.show();
			bowlingGameController.showNewBowlersDialog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
