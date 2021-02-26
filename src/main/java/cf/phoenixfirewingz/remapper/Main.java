package cf.phoenixfirewingz.remapper;

import cf.phoenixfirewingz.remapper.common.Config;
import cf.phoenixfirewingz.remapper.utils.FileHandler;
import cf.phoenixfirewingz.remapper.utils.JsonData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class Main
{

	public static Constansts constansts;
	public static final Config config = JsonData.readJsonConfig();

	private static String TINY_MAPPINGS_FILE = String.format("https://maven.fabricmc.net/net/fabricmc/yarn/%s+build.%s/yarn-%s+build.%s-tiny.gz", config.version, config.yarnVersion, config.version, config.yarnVersion);
	public static String TINY_MAPPINGS_CONVERTED;

	public static void main(String[] args)
	{
		System.out.println("getting lastest ForgeToFabricRemapper mapping....\n");
		constansts = new Constansts(System.getProperty("user.dir") + "\\mappings.json");
		try {
			try {
				FileHandler.decompressGzip(TINY_MAPPINGS_FILE, Paths.get(System.getProperty("user.dir") + "\\resource\\tiny_mappings_" + config.version + ".txt"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			TINY_MAPPINGS_CONVERTED = FileHandler.read(true, System.getProperty("user.dir") + "\\resource\\tiny_mappings" + config.version + ".txt");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		System.out.println("start remapping classes.......\n");
		readDir(new File(System.getProperty("user.dir") + "\\resource\\old"));
	}

	public static void readDir(File in)
	{
		for(File s: Objects.requireNonNull(in.listFiles()))
		{
			if(s.isDirectory()) readDir(s);
			else if(s.isFile())
				if(!s.getName().contains("package-info.java")) new Thread(new Process(s)).start();
		}
	}
}

