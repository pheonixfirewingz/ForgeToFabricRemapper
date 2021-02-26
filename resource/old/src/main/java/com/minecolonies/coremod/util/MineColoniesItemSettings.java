package com.minecolonies.coremod.util;

import com.minecolonies.coremod.Minecolonies;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;

public class MineColoniesItemSettings extends FabricItemSettings
{
	public MineColoniesItemSettings(int food_count,float sat)
	{
		group(Minecolonies.ITEM_GROUP);
		food(new FoodComponent.Builder().hunger(food_count).saturationModifier(sat).build());
	}

	public MineColoniesItemSettings()
	{
		group(Minecolonies.ITEM_GROUP);
	}
}
