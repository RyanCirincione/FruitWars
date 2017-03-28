package entity;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import data.EntityStore;
import javafx.scene.image.Image;

public class Cauliflower extends Structure
{
	static Image[][] sprite = loadSprite();
	final static double RADIUS = 32;

	public Cauliflower(EntityStore<Entity> root, Point2D location, boolean friendly, double health)
	{
		super(root, sprite, location, RADIUS, friendly, health);
		type = "cauliflower";
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
