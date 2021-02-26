package com.minecolonies.coremod.items;

import cf.pheonixfirewingz.minecolonies.utils.MineColoniesItemSettings;
import net.minecraft.item.*;
import net.minecraft.util.*;

public class BuildWand extends Item
{
	public BuildWand()
	{
		super(new MineColoniesItemSettings());
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		if(context.getWorld().isClient) return super.useOnBlock(context);

		return ActionResult.SUCCESS;
	}
}
