package fruitwars;

import java.awt.geom.Point2D;
import java.util.HashMap;

import entity.Blueberry;
import entity.BlueberryBush;
import entity.Cauliflower;
import entity.Grape;
import entity.GrapeVine;
import entity.Potato;
import entity.Strawberry;
import entity.StrawberryBush;
import entity.Structure;
import entity.StructureBuilder;
import entity.Unit;
import entity.UnitBuilder;

public class Mission
{
	private static HashMap<String, StructureBuilder> structures;
	private static HashMap<String, UnitBuilder> units;

	private static void register(Class<?> cls, UnitBuilder ctor)
	{
		units.put(cls.getName(), ctor);
	}

	private static void register(Class<?> cls, StructureBuilder ctor)
	{
		structures.put(cls.getName(), ctor);
	}

	static
	{
		register(Blueberry.class, Blueberry::new);
		register(BlueberryBush.class, BlueberryBush::new);
		register(Grape.class, Grape::new);
		register(GrapeVine.class, GrapeVine::new);
		register(Strawberry.class, Strawberry::new);
		register(StrawberryBush.class, StrawberryBush::new);
		register(Cauliflower.class, Cauliflower::new);
		register(Potato.class, Potato::new);
	}

	public static void load(String data, Game game)
	{
		String[] lines = data.split("\n");
		for (String line : lines)
		{
			String[] tokens = line.split("\\s*");
			String type = tokens[0];
			double x = Double.parseDouble(tokens[1]);
			double y = Double.parseDouble(tokens[2]);
			double rallyX = Double.parseDouble(tokens[3]);
			double rallyY = Double.parseDouble(tokens[4]);
			boolean friendly = Boolean.parseBoolean(tokens[5]);
			double health = Double.parseDouble(tokens[6]);
			if (units.containsKey(type))
			{
				Unit unit = units.get(type).build(game.entities, new Point2D.Double(x, y),
						new Point2D.Double(rallyX, rallyY), friendly);
				unit.setHealth(health);
				game.addEntity(unit);
			} else
			{
				Structure struct = structures.get(type).build(game.entities, new Point2D.Double(x, y), friendly,
						health);
				game.addEntity(struct);
			}
		}
	}
}
