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
			switch(Main.config.convertion.to) {
				case FORGE:
					builder.append(Main.constants.mapToImportsForge(s));
				case FABRIC:
					builder.append(Main.constants.mapToImportsFabric(s));
				case MOJANG:
					builder.append(Main.constants.mapToImportsMojang(s));
			}
			builder.append("\n");
		}
		return builder.toString();
	}

	public static String remap(String data)
	{
		switch(Main.config.convertion.to) {
			case FORGE:
				return Main.constants.mapToForge(data);
			case FABRIC:
				return Main.constants.mapToFabric(data);
			case MOJANG:
				return Main.constants.mapToMojang(data);
			default:
				throw new IllegalStateException();
		}
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