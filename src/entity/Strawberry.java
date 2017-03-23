package entity;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import data.QuadNode;
import javafx.scene.image.Image;

public class Strawberry extends Unit
{
	private static final double RADIUS = 16, RANGE = 150;
	private static final double P_RADIUS = 1, P_SPEED = 4, DAMAGE = 2.0;
	private static final long MAXCOOLDOWN = 2000;
	private long coolDown;
	public final static double MAX_HEALTH = 30, SPEED = 1.5;
	public static ArrayList<String> strawberryLastNames = loadStrawberryVarieties();
	private String name;
	public static Image[][] sprite = loadSprite();
	private boolean attacking;
	

	public Strawberry(QuadNode<Entity> root, Point2D location, Point2D rallyPoint, boolean friendly)
	{
		super(root, sprite, location, rallyPoint, RADIUS, SPEED, MAX_HEALTH, friendly);
		mass = 0.1f;
		
		name = getRank() + " " + getName() + " " + strawberryLastNames.get((int) (Math.random() * strawberryLastNames.size()));
		
		coolDown = 0;
		attacking = false;
		type = "strawberry";
	}

	private static Image[][] loadSprite()
	{
		try
		{
			sprite = new Image[1][1];
			sprite[0][0] = new Image(new FileInputStream("assets/tempStrawberry.png"));
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
	 * Source: Rutgers New Jersey Agricultural Experiment Station
	 */
	private static ArrayList<String> loadStrawberryVarieties()
	{
		System.out.println("Loading types of strawberries...");
		ArrayList<String> strawberries = new ArrayList<>();
		try (Scanner fileScan = new Scanner(new File("assets/strawberryNames.txt")))
		{
			while (fileScan.hasNextLine())
				strawberries.add(fileScan.nextLine());
			fileScan.close();
		} catch (FileNotFoundException e)
		{
			System.err.println("Could not load a ridiculously long list of strawberry varieties ¯\\_(ツ)_/¯");
			e.printStackTrace();
		}
		return strawberries;
	}

	public String toString()
	{
		String status = name + " (Strawberry)";
		while (status.length() < 50)
			status = status + ".";
		return status + "HP: " + health + "/" + MAX_HEALTH;
	}

	@Override
	public void attack(Entity enemy)
	{
		coolDown = MAXCOOLDOWN;
		int projectiles = 8;
		for(int i = 0; i < projectiles; i++)
		{
			double angle = (Math.PI * i) / (projectiles/2);
			double dist = getCenter().distance(enemy.getCenter());
			Point2D.Double destination = new Point2D.Double(getCenter().getX() + (Math.cos(angle) * dist), getCenter().getY() + (Math.sin(angle) * dist));
			root.add(new Projectile(root, (Point2D) getCenter().clone(), destination, this,
					P_RADIUS, P_SPEED, DAMAGE, RANGE));
		}
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
