package entity;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Entity
{
	private Image[][] sprite; // a 2d array of [animation][frame]
	public Point2D location;
	protected double health;
	public double radius;
	private int animation, frame;
	public boolean noclip;
	public float mass;
	protected boolean friendly;

	public Entity(Image[][] sprite, Point2D location, double radius, boolean friendly, double health)
	{
		this.sprite = sprite;
		this.location = location;
		noclip = false;
		this.radius = radius;
		this.friendly = friendly;
		this.health = health;
	}

	public void tick(long millis, ArrayList<Entity> entities)
	{

	}

	public void draw(GraphicsContext g2, long millis)
	{
		// advance the current animation
		frame = (int) (frame + 60 * millis / 1000.0) % sprite[animation].length;
		Image tex = getCurrentImage();
		// Draw the object, centered
		g2.drawImage(tex, location.getX() - tex.getWidth() / 2, location.getY() - tex.getHeight() / 2);
	}

	public boolean isFriendly()
	{
		return friendly;
	}

	public double getHealth()
	{
		return health;
	}

	public void setHealth(double health)
	{
		this.health = health;
	}

	public Image getCurrentImage()
	{
		return sprite[animation][frame];
	}

	private void shake(float otherMass)
	{
		location.setLocation(location.getX() + (Math.random() - 0.5), location.getY() + (Math.random() - 0.5));
	}

	public void separate(Entity other)
	{
		if (noclip || other.noclip || this == other)
			return;
		double radiusSum = radius + other.radius;
		if (location.distanceSq(other.location) > radiusSum * radiusSum)
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
			location.setLocation(location.getX() + (dx * radiusSum / distance - dx) * (1 - mass) * other.mass,
					location.getY() + (dy * radiusSum / distance - dy) * (1 - mass) * other.mass);
			dx = location.getX() - other.location.getX();
			dy = location.getY() - other.location.getY();
			other.location.setLocation(
					other.location.getX() + (-dx * radiusSum / distance + dx) * mass * (1 - other.mass),
					other.location.getY() + (-dy * radiusSum / distance + dy) * mass * (1 - other.mass));
		}
	}
}
