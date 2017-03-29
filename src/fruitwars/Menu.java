package fruitwars;

import java.io.File;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class Menu extends Scene
{
	final static int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 600;
	
	public static Menu construct(Stage stage) 
	{
		TilePane root = new TilePane();
		Button button = new Button();
		button.setText("Play game");
		button.setOnMouseClicked(evt -> {
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
		root.setAlignment(Pos.CENTER);

		root.getChildren().add(button);
		
		return new Menu(root);
	}

	public Menu(Parent root)
	{
		super(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		getStylesheets().clear();
		getStylesheets().add("file:///" + new File("style.css").getAbsolutePath().replace("\\", "/"));
	}

}
