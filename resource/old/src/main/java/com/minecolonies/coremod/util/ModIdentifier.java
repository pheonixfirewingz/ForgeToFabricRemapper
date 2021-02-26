package com.minecolonies.coremod.util;

import cf.pheonixfirewingz.minecolonies.Minecolonies;
import net.minecraft.util.*;

public class ModIdentifier extends Identifier
{
	public ModIdentifier(String path)
	{
		super(Minecolonies.MOD_ID, path);
	}
}
