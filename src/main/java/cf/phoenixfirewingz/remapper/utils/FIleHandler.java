package cf.phoenixfirewingz.remapper.utils;

import cf.phoenixfirewingz.remapper.Main;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class FileHandler
{
	public static BufferedReader getReader(boolean isLocal,String path)
	{
		BufferedReader in = null;
		if(!isLocal)
		{
			try
			{
				in = new BufferedReader(new InputStreamReader(new URL(path).openStream()));
			} catch(IOException e)
			{
				Main.common_logger.logError(e.getMessage());
			}
		}
		else
		{
			try
			{
				in = new BufferedReader(new FileReader(path));
			} catch(FileNotFoundException e)
			{
				Main.common_logger.logError(e.getMessage());
			}
		}

		return in;
	}

	public static String read(boolean isLocal,String path) {
		StringBuilder data = new StringBuilder();
		BufferedReader in = getReader(isLocal,path);

		String line = null;
		while(true)
		{
			try
			{
				if(!((line = in.readLine()) != null)) break;
			} catch(IOException e)
			{
				Main.common_logger.logError(e.getMessage());
			}
			data.append(line).append("\n");
		}
		try
		{
			in.close();
		} catch(IOException e)
		{
			Main.common_logger.logError(e.getMessage());
		}

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
			Main.common_logger.logError(e.getMessage());
		}
	}

	public static void decompressGzip(String source, Path target) {
		try (GZIPInputStream gis = new GZIPInputStream(new BufferedInputStream(new URL(source).openStream()));
			 FileOutputStream fos = new FileOutputStream(target.toFile())) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = gis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		}
		catch(IOException e)
		{
			Main.common_logger.logError(e.getMessage());
		}
	}



	public static File download(String url) {
		Main.common_logger.log("downloading " + url);

		Defines.cache_location.mkdirs();
		File file = new File(Defines.cache_location, url.substring(url.lastIndexOf('/') + 1));

		if (!file.exists()) {
			try (InputStream in = new URL(url).openStream()) {
				Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			catch(IOException e)
			{
				Main.common_logger.logError(e.getMessage());
			}
		}

		return file;
	}

	public static File extract(File zip, String path) {
		Defines.cache_location.mkdirs();
		File file = new File(Defines.cache_location, path);
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
		catch(IOException e)
		{
			Main.common_logger.logError(e.getMessage());
		}

		return file;
	}


}
