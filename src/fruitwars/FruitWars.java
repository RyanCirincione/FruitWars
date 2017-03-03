package fruitwars;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import graphics.Canvas;

public class FruitWars 
{
	public static void main(String[] args) 
	{
		JFrame frame = new JFrame("Fruit Wars");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		
		Canvas c = new Canvas();
		frame.add(c);
		frame.pack();
		
		frame.setVisible(true);
		Runnable r = () -> {
			c.tick();
			c.repaint();
		};
		ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(1);
		ex.scheduleAtFixedRate(r, 0, 1, TimeUnit.MILLISECONDS);
	}
}