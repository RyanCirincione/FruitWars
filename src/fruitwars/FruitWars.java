package fruitwars;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class FruitWars extends Application
{
	final static int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 600;

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception
	{
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
		stage.show();
	}
}