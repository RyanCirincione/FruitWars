package ui;

import java.awt.Rectangle;
import java.util.ArrayList;

import entity.Unit;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class UnitSelectionBar extends UIComponent
{
	private ArrayList<Unit> selectedUnits = new ArrayList<Unit>();
	private static final Color FILL_GREEN= Color.color(102.0 / 255, 255.0 / 255, 102 / 255);
	
	public UnitSelectionBar(ArrayList<Unit> selectedUnits)
	{
		super(new Rectangle(200, 475, 600, 125));
		this.selectedUnits = selectedUnits;
	}

	@Override
	public void draw(GraphicsContext g2, long millis)
	{
		if(selectedUnits.size() > 0)
		{
			g2.setFill(FILL_GREEN);
			g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			g2.fill();
			g2.setStroke(Color.GREEN);
			g2.strokeRect(bounds.x, bounds.y, bounds.width, bounds.height);
			g2.stroke();
			g2.setStroke(Color.BLACK);
			//TODO: Add Pages
			double textHeight = g2.getFont().getSize();
			for(int i = 0; i < selectedUnits.size(); i++)
			{
				int yPos = (int)(bounds.y + (bounds.height - (bounds.height * ((i + 1)/(double)selectedUnits.size())))) + 2;
				g2.strokeRect(bounds.x + 6, yPos - 2, bounds.width - 12, (int)(bounds.height * ((i + 1)/(double)selectedUnits.size())));
				g2.strokeText(selectedUnits.get(i).toString(), bounds.x + 8, yPos + textHeight);
			}
			g2.stroke();
		}
	}

	@Override
	public boolean handlePressed(MouseEvent e)
	{
		if(selectedUnits.size() > 0)
		{
			//selects unit
			int selectedIndex = (int)((e.getY() - bounds.y) / (bounds.height * (1.0 / selectedUnits.size())));
			selectedIndex = (selectedUnits.size() - 1) - selectedIndex;
			Unit newSelected = selectedUnits.get(selectedIndex);
			selectedUnits.forEach(unit -> unit.setSelected(false));
			selectedUnits.clear();
			selectedUnits.add(newSelected);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean handleReleased(MouseEvent e)
	{
		if(selectedUnits.size() > 0)
			return true;
		return false;
		
	}
}
