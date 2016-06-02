package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
	private Stage primaryStage;
	private AnchorPane rootLayout;

    public static void main(String[] args) {
        launch(args);
    }

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Traffic Simulation");
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("view/SimulationOverview.fxml"));
            rootLayout = loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("img/stau.png")));
            primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
