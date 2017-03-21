package fruitwars;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import data.FastList;
import data.QuadNode;
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
	private QuadNode<Entity> entities;
	private List<Unit> selectedUnits;
	private List<UIComponent> gui;
	private ConstructionBar cBar;
	private boolean selecting;
	private Color selectionBlue = new Color(102.0 / 255, 153.0 / 255, 1, 64 / 255.0);
	private Point2D selectionCorner, mousePosition;
	private Rectangle camera;

	public Game(Group root, GraphicsContext ctx)
	{
		super(root);
		g = ctx;
		Rectangle gameBounds = new Rectangle(0, 0, (int) ctx.getCanvas().getWidth(), (int) ctx.getCanvas().getHeight());
		entities = new QuadNode<>(gameBounds, 128, 128);
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

		GrapeVine e = new GrapeVine(entities, new Point2D.Double(100, 100), true, 150);
		e.setRally(new Point2D.Double(600, 300));
		entities.add(e);
		e = new GrapeVine(entities, new Point2D.Double(100, 500), true, 150);
		e.setRally(new Point2D.Double(600, 300));
		entities.add(e);
		e = new GrapeVine(entities, new Point2D.Double(600, 100), false, 150);
		e.setRally(new Point2D.Double(100, 300));
		entities.add(e);
		e = new GrapeVine(entities, new Point2D.Double(600, 500), false, 150);
		e.setRally(new Point2D.Double(100, 300));
		entities.add(e);
		BlueberryBush b = new BlueberryBush(entities, new Point2D.Double(100, 300), true, 150);
		b.setRally(new Point2D.Double(600, 300));
		entities.add(b);
		b = new BlueberryBush(entities, new Point2D.Double(600, 300), false, 150);
		b.setRally(new Point2D.Double(100, 300));
		entities.add(b);
		
		camera = new Rectangle(0, 0, 800, 600);
	}

	public void tick(long milli)
	{
		entities.tickAll(milli);
		entities.filter(entity -> entity.getHealth() > 0);
	}

	public void draw(long milli)
	{
		g.translate(-camera.x, -camera.y);
		g.scale(g.getCanvas().getWidth() / camera.width, g.getCanvas().getHeight() / camera.height);
		g.clearRect(0, 0, FruitWars.WINDOW_WIDTH, FruitWars.WINDOW_HEIGHT);
		entities.forEach(e -> e.draw(g, milli));
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
		g.translate(camera.x, camera.y);
		g.scale(camera.width / g.getCanvas().getWidth(), camera.height / g.getCanvas().getHeight());
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
			selectedUnits.forEach(unit -> unit.setDestination(unit.getCenter())); // Stop
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

	private List<Entity> selectionBuffer = new FastList<>();

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
				entities.addContained((int) selectionRect.getX(), (int) selectionRect.getY(),
						(int) selectionRect.getWidth(), (int) selectionRect.getHeight(), selectionBuffer);
				for (Entity ent : selectionBuffer)
					if (ent instanceof Unit)
					{
						Unit u = (Unit) ent;
						u.setSelected(true);
						selectedUnits.add(u);
					}
				selecting = false;
			} else
			{
				Entity clicked = entities.getAtPoint(mousePosition);
				if (clicked != null)
				{
					selectedUnits.forEach(u -> u.target(clicked));
					handled = true;
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
