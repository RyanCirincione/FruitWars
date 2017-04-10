package fruitwars;

import java.awt.geom.Point2D;
import java.util.HashMap;

import data.EntityStore;
import entity.Blueberry;
import entity.Entity;

public class Mission
{
	
	private static HashMap<String, EntityBuilder> constructors;
	
	private static void register(Class<?> cls, EntityBuilder ctor) {
		constructors.put(cls.getName(), ctor);
	}
	
	static {
		register(Blueberry.class, Blueberry::new);
	}
}
