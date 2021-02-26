package cf.phoenixfirewingz.remapper.common;

import cf.phoenixfirewingz.remapper.utils.JsonData;
import com.google.gson.annotations.SerializedName;

import java.util.Vector;

public class Config
{
	@SerializedName("mod_directory")
	public String modDirectory;
	@SerializedName("should_map_to_fabric")
	public boolean shouldMapToFabric;

	public String getModDirectory()
	{
		return modDirectory;
	}

	public boolean shouldMapToFabric()
	{
		return shouldMapToFabric;
	}
}
