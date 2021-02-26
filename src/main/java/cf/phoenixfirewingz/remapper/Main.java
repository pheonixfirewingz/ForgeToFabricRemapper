package cf.phoenixfirewingz.remapper;

import cf.phoenixfirewingz.remapper.common.Config;
import cf.phoenixfirewingz.remapper.utils.FileHandler;
import cf.phoenixfirewingz.remapper.utils.JsonData;

import java.io.File;
import java.util.Objects;
import java.util.Scanner;

public class Main
{

	public static Constansts constansts;
	public static final Config config = JsonData.readJsonConfig();

	private static final String CSV_MAPPINGS =
			String.format("https://files.minecraftforge.net/maven/de/oceanlabs/mcp/mcp_%s/%s/mcp_%s-%s.zip", config.mcpMappingsChannel, config.mcpMappings, config.mcpMappingsChannel, config.mcpMappings);
	private static final String YARN = String.format("https://maven.fabricmc.net/net/fabricmc/yarn/%s+build.%s/yarn-%s+build.%s.jar", config.version, config.yarnVersion, config.version, config.yarnVersion);

	public static void main(String[] args)
	{
		File cacheDir = new File(System.getProperty("user.dir") + "\\resource\\cache");
		System.out.println("getting lastest ForgeToFabricRemapper mapping....\n");
		try {
			FileHandler.Mappings srg = FileHandler.readTsrg(
					new Scanner(FileHandler.download(CSV_MAPPINGS, new File(System.getProperty("user.dir") + "\\resource\\cache"))),
					FileHandler.readCsv(new Scanner(Objects.requireNonNull(FileHandler.extract(
							FileHandler.download(CSV_MAPPINGS, new File(System.getProperty("user.dir") + "\\resource\\cache")),
							"fields.csv",
							new File(System.getProperty("user.dir") + "\\resource\\cache")
					)))),
					FileHandler.readCsv(new Scanner(Objects.requireNonNull(FileHandler.extract(
							FileHandler.download(CSV_MAPPINGS, new File(System.getProperty("user.dir") + "\\resource\\cache")),
							"methods.csv",
							new File(System.getProperty("user.dir") + "\\resource\\cache")
					))))
			);

			FileHandler.Mappings yarn = FileHandler.readYarnV1(
					new Scanner(Objects.requireNonNull(FileHandler.extract(FileHandler.download(YARN, new File(System.getProperty("user.dir") + "\\resource\\cache")), "mappings/mappings.tiny",
							new File(System.getProperty("user.dir") + "\\resource\\cache")
					))),
					"official", "named"
			);

			FileHandler.Mappings mappings = srg.invert().chain(yarn, false);


			mappings.writeDebugMapping(new File(System.getProperty("user.dir") + "\\resource\\cache\\mappings"), new File(cacheDir, "chained_mapping.json"));
			srg.writeDebugMapping(new File(System.getProperty("user.dir") + "\\resource\\cache\\srg"), new File(cacheDir, "mcp.json"));
			yarn.writeDebugMapping(new File(System.getProperty("user.dir") + "\\resource\\cache\\yarn"), new File(cacheDir, "yarn.json"));

			mappings.methods.forEach((s, s2) -> {
				System.out.println("1:" + s + " 2:" + s2);
			});
		} catch (Exception exception) {
			exception.printStackTrace();
		}
<<<<<<< Updated upstream
<<<<<<< Updated upstream
//		System.out.println("start remapping classes.......\n");
//		readDir(new File(System.getProperty("user.dir") + "\\resource\\old"));
=======
=======
>>>>>>> Stashed changes
		constansts = new Constansts(System.getProperty("user.dir") + "\\mappings.json");
		System.out.println("start remapping classes.......\n");
		readDir(new File(System.getProperty("user.dir") + "\\resource\\old"));
>>>>>>> Stashed changes
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

