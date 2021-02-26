package cf.phoenixfirewingz.remapper.common;

public class Mapping
{
	public String mcp_version;
	public String yarn_version;
	public MapEntry[] mappings;

	public String getMcp_version()
	{
		return mcp_version;
	}

	public String getYarn_version()
	{
		return yarn_version;
	}

	public MapEntry[] getMappings()
	{
		return mappings;
	}
}
