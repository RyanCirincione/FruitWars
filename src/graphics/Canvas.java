package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import entity.Entity;
import entity.Grape;
import entity.Unit;

public class Canvas extends JPanel implements MouseAdapter, KeyAdapter
{
	private static final long serialVersionUID = 1L;

	private ArrayList<Entity> entities;
	private ArrayList<Unit> selectedUnits;
	private Point2D selectionCorner;
	private Point mousePosition;
	private boolean selecting;
	private long prevClock, millis;
	private Color selectionBlue = new Color(102, 153, 255, 64);

	public Canvas()
	{
		setPreferredSize(new Dimension(800, 600));
		prevClock = System.currentTimeMillis();
		entities = new ArrayList<>();
		selectedUnits = new ArrayList<>();
		selecting = false;
		selectionCorner = new Point2D.Double(0, 0);
		for (int i = 0; i < 15; i++)
			entities.add(new Grape(new Point2D.Double(200 + i, 200 + i)));

		setFocusable(true);
		addMouseListener(this);
		addKeyListener(this);
		requestFocus();
		mousePosition = new Point();
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g); // clear the screen
		Graphics2D g2 = (Graphics2D) g;
		for (Entity e : entities)
			e.draw(g2, millis);
		Point2D mousePos = getMousePosition();
		if (selecting && !mousePos.equals(selectionCorner))
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
		millis = prevClock - System.currentTimeMillis(); // calculate delta time
		for(int i = 0; i < entities.size(); i++)
		{
			entities.get(i).tick(millis);
			for(int j = i; j < entities.size(); j++)
				entities.get(i).separate(entities.get(j));
		}
		prevClock = System.currentTimeMillis();
	}

	/**
	 * Returns a reference to a single object, please copy before modifying
	 */
	public Point getMousePosition()
	{
		mousePosition.setLocation(MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x,
				MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y);
		return mousePosition;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			selecting = true;
			selectionCorner.setLocation(getMousePosition());
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			clearSelected(); // Unselect units
		}
		if (e.getKeyCode() == KeyEvent.VK_C)
		{
			selectedUnits.forEach(unit -> unit.setDestination(unit.location)); // Stop
																			   // moving
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			Rectangle2D selectionRect = getSelectionRect();
			clearSelected();
			entities.stream().filter(ent -> ent instanceof Unit).map(ent -> (Unit) ent)
					.filter(unit -> selectionRect.contains(unit.location)).forEach(unit -> {
						unit.setSelected(true);
						selectedUnits.add(unit);
					});
			selecting = false;
		} else
		{
			Point2D destination = new Point2D.Double(e.getX(), e.getY());
			selectedUnits.forEach(unit -> unit.setDestination(destination));
			selecting = false;
		}
	}

	private void clearSelected()
	{
		selectedUnits.forEach(unit -> unit.setSelected(false));
		selectedUnits.clear();
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
