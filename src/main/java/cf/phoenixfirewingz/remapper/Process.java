package cf.phoenixfirewingz.remapper;

import cf.phoenixfirewingz.remapper.utils.FIleHandler;

import java.io.*;
import java.util.Objects;

public class Process implements Runnable
{
	File file;
	boolean forge;
	String path_to_new_source_file;
	public Process(File file,boolean map_to_fabric)
	{
		forge =! map_to_fabric;
		this.file = file;
		path_to_new_source_file = file.toString().replace("old","new");
	}

	@Override public void run()
	{
		System.out.println("mapping: " + file.toString());
		String data = null,remapped_data;
		try
		{
			data = FIleHandler.read(true,file.toString());
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}

		if(!forge) remapped_data = ReMapper.maptoFabric(data);
		else remapped_data = ReMapper.maptoForge(data);

		try
		{
			File file = new File(path_to_new_source_file);
			if(!file.exists())
			{
				if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(Objects.requireNonNull(remapped_data));
			writer.flush();
			writer.close();
		}
		catch(IOException e)
		{
			System.err.println("ERROR:" + e.getMessage());
		}
	}
}
