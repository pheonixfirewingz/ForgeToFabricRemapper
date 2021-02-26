package cf.phoenixfirewingz.remapper.utils;

import java.io.*;
import java.net.*;

public class FIleHandler
{
	public static String read(boolean isLocal,String path) throws Exception
	{
		StringBuilder data = new StringBuilder();
		BufferedReader in;
		if(!isLocal)
			in = new BufferedReader(new InputStreamReader(new URL(path).openStream()));
		else
			in = new BufferedReader(new FileReader(new File(path)));

		String line;
		while((line = in.readLine()) != null) data.append(line).append("\n");
		in.close();

		return data.toString();
	}
}
