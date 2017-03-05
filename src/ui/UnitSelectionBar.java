package ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import entity.Unit;

public class UnitSelectionBar extends UIComponent
{
	private ArrayList<Unit> selectedUnits = new ArrayList<Unit>();
	private static final Color FILL_GREEN= new Color(102, 255, 102);
	
	public UnitSelectionBar(ArrayList<Unit> selectedUnits)
	{
		super(new Rectangle(200, 475, 600, 125));
		this.selectedUnits = selectedUnits;

	}

	@Override
	public void draw(Graphics2D g2, long millis)
	{
		if(selectedUnits.size() > 0)
		{
			g2.setColor(FILL_GREEN);
			g2.fill(bounds);
			g2.setColor(Color.GREEN);
			g2.draw(bounds);
			g2.setColor(Color.BLACK);
			//TODO: Add Pages
			int textHeight = g2.getFont().getSize();
			for(int i = 0; i < selectedUnits.size(); i++)
			{
				int yPos = (int)(bounds.y + (bounds.height - (bounds.height * ((i + 1)/(double)selectedUnits.size())))) + 2;
				g2.drawRect(bounds.x + 6, yPos - 2, bounds.width - 12, (int)(bounds.height * ((i + 1)/(double)selectedUnits.size())));
				g2.drawString(selectedUnits.get(i).toString(), bounds.x + 8, yPos + textHeight);
			}
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
