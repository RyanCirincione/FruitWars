package ui;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import entity.Unit;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class UnitSelectionBar extends UIComponent
{
	private static final int BORDER = 2, BUTTON_SIZE = 16, BUTTON_BORDER_SIZE = 20;
	private ArrayList<Unit> selectedUnits = new ArrayList<Unit>();
	private boolean handlingClickDown;

	public UnitSelectionBar(ArrayList<Unit> selectedUnits)
	{
		super(new Rectangle(200, 475, 600, 125));
		this.selectedUnits = selectedUnits;
	}

	@Override
	public void draw(GraphicsContext g2, long millis, Point2D mousePos)
	{
		if (selectedUnits.size() > 0)
		{
			g2.setFill(UIComponent.FILL_GREEN);
			g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			g2.fill();
			g2.setStroke(Color.GREEN);
			g2.strokeRect(bounds.x, bounds.y, bounds.width, bounds.height);
			g2.stroke();
			g2.setStroke(Color.BLACK);
			// TODO: Add Pages
			for (int i = 0; i < selectedUnits.size(); i++)
			{
				int yPos = bounds.y + (i / (600 / BUTTON_SIZE)) * BUTTON_BORDER_SIZE + BORDER;
				int xPos = bounds.x + (i % (600 / BUTTON_SIZE)) * BUTTON_BORDER_SIZE + BORDER;
				g2.strokeRect(xPos, yPos, BUTTON_BORDER_SIZE, BUTTON_BORDER_SIZE);
				g2.stroke();
				g2.drawImage(selectedUnits.get(i).getIcon(), xPos + ((BUTTON_BORDER_SIZE - BUTTON_SIZE) / 2),
						yPos + ((BUTTON_BORDER_SIZE - BUTTON_SIZE) / 2), BUTTON_SIZE, BUTTON_SIZE);
			}
			drawHoverText(g2, mousePos);
			g2.stroke();
		}
	}

	private int mousePosToIndex(double mouseX, double mouseY)
	{
		int index = (int) ((mouseY - (bounds.y + BORDER)) / BUTTON_BORDER_SIZE) * (bounds.width / BUTTON_BORDER_SIZE);
		index += (mouseX - (bounds.x + BORDER)) / BUTTON_BORDER_SIZE;
		return index;
	}

	private void drawHoverText(GraphicsContext g2, Point2D mousePos)
	{
		if (selectedUnits.size() > 0 && bounds.contains(mousePos))
		{
			int selectedIndex = mousePosToIndex(mousePos.getX(), mousePos.getY());
			if (selectedIndex < selectedUnits.size())
			{
				g2.setFill(Color.ORANGE);
				String text = selectedUnits.get(selectedIndex).toString();
				g2.fillRect(mousePos.getX(), mousePos.getY(), text.length() * g2.getFont().getSize() + 2,
						g2.getFont().getSize() + 2);
				g2.fill();
				g2.setFill(Color.BLACK);
				g2.strokeText(text, mousePos.getX() + 1, mousePos.getY() + g2.getFont().getSize() - 1);
				g2.stroke();
			}
		}
	}

	@Override
	public boolean handlePressed(MouseEvent e)
	{
		if (bounds.contains(new Point2D.Double(e.getX(), e.getY())) && selectedUnits.size() > 0
				&& e.getButton() == MouseButton.PRIMARY)
		{
			// selects unit
			int selectedIndex = mousePosToIndex(e.getX(), e.getY());
			if (selectedIndex < selectedUnits.size())
			{
				Unit newSelected = selectedUnits.get(selectedIndex);
				selectedUnits.forEach(unit -> unit.setSelected(false));
				selectedUnits.clear();
				selectedUnits.add(newSelected);
				newSelected.setSelected(true);
			}
			handlingClickDown = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean handleReleased(MouseEvent e)
	{
		if (bounds.contains(new Point2D.Double(e.getX(), e.getY())) && selectedUnits.size() > 0 && handlingClickDown)
		{
			handlingClickDown = false;
			return true;
		}
		return false;

	}
}
