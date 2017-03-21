package entity;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import data.QuadNode;
import javafx.scene.image.Image;

public class Grape extends Unit
{
	private static final double RADIUS = 16, RANGE = 5;
	private static final long MAXCOOLDOWN = 1000;
	private long coolDown;
	public final static double MAX_HEALTH = 50, SPEED = 1;
	public static ArrayList<String> grapeLastNames = loadGrapeVarieties();
	private String name;
	public static Image[][] sprite = loadSprite();
	private double damage = 5.0;
	private boolean attacking;
	
	public Grape(QuadNode<Entity> root, Point2D location, Point2D rallyPoint, boolean friendly)
	{
		super(root, sprite, location, rallyPoint, RADIUS, SPEED, MAX_HEALTH, friendly);
		mass = 0.1f;
		name = "Pvt. " + getName() + " " + grapeLastNames.get((int) (Math.random() * grapeLastNames.size()));
		coolDown = 0;
		attacking = false;
	}

	private static Image[][] loadSprite()
	{
		try
		{
			sprite = new Image[1][1];
			sprite[0][0] = new Image(new FileInputStream("assets/tempGrape.png"));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return sprite;
	}

	@Override
	public void tick(long millis)
	{
		coolDown -= millis;
		boolean attackingNow = false;
		if (coolDown <= 0)
		{
			Entity e = root.getClosest(this, (e1, e2) -> {
				return e1.isFriendly() != e2.isFriendly() && !(e2 instanceof Projectile);
			});
			if (e != null)
			{
				double radiusSum = radius + RANGE + e.radius;
				if (getCenter().distanceSq(e.getCenter()) <= radiusSum * radiusSum)
					attack(e);
				else
					target(e);
				attackingNow = true;
			}
			if(attacking && !attackingNow)
			{
				attacking = false;
				super.endAttack();
			}
		}
		super.tick(millis);
	}

	/**
	 * Source: National Grape Registry
	 */
	private static ArrayList<String> loadGrapeVarieties()
	{
		System.out.println("Loading types of grapes...");
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
		String status = name + " (GRAPE)";
		while (status.length() < 50)
			status = status + ".";
		return status + "HP: " + health + "/" + MAX_HEALTH;
	}

	@Override
	public void attack(Entity enemy)
	{
		enemy.setHealth(enemy.getHealth() - damage);
		super.setDestination(getCenter());
		coolDown = MAXCOOLDOWN;
	}

	@Override
	public void target(Entity enemy)
	{
		if (enemy.isFriendly() != isFriendly())
		{
			// if within range
			double radiusSum = radius + RANGE + enemy.radius;
			if (getCenter().distanceSq(enemy.getCenter()) <= radiusSum * radiusSum && !(enemy instanceof Projectile))
				attack(enemy);
			else
				super.setDestination(enemy.getCenter());
		}
	}
}
