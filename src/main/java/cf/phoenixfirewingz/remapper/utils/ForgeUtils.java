package cf.phoenixfirewingz.remapper.utils;

import cf.phoenixfirewingz.remapper.Main;
import cf.phoenixfirewingz.remapper.common.Mappings;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class ForgeUtils extends CommonUtils
{

	public static Mappings readSrg(BufferedReader s, Map<String, String> fieldNames, Map<String, String> methodNames) {
		Main.forge_logger.log("reading svg mapping");
		Map<String, String> classes = new LinkedHashMap<>();
		Map<String, String> fields = new LinkedHashMap<>();
		Map<String, String> methods = new LinkedHashMap<>();

		String currentClassA = null;
		String currentClassB = null;
		try {
			String string = "";
			while ((string = s.readLine()) != null) {

				if (!string.startsWith("\t")) {
					String[] parts = string.split(" ");
					classes.put(parts[0], parts[1]);
					currentClassA = parts[0];
					currentClassB = parts[1];
					continue;
				}

				string = string.substring(1);

				String[] parts = string.split(" ");

				if (parts.length == 2) {
					fields.put(
							currentClassA + ":" + parts[0],
							currentClassB + ":" + fieldNames.getOrDefault(parts[1], parts[1])
					);
				} else if (parts.length == 3) {
					methods.put(
							currentClassA + ":" + parts[0] + parts[1],
							currentClassB + ":" + methodNames.getOrDefault(parts[2], parts[2]) + parts[1]
					);
				}
			}

			s.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

		Mappings mappings = new Mappings();
		mappings.classes.putAll(classes);
		mappings.fields.putAll(fields);
		methods.forEach((a, b) -> mappings.methods.put(a, remapMethodDescriptor(b, classes)));
		return mappings;
	}

	public static Map<String, String> readCsv(BufferedReader s) throws IOException {
		Main.forge_logger.log("reading csv mapping");
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
