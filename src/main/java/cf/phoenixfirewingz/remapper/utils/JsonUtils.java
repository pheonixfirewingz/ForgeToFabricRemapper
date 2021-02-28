package cf.phoenixfirewingz.remapper.utils;

import cf.phoenixfirewingz.remapper.common.Config;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.util.Objects;

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
			config = gson.fromJson(new JsonReader(Objects.requireNonNull(FileHandler.getReader(new ResourceLocation("Config.json")))), Config.class);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		return config;
	}
}
