package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class Unit extends Entity
{
	private boolean selected;
	private double speed;
	protected double health;
	private Point2D destination;
	private static ArrayList<String> names = loadNames();
	
	public Unit(Image[][] sprite, Point2D location, double radius, double speed, double health)
	{
		super(sprite, location, radius);
		destination = location;
		this.speed = speed;
		this.health = health;
	}

	public void moveToward(long millis)
	{
		if (!location.equals(destination))
		{
			double distance = location.distance(destination);
			if (distance < speed)
			{
				location.setLocation(destination);
			} else
			{
				double x = (destination.getX() - location.getX()) / distance * speed;
				double y = (destination.getY() - location.getY()) / distance * speed;
				x *= -1 * 60.0 * millis / 1000.0;
				y *= -1 * 60.0 * millis / 1000.0;
				location.setLocation(location.getX() + x, location.getY() + y);
			}
		}
	}

	public void tick(long millis)
	{
		moveToward(millis);
	}

	public void draw(Graphics2D g2, long millis)
	{
		if (!location.equals(destination))
		{
			g2.setColor(Color.RED);
			g2.drawOval((int) destination.getX(), (int) destination.getY(), 8, 8);
		}
		super.draw(g2, millis);
		if (selected)
		{
			g2.setColor(Color.CYAN);
			g2.drawOval((int)(location.getX() - radius), (int)(location.getY() - radius), 2 * (int)radius, 2 * (int)radius);
		}
	}

	/**
	 * Source: 2010 US Census
	 */
	private static ArrayList<String> loadNames()
	{
		System.out.println("Loading First Names...");
		ArrayList<String> names = new ArrayList<>();
		try
		{
			Scanner fileScan = new Scanner(new File("assets/firstNames.txt"));
			while(fileScan.hasNextLine())
				names.add(fileScan.nextLine());
		} catch (FileNotFoundException e)
		{
			System.err.println("Could not load a ridiculously long list of first names ¯\\_(ツ)_/¯");
			e.printStackTrace();
		}
		return names;
	}
	
	public String getName()
	{
		return names.get((int)(Math.random() * names.size()));
	}
	
	public void setDestination(Point2D destination)
	{
		this.destination = destination;
	}
	
	public double getHealth()
	{
		return health;
	}
	
	public void setHealth(double health)
	{
		this.health = health;
	}

	public void setSelected(boolean select)
	{
		selected = select;
	}
}
