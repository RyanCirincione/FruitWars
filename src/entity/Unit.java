package entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;

public class Unit extends Entity
{
	private boolean selected;
	private double speed;
	private Point2D destination;

	public Unit(Image[][] sprite, Point2D location, double radius, double speed)
	{
		super(sprite, location, radius);
		destination = location;
		this.speed = speed;
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
		Composite previous = g2.getComposite();
		if(selected)
		{
			g2.setComposite(AlphaComposite.Xor); //todo: find a better effect
		}
		super.draw(g2, millis);
		g2.setComposite(previous);
		
	}

	public void setDestination(Point2D destination)
	{
		this.destination = destination;
	}

	public void setSelected(boolean select)
	{
		selected = select;
	}
}
