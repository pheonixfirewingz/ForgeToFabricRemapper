package cf.phoenixfirewingz.remapper.utils;

import cf.phoenixfirewingz.remapper.Main;
import cf.phoenixfirewingz.remapper.common.Defines;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.zip.*;

import static java.util.Objects.requireNonNull;

public class FileHandler
{
	public static void deleteAllCasheFilesExceptThisDirectory(String exception) throws IOException
	{
		for(File file : requireNonNull(Defines.cache_location.listFiles()))
		{
			if(!file.getName().equals(exception))
			{
				if(file.isFile()) FileUtils.deleteQuietly(file);
				else FileUtils.deleteDirectory(file);
			}
		}
	}

	public static BufferedReader getReader(File path)
	{
		try
		{
			return new BufferedReader(new FileReader(path));
		}
		catch(IOException e)
		{
			Main.common_logger.logError(e.getMessage());
		}
		return null;
	}

	public static String read(File path)
	{
		try
		{
			return FileUtils.readFileToString(path, StandardCharsets.UTF_8);
		}
		catch(IOException e)
		{
			Main.common_logger.logError(e.getMessage());
		}
		return "failed to read file";
	}

	public static void write(String data, String path)
	{
		try
		{
			FileUtils.writeStringToFile(new File(path), data);
		}
		catch(IOException e)
		{
			Main.common_logger.logError(e.getMessage());
		}
	}

	public static File download(String url) throws IOException
	{
		Main.common_logger.log("downloading " + url);
		File file = new File(Defines.cache_location, url.substring(url.lastIndexOf('/') + 1));
		FileUtils.copyURLToFile(new URL(url),file , 10000, 10000);
		return file;
	}

	public static File extract(File zip, String path) throws IOException
	{
		File file = new File(Defines.cache_location, path);
		file.mkdirs();

		ZipFile zipFile = new ZipFile(zip);
		InputStream is = null;

		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while(entries.hasMoreElements())
		{
			ZipEntry zipEntry = entries.nextElement();
			if(zipEntry.getName().equals(path))
			{
				is = zipFile.getInputStream(zipEntry);
				break;
			}
		}

		if(is == null) return null;

		Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

		return file;
	}
}
