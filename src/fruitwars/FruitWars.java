package fruitwars;

import javafx.application.Application;
import javafx.stage.Stage;

public class FruitWars extends Application
{

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception
	{
		stage.setScene(Menu.construct(stage));
		stage.show();
	}
}