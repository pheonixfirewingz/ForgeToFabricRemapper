import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class Process implements Runnable
{
	File file;
	boolean forge;
	String path_to_new_source_file;
	public Process(File file,boolean map_to_fabric)
	{
		forge =! map_to_fabric;
		this.file = file;
		String [] path_brake_down = file.toPath().toUri().toASCIIString().split("///")[1].split("/");

		String temp = new String();
		boolean start_logging = false;
		for(String s: path_brake_down)
		{
			if(start_logging)
				path_to_new_source_file += "/" + s;

			if(!s.contains("old") && !start_logging)
				temp += "/" + s;

			if(s.contains("old"))
				start_logging = true;
		}
		path_to_new_source_file = temp + "/new/" + path_to_new_source_file.substring(5);
	}

	@Override public void run()
	{
		String data = new String();
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) data += (line + "\n");
			br.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		String remapped_data;
		if(!forge) remapped_data = ReMapper.maptoFabric(data);
		else remapped_data = ReMapper.maptoForge(data);

		try
		{
			File file = new File(new URI(path_to_new_source_file).getPath());
			if(!file.exists())
			{
				if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(remapped_data);
			writer.flush();
			writer.close();
		}
		catch(IOException | URISyntaxException e)
		{
			System.err.println("ERROR:" + e.getMessage());
		}
	}
}
