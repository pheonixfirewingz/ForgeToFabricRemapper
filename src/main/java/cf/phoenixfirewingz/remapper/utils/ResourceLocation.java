package cf.phoenixfirewingz.remapper.utils;

import cf.phoenixfirewingz.remapper.common.Defines;

import java.io.File;

public class ResourceLocation extends File
{

	public ResourceLocation(String pathname)
	{
		super(Defines.jar_root + "\\resource\\" + pathname);
	}
}
