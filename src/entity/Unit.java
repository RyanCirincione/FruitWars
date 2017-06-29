package entity;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import data.EntityStore;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public abstract class Unit extends Entity
{
	private boolean selected;
	protected boolean attackMove;
	private double speed;
	protected Point2D destination;
	private Point2D longTermDestination;
	private final static String[] RANKS = { "Pvt.", "Cpl.", "Sgt." };
	private int kills = 0;
	private static ArrayList<String> names = loadNames();

	public Unit(EntityStore<Entity> root, Image[][] sprite, Point2D location, Point2D rallyPoint, double radius,
			double speed, double health, boolean friendly)
	{
		super(root, sprite, location, radius, friendly, health);
		destination = rallyPoint;
		this.speed = speed;
		this.health = health;
		type = "unit";
		attackMove = true;
	}

	public void moveToward(long millis)
	{
		Point2D location = getCenter();
		if (!getCenter().equals(destination))
		{
			double distance = location.distance(destination);
			if (distance < speed)
			{
				location.setLocation(destination);
			} else
			{
				double x = (destination.getX() - location.getX()) / distance;
				double y = (destination.getY() - location.getY()) / distance;
				x *= 60.0 * millis / (1000.0 / speed);
				y *= 60.0 * millis / (1000.0 / speed);
				location.setLocation(location.getX() + x, location.getY() + y);
			}
		}
	}

	@Override
	public void tick(long millis)
	{
		moveToward(millis);
		super.tick(millis);
	}

	public void startAttack()
	{
		longTermDestination = (Point2D) destination.clone();
	}

	public void endAttack()
	{
		destination = longTermDestination;
	}

	@Override
	public void draw(GraphicsContext g2, long millis)
	{
		Point2D location = getCenter();
		g2.setStroke(Color.RED);
		if (!location.equals(destination))
			g2.strokeOval((int) destination.getX(), (int) destination.getY(), 8, 8);
		g2.stroke();
		super.draw(g2, millis);
		g2.setStroke(Color.CYAN);
		if (selected)
			g2.strokeOval((int) (location.getX() - radius), (int) (location.getY() - radius), 2 * (int) radius,
					2 * (int) radius);
		g2.stroke();
	}

	/**
	 * Source: 2010 US Census
	 */
	private static ArrayList<String> loadNames()
	{
		System.out.println("Loading first names...");
		ArrayList<String> names = new ArrayList<>();
		try (Scanner fileScan = new Scanner(new File("assets/firstNames.txt")))
		{
			while (fileScan.hasNextLine())
				names.add(fileScan.nextLine());
			fileScan.close();
		} catch (FileNotFoundException e)
		{
			System.err.println("Could not load a ridiculously long list of first names ¯\\_(ツ)_/¯");
			e.printStackTrace();
		}
		return names;
	}

	public String getName()
	{
		return names.get((int) (Math.random() * names.size()));
	}

	public void setDestination(Point2D destination, boolean attackMove)
	{
		this.destination = destination;
		this.attackMove = attackMove;
	}
	
	public void setSelected(boolean select)
	{
		selected = select;
	}

	protected void addKill()
	{
		kills++;
	}

	protected String getRank()
	{
		if (kills < 5)
			return RANKS[0];
		else if (kills < 10)
			return RANKS[1];
		else
			return RANKS[2];
	}

	public abstract void attack(Entity enemy);

	public abstract void target(Entity enemy);
}
