package cf.phoenixfirewingz.remapper.common;

public class Config
{
	public Convertion convertion;
	public String version;
	public String yarnVersion;
	public String mcpMappings;
	public String mcpMappingsChannel;

	public static class Convertion {

		public MappingType from;
		public MappingType to;
		public boolean reverse;

		public static enum MappingType {

			FORGE,
			FABRIC,
			MOJANG;

		}

	}
}
