package entity;

import java.awt.geom.Point2D;

import data.FastList;
import data.QuadNode;
import javafx.scene.image.Image;

public abstract class Structure extends Entity
{
	static FastList<String> techRequired, techAvoided;
	
	public Structure(QuadNode<Entity> root, Image[][] sprite, Point2D location, double radius, boolean friendly,
			double health)
	{
		super(root, sprite, location, radius, friendly, health);
		mass = 1;
		techRequired = new FastList<String>();
		techAvoided = new FastList<String>();
		type = "structure";
	}
}
