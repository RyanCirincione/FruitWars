package fruitwars;

import java.util.HashMap;

import entity.Blueberry;
import entity.BlueberryBush;
import entity.Cauliflower;
import entity.Grape;
import entity.GrapeVine;
import entity.Potato;
import entity.Strawberry;
import entity.StrawberryBush;
import entity.StructureBuilder;
import entity.UnitBuilder;

public class Mission
{
	private static HashMap<String, StructureBuilder> structure;
	private static HashMap<String, UnitBuilder> units;
	
	
	private static void register(Class<?> cls, UnitBuilder ctor) {
		units.put(cls.getName(), ctor);
	}
	
	private static void register(Class<?> cls, StructureBuilder ctor) {
		structure.put(cls.getName(), ctor);
	}
	
	static {
		register(Blueberry.class, Blueberry::new);
		register(BlueberryBush.class, BlueberryBush::new);
		register(Grape.class, Grape::new);
		register(GrapeVine.class, GrapeVine::new);
		register(Strawberry.class, Strawberry::new);
		register(StrawberryBush.class, StrawberryBush::new);
		register(Cauliflower.class, Cauliflower::new);
		register(Potato.class, Potato::new);

	}
}
