package com.minecolonies.coremod.items;


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
