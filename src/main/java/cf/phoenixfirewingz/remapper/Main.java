package cf.phoenixfirewingz.remapper;

import cf.phoenixfirewingz.remapper.common.Config;
import cf.phoenixfirewingz.remapper.common.Mappings;
import cf.phoenixfirewingz.remapper.utils.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Main
{
	public static final Config config = JsonUtils.readJsonConfig();
	public static final Logger common_logger = new Logger("common");
	public static final Logger fabric_logger = new Logger("fabric");
	public static final Logger forge_logger = new Logger("forge");
	
	public static Constansts constansts;
	private static final String CSV_MAPPINGS =
			String.format("https://files.minecraftforge.net/maven/de/oceanlabs/mcp/mcp_%s/%s/mcp_%s-%s.zip", config.mcpMappingsChannel, config.mcpMappings, config.mcpMappingsChannel, config.mcpMappings);
	private static final String SRG_MAPPINGS = String.format("https://raw.githubusercontent.com/MinecraftForge/MCPConfig/master/versions/release/%s/joined.tsrg", config.version);
	private static final String YARN = String.format("https://maven.fabricmc.net/net/fabricmc/yarn/%s+build.%s/yarn-%s+build.%s.jar", config.version, config.yarnVersion, config.version, config.yarnVersion);

	public static void main(String[] args) throws Exception {
		common_logger.log("getting latest defined mappings....");
		Mappings mappings = null;
		try {
			File csvFile = FileHandler.download(CSV_MAPPINGS);
			File srgFile = FileHandler.download(SRG_MAPPINGS);
			Mappings srg = ForgeUtils.readSrg(
					FileHandler.getReader(true, srgFile.getPath()),
					ForgeUtils.readCsv(FileHandler.getReader(true, FileHandler.extract(csvFile,"fields.csv").getPath())),
					ForgeUtils.readCsv(FileHandler.getReader(true,  FileHandler.extract(csvFile,"methods.csv").getPath()))
			);

			Mappings yarn = FabricUtils.readYarnV1(
					new Scanner(Objects.requireNonNull(
								FileHandler.extract(FileHandler.download(YARN), "mappings/mappings.tiny"))),
					"official",
					"named");

			mappings = srg.invert().chain(yarn, false);

			if (!config.shouldMapToFabric) {
				mappings = mappings.invert();
			}

			mappings.writeDebugMapping(new File(Defines.jar_root + "\\resource\\cache\\mappings"), new File(Defines.cache_location, "chained_mapping.json"));
			srg.writeDebugMapping(new File(Defines.jar_root + "\\resource\\cache\\srg"), new File(Defines.cache_location, "mcp.json"));
			yarn.writeDebugMapping(new File(Defines.jar_root + "\\resource\\cache\\yarn"), new File(Defines.cache_location, "yarn.json"));

			mappings.classes.put("net/minecraftforge/api/distmarker/OnlyIn", "net/fabricmc/api/Environment");
			mappings.classes.put("net/minecraftforge/api/distmarker/Dist", "net/fabricmc/api/EnvType");

			for(File file : Defines.cache_location.listFiles()) {
				if (!file.getPath().endsWith(".json")) {
					file.delete();
				}
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		common_logger.log("creating constants from mappings........");
		constansts = new Constansts(mappings);
		common_logger.log("start remapping classes.......");
		readDir(new ResourceLocation("old"));
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

