package data;

import java.awt.geom.Point2D;

/**
 * An interface for objects that can be inserted into the quadtree
 */
public interface Bounded<E extends Bounded<E>>
{
	public Point2D getCenter();

	double getRadius();

	void setCenter(double x, double y);

	EntityStore<E> getCurrentNode();

	void setCurrentNode(EntityStore<E> node);

	void collide(E other, long millis);

	void tick(long millis);
}