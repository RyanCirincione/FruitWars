package entity;

import java.awt.geom.Point2D;
import java.util.List;

import data.EntityStore;
import data.FastList;
import javafx.scene.image.Image;

public abstract class Structure extends Entity
{
	static List<Class<? extends Entity>> techRequired, techAvoided;
	
	public Structure(EntityStore<Entity> root, Image[][] sprite, Point2D location, double radius, boolean friendly,
			double health)
	{
		super(root, sprite, location, radius, friendly, health);
		mass = 1;
		techRequired = new FastList<>();
		techAvoided = new FastList<>();
		type = "structure";
	}
}
