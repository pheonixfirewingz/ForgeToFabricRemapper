package cf.phoenixfirewingz.remapper.utils;

public class Logger
{
	public final String name;

	public Logger(String name_in)
	{
		name = name_in;
	}

	public void log(String s)
	{
		System.out.println(name.toUpperCase() + ":" + s);
	}

	public void logError(String s)
	{
		System.err.println(name.toUpperCase() + ":ERROR! -> " + s);
	}
}
