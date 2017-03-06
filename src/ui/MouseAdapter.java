package ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Uses the 'default' feature of Java 8 interfaces to allow implementing classes to ignore unnecessary mouse methods
 */
public interface MouseAdapter extends MouseListener
{

	@Override
	default public void mouseClicked(MouseEvent arg0)
	{
	}

	@Override
	default public void mouseEntered(MouseEvent arg0)
	{
	}

	@Override
	default public void mouseExited(MouseEvent arg0)
	{
	}

	@Override
	default public void mousePressed(MouseEvent arg0)
	{
	}

	@Override
	default public void mouseReleased(MouseEvent arg0)
	{
	}

}
