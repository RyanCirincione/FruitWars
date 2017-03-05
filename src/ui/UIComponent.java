package ui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

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
	public abstract void draw(Graphics2D g2, Point mousePos, long millis);
	
	public abstract boolean handlePressed(MouseEvent e);
	public abstract boolean handleReleased(MouseEvent e);

}
