package entity;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.scene.image.Image;

public class Grape extends Unit
{
	private static final double RADIUS = 16;
	public final static double MAX_HEALTH = 50;
	public static ArrayList<String> grapeLastNames = loadGrapeVarieties();
	private String name;
	
	public Grape(Point2D location)
	{
		super(loadSprite(), location, RADIUS, 1, MAX_HEALTH);
		mass = 0.1f;
		name = "Pvt. " + getName() + " " + grapeLastNames.get((int)(Math.random() * grapeLastNames.size()));
	}

	private static Image[][] loadSprite()
	{
		// temp
		Image[][] sprite = new Image[1][1];
		try
		{
			sprite[0][0] = new Image(new FileInputStream("assets/tempGrape.png"));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return sprite;
	}
	
	/**
	 * Source: National Grape Registry
	 */
	private static ArrayList<String> loadGrapeVarieties()
	{
		System.out.println("Loading types of grapes...");
		ArrayList<String> grapes = new ArrayList<>();
		try
		{
			Scanner fileScan = new Scanner(new File("assets/grapeNames.txt"));
			while(fileScan.hasNextLine())
				grapes.add(fileScan.nextLine());
			fileScan.close();
		} catch (FileNotFoundException e)
		{
			System.err.println("Could not load a ridiculously long list of grape varieties ¯\\_(ツ)_/¯");
			e.printStackTrace();
		}
		return grapes;
	}
	
	public String toString()
	{
		String status = name + " (GRAPE)";
		while(status.length() < 50)
			status = status + ".";
		return status + "HP: " + health + "/" + MAX_HEALTH;
	}
}
