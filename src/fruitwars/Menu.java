package fruitwars;

import java.io.File;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Menu extends Scene
{
	final static int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 600;
	
	public static Menu construct(Stage stage) 
	{
		VBox root = new VBox();
		Button play = new Button();
		play.setText("Play game");
		play.setOnMouseClicked(evt -> {
			Game game = Game.construct(WINDOW_WIDTH, WINDOW_HEIGHT);
			stage.setScene(game);
			
			stage.widthProperty().addListener((obs, oldVal, newVal) -> {
				game.g.getCanvas().setWidth(newVal.doubleValue());
			});
			stage.heightProperty().addListener((obs, oldVal, newVal) -> {
				game.g.getCanvas().setHeight(newVal.doubleValue());
			});

			new AnimationTimer()
			{
				long previousMilli = 0;

				public void handle(long currentNano)
				{
					long currentMilli = currentNano / 1000000;
					long delta = currentMilli - previousMilli;
					if (previousMilli != 0)
						game.tick(delta);
					game.draw(delta);
					previousMilli = currentMilli;
				}
			}.start();
		});
		play.setPrefWidth(400);
		play.setPrefHeight(100);
		root.getChildren().add(play);
		Button exit = new Button();
		exit.setText("Exit Game");
		exit.setPrefWidth(400);
		exit.setPrefHeight(100);
		exit.setOnMouseClicked(evt -> {
			System.exit(0);
		});
		root.getChildren().add(exit);
		root.setSpacing(50);
		root.setAlignment(Pos.CENTER);

		
		
		return new Menu(root);
	}

	public Menu(Parent root)
	{
		super(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		getStylesheets().clear();
		getStylesheets().add("file:///" + new File("style.css").getAbsolutePath().replace("\\", "/"));
	}

}
