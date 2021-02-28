package cf.phoenixfirewingz.remapper.utils;

import cf.phoenixfirewingz.remapper.Main;
import cf.phoenixfirewingz.remapper.common.Mappings;

import java.io.*;
import java.util.*;

public class ForgeUtils extends CommonUtils
{
	private static final Logger forge_logger = new Logger("forge");

	public static Mappings readForgeMapping(String svg,String csv)
	{
		try
		{
			forge_logger.log("reading svg mapping");
			BufferedReader s = FileHandler.getReader(FileHandler.download(svg));
			File csvFile = FileHandler.download(csv);
			Map<String, String> fieldNames = readCsv(csvFile, "fields.csv"),
								methodNames= readCsv(csvFile, "methods.csv");

			Map<String, String> classes = new LinkedHashMap<>();
			Map<String, String> fields = new LinkedHashMap<>();
			Map<String, String> methods = new LinkedHashMap<>();

			String currentClassA = null;
			String currentClassB = null;
			String string = "";
			while((string = s.readLine()) != null)
			{

				if(!string.startsWith("\t"))
				{
					String[] parts = string.split(" ");
					classes.put(parts[0], parts[1]);
					currentClassA = parts[0];
					currentClassB = parts[1];
					continue;
				}

				string = string.substring(1);

				String[] parts = string.split(" ");

				if(parts.length == 2)
				{
					fields.put(
							currentClassA + ":" + parts[0],
							currentClassB + ":" + fieldNames.getOrDefault(parts[1], parts[1])
					);
				} else if(parts.length == 3)
				{
					methods.put(
							currentClassA + ":" + parts[0] + parts[1],
							currentClassB + ":" + methodNames.getOrDefault(parts[2], parts[2]) + parts[1]
					);
				}
			}

			s.close();
			Mappings mappings = new Mappings();
			mappings.classes.putAll(classes);
			mappings.fields.putAll(fields);
			methods.forEach((a, b) -> mappings.methods.put(a, remapMethodDescriptor(b, classes)));
			return mappings;
		}
		catch(IOException e)
		{
			forge_logger.logError(e.getMessage());
		}
		return null;
	}

	private static Map<String, String> readCsv(File file, String csv) throws IOException
	{
		BufferedReader s = FileHandler.getReader(FileHandler.extract(file, csv));
		forge_logger.log("reading csv mapping");
		Map<String, String> mappings = new LinkedHashMap<>();

		String string;
		while ((string = s.readLine()) != null) {
			String[] parts = string.split(",");
			mappings.put(parts[0], parts[1]);
		}

		s.close();
		return mappings;
	}
}
