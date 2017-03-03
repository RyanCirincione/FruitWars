package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import javax.swing.JPanel;

import entity.Entity;
import entity.Grape;
import entity.Unit;

public class Canvas extends JPanel implements MouseListener
{
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
		Grape g = (Grape)entities.get(0);
		//*************************************
		
		setFocusable(true);
		addMouseListener(this);
		requestFocus();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		for(Entity e : entities)
			e.draw(g2, millis);
		Point2D mousePos = MouseInfo.getPointerInfo().getLocation();
		if(selecting && !mousePos.equals(selectionCorner))
		{
			g2.setColor(Color.BLUE);
			Point2D topLeft = new Point2D.Double(((selectionCorner.getX() < mousePos.getX())? selectionCorner.getX() : mousePos.getX()),
			((selectionCorner.getY() < mousePos.getY())? selectionCorner.getY() : mousePos.getY()));
			Point2D bottomRight = new Point2D.Double(((selectionCorner.getX() > mousePos.getX())? selectionCorner.getX() : mousePos.getX()),
					((selectionCorner.getY() > mousePos.getY())? selectionCorner.getY() : mousePos.getY()));
			Rectangle2D selectionRect = new Rectangle2D.Double(topLeft.getX(), topLeft.getY(), 
					bottomRight.getX() - topLeft.getX(), bottomRight.getY() - topLeft.getY());
			g2.draw(selectionRect);
			g2.setColor(selectionBlue);
			g2.fill(selectionRect);
			
		}
		Toolkit.getDefaultToolkit().sync();
	}
	
	public void tick()
	{
		millis = prevClock - System.currentTimeMillis();
		for(Entity e : entities)
			e.tick(millis);
		prevClock = System.currentTimeMillis();
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
		selectionCorner = MouseInfo.getPointerInfo().getLocation();
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		Point2D mousePos = MouseInfo.getPointerInfo().getLocation();
		if(!selectionCorner.equals(mousePos))
		{
			Point2D topLeft = new Point2D.Double(((selectionCorner.getX() < mousePos.getX())? selectionCorner.getX() : mousePos.getX()),
					((selectionCorner.getY() < mousePos.getY())? selectionCorner.getY() : mousePos.getY()));
			Point2D bottomRight = new Point2D.Double(((selectionCorner.getX() > mousePos.getX())? selectionCorner.getX() : mousePos.getX()),
					((selectionCorner.getY() > mousePos.getY())? selectionCorner.getY() : mousePos.getY()));
			Rectangle2D selectionRect = new Rectangle2D.Double(topLeft.getX(), topLeft.getY(), 
					bottomRight.getX() - topLeft.getX(), bottomRight.getY() - topLeft.getY());
			selectedUnits = new ArrayList<Unit>();
			for(Entity en : entities)
			{
				if(en instanceof Unit)
				{
					if(selectionRect.contains(en.getLocation()))
						selectedUnits.add((Unit)en);
				}
			}
			selecting = false;
		}
	}
}
