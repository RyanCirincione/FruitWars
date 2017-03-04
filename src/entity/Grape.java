package entity;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Grape extends Unit
{
	private static final double RADIUS = 16;

	public Grape(Point2D location)
	{
		super(loadSprite(), location, RADIUS, 1);
		mass = 0.1f;
	}

	private static Image[][] loadSprite()
	{
		// temp
		Image[][] sprite = new Image[1][1];
		try
		{
			sprite[0][0] = ImageIO.read(new File("assets/tempGrape.png"));
		} catch (IOException e)
		{
			System.err.println("Could not find grape sprite asset cause fuck you");
			e.printStackTrace();
		}
		return sprite;
	}
}
