package cf.phoenixfirewingz.remapper.common;

import cf.phoenixfirewingz.remapper.utils.FileHandler;
import cf.phoenixfirewingz.remapper.utils.JsonUtils;

import java.io.*;
import java.util.*;

public class Mappings {
	public final Map<String, String> classes = new LinkedHashMap<>();
	public final Map<String, String> fields = new LinkedHashMap<>();
	public final Map<String, String> methods = new LinkedHashMap<>();



	public Mappings() {}

	public Mappings(CashedMapping mapping)
	{
		mapping.classes.forEach((s) -> classes.put(s.getForge(),s.getFabric()));
		mapping.fields.forEach((s) -> fields.put(s.getForge(),s.getFabric()));
		mapping.methods.forEach((s) -> methods.put(s.getForge(),s.getFabric()));
	}

	public Mappings chain(Mappings other, boolean defaultIfMissing) {
		Mappings result = new Mappings();

		if (defaultIfMissing) {
			classes.forEach((a, b) -> result.classes.put(
					a, other.classes.getOrDefault(b, b)
			));
			fields.forEach((a, b) -> result.fields.put(
					a, other.fields.getOrDefault(b, b)
			));
			methods.forEach((a, b) -> result.methods.put(
					a, other.methods.getOrDefault(b, b)
			));
		}
		else {
			classes.forEach((a, b) -> {
				String s = other.classes.get(b);
				if (s != null) {
					result.classes.put(a, s);
				}
			});
			fields.forEach((a, b) -> {
				String s = other.fields.get(b);
				if (s != null) {
					result.fields.put(a, s);
				}
			});
			methods.forEach((a, b) -> {
				String s = other.methods.get(b);
				if (s != null) {
					result.methods.put(a, s);
				}
			});
		}

		return result;
	}

	public Mappings invert() {
		Mappings result = new Mappings();

		classes.forEach((a, b) -> result.classes.put(b, a));
		fields.forEach((a, b) -> result.fields.put(b, a));
		methods.forEach((a, b) -> result.methods.put(b, a));

		return result;
	}

	public void writeDebugMapping(File dir, File file) {
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (FileWriter fileWriter = new FileWriter(file)) {
			JsonUtils.getGson().toJson(this, fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		dir.mkdirs();
		writeMappingData(this.classes, new File(dir, "classes.txt"));
		writeMappingData(this.fields, new File(dir, "fields.txt"));
		writeMappingData(this.methods, new File(dir, "methods.txt"));
	}

	private void writeMappingData(Map<String, String> data, File textFile) {
		StringBuilder builder = new StringBuilder();
		data.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.forEach(entry -> builder.append(entry.getKey()).append("->").append(entry.getValue()).append("\n"));
		FileHandler.write(builder.toString(), textFile.getPath());
	}
}
