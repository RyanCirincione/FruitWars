package data;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import data.QuadNode.ValidPair;

public class BruteStore<T extends Bounded<T>> implements EntityStore<T>
{

	private List<T> list, buffer;
	private Rectangle tmp1, tmp2;

	public BruteStore()
	{
		list = new FastList<>();
		buffer = new FastList<>();
		tmp1 = new Rectangle();
		tmp2 = new Rectangle();
	}

	@Override
	public void add(T obj)
	{
		buffer.add(obj);
	}

	@Override
	public boolean contains(T obj)
	{
		return list.contains(obj);
	}

	@Override
	public void remove(T obj)
	{
		list.remove(obj);
	}

	@Override
	public void clear()
	{
		list.clear();
	}

	@Override
	public void addContained(Rectangle2D rect, Collection<T> list)
	{
		for(T obj : this.list)
		{
			double x = obj.getCenter().getX();
			double y = obj.getCenter().getY();
			double r = obj.getRadius();
			if(rect.contains(x - r, y - r, r * 2, r * 2))
				list.add(obj);
		}
	}

	@Override
	public void addIntersecting(Rectangle2D rect, Collection<T> list)
	{
		for(T obj : this.list)
		{
			double x = obj.getCenter().getX();
			double y = obj.getCenter().getY();
			double r = obj.getRadius();
			if(rect.intersects(x - r, y - r, r * 2, r * 2))
				list.add(obj);
		}
	}

	@Override
	public T getClosest(T obj, ValidPair<T> func)
	{
		T closest = list.get(0);
		for(T other : list)
			if(func.check(obj, other) && obj.getCenter().distanceSq(other.getCenter()) <= obj.getCenter().distanceSq(closest.getCenter()))
				closest = other;
		return closest;
	}

	@Override
	public T getAtPoint(Point2D pointer)
	{
		for(T obj : list) 
			if(obj.getCenter().distanceSq(pointer) <= obj.getRadius() * obj.getRadius())
				return obj;
		return null;
	}

	@Override
	public void tickAll(long millis)
	{
		for (T obj : list)
			obj.tick(millis);
		for (int i = 0; i < list.size(); i++)
		{
			for (int j = i + 1; j < list.size(); j++)
			{
				T o1 = list.get(i);
				T o2 = list.get(j);
				if (objectsOverlap(o1, o2))
				{
					o1.collide(o2, millis);
					o2.collide(o1, millis);
				}
			}
		}
		list.addAll(buffer);
		buffer.clear();
	}
	
	private boolean objectsOverlap(T o1, T o2)
	{
		double radSum = o1.getRadius() + o2.getRadius();
		return o1.getCenter().distanceSq(o2.getCenter()) <= radSum * radSum;
	}

	@Override
	public boolean areaFree(double x, double y, double radius)
	{
		tmp1.setBounds((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));
		for(T obj : list)
		{
			int ox = (int)obj.getCenter().getX();
			int oy = (int)obj.getCenter().getY();
			int r = (int)obj.getRadius();
			tmp2.setBounds(ox - r, oy - r, r * 2, r * 2);
			if(tmp1.intersects(tmp2))
				return false;
		}
		return true;
	}

	@Override
	public void forEach(Consumer<T> func)
	{
		list.forEach(func);
	}

	@Override
	public void filter(Predicate<T> func, Consumer<T> action)
	{
		for(Iterator<T> iter = list.iterator(); iter.hasNext(); )
		{
			T obj = iter.next();
			if(!func.test(obj))
			{
				action.accept(obj);
				iter.remove();
			}
		}
	}

}
