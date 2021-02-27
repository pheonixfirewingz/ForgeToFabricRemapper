package cf.phoenixfirewingz.remapper.utils;

import cf.phoenixfirewingz.remapper.common.*;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

public class JsonUtils
{
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static Gson getGson()
	{
		return gson;
	}

	public static Config readJsonConfig()
	{
		Config config = null;
		try
		{
			config = gson.fromJson(new JsonReader(FileHandler.getReader(true,new ResourceLocation("Config.json").getPath())), Config.class);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		return config;
	}
}
