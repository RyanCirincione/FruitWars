package entity;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import data.EntityStore;
import javafx.scene.image.Image;

public class GrapeVine extends Structure
{
	private final static double SPAWN_TIME = 7 * 1000, SPAWN_TIME_ERROR = 1.5 * 1000, RADIUS = 48;
	private Point2D.Double rally;
	public static Image[][] sprite = loadSprite();
	private double timeToSpawn;

	public GrapeVine(EntityStore<Entity> root, Point2D location, boolean friendly, double health)
	{
		super(root, sprite, location, RADIUS, friendly, health);
		this.rally = new Point2D.Double();
		timeToSpawn = SPAWN_TIME + (2 * Math.random() * SPAWN_TIME_ERROR) - SPAWN_TIME_ERROR;
		type = "grape_vine";
	}

	private static Image[][] loadSprite()
	{
		try
		{
			sprite = new Image[1][1];
			sprite[0][0] = new Image(new FileInputStream("assets/tempGrapeVine.png"));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return sprite;
	}

	public void setRally(Point2D.Double r)
	{
		rally = r;
	}

	@Override
	public void tick(long millis)
	{
		timeToSpawn -= millis;
		if (timeToSpawn <= 0)
		{
			timeToSpawn += SPAWN_TIME + (2 * Math.random() * SPAWN_TIME_ERROR) - SPAWN_TIME_ERROR;
			int count = 12 + (int) (Math.random() * 2);
			for (int i = 0; i < count; i++)
			{
				Point2D point = new Point2D.Double(getCenter().getX() + Math.random() * radius * 2 - radius,
						getCenter().getY() + Math.random() * radius * 2 - radius);
				root.add(new Grape(root, point, rally, friendly));
			}
		}
	}
}
