package entity;

import java.awt.geom.Point2D;

import data.QuadNode;
import javafx.scene.image.Image;

public abstract class Structure extends Entity
{
	public Structure(QuadNode<Entity> root, Image[][] sprite, Point2D location, double radius, boolean friendly, double health)
	{
		super(root, sprite, location, radius, friendly, health);
		mass = 1;
	}
}
