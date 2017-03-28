package data;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A quadtree to make collision checking between objects more efficient
 */
public class QuadNode<T extends Bounded<T>> implements EntityStore<T>
{

	public static interface ValidPair<T>
	{
		boolean check(T one, T two);
	}

	List<T> contained;
	List<QuadNode<T>> children;
	private List<T> buffer;
	private Rectangle bounds, temp;

	public QuadNode(Rectangle bounds, int smallestWidth, int smallestHeight)
	{
		contained = new FastList<>();
		children = new FastList<>(4);
		buffer = new FastList<>();
		temp = new Rectangle();
		this.bounds = bounds;
		int childWidth = (int) (this.bounds.getWidth() / 2);
		int childHeight = (int) (this.bounds.getHeight() / 2);
		int xOffset = childWidth / 2;
		int yOffset = childHeight / 2;
		if (childWidth >= smallestWidth && childHeight >= smallestHeight)
		{
			children.add(
					new QuadNode<T>(new Rectangle((int) bounds.getX(), (int) bounds.getY(), childWidth, childHeight),
							smallestWidth, smallestHeight));
			children.add(new QuadNode<T>(
					new Rectangle((int) bounds.getX() + xOffset * 2, (int) bounds.getY(), childWidth, childHeight),
					smallestWidth, smallestHeight));
			children.add(new QuadNode<T>(new Rectangle((int) bounds.getX() + xOffset * 2, (int) bounds.getY() + yOffset * 2,
					childWidth, childHeight), smallestWidth, smallestHeight));
			children.add(new QuadNode<T>(
					new Rectangle((int) bounds.getX(), (int) bounds.getY() + yOffset * 2, childWidth, childHeight),
					smallestWidth, smallestHeight));
		}
	}

	private boolean rectIntersectsObject(T obj, Rectangle r)
	{
		Point2D location = obj.getCenter();
		double radius = obj.getRadius();
		return r.intersects(location.getX() - radius, location.getY() - radius, 2 * radius, 2 * radius);
	}

	private boolean rectContainsObject(T obj, Rectangle r)
	{
		Point2D location = obj.getCenter();
		double radius = obj.getRadius();
		return r.contains(location.getX() - radius, location.getY() - radius, 2 * radius, 2 * radius);
	}

	private boolean objectsOverlap(T o1, T o2)
	{
		double radSum = o1.getRadius() + o2.getRadius();
		return o1.getCenter().distanceSq(o2.getCenter()) <= radSum * radSum;
	}

	public boolean contains(Rectangle r)
	{
		return bounds.contains(r);
	}

	public boolean contains(double x, double y, double width, double height)
	{
		return bounds.contains(x, y, width, height);
	}

	public boolean intersects(double x, double y, double width, double height)
	{
		return bounds.intersects(x, y, width, height);
	}

	public boolean contains(T obj)
	{
		return rectContainsObject(obj, bounds);
	}

	private QuadNode<T> findChild(T obj)
	{
		for (QuadNode<T> child : children)
		{
			if (child.contains(obj))
			{
				return child;
			}
		}
		return null;
	}

	public void add(T obj)
	{
		for(QuadNode<T> child : children)
			if(child.contains(obj))
			{
				child.add(obj);
				return;
			}
		buffer.add(obj);
	}

	public void remove(T obj)
	{
		((QuadNode<T>)obj.getCurrentNode()).contained.remove(obj);
		obj.setCurrentNode(null);
	}

	public void updateNode(T obj)
	{
		QuadNode<T> current = ((QuadNode<T>)obj.getCurrentNode());
		if (!current.contains(obj))
		{
			current.contained.remove(obj);
			add(obj);
		}
	}

	public void clear()
	{
		contained.clear();
		for (QuadNode<T> child : children)
			child.clear();
	}

