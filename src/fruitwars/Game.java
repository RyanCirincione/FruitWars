package fruitwars;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import data.BruteStore;
import data.EntityStore;
import data.FastList;
import entity.BlueberryBush;
import entity.Entity;
import entity.GrapeVine;
import entity.StrawberryBush;
import entity.Unit;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
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
	private EntityStore<Entity> entities;
	private List<Unit> selectedUnits;
	private List<UIComponent> gui;
	private ConstructionBar cBar;
	private boolean selecting;
	private Color selectionBlue = new Color(102.0 / 255, 153.0 / 255, 1, 64 / 255.0);
	private Point2D selectionCorner, mousePosition;
	private Rectangle2D camera;
	private float camera_xspeed, camera_yspeed;
	private final float CAMERA_SPEED = 100;

	public Game(Group root, GraphicsContext ctx)
	{
		super(root);
		g = ctx;
		entities = new BruteStore<>();
		selectedUnits = new FastList<>(1000009);

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

		StrawberryBush s = new StrawberryBush(entities, new Point2D.Double(100, 100), true, 150);
		s.setRally(new Point2D.Double(600, 300));
		entities.add(s);
		s = new StrawberryBush(entities, new Point2D.Double(600, 100), false, 150);
		s.setRally(new Point2D.Double(600, 300));
		entities.add(s);
		BlueberryBush b = new BlueberryBush(entities, new Point2D.Double(100, 500), true, 150);
		b.setRally(new Point2D.Double(100, 300));
		entities.add(b);
		b = new BlueberryBush(entities, new Point2D.Double(600, 500), false, 150);
		b.setRally(new Point2D.Double(100, 300));
		entities.add(b);
		GrapeVine g = new GrapeVine(entities, new Point2D.Double(100, 300), true, 150);
		g.setRally(new Point2D.Double(600, 300));
		entities.add(g);
		g = new GrapeVine(entities, new Point2D.Double(600, 300), false, 150);
		g.setRally(new Point2D.Double(100, 300));
		entities.add(g);
		
		camera = new Rectangle2D.Double(0, 0, 800, 600);
	}

	public void tick(long milli)
	{
		entities.tickAll(milli);
		entities.filter(entity -> entity.getHealth() > 0, entity -> selectedUnits.remove(entity));
		camera.setRect(camera.getX() + camera_xspeed * 60.0 / 1000, camera.getY() + camera_yspeed * 60.0 / 1000, g.getCanvas().getWidth(), g.getCanvas().getHeight());
	}

	public void draw(long milli)
	{
		g.clearRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());
		g.translate(-camera.getX(), -camera.getY());
		g.scale(g.getCanvas().getWidth() / camera.getWidth(), g.getCanvas().getHeight() / camera.getHeight());
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
		g.translate(camera.getX(), camera.getY());
		g.scale(camera.getWidth() / g.getCanvas().getWidth(), camera.getHeight() / g.getCanvas().getHeight());
	}

	public void mouseMove(MouseEvent e)
	{
		mousePosition.setLocation(e.getX(), e.getY());
	}

	public void keyPressed(KeyEvent e)
	{
		if(e.getCode() == KeyCode.UP)
			camera_yspeed = -CAMERA_SPEED;
		if(e.getCode() == KeyCode.DOWN)
			camera_yspeed = CAMERA_SPEED;
		if(e.getCode() == KeyCode.RIGHT)
			camera_xspeed = CAMERA_SPEED;
		if(e.getCode() == KeyCode.LEFT)
			camera_xspeed = -CAMERA_SPEED;
	}

	public void keyReleased(KeyEvent e)
	{
		if(e.getCode() == KeyCode.UP)
			camera_yspeed = 0;
		if(e.getCode() == KeyCode.DOWN)
			camera_yspeed = 0;
		if(e.getCode() == KeyCode.RIGHT)
			camera_xspeed = 0;
		if(e.getCode() == KeyCode.LEFT)
			camera_xspeed = 0;
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
				selectionCorner.setLocation(mousePosition.getX() + camera.getX(), mousePosition.getY() + camera.getY());
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
				selectionBuffer.clear();
				if (!e.isControlDown())
					clearSelected();
				entities.addContained(selectionRect, selectionBuffer);
				for (Entity ent : selectionBuffer)
				{
					if (ent instanceof Unit && ent.isFriendly() && !selectedUnits.contains(ent))
					{
						Unit u = (Unit) ent;
						u.setSelected(true);
						selectedUnits.add(u);
					}
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
		double x = Math.min(selectionCorner.getX(), mousePosition.getX() + camera.getY());
		double y = Math.min(selectionCorner.getY(), mousePosition.getY() + camera.getY());
		double width = Math.max(selectionCorner.getX(), mousePosition.getX() + camera.getY()) - x;
		double height = Math.max(selectionCorner.getY(), mousePosition.getY() + camera.getX()) - y;
		return new Rectangle2D.Double(x, y, width, height);
	}
}
