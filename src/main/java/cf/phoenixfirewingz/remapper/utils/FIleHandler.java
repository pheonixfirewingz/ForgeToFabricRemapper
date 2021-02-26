package cf.phoenixfirewingz.remapper.utils;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.Objects;

public class FIleHandler
{
	public static BufferedReader getReader(boolean isLocal,String path) throws Exception
	{
		BufferedReader in;
		if(!isLocal) in = new BufferedReader(new InputStreamReader(new URL(path).openStream()));
		else in = new BufferedReader(new FileReader(path));

		return in;
	}

	public static File getAsFile(boolean isLocal,String path) throws Exception
	{
		File in;
		if(!isLocal) in = new File(new URL(path).toURI());
		else in = new File(path);

		return in;
	}

	public static String read(boolean isLocal,String path) throws Exception
	{
		StringBuilder data = new StringBuilder();
		BufferedReader in = getReader(isLocal,path);

		String line;
		while((line = in.readLine()) != null) data.append(line).append("\n");
		in.close();

		return data.toString();
	}

	public static void write(String data,String path)
	{
		try
		{
			File file = new File(path);
			if(!file.exists())
			{
				if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(Objects.requireNonNull(data));
			writer.flush();
			writer.close();
		}
		catch(IOException e)
		{
			System.err.println("ERROR:" + e.getMessage());
		}
	}
}
