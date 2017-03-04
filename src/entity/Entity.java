package entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;

public class Entity
{
	private Image[][] sprite; // a 2d array of [animation][frame]
	public Point2D location;
	public double radius;
	private int animation, frame;
	public boolean noclip;
	public float mass;

	public Entity(Image[][] sprite, Point2D location, double radius)
	{
		this.sprite = sprite;
		this.location = location;
		noclip = false;
		this.radius = radius;
	}

	public void tick(long millis)
	{

	}

	public void draw(Graphics2D g2, long millis)
	{
		// advance the current animation
		frame = (int) (frame + 60 * millis / 1000.0) % sprite[animation].length;
		Image tex = sprite[animation][frame];
		// Draw the object, centered
		g2.drawImage(tex, (int) (location.getX() - tex.getWidth(null) / 2),
				(int) (location.getY() - tex.getHeight(null) / 2), null);
	}

	private void shake(float otherMass)
	{
		location.setLocation(location.getX() + (Math.random() - 0.5), location.getY() + (Math.random() - 0.5));
	}

	// TODO: It's broken
	public void separate(Entity other)
	{
		if (noclip || other.noclip || this == other)
			return;
		double radiusSum = radius + other.radius;
		if (location.distanceSq(other.location) > radiusSum)
			return;
		// If the two objects are perfectly centered, shake them around a bit
		if (location.equals(other.location))
		{
			shake(other.mass);
			other.shake(mass);
		} else
		{
			double distance = location.distance(other.location);
			double dx = location.getX() - other.location.getX();
			double dy = location.getY() - other.location.getY();
			location.setLocation(location.getX() + (dx * radiusSum / distance) * (1 - mass) * other.mass,
					location.getY() + (dy * radiusSum / distance) * (1 - mass) * other.mass);
			dx = location.getX() - other.location.getX();
			dy = location.getY() - other.location.getY();
			other.location.setLocation(other.location.getX() + (-dx * radiusSum / distance) * mass * (1 - other.mass),
					other.location.getY() + (-dy * radiusSum / distance) * mass * (1 - other.mass));
		}
	}
}
