package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;

public class Unit extends Entity {

	private double speed;
	private Point2D destination;
	
	public Unit(Image[][] sprite, Point2D location, double speed) 
	{
		super(sprite, location);
		this.speed = speed;
	}
	
	public void moveToward(long millis)
	{
		if(!location.equals(destination))
		{
			double xDist = destination.getX() - location.getX();
			double yDist = destination.getY() - location.getY();
			double theta = Math.atan(Math.abs(yDist/xDist));
			double xMove = speed * Math.cos(theta) * ((xDist < 0)? 1 : -1);
			double yMove = speed * Math.sin(theta) * ((yDist < 0)? 1 : -1);
			xMove *= (60.0 * (millis/1000.0));
			yMove *= (60.0 * (millis/1000.0));
			if(Math.abs(xDist) < Math.abs(xMove))
				location.setLocation(destination.getX(), location.getY());
			else
				location.setLocation(location.getX() + xMove, location.getY());
			if(Math.abs(yDist) < Math.abs(yMove))
				location.setLocation(location.getX(), destination.getY());
			else
				location.setLocation(location.getX(), location.getY() + yMove);
		}
	}
	
	public void tick(long millis)
	{
		moveToward(millis);
	}
	
	public void draw(Graphics2D g2, long millis)
	{
		if(!location.equals(destination))
		{
			g2.setColor(Color.RED);
			g2.drawOval((int)destination.getX(), (int)destination.getY(), 8, 8);
		}
		super.draw(g2, millis);
	}
	
	public void setDestination(Point2D destination)
	{
		this.destination = destination;
	}
}
