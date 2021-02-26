package cf.phoenixfirewingz.remapper.utils;

import cf.phoenixfirewingz.remapper.common.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class JsonData
{
	private static final Gson gson = new Gson();

	public static Config readJsonConfig()
	{
		Config config = null;
		try
		{
			config = gson.fromJson(new JsonReader(FileHandler.getReader(true,System.getProperty("user.dir") + "\\Config.json")), Config.class);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		return config;
	}

	public static Mapping readJsonMap(boolean isLocal, String path)
	{
		Mapping mapping = null;
		try
		{
			mapping = gson.fromJson(new JsonReader(FileHandler.getReader(isLocal, path)), Mapping.class);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		return mapping;
	}
}
