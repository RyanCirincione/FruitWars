package entity;

import java.awt.geom.Point2D;

import data.QuadNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Entity implements QuadNode.Bounded<Entity>
{
	private Image[][] sprite; // a 2d array of [animation][frame]
	protected String type;
	/**
	 * The center of the Entity
	 */
	private Point2D location;
	protected double health;
	/**
	 * The radius of the Entity's hitbox
	 */
	public double radius;
	private int animation, frame; // the indices for the sprite
	/**
	 * If the entity should not separate from other entities
	 */
	public boolean noclip;
	/**
	 * The mass of the entity, used for separation 0 is a bad idea, 1 means it
	 * has infinite mass
	 */
	public float mass;
	/**
	 * If it is friendly to the player
	 */
	protected boolean friendly;
	/**
	 * The QuadNode this entity is stored in
	 */
	private QuadNode<Entity> currentNode;
	/**
	 * The root of the quadtree
	 */
	protected QuadNode<Entity> root;

	public Entity(QuadNode<Entity> root, Image[][] sprite, Point2D location, double radius, boolean friendly,
			double health)
	{
		this.root = root;
		this.sprite = sprite;
		this.location = location;
		noclip = false;
		this.radius = radius;
		this.friendly = friendly;
		this.health = health;
		type = "entity";
	}

	@Override
	public void tick(long millis)
	{

	}

	public void draw(GraphicsContext g2, long millis)
	{
		// advance the current animation
		frame = (int) (frame + 60 * millis / 1000.0) % sprite[animation].length;
		Image tex = getCurrentImage();
		// Draw the object, centered
		g2.drawImage(tex, location.getX() - radius, location.getY() - radius, radius * 2, radius * 2);
	}

	public boolean isFriendly()
	{
		return friendly;
	}
	
	public String getType()
	{
		return type;
	}

	public double getHealth()
	{
		return health;
	}

	public void setHealth(double health)
	{
		this.health = health;
	}

	private Image getCurrentImage()
	{
		return sprite[animation][frame];
	}

	public Image getIcon()
	{
		return sprite[animation][frame];
	}

	/**
	 * Randomizes location slightly for the separation algorithm
	 */
	private void shake(float otherMass)
	{
		location.setLocation(location.getX() + (Math.random() - 0.5), location.getY() + (Math.random() - 0.5));
	}

	/**
	 * Moves entities out of one another
	 */
	public void separate(Entity other, long millis)
	{
		if (noclip || other.noclip || this == other)
			return;
		double radiusSum = radius + other.radius;
		if (!intersects(other))
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
			double moveX = (dx * radiusSum / distance - dx) * (1 - mass) * other.mass;
			double moveY = (dy * radiusSum / distance - dy) * (1 - mass) * other.mass;
			moveX *= 60.0 * millis / 1000.0;
			moveY *= 60.0 * millis / 1000.0;
			location.setLocation(location.getX() + moveX, location.getY() + moveY);
			dx = location.getX() - other.location.getX();
			dy = location.getY() - other.location.getY();
			moveX = (-dx * radiusSum / distance + dx) * mass * (1 - other.mass);
			moveY = (-dy * radiusSum / distance + dy) * mass * (1 - other.mass);
			moveX *= 60.0 * millis / 1000.0;
			moveY *= 60.0 * millis / 1000.0;
			other.location.setLocation(other.location.getX() + moveX, other.location.getY() + moveY);
		}
	}

	@Override
	public Point2D getCenter()
	{
		return location;
	}

	@Override
	public double getRadius()
	{
		return radius;
	}

	@Override
	public void setCenter(double x, double y)
	{
		location.setLocation(x, y);
	}

	@Override
	public QuadNode<Entity> getCurrentNode()
	{
		return currentNode;
	}

	@Override
	public void setCurrentNode(QuadNode<Entity> node)
	{
		currentNode = node;
	}

	@Override
	public void collide(Entity other, long millis)
	{
		separate(other, millis);
	}

	public boolean intersects(Entity other)
	{
		double radSum = radius + other.radius;
		return location.distanceSq(other.location) < radSum * radSum;
	}
	
	@Override
	public boolean equals(Object o)
	{
		//Since two entities cannot overlap, any two with the same location must be the same entity
		return (o instanceof Entity && ((Entity)o).location.equals(location));
	}
}
