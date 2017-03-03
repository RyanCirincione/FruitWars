package entity;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Grape extends Unit
{

	public Grape(Point2D location) 
	{
		super(loadSprite(), location, 1);
	}
	
	private static Image[][] loadSprite()
	{
		//temp
		Image[][] sprite = new Image[1][1];
		try 
		{
			sprite[0][0] = ImageIO.read(new File("assets/tempGrape.png"));
		}
		catch(IOException e) 
		{
			System.err.println("Could not find grape sprite asset");
			e.printStackTrace();
		}
		return sprite;
	}
}
