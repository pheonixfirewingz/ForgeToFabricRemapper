package cf.phoenixfirewingz.remapper.utils;

import cf.phoenixfirewingz.remapper.Main;
import cf.phoenixfirewingz.remapper.common.Mappings;

import java.io.*;
import java.util.*;

public class FabricUtils extends CommonUtils
{
	public static Mappings readYarnV1(BufferedReader s, String from, String to)
	{
		try
		{
		Main.fabric_logger.log("reading yarn mappings");
		String[] header = s.readLine().split("\t");
		Map<String, Integer> columns = new HashMap<>();

		for(int i = 1; i < header.length; i++)
			columns.put(header[i], i - 1);

		int fromColumn = columns.get(from);
		int toColumn = columns.get(to);

		Map<String, String> classes = new LinkedHashMap<>();
		Map<String, String> fields = new LinkedHashMap<>();
		Map<String, String> methods = new LinkedHashMap<>();

		String l;
		while((l = s.readLine()) != null)
		{
			String[] line = l.split("\t");
			switch(line[0])
			{
				case "CLASS":
				{
					classes.put(line[fromColumn + 1], line[toColumn + 1]);
					break;
				}
				case "FIELD":
				{
					fields.put(line[1] + ":" + line[fromColumn + 3],
							classes.get(line[1]) + ":" + line[toColumn + 3]);
					break;
				}
				case "METHOD":
				{
					String m1 = line[1] + ":" + line[fromColumn + 3] + line[2];
					String m2 = classes.get(line[1]) + ":" + line[toColumn + 3] + line[2];
					methods.put(m1, m2);
					break;
				}
			}
		}

		Mappings mappings = new Mappings();
		mappings.classes.putAll(classes);
		mappings.fields.putAll(fields);
		methods.forEach((a, b) -> mappings.methods.put(a, remapMethodDescriptor(b, classes)));

		s.close();
		return mappings;
		}
		catch(IOException e)
		{
			Main.fabric_logger.logError(e.getMessage());
		}
		return null;
	}
}
