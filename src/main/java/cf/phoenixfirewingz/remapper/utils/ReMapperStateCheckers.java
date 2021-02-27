package cf.phoenixfirewingz.remapper.utils;

import cf.phoenixfirewingz.remapper.Main;

public class ReMapperStateCheckers
{
	private static final String[] ok_starts = {"java","com.google","org.jetbrains", Main.config.modDirectory};
	public static boolean checkifImportNeedsEdit(String s)
	{
		for(String t:ok_starts)
		{
			if(s.startsWith(t))
				return false;
		}
		return true;
	}

	public static boolean needEditClass(String token)
	{
		if(Main.config.shouldMapToFabric)
			return Main.constansts.isForgeClass(token);
		else
			return Main.constansts.isFabricClass(token);
	}


	public static boolean needEditMethod(String token)
	{
		if(Main.config.shouldMapToFabric)
			return Main.constansts.isForgeName(token);
		else
			return Main.constansts.isFabricName(token);
	}
}
