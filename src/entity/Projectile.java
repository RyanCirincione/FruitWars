package entity;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import data.EntityStore;
import javafx.scene.image.Image;

public class Projectile extends Entity
{
	public final static double MAX_HEALTH = 1000;
	public static Image[][] sprite = loadSprite();
	private double range, damage, speed;
	private Point2D velocity, startingPoint;
	private Unit firingUnit;

	public Projectile(EntityStore<Entity> root, Point2D location, Point2D target, Unit firingUnit, double radius,
			double speed, double damage, double range)
	{
		super(root, sprite, location, radius, firingUnit.friendly, MAX_HEALTH);

		double distance = location.distance(target);
		this.speed = speed;
		double x = (target.getX() - location.getX()) / distance;
		double y = (target.getY() - location.getY()) / distance;
		velocity = new Point2D.Double(x, y);
		this.range = range;
		this.damage = damage;
		location.setLocation(startingPoint = new Point2D.Double(location.getX(), location.getY()));
		mass = 0f;
		noclip = true;
		type = "projectile";
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
	public void tick(long millis)
	{
		double x = velocity.getX() * 60.0 * millis / (1000.0 / speed);
		double y = velocity.getY() * 60.0 * millis / (1000.0 / speed);
		Point2D location = getCenter();
		location.setLocation(location.getX() + x, location.getY() + y);
		// Out of range
		if (startingPoint.distanceSq(location) > range * range)
			health = 0;
		else
			super.tick(millis);
	}

	@Override
	public void collide(Entity e, long milis)
	{
		if (e.isFriendly() != isFriendly() && !(e instanceof Projectile))
		{
			if(e.getHealth() <= damage && firingUnit != null)
				firingUnit.addKill();
			e.setHealth(e.getHealth() - damage);
			health = -1;
		}
	}
}
