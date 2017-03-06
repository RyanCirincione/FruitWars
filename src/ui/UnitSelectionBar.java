package ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import entity.Unit;

public class UnitSelectionBar extends UIComponent
{
	private ArrayList<Unit> selectedUnits = new ArrayList<Unit>();
	private static final Color FILL_GREEN= new Color(102, 255, 102);
	private boolean handlingClickDown;
	
	public UnitSelectionBar(ArrayList<Unit> selectedUnits)
	{
		super(new Rectangle(200, 475, 600, 125));
		this.selectedUnits = selectedUnits;
		handlingClickDown = false;
	}

	@Override
	public void draw(Graphics2D g2, Point mousePos, long millis)
	{
		if(selectedUnits.size() > 0)
		{
			g2.setColor(FILL_GREEN);
			g2.fill(bounds);
			g2.setColor(Color.ORANGE);
			g2.draw(bounds);
			g2.setColor(Color.BLACK);
			//TODO: Add Pages
			int textHeight = g2.getFont().getSize();
			for(int i = 0; i < selectedUnits.size(); i++)
			{
				int yPos = bounds.y + (i / (600/16)) * 20 + 2;
				int xPos = bounds.x + (i % (600/16)) * 20 + 2;
				g2.drawRect(xPos, yPos, 20, 20);
				g2.drawImage(selectedUnits.get(i).getCurrentImage(), xPos + 2, yPos + 2, 16, 16, null);
				drawHoverText(g2, mousePos);
			}
		}
	}
	
	private int mousePosToIndex(int mouseX, int mouseY)
	{
		return ((mouseY - 477) / 20) + ((mouseX - 202) / 20);
	}
	
	private void drawHoverText(Graphics2D g2, Point mousePos)
	{
		if(selectedUnits.size() > 0 && bounds.contains(mousePos))
		{
			int selectedIndex = mousePosToIndex(mousePos.x, mousePos.y);
			if(selectedIndex < selectedUnits.size())
			{
				g2.setColor(Color.ORANGE);
				String text = selectedUnits.get(selectedIndex).toString(); 
				g2.fillRect(mousePos.x, mousePos.y, text.length() * g2.getFont().getSize() + 2, g2.getFont().getSize() + 2);
				g2.setColor(Color.BLACK);
				g2.drawString(text, mousePos.x + 1, mousePos.y + g2.getFont().getSize() - 1);
			}
		}
	}
	
	@Override
	public boolean handlePressed(MouseEvent e)
	{
		if(selectedUnits.size() > 0 && e.getButton() == MouseEvent.BUTTON1)
		{
			//selects unit
			int selectedIndex = mousePosToIndex(e.getX(), e.getY());
			if(selectedIndex < selectedUnits.size())
			{
				Unit newSelected = selectedUnits.get(selectedIndex);
				selectedUnits.forEach(unit -> unit.setSelected(false));
				selectedUnits.clear();
				selectedUnits.add(newSelected);
			}
			handlingClickDown = true;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean handleReleased(MouseEvent e)
	{
		if(selectedUnits.size() > 0 && handlingClickDown)
		{
			handlingClickDown = false;
			return true;
		}
		return false;
		
	}
}
