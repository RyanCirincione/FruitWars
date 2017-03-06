package entity;

import java.awt.Image;
import java.awt.geom.Point2D;

public class Structure extends Entity
{
	public Structure(Image[][] sprite, Point2D location, double radius)
	{
		super(sprite, location, radius);
		mass = 1;
	}
}
