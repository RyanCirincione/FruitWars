package ui;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.function.Supplier;

import entity.BlueberryBush;
import entity.Entity;
import entity.GrapeVine;
import entity.Structure;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ConstructionBar extends UIComponent
{
	private static final int BORDER = 2, BUTTON_SIZE = 16, BUTTON_BORDER_SIZE = 20;
	private ArrayList<Entity> entities;
	
	private ArrayList<Image> prototypes;
	private ArrayList<StructureBuilder> constructors;
	private Image ghost;
	private int placingIndex;
	private boolean active, placing, handlingClickDown;
	
	private interface StructureBuilder
	{
		public Structure build(Point2D location, Point2D.Double rally, double radius, boolean friendly, double health);
	}
	
	public ConstructionBar(ArrayList<Entity> entities)
	{
		super(new Rectangle(400 - (200 / 2), 300 - (125 / 2), 200, 125));
		this.entities = entities;
		active = false;
		
		constructors = new ArrayList<>();
		constructors.add(GrapeVine::new);
		constructors.add(BlueberryBush::new);
		
		//Prototype structures
		prototypes = new ArrayList<>();
		for(StructureBuilder b : constructors)
			prototypes.add(b.build(null, null, 0, true, 0).getIcon());
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
			for (int i = 0; i < prototypes.size(); i++)
			{
				g2.strokeRect(bounds.x + BORDER + BUTTON_BORDER_SIZE * i, bounds.y + BORDER + BUTTON_BORDER_SIZE * (i/(bounds.width/BUTTON_BORDER_SIZE)),
						BUTTON_BORDER_SIZE, BUTTON_BORDER_SIZE);
				g2.stroke();
				g2.drawImage(prototypes.get(i), bounds.x + (BUTTON_BORDER_SIZE * i) + BORDER + (BUTTON_BORDER_SIZE - BUTTON_SIZE) / 2,
						bounds.y + (BUTTON_BORDER_SIZE * (i/(bounds.width/BUTTON_BORDER_SIZE))) + BORDER + (BUTTON_BORDER_SIZE - BUTTON_SIZE) / 2, BUTTON_SIZE,
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
				if(selectedIndex < prototypes.size())
				{
					ghost = prototypes.get(selectedIndex);
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
				Structure s = null;
				s = constructors.get(placingIndex).build(placement,	new Point2D.Double(placement.getX() + 32, placement.getY()), 48, true, 150);
				for(Entity ent : entities)
				{
					if(ent.location.distanceSq(placement) < ((ent.radius + s.radius) * (ent.radius + s.radius)))
					{
						collides = true;
						break;
					}
				}
				if(!collides)
				{
					entities.add(s);
					if(!e.isShiftDown())
						placing = false;
				}
				return true;
			}
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
