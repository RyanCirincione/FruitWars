package entity;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import data.QuadNode;
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
	private boolean attacking;
	

	public Blueberry(QuadNode<Entity> root, Point2D location, Point2D rallyPoint, boolean friendly)
	{
		super(root, sprite, location, rallyPoint, RADIUS, SPEED, MAX_HEALTH, friendly);
		mass = 0.1f;
		name = "Pvt. " + getName() + " " + blueberryLastNames.get((int) (Math.random() * blueberryLastNames.size()));
		coolDown = 0;
		attacking = false;
		type = "blueberry";
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
	 * Source: Rutgers New Jersey Agricultural Experiment Station
	 */
	private static ArrayList<String> loadGrapeVarieties()
	{
		System.out.println("Loading types of blueberries...");
		ArrayList<String> grapes = new ArrayList<>();
		try (Scanner fileScan = new Scanner(new File("assets/blueberryNames.txt")))
		{
			while (fileScan.hasNextLine())
				grapes.add(fileScan.nextLine());
			fileScan.close();
		} catch (FileNotFoundException e)
		{
			System.err.println("Could not load a ridiculously long list of blueberry varieties ¯\\_(ツ)_/¯");
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
	public void attack(Entity enemy)
	{
		coolDown = MAXCOOLDOWN;
		root.add(new Projectile(root, (Point2D) getCenter().clone(), (Point2D) enemy.getCenter().clone(), isFriendly(),
				P_RADIUS, P_SPEED, DAMAGE, RANGE));
		super.setDestination(getCenter());
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
