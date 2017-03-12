package entity;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.image.Image;

public class Cauliflower extends Structure
{
	static Image[][] sprite = loadSprite();
	final static double RADIUS = 32;
	
	public Cauliflower(Point2D location, double radius, boolean friendly, double health)
	{
		super(sprite, location, RADIUS, friendly, health);
	}

	private static Image[][] loadSprite()
	{
		try
		{
			sprite = new Image[1][1];
			sprite[0][0] = new Image(new FileInputStream("assets/tempCauliflower.png"));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return sprite;
	}
}
