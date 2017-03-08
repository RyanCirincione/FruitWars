package ui;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import entity.Entity;
import entity.GrapeVine;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ConstructionBar extends UIComponent
{
	private static final int BORDER = 2, BUTTON_SIZE = 16, BUTTON_BORDER_SIZE = 20;
	private Image[] icons = new Image[1];
	private ArrayList<Entity> entities;
	private Image ghost;
	private int placingIndex;
	private boolean active, placing, handlingClickDown;

	public ConstructionBar(ArrayList<Entity> entities)
	{
		super(new Rectangle(400 - (200 / 2), 300 - (125 / 2), 200, 125));
		icons[0] = GrapeVine.sprite[0][0];
		this.entities = entities;
		active = false;
	}
	
	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	public boolean getActive()
	{
		return active;
	}
	
	@Override
	public void draw(GraphicsContext g2, long millis, Point2D mouse)
	{
		if(active)
		{
			g2.setFill(UIComponent.FILL_GREEN);
			g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			g2.fill();
			g2.setStroke(Color.GREEN);
			g2.strokeRect(bounds.x, bounds.y, bounds.width, bounds.height);
			
			// Drawing buttons
			for (int i = 0; i < icons.length; i++)
			{
				g2.strokeRect(bounds.x + BORDER + BUTTON_BORDER_SIZE * i, bounds.y + BORDER + BUTTON_BORDER_SIZE * i,
						BUTTON_BORDER_SIZE, BUTTON_BORDER_SIZE);
				g2.stroke();
				g2.drawImage(icons[i], bounds.x + (BUTTON_SIZE * i) + BORDER + (BUTTON_BORDER_SIZE - BUTTON_SIZE) / 2,
						bounds.y + (BUTTON_SIZE * i) + BORDER + (BUTTON_BORDER_SIZE - BUTTON_SIZE) / 2, BUTTON_SIZE,
						BUTTON_SIZE);
			}
		}
		if(placing)
		{
			g2.drawImage(ghost, mouse.getX() - ghost.getWidth() / 2, mouse.getY() - ghost.getHeight() / 2);
		}
	}
	
	private int mousePosToIndex(double mouseX, double mouseY)
	{
		int index = (int)((mouseY - (bounds.y + BORDER)) / BUTTON_BORDER_SIZE) * (bounds.width / BUTTON_BORDER_SIZE);
		index += (mouseX - (bounds.x + BORDER)) / BUTTON_BORDER_SIZE;
		return index;
	}


	@Override
	public boolean handlePressed(MouseEvent e)
	{
		if(active)
		{
			if(bounds.contains(new Point2D.Double(e.getX(), e.getY())) && !placing)
			{
				int selectedIndex = mousePosToIndex(e.getX(), e.getY());
				if(selectedIndex < icons.length)
				{
					ghost = icons[selectedIndex];
					placingIndex = selectedIndex;
					placing = true;
					handlingClickDown = true;
					return true;
				}
			}
		}
		if(!handlingClickDown)
		{
			if(placing)
			{
				Point2D placement = new Point2D.Double(e.getX(), e.getY());
				boolean collides = false;
				switch (placingIndex)
				{
				case 0:
					GrapeVine g = new GrapeVine(placement,
							new Point2D.Double(placement.getX() + 32, placement.getY()), 48, true, 150);
					for(Entity ent : entities)
					{
						if(ent.location.distanceSq(placement) < ((ent.radius + g.radius) * (ent.radius + g.radius)))
						{
							collides = true;
							break;
						}
					}
					if(collides)
						break;
					entities.add(g);
					break;
				default:
					break;
				}
				if(!e.isShiftDown() && !collides)
					placing = false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean handleReleased(MouseEvent e)
	{
		if(active && handlingClickDown)
		{
			handlingClickDown = false;
			return true;
		}
		return false;
	}

}
