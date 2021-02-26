package cf.phoenixfirewingz.remapper;

import cf.phoenixfirewingz.remapper.utils.FileHandler;

import java.io.*;

public class Process implements Runnable
{
	String data_of_file, path_to_new_source_file, name;
	public Process(File file)
	{
		name = file.getName();
		try
		{
			data_of_file = FileHandler.read(true,file.toString());
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		path_to_new_source_file = file.toString().replace("old","new");
	}

	@Override public void run()
	{
		System.out.println("mapping: " + name);
		String intermediary_source = null;
		try
		{
			if(Main.config.shouldMapToFabric)
				intermediary_source = ReMapper.mapFromForge(data_of_file);
			else
				intermediary_source = ReMapper.mapFromYarn(data_of_file);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}

		String remapped_data;
		if(Main.config.shouldMapToFabric) remapped_data = ReMapper.mapToYarn(intermediary_source);
		else remapped_data = ReMapper.maptoForge(intermediary_source);

		FileHandler.write(remapped_data, path_to_new_source_file);
	}
}