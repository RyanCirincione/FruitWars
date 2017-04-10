package ui;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import data.EntityStore;
import data.FastList;
import entity.BlueberryBush;
import entity.Cauliflower;
import entity.Entity;
import entity.GrapeVine;
import entity.Potato;
import entity.StrawberryBush;
import entity.Structure;
import entity.StructureBuilder;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ConstructionBar extends UIComponent
{
	private static final int BORDER = 2, BUTTON_SIZE = 16, BUTTON_BORDER_SIZE = 20;

	private List<Image> prototypes;
	private List<StructureBuilder> constructors;
	private Image ghost;
	private int placingIndex;
	private boolean active, placing, handlingClickDown;
	private EntityStore<Entity> root;

	public ConstructionBar(EntityStore<Entity> root)
	{
		super(new Rectangle(400 - (200 / 2), 300 - (125 / 2), 200, 125));
		active = false;

		constructors = new FastList<>(new StructureBuilder[] { GrapeVine::new, BlueberryBush::new, StrawberryBush::new, Cauliflower::new,
				Potato::new});

		// Prototype structures
		prototypes = new ArrayList<>();
		for (StructureBuilder b : constructors)
			prototypes.add(b.build(null, null, true, 0).getIcon());

		this.root = root;
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
		if (active)
		{
			g2.setFill(UIComponent.FILL_GREEN);
			g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			g2.fill();
			g2.setStroke(Color.GREEN);
			g2.strokeRect(bounds.x, bounds.y, bounds.width, bounds.height);

			// Drawing buttons
			for (int i = 0; i < prototypes.size(); i++)
			{
				g2.strokeRect(bounds.x + BORDER + BUTTON_BORDER_SIZE * i,
						bounds.y + BORDER + BUTTON_BORDER_SIZE * (i / (bounds.width / BUTTON_BORDER_SIZE)),
						BUTTON_BORDER_SIZE, BUTTON_BORDER_SIZE);
				g2.stroke();
				g2.drawImage(prototypes.get(i),
						bounds.x + (BUTTON_BORDER_SIZE * i) + BORDER + (BUTTON_BORDER_SIZE - BUTTON_SIZE) / 2,
						bounds.y + (BUTTON_BORDER_SIZE * (i / (bounds.width / BUTTON_BORDER_SIZE))) + BORDER
								+ (BUTTON_BORDER_SIZE - BUTTON_SIZE) / 2,
						BUTTON_SIZE, BUTTON_SIZE);
			}
		}
		if (placing)
		{
			g2.drawImage(ghost, mouse.getX() - ghost.getWidth() / 2, mouse.getY() - ghost.getHeight() / 2);
		}
	}

	private int mousePosToIndex(double mouseX, double mouseY)
	{
		int index = (int) ((mouseY - (bounds.y + BORDER)) / BUTTON_BORDER_SIZE) * (bounds.width / BUTTON_BORDER_SIZE);
		index += (mouseX - (bounds.x + BORDER)) / BUTTON_BORDER_SIZE;
		return index;
	}

	@Override
	public boolean handlePressed(MouseEvent e)
	{
		if (active)
		{
			if (bounds.contains(new Point2D.Double(e.getX(), e.getY())) && !placing)
			{
				int selectedIndex = mousePosToIndex(e.getX(), e.getY());
				if (selectedIndex < prototypes.size())
				{
					ghost = prototypes.get(selectedIndex);
					placingIndex = selectedIndex;
					placing = true;
					handlingClickDown = true;
					return true;
				}
			}
		}
		if (!handlingClickDown)
		{
			if (placing)
			{
				Point2D placement = new Point2D.Double(e.getX(), e.getY());
				Structure s = null;
				s = constructors.get(placingIndex).build(root, placement, true, 150);
				if (root.areaFree(e.getX(), e.getY(), s.getRadius()))
				{
					root.add(s);
					if (!e.isShiftDown())
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
		if (active && handlingClickDown)
		{
			handlingClickDown = false;
			return true;
		}
		return false;
	}

}
