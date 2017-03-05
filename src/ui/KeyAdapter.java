package ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Uses the 'default' feature of Java 8 interfaces to allow implementing classes to ignore unnecessary key methods
 */
public interface KeyAdapter extends KeyListener
{

	@Override
	default public void keyPressed(KeyEvent arg0)
	{

	}

	@Override
	default public void keyReleased(KeyEvent arg0)
	{

	}

	@Override
	default public void keyTyped(KeyEvent arg0)
	{

	}

}
