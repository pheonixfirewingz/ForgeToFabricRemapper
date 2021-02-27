package cf.phoenixfirewingz.remapper;

import cf.phoenixfirewingz.remapper.utils.FileHandler;

import java.io.File;

public class Process implements Runnable
{
	File file;
	public Process(File file_in)
	{
		file = file_in;
	}

	public static String remap(String[] data)
	{
		StringBuilder builder = new StringBuilder();
		for(String s:data)
		{
			if(Main.config.shouldMapToFabric)
				builder.append(Main.constansts.mapToFabric(s));
			else
				builder.append(Main.constansts.mapToForge(s));
			builder.append("\n");
		}
		return builder.toString();
	}

	public static String remap(String data)
	{
		if(Main.config.shouldMapToFabric)
			return Main.constansts.mapToFabric(data);
		else
			return Main.constansts.mapToForge(data);
	}

	@Override public void run()
	{
		String[] tokened_data = FileHandler.read(true, file.toString()).split("\n");
		String path_to_new_source_file = file.getPath().replace("old","new"), name = file.getName();
		Main.common_logger.log("ReMapping:" + name);
		String remapped_data = remap(tokened_data);
		String changed_source_name = remap(path_to_new_source_file);
		FileHandler.write(remapped_data, changed_source_name);
		Main.common_logger.log(" Done ReMapping: " + name + " -> " + new File(changed_source_name).getName());
	}
}