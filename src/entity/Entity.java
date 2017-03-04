package entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;

public class Entity 
{
	private Image[][] sprite; //a 2d array of [animation][frame]
	public Point2D location;
	private int animation, frame;
	
	public Entity(Image[][] sprite, Point2D location)
	{
		this.sprite = sprite;
		this.location = location;
	}
	
	public void tick(long millis)
	{
		
	}
	
	public void draw(Graphics2D g2, long millis)
	{
		//advance the current animation
		frame = (int)(frame + 60 * millis / 1000.0) % sprite[animation].length;
		g2.drawImage(sprite[animation][frame], (int)location.getX(), (int)location.getY(), null);
	}
}
