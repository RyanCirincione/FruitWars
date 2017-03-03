package graphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import entity.Entity;
import entity.Grape;
import entity.Unit;

public class Canvas extends JPanel implements MouseListener
{
	private ArrayList<Entity> entities;
	private ArrayList<Unit> selectedUnits;
	private long prevClock, millis;
	public Canvas()
	{
		setPreferredSize(new Dimension(800, 600));
		prevClock = System.currentTimeMillis();
		entities = new ArrayList<Entity>();
		selectedUnits = new ArrayList<Unit>();
		//test*********************************
		entities.add(new Grape(new Point2D.Double(200, 200)));
		selectedUnits.add((Unit)entities.get(0));
		Grape g = (Grape)entities.get(0);
		g.setDestination(new Point2D.Double(400, 400));
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
		
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		
	}
}
