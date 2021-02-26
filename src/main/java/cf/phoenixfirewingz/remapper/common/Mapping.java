package cf.phoenixfirewingz.remapper.common;

import com.google.gson.annotations.SerializedName;

public class Mapping
{
	@SerializedName("mcp_version")
	public String mcpVersion;
	@SerializedName("yarn_version")
	public String yarnVersion;
	public MapEntry[] mappings;

	public String getMcpVersion()
	{
		return mcpVersion;
	}

	public String getYarnVersion()
	{
		return yarnVersion;
	}

	public MapEntry[] getMappings()
	{
		return mappings;
	}
}
