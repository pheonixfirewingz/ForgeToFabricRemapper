package cf.phoenixfirewingz.remapper.utils;

import java.io.File;

public class ResourceLocation extends File
{

	public ResourceLocation(String pathname)
	{
		super(Defines.jar_root + "\\resource\\" + pathname);
	}
}
