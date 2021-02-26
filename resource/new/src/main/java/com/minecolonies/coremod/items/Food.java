package com.minecolonies.coremod.items;


public class Food extends Item
{
	public Food(int food_count,float sat)
	{
		super(new MineColoniesItemSettings(food_count,sat));
	}
}
