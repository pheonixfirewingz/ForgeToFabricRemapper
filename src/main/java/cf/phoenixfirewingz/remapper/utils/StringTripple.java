package cf.phoenixfirewingz.remapper.utils;

public class StringTripple
{
	private final String forge, fabric, mojang;

	public StringTripple(String forgeIn, String fabricIn, String mojangIn)
	{
		forge = forgeIn;
		fabric = fabricIn;
		mojang = mojangIn;
	}

	public String getForge()
	{
		return forge;
	}

	public String getFabric()
	{
		return fabric;
	}

	public String getMojang()
	{
		return mojang;
	}
}
