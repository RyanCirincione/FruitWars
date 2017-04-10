package entity;

import java.awt.geom.Point2D;

import data.EntityStore;

public interface StructureBuilder
{
	public Structure build(EntityStore<Entity> root, Point2D location, boolean friendly, double health);
}
