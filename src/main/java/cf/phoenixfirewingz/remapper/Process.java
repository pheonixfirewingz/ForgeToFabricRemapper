package cf.phoenixfirewingz.remapper;

import cf.phoenixfirewingz.remapper.utils.FileHandler;

import java.io.File;

public class Process implements Runnable
{
	String[] tokened_data;
	String path_to_new_source_file, name;
	public Process(File file)
	{
		name = file.getName();
		tokened_data = FileHandler.read(true, file.toString()).replace('\n', ' ').split(" ");
		path_to_new_source_file = file.getPath().replace("old","new");
	}

	@Override public void run()
	{
		Main.common_logger.log("ReMapping:" + name);
		String intermediary = ReMapper.mapToIntermediary(tokened_data);

		Main.common_logger.logDebug(intermediary);

		String remapped_data;
		if(Main.config.shouldMapToFabric) remapped_data = ReMapper.mapToYarn(intermediary);
		else ReMapper.mapToForge(intermediary);
		//Main.common_logger.log("mapping: " + name);
	}
}