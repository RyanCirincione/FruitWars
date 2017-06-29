package data;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

import data.QuadNode.ValidPair;

public interface EntityStore<T extends Bounded<T>>
{
	public void add(T obj);
	public boolean contains(T obj); 
	public void remove(T obj);
	public void clear();
	public void addContained(Rectangle2D r, Collection<T> list);
	public void addIntersecting(Rectangle2D r, Collection<T> list);
	public T getClosest(T obj, ValidPair<T> func);
	public T getAtPoint(Point2D pointer);
	public void tickAll(long millis);
	public boolean areaFree(double x, double y, double radius);
	public void forEach(Consumer<T> func);
	public void filter(Predicate<T> func, Consumer<T> consumer);


}