	public void addContained(Rectangle2D r, Collection<T> list)
	{
		addContained((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight(), list);
	}

	public void addContained(int x, int y, int width, int height, Collection<T> list)
	{
		for (QuadNode<T> child : children)
		{
			if (child.intersects(x, y, width, height))
			{
				child.addContained(x, y, width, height, list);
			}
		}
		temp.setBounds(x, y, width, height);
		for (T obj : contained)
			if (rectContainsObject(obj, temp))
				list.add(obj);
	}

	public void addIntersecting(Rectangle2D r, Collection<T> list)
	{
		addIntersecting((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight(), list);
	}

	public void addIntersecting(int x, int y, int width, int height, Collection<T> list)
	{
		for (QuadNode<T> child : children)
		{
			child.addIntersecting(x, y, width, height, list);
		}
		temp.setBounds(x, y, width, height);
		for (T obj : contained)
			if (rectIntersectsObject(obj, temp))
				list.add(obj);
	}

	public T getClosest(T obj, ValidPair<T> func)
	{
		T closest = null;
		for (QuadNode<T> child : children)
		{
			T nodeClosest = child.getClosest(obj, func);
			if (closest == null)
				closest = nodeClosest;
			else if (nodeClosest != null && obj.getCenter().distanceSq(nodeClosest.getCenter()) < obj.getCenter()
					.distanceSq(closest.getCenter()))
			{
				closest = nodeClosest;
			}
		}
		for (T other : contained)
		{
			if (closest == null && func.check(obj, other))
				closest = other;
			else if (func.check(obj, other)
					&& obj.getCenter().distanceSq(other.getCenter()) < obj.getCenter().distanceSq(closest.getCenter()))
			{
				closest = other;
			}
		}
		return closest;
	}

	public T getAtPoint(Point2D pointer)
	{
		for (T obj : contained)
			if (obj.getCenter().distanceSq(pointer) < obj.getRadius() * obj.getRadius())
				return obj;
		for (QuadNode<T> child : children)
		{
			if (!child.bounds.contains(pointer))
				break;
			T returned = child.getAtPoint(pointer);
			if (returned != null)
				return returned;
		}
		return null;
	}

	private void checkCollisions(QuadNode<T> checkAgainst, long millis)
	{
		for (T outer : checkAgainst.contained)
		{
			for (T inner : contained)
			{
				if (objectsOverlap(outer, inner))
				{
					outer.collide(inner, millis);
					inner.collide(outer, millis);
				}
			}
		}
		for (QuadNode<T> child : children)
			child.checkCollisions(checkAgainst, millis);
	}

	public void tickAll(long millis)
	{
		for (T obj : contained)
			obj.tick(millis);
		for (int i = 0; i < contained.size(); i++)
		{
			for (int j = i + 1; j < contained.size(); j++)
			{
				T o1 = contained.get(i);
				T o2 = contained.get(j);
				if (objectsOverlap(o1, o2))
				{
					o1.collide(o2, millis);
					o2.collide(o1, millis);
				}
			}
		}
		checkCollisions(this, millis);
		for (QuadNode<T> child : children)
		{
			child.checkCollisions(this, millis);
			child.tickAll(millis);
		}
		for (T child : contained)
			updateNode(child);
		for (T obj : buffer)
		{
			QuadNode<T> child = findChild(obj);
			if (child != null)
				child.add(obj);
			else
			{
				contained.add(obj);
				obj.setCurrentNode(this);
			}
		}
		buffer.clear();
	}

	public boolean areaFree(double x, double y, double radius)
	{
		for (T obj : contained)
		{
			double radSum = obj.getRadius() + radius;
			if (obj.getCenter().distanceSq(x, y) < radSum * radSum)
				return false;
		}
		for (QuadNode<T> child : children)
		{
			if (!child.contains(x - radius, y - radius, 2 * radius, 2 * radius))
				break;
			boolean free = child.areaFree(x, y, radius);
			if (!free)
				return false;
		}
		return true;
	}

	public void forEach(Consumer<T> func)
	{
		for (T obj : contained)
			func.accept(obj);
		for (QuadNode<T> child : children)
			child.forEach(func);
	}

	public void filter(Predicate<T> func, Consumer<T> consume)
	{
		for(Iterator<T> iter = contained.iterator(); iter.hasNext(); )
		{
			T obj = iter.next();
			if(!func.test(obj))
			{
				consume.accept(obj);
				iter.remove();
			}
		}
		for (QuadNode<T> child : children)
		{
			child.filter(func, consume);
		}
	}
	
	public String toString() 
	{
		return toString(0);
	}
	
	private String toString(int indent) {
		String tabs = "";
		for(int i = 0; i < indent; i++) {
			tabs += "\t";
		}
		String thisString = tabs + "QuadNode " + bounds.toString() + "\n" + tabs + buffer.toString();
		for(QuadNode<T> child : children)
			thisString += "\n" + child.toString(indent + 1);
		return thisString;
	}
}
