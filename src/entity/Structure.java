package entity;

import java.awt.geom.Point2D;

import javafx.scene.image.Image;

public abstract class Structure extends Entity
{
	public Structure(Image[][] sprite, Point2D location, double radius, boolean friendly, double health)
	{
		super(sprite, location, radius, friendly, health);
		mass = 1;
	}
}
