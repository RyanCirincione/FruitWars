package entity;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class GrapeVine extends Structure
{
	private final double SPAWN_TIME = 7*1000, SPAWN_TIME_ERROR = 1.5*1000;
	private Point2D.Double rally;
	private static Image[][] sprite;
	private double timeToSpawn;
	
	public GrapeVine(Point2D location, Point2D.Double rally, double radius, boolean friendly, double health)
	{
		super(loadImage(), location, radius, friendly, health);
		this.rally = rally;
		timeToSpawn = SPAWN_TIME + (2*Math.random()*SPAWN_TIME_ERROR) - SPAWN_TIME_ERROR;
	}

	private static Image[][] loadImage()
	{
		if(sprite == null)
		{
			sprite = new Image[1][1];
			try
			{
				System.out.println("Loading Grape Vine Sprite...");
				sprite[0][0] = ImageIO.read(new File("assets/tempGrapeVine.png"));
			} catch (IOException e)
			{
				System.err.println("Could not find grape vine sprite asset");
				e.printStackTrace();
			}
		}
		return sprite;
	}
	
	public void tick(long millis, ArrayList<Entity> entities)
	{
		timeToSpawn -= millis;
		if(timeToSpawn <= 0)
		{
			timeToSpawn += SPAWN_TIME + (2*Math.random()*SPAWN_TIME_ERROR) - SPAWN_TIME_ERROR;
			int count = 3 + (int)(Math.random()*2);
			for(int i = 0; i < count; i++)
				entities.add(new Grape(new Point2D.Double(location.getX() + Math.random()*radius*2 - radius,
						location.getY() + Math.random()*radius*2 - radius), rally, friendly));
		}
	}
}
