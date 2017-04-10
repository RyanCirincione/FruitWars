package entity;

import java.awt.geom.Point2D;

import data.EntityStore;

public interface UnitBuilder
{
	public Unit build(EntityStore<Entity> root, Point2D location, boolean friendly);
}
