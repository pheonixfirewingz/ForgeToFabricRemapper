package cf.phoenixfirewingz.remapper.utils;

import cf.phoenixfirewingz.remapper.Main;
import cf.phoenixfirewingz.remapper.common.Defines;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

import static java.util.Objects.*;

public class FileHandler
{
	public static void deleteAllCasheFilesExceptThisDirectory(String exception) throws IOException
	{
		for(File file : requireNonNull(Defines.cache_location.listFiles()))
		{
			if(file.isFile()) file.delete();
			else
			{
				if(file.isDirectory() && !file.getName().equals(exception))
				{
					for(File f : requireNonNull(file.listFiles())) f.delete();
					Files.delete(file.toPath());
				}
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
		StringBuilder data = new StringBuilder();
		BufferedReader in = getReader(path);
		String line = null;
		try
		{
			while((line = in.readLine()) != null) data.append(line).append("\n");
			in.close();
		}
		catch(IOException e)
		{
			Main.common_logger.logError(e.getMessage());
		}
		return data.toString();
	}

	public static void write(String data, String path)
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
			writer.write(requireNonNull(data));
			writer.flush();
			writer.close();
		}
		catch(IOException e)
		{
			Main.common_logger.logError(e.getMessage());
		}
	}

	public static void decompressGzip(String source, Path target)
	{
		try(GZIPInputStream gis = new GZIPInputStream(new BufferedInputStream(new URL(source).openStream()));
			FileOutputStream fos = new FileOutputStream(target.toFile()))
		{
			byte[] buffer = new byte[1024];
			int len;
			while((len = gis.read(buffer)) > 0) fos.write(buffer, 0, len);
		}
		catch(IOException e)
		{
			Main.common_logger.logError(e.getMessage());
		}
	}


	public static File download(String url)
	{
		Main.common_logger.log("downloading " + url);
		Defines.cache_location.mkdirs();
		File file = new File(Defines.cache_location, url.substring(url.lastIndexOf('/') + 1));

		if(!file.exists())
		{
			try(InputStream in = new URL(url).openStream())
			{
				Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			catch(IOException e)
			{
				Main.common_logger.logError(e.getMessage());
			}
		}
		return file;
	}

	public static File extract(File zip, String path)
	{
		Defines.cache_location.mkdirs();
		File file = new File(Defines.cache_location, path);
		file.mkdirs();

		try(ZipFile zipFile = new ZipFile(zip))
		{
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
		}
		catch(IOException e)
		{
			Main.common_logger.logError(e.getMessage());
		}
		return file;
	}
}
