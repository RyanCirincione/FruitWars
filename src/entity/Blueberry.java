package entity;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.scene.image.Image;

public class Blueberry extends Unit
{
	private static final double RADIUS = 16, RANGE = 150;
	private static final double P_RADIUS = 1, P_SPEED = 4, DAMAGE = 2.0;
	private static final long MAXCOOLDOWN = 500;
	private long coolDown;
	public final static double MAX_HEALTH = 25, SPEED = 2;
	public static ArrayList<String> blueberryLastNames = loadGrapeVarieties();
	private String name;
	public static Image[][] sprite = loadSprite();
	
	public Blueberry(Point2D location, Point2D rallyPoint, boolean friendly)
	{
		super(sprite, location, rallyPoint, RADIUS, SPEED, MAX_HEALTH, friendly);
		mass = 0.1f;
		name = "Pvt. " + getName() + " " + blueberryLastNames.get((int) (Math.random() * blueberryLastNames.size()));
		coolDown = 0;

	}

	private static Image[][] loadSprite()
	{
		try
		{
			sprite = new Image[1][1];
			sprite[0][0] = new Image(new FileInputStream("assets/tempBlueberry.png"));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return sprite;
	}

	@Override
	public void tick(long millis, List<Entity> entities)
	{
		coolDown -= millis;
		if (coolDown <= 0)
		{
			for (Entity e : entities)
			{
				double radiusSum = radius + RANGE + e.radius;
				if (!(e.isFriendly() == isFriendly()) && location.distanceSq(e.location) <= radiusSum * radiusSum  && !(e instanceof Projectile))
				{
					attack(e, entities);
					coolDown = MAXCOOLDOWN;
					break;
				}
			}
		}
		super.tick(millis, entities);
	}

	/**
	 * Source: National Grape Registry
	 */
	private static ArrayList<String> loadGrapeVarieties()
	{
		System.out.println("Loading types of grapes for blueberries for now...");
		ArrayList<String> grapes = new ArrayList<>();
		try (Scanner fileScan = new Scanner(new File("assets/grapeNames.txt")))
		{
			while (fileScan.hasNextLine())
				grapes.add(fileScan.nextLine());
			fileScan.close();
		} catch (FileNotFoundException e)
		{
			System.err.println("Could not load a ridiculously long list of grape varieties ¯\\_(ツ)_/¯");
			e.printStackTrace();
		}
		return grapes;
	}
	
	public String toString()
	{
		String status = name + " (Blueberry)";
		while (status.length() < 50)
			status = status + ".";
		return status + "HP: " + health + "/" + MAX_HEALTH;
	}

	@Override
	public void attack(Entity enemy, List<Entity> entities)
	{
		if (!(enemy.isFriendly() == isFriendly()))
		{
			// if within range
			double radiusSum = radius + RANGE + enemy.radius;
			if (location.distanceSq(enemy.location) <= radiusSum * radiusSum  && !(enemy instanceof Projectile))
			{
				entities.add(new Projectile((Point2D)location.clone(), (Point2D)enemy.location.clone(), isFriendly(), P_RADIUS, P_SPEED, DAMAGE, RANGE));
				super.setDestination(location);
			} else
				super.setDestination(enemy.location);
		}
	}

}
