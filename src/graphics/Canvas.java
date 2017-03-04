package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import entity.Entity;
import entity.Grape;
import entity.Unit;

public class Canvas extends JPanel implements MouseListener
{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Entity> entities;
	private ArrayList<Unit> selectedUnits;
	private Point2D selectionCorner;
	private boolean selecting;
	private long prevClock, millis;
	private Color selectionBlue = new Color(102, 153, 255, 64);
	
	public Canvas()
	{
		setPreferredSize(new Dimension(800, 600));
		prevClock = System.currentTimeMillis();
		entities = new ArrayList<Entity>();
		selectedUnits = new ArrayList<Unit>();
		selecting = false;
		selectionCorner = new Point2D.Double(0, 0);
		//test*********************************
		entities.add(new Grape(new Point2D.Double(200, 200)));
		//selectedUnits.add((Unit)entities.get(0));
		entities.get(0);
		//*************************************
		
		setFocusable(true);
		addMouseListener(this);
		requestFocus();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g); //clear the screen
		Graphics2D g2 = (Graphics2D)g;
		for(Entity e : entities)
			e.draw(g2, millis);
		Point2D mousePos = getMousePosition();
		if(selecting && !mousePos.equals(selectionCorner))
		{
			g2.setColor(Color.BLUE);
			Rectangle2D selectionRect = getSelectionRect();
			g2.draw(getSelectionRect());
			g2.setColor(selectionBlue);
			g2.fill(selectionRect);
			
		}
		Toolkit.getDefaultToolkit().sync();
	}
	
	public void tick()
	{
		millis = prevClock - System.currentTimeMillis(); //calculate delta time
		for(Entity e : entities)
			e.tick(millis);
		prevClock = System.currentTimeMillis();
	}
	
	public Point getMousePosition()
	{
		return new Point(MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x, 
				MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y);
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		for(Unit u : selectedUnits)
			u.setDestination(new Point2D.Double(e.getX(), e.getY()));
		selecting = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{
			
	}

	@Override
	public void mouseExited(MouseEvent e) 
	{
			
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		selecting = true;
		selectionCorner = getMousePosition();
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		Point2D mousePos = getMousePosition();
		if(!selectionCorner.equals(mousePos))
		{
			Rectangle2D selectionRect = getSelectionRect();
			selectedUnits = new ArrayList<Unit>();
			for(Entity en : entities)
			{
				if(en instanceof Unit)
				{
					if(selectionRect.contains(en.location))
						selectedUnits.add((Unit)en);
				}
			}
			selecting = false;
		}
	}
	
	private Rectangle2D getSelectionRect() 
	{
		Point2D mousePos = getMousePosition();
		double x = Math.min(selectionCorner.getX(), mousePos.getX());
		double y = Math.min(selectionCorner.getY(), mousePos.getY());
		double width = Math.max(selectionCorner.getX(), mousePos.getX()) - x;
		double height = Math.max(selectionCorner.getY(), mousePos.getY()) - y;
		return new Rectangle2D.Double(x, y, width, height);
	}
}
