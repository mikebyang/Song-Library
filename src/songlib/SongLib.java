package songlib;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import view.SongListController;

public class SongLib extends Application{
	
	@Override
	public void start(Stage primaryStage)
	throws IOException {
		
		primaryStage.setTitle("Song Library");
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/SongLib.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		
		SongListController SongListC = loader.getController();
		SongListC.start(primaryStage);
		
		Scene scene = new Scene(root, 415, 465);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
