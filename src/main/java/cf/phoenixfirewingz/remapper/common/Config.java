package cf.phoenixfirewingz.remapper.common;

import cf.phoenixfirewingz.remapper.utils.JsonData;

import java.util.Vector;

public class Config
{
	public String mod_directory;
	public boolean map_to_fabric;

	public String getMod_directory()
	{
		return mod_directory;
	}

	public boolean isMap_to_fabric()
	{
		return map_to_fabric;
	}
}
