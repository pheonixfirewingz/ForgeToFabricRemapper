package cf.phoenixfirewingz.remapper.common;

import cf.phoenixfirewingz.remapper.Main;
import cf.phoenixfirewingz.remapper.utils.FileHandler;

import java.io.File;

public class Process implements Runnable
{
	File file;
	public Process(File file_in)
	{
		file = file_in;
	}

	private String remapImports(String[] split)
	{
		StringBuilder builder = new StringBuilder();
		for(String s:split)
		{
			if(Main.config.shouldMapToFabric)
				builder.append(Main.constants.mapToImportsFabric(s));
			else
				builder.append(Main.constants.mapToImportsForge(s));
			builder.append("\n");
		}
		return builder.toString();
	}

	public static String remap(String data)
	{
		if(Main.config.shouldMapToFabric)
			return Main.constants.mapToFabric(data);
		else
			return Main.constants.mapToForge(data);
	}

	public static String remap(String[] data)
	{
		StringBuilder builder = new StringBuilder();
		for(String s:data)
		{
			builder.append(remap(s));
			builder.append("\n");
		}
		return builder.toString();
	}

	@Override public void run()
	{
		String name,name_new;
		Main.common_logger.log("ReMapping:" + (name = file.getName()));
		FileHandler.write( remap(remapImports(FileHandler.read(file).split("\n")).split("\n")),
						 (name_new = remap(file.getPath().replace("old","new"))));
		Main.common_logger.log(" Done ReMapping: " + name + " -> " + new File(name_new).getName());
	}

}