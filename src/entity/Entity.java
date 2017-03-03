package entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D;

public class Entity 
{
	private Image[][] sprite;
	protected Point2D location;
	private int animation, frame;
	
	public Entity(Image[][] sprite, Point2D location)
	{
		this.sprite = sprite;
		this.location = location;
	}
	
	public Point2D getLocation()
	{
		return location;
	}
	
	public void tick(long millis)
	{
		
	}
	
	public void draw(Graphics2D g2, long millis)
	{
		frame = (frame == sprite[animation].length - 1)? 0 : (int)(frame + (60.0 * (millis/1000.0)));
		g2.drawImage(sprite[animation][frame], (int)location.getX(), (int)location.getY(), null);
	}
}
