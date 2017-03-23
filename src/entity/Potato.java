package entity;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import data.QuadNode;
import javafx.scene.image.Image;

public class Potato extends Structure
{
	private static final double RADIUS = 28;
	public static Image[][] sprite = loadSprite();
	
	public Potato(QuadNode<Entity> root, Point2D location, boolean friendly, double health)
	{
		super(root, sprite, location, RADIUS, friendly, health);
		techRequired.add(Cauliflower.class);
		type = "potato";
	}

	private static Image[][] loadSprite()
	{
		try
		{
			sprite = new Image[1][1];
			sprite[0][0] = new Image(new FileInputStream("assets/tempPotato.png"));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return sprite;
	}
}
