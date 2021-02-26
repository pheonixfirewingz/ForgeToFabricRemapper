package cf.phoenixfirewingz.remapper.utils;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class FileHandler
{
	public static BufferedReader getReader(boolean isLocal,String path) throws Exception
	{
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

	public static String read(boolean isLocal,String path) throws Exception
	{
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
}
