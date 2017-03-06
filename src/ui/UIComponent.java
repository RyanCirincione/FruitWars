package ui;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

public abstract class UIComponent
{
	protected Rectangle bounds;

	public UIComponent(Rectangle bounds)
	{
		this.bounds = bounds;
	}

	public Rectangle getBounds()
	{
		return bounds;
	}

	public abstract void draw(GraphicsContext g2, long millis, Point2D mouse);

	public abstract boolean handlePressed(MouseEvent e);

	public abstract boolean handleReleased(MouseEvent e);

}
