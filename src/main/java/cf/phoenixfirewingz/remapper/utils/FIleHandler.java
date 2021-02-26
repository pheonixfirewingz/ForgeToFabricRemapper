package cf.phoenixfirewingz.remapper.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileHandler
{
	public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static BufferedReader getReader(boolean isLocal,String path) throws Exception {
		BufferedReader in;
		if(!isLocal) in = new BufferedReader(new InputStreamReader(new URL(path).openStream()));
		else in = new BufferedReader(new FileReader(path));

		return in;
	}

	public static File getAsFile(boolean isLocal, String path) {
		File in;
		if(!isLocal) in = new File(path);
		else in = new File(path);

		return in;
	}

	public static void downloadFile(String url, String fileName) {
		try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
			 FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String read(boolean isLocal,String path) throws Exception {
		StringBuilder data = new StringBuilder();
		BufferedReader in = getReader(isLocal,path);

		String line;
		while((line = in.readLine()) != null) data.append(line).append("\n");
		in.close();

		return data.toString();
	}

	public static void write(String data,String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(Objects.requireNonNull(data));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println("ERROR:" + e.getMessage());
		}
	}

	public static void decompressGzip(String source, Path target) throws IOException {
		try (GZIPInputStream gis = new GZIPInputStream(new BufferedInputStream(new URL(source).openStream()));
			 FileOutputStream fos = new FileOutputStream(target.toFile())) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = gis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		}
	}

	public static Mappings readTsrg(Scanner s, Map<String, String> fieldNames, Map<String, String> methodNames) {
		Map<String, String> classes = new LinkedHashMap<>();
		Map<String, String> fields = new LinkedHashMap<>();
		Map<String, String> methods = new LinkedHashMap<>();

		String currentClassA = null;
		String currentClassB = null;
		while (s.hasNextLine()) {
			String line = s.nextLine();

			if (!line.startsWith("\t")) {
				String[] parts = line.split(" ");
				classes.put(parts[0], parts[1]);
				currentClassA = parts[0];
				currentClassB = parts[1];
				continue;
			}

			line = line.substring(1);

			String[] parts = line.split(" ");

			if (parts.length == 2) {
				fields.put(
						currentClassA + ":" + parts[0],
						currentClassB + ":" + fieldNames.getOrDefault(parts[1], parts[1])
				);
			}
			else if (parts.length == 3) {
				methods.put(
						currentClassA + ":" + parts[0] + parts[1],
						currentClassB + ":" + methodNames.getOrDefault(parts[2], parts[2]) + parts[1]
				);
			}
		}

		Mappings mappings = new Mappings();
		mappings.classes.putAll(classes);
		mappings.fields.putAll(fields);
		methods.forEach((a, b) -> mappings.methods.put(a, remapMethodDescriptor(b, classes)));

		s.close();
		return mappings;
	}

	public static Mappings readYarnV1(Scanner s, String from, String to) {
		String[] header = s.nextLine().split("\t");
		Map<String, Integer> columns = new HashMap<>();

		for (int i = 1; i < header.length; i++) {
			columns.put(header[i], i - 1);
		}

		int fromColumn = columns.get(from);
		int toColumn = columns.get(to);

		Map<String, String> classes = new LinkedHashMap<>();
		Map<String, String> fields = new LinkedHashMap<>();
		Map<String, String> methods = new LinkedHashMap<>();

		while (s.hasNextLine()) {
			String[] line = s.nextLine().split("\t");
			switch (line[0]) {
				case "CLASS": {
					classes.put(line[fromColumn + 1], line[toColumn + 1]);
					break;
				}

				case "FIELD": {
					fields.put(
							line[1] + ":" + line[fromColumn + 3],
							classes.get(line[1]) + ":" + line[toColumn + 3]
					);
					break;
				}

				case "METHOD": {
					String m1 = line[1] + ":" + line[fromColumn + 3] + line[2];
					String m2 = classes.get(line[1]) + ":" + line[toColumn + 3] + line[2];
					methods.put(
							m1,
							m2
					);
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

	public static File download(String url, File directory) throws IOException {
		System.out.println("downloading " + url);

		directory.mkdirs();
		File file = new File(directory, url.substring(url.lastIndexOf('/') + 1));

		if (!file.exists()) {
			try (InputStream in = new URL(url).openStream()) {
				Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		}

		return file;
	}

	public static Map<String, String> readCsv(Scanner s) {
		Map<String, String> mappings = new LinkedHashMap<>();

		try (Scanner r = s) {
			r.nextLine();
			while (r.hasNextLine()) {
				String[] parts = r.nextLine().split(",");
				mappings.put(parts[0], parts[1]);
			}
		}

		s.close();
		return mappings;
	}

	public static File extract(File zip, String path, File directory) throws IOException {
		directory.mkdirs();
		File file = new File(directory, path);
		file.mkdirs();

		try (ZipFile zipFile = new ZipFile(zip)) {
			InputStream is = null;

			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				if (zipEntry.getName().equals(path)) {
					is = zipFile.getInputStream(zipEntry);
					break;
				}
			}

			if (is == null) {
				return null;
			}

			Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		return file;
	}

	private static String remapMethodDescriptor(String method, Map<String, String> classMappings) {
		try {
			Reader r = new StringReader(method);
			StringBuilder result = new StringBuilder();
			boolean started = false;
			boolean insideClassName = false;
			StringBuilder className = new StringBuilder();
			while (true) {
				int c = r.read();
				if (c == -1) {
					break;
				}

				if (c == ';') {
					insideClassName = false;
					result.append(classMappings.getOrDefault(
							className.toString(),
							className.toString()
					));
				}

				if (insideClassName) {
					className.append((char) c);
				}
				else {
					result.append((char) c);
				}

				if (c == '(') {
					started = true;
				}

				//qouteall changed
				if (started && c == 'L' && !insideClassName) {
					insideClassName = true;
					className.setLength(0);
				}
			}

			return result.toString();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	public static class Mappings {
		public final Map<String, String> classes = new LinkedHashMap<>();
		public final Map<String, String> fields = new LinkedHashMap<>();
		public final Map<String, String> methods = new LinkedHashMap<>();

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
				gson.toJson(this, fileWriter);
			} catch (IOException e) {
				e.printStackTrace();
			}
			dir.mkdirs();
            writeMappingData(this.classes, new File(dir, "classes.txt"));
            writeMappingData(this.fields, new File(dir, "fields.txt"));
            writeMappingData(this.methods, new File(dir, "methods.txt"));
		}

        private void writeMappingData(Map<String, String> data, File textFile) {
            try {
                textFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (FileWriter fileWriter = new FileWriter(textFile)) {
                data.entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .forEach(entry -> {
                        try {
                            fileWriter.write(entry.getKey() + "->" + entry.getValue() + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                fileWriter.flush();
            } catch (IOException ignored) {
            }
        }
	}
}
