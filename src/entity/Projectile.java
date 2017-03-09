package entity;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.scene.image.Image;

public class Projectile extends Entity
{
	public final static double MAX_HEALTH = 1000;
	private boolean friendly;
	public static Image[][] sprite = loadSprite();
	private double range, radius, damage, speed;
	private Point2D velocity, startingPoint;
	
	public Projectile(Point2D location, Point2D target, boolean friendly, double radius, double speed, double damage, double range)
	{
		super(sprite, location, radius, friendly, MAX_HEALTH);
		
		double distance = location.distance(target);
		this.speed = speed;
		double x = (target.getX() - location.getX()) / distance;
		double y = (target.getY() - location.getY()) / distance;
		velocity = new Point2D.Double(x, y);
		this.range = range;
		this.damage = damage;
		location.setLocation(startingPoint = new Point2D.Double(location.getX(), location.getY()));
		mass = 0f;
	}
	
	private static Image[][] loadSprite()
	{
		try
		{
			sprite = new Image[1][1];
			sprite[0][0] = new Image(new FileInputStream("assets/tempProjectile.png"));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return sprite;
	}
	
	@Override
	public void tick(long millis, ArrayList<Entity> entities)
	{
		double x = velocity.getX() * 60.0 * millis / (1000.0 / speed);
		double y = velocity.getY() * 60.0 * millis / (1000.0 / speed);
		location.setLocation(location.getX() + x, location.getY() + y);
		//Out of range
		if(startingPoint.distanceSq(location) > range * range)
			entities.remove(this);
		else
		{
			for (Entity e : entities)
			{
				double radiusSum = radius + e.radius;
				if (!(e.isFriendly() == isFriendly()) && location.distanceSq(e.location) <= radiusSum * radiusSum && !(e instanceof Projectile))
				{
					e.setHealth(e.getHealth() - damage);
					health = 0;
					break;
				}
			}
			super.tick(millis, entities);
		}
	}
}
