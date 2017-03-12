package fruitwars;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import data.FastList;
import entity.BlueberryBush;
import entity.Entity;
import entity.GrapeVine;
import entity.Unit;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import ui.ConstructionBar;
import ui.UIComponent;
import ui.UnitSelectionBar;

public class Game extends Scene
{
	private GraphicsContext g;
	private List<Entity> entities;
	private List<Unit> selectedUnits;
	private List<UIComponent> gui;
	private ConstructionBar cBar;
	private boolean selecting;
	private Color selectionBlue = new Color(102.0 / 255, 153.0 / 255, 1, 64 / 255.0);
	private Point2D selectionCorner, mousePosition;

	public Game(Group root, GraphicsContext ctx)
	{
		super(root);
		g = ctx;
		entities = new FastList<>();
		selectedUnits = new FastList<>();

		selecting = false;
		selectionCorner = new Point2D.Double(0, 0);
		mousePosition = new Point2D.Double();

		gui = new FastList<>();
		gui.add(new UnitSelectionBar(selectedUnits));
		cBar = new ConstructionBar(entities);
		gui.add(cBar);

		addEventHandler(MouseEvent.ANY, this::mouseMove);
		addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
		addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
		addEventHandler(KeyEvent.KEY_PRESSED, this::keyPressed);
		addEventHandler(KeyEvent.KEY_RELEASED, this::keyReleased);

		GrapeVine e = new GrapeVine(new Point2D.Double(100, 100), true, 150);
		e.setRally(new Point2D.Double(600, 300));
		entities.add(e);
		e = new GrapeVine(new Point2D.Double(100, 500), true, 150);
		e.setRally(new Point2D.Double(600, 300));
		entities.add(e);
		e = new GrapeVine(new Point2D.Double(600, 100), true, 150);
		e.setRally(new Point2D.Double(100, 300));
		entities.add(e);
		e = new GrapeVine(new Point2D.Double(600, 500), true, 150);
		e.setRally(new Point2D.Double(100, 300));
		entities.add(e);
		BlueberryBush b = new BlueberryBush(new Point2D.Double(100, 300), true, 150);
		b.setRally(new Point2D.Double(600, 300));
		entities.add(b);
		b = new BlueberryBush(new Point2D.Double(600, 300), true, 150);
		b.setRally(new Point2D.Double(100, 300));
		entities.add(b);
	}

	public void tick(long milli)
	{
		for (int i = 0; i < entities.size(); i++)
		{
			entities.get(i).tick(milli, entities);
			for (int j = i + 1; j < entities.size(); j++)
			{
				entities.get(i).separate(entities.get(j), milli);
			}
		}
		for(Iterator<Entity> iter = entities.iterator(); iter.hasNext(); )
		{
			Entity ent = iter.next();
			if(ent.getHealth() <= 0)
			{
				iter.remove();
				selectedUnits.remove(ent);
			}
		}
	}

	public void draw(long milli)
	{
		g.clearRect(0, 0, FruitWars.WINDOW_WIDTH, FruitWars.WINDOW_HEIGHT);
		for (Entity e : entities)
			e.draw(g, milli);
		for (UIComponent u : gui)
			u.draw(g, milli, mousePosition);
		if (selecting && !mousePosition.equals(selectionCorner))
		{
			g.setStroke(Color.BLUE);
			Rectangle2D selectionRect = getSelectionRect();
			g.strokeRect(selectionRect.getX(), selectionRect.getY(), selectionRect.getWidth(),
					selectionRect.getHeight());
			g.stroke();
			g.setFill(selectionBlue);
			g.fillRect(selectionRect.getX(), selectionRect.getY(), selectionRect.getWidth(), selectionRect.getHeight());
			g.fill();
		}
	}

	public void mouseMove(MouseEvent e)
	{
		mousePosition.setLocation(e.getX(), e.getY());
	}

	public void keyPressed(KeyEvent e)
	{
		
	}

	public void keyReleased(KeyEvent e)
	{
		switch (e.getCode())
		{
		case SPACE:
			clearSelected(); // Unselect units
			break;
		case C:
			selectedUnits.forEach(unit -> unit.setDestination(unit.location)); // Stop
			// moving
			break;
		case B:
			cBar.setActive(!cBar.getActive());
		default:
			break;
		}
	}

	public void mousePressed(MouseEvent e)
	{
		boolean handled = false;
		for (UIComponent u : gui)
		{
			handled = u.handlePressed(e);
			if (handled)
				break;
		}
		if (!handled)
		{
			if (e.getButton() == MouseButton.PRIMARY)
			{
				selecting = true;
				selectionCorner.setLocation(mousePosition);
			}
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		boolean handled = false;
		for (UIComponent u : gui)
		{
			if (u.getBounds().contains(mousePosition))
			{
				handled = u.handleReleased(e);
				if (handled)
					break;
			}
		}
		if (!handled)
		{
			if (e.getButton() == MouseButton.PRIMARY)
			{
				Rectangle2D selectionRect = getSelectionRect();
				if (!e.isControlDown())
					clearSelected();
				entities.stream().filter(ent -> ent instanceof Unit).map(ent -> (Unit) ent)
						.filter(unit -> unit.isFriendly()).filter(unit -> selectionRect.contains(unit.location))
						.forEach(unit -> {
							unit.setSelected(true);
							selectedUnits.add(unit);
						});
				selecting = false;
			} else
			{
				for (Entity ent : entities)
				{
					if (!ent.isFriendly() && ent.location.distanceSq(mousePosition) < ent.radius * ent.radius)
					{
						for (Unit u : selectedUnits)
							u.attack(ent, entities);
						handled = true;
						break;
					}
				}
				if (!handled)
				{
					Point2D destination = new Point2D.Double(e.getX(), e.getY());
					selectedUnits.forEach(unit -> unit.setDestination(destination));
					selecting = false;
				}
			}
		}
	}

	private void clearSelected()
	{
		selectedUnits.forEach(unit -> unit.setSelected(false));
		selectedUnits.clear();
	}

	private Rectangle2D getSelectionRect()
	{
		double x = Math.min(selectionCorner.getX(), mousePosition.getX());
		double y = Math.min(selectionCorner.getY(), mousePosition.getY());
		double width = Math.max(selectionCorner.getX(), mousePosition.getX()) - x;
		double height = Math.max(selectionCorner.getY(), mousePosition.getY()) - y;
		return new Rectangle2D.Double(x, y, width, height);
	}
}
