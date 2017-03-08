package ui;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public abstract class UIComponent
{
	protected Rectangle bounds;
	public static final Color FILL_GREEN = Color.color(102.0 / 255, 255.0 / 255, 102 / 255);

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
