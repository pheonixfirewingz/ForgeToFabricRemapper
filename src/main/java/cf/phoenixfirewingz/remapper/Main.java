package cf.phoenixfirewingz.remapper;

import cf.phoenixfirewingz.remapper.common.Process;
import cf.phoenixfirewingz.remapper.common.*;
import cf.phoenixfirewingz.remapper.utils.*;
import java.io.*;
import java.util.*;

public class Main
{
	public static final Config config = JsonUtils.readJsonConfig();
	public static final Logger common_logger = new Logger("common");
	public static final Logger fabric_logger = new Logger("fabric");
	public static final Logger forge_logger = new Logger("forge");
	
	public static Constants constants;
	private static final String CSV_MAPPINGS =
			String.format("https://files.minecraftforge.net/maven/de/oceanlabs/mcp/mcp_%s/%s/mcp_%s-%s.zip", config.mcpMappingsChannel, config.mcpMappings, config.mcpMappingsChannel, config.mcpMappings);
	private static final String SRG_MAPPINGS =
			String.format("https://raw.githubusercontent.com/MinecraftForge/MCPConfig/master/versions/release/%s/joined.tsrg", config.version);
	private static final String YARN =
			String.format("https://maven.fabricmc.net/net/fabricmc/yarn/%s+build.%s/yarn-%s+build.%s.jar", config.version, config.yarnVersion, config.version, config.yarnVersion);

	public static void main(String[] args)
	{
		Mappings mappings = null;
		File mapping = new File(Defines.cacheMap_location, "mappingsgrwgwe.tiny");
		if(!mapping.exists())
		{
			common_logger.log("getting latest defined mappings....");
			try
			{
				File csvFile = FileHandler.download(CSV_MAPPINGS);
				File srgFile = FileHandler.download(SRG_MAPPINGS);
				Mappings srg = ForgeUtils.readSrg(Objects.requireNonNull(FileHandler.getReader(srgFile)),
						ForgeUtils.readCsv(Objects.requireNonNull(FileHandler.getReader(FileHandler.extract(csvFile, "fields.csv")))),
						ForgeUtils.readCsv(Objects.requireNonNull(FileHandler.getReader(FileHandler.extract(csvFile, "methods.csv"))))
				);
				File yarn_File = FileHandler.extract(FileHandler.download(YARN),"mappings/mappings.tiny");
				Mappings yarn = FabricUtils.readYarnV1(Objects.requireNonNull(FileHandler.getReader(yarn_File)),"official","named");
				assert yarn != null;

				File mojang_client_File = FileHandler.download("https://launcher.mojang.com/v1/objects/374c6b789574afbdc901371207155661e0509e17/client.txt");
				File mojang_server_File = FileHandler.download("https://launcher.mojang.com/v1/objects/41285beda6d251d190f2bf33beadd4fee187df7a/server.txt");

				mappings = srg.invert().chain(yarn, false);

				if(!config.convertion.reverse) mappings = mappings.invert();

				mappings.classes.put("net/minecraftforge/api/distmarker/OnlyIn", "net/fabricmc/api/Environment");
				mappings.classes.put("net/minecraftforge/api/distmarker/Dist", "net/fabricmc/api/EnvType");

				mappings.writeDebugMapping(new File(Defines.jar_root + "\\resource\\cache\\mappings"), new File(Defines.cache_location, "chained_mapping.json"));
				srg.writeDebugMapping(new File(Defines.jar_root + "\\resource\\cache\\srg"), new File(Defines.cache_location, "mcp.json"));
				yarn.writeDebugMapping(new File(Defines.jar_root + "\\resource\\cache\\yarn"), new File(Defines.cache_location, "yarn.json"));

//				FileHandler.deleteAllCasheFilesExceptThisDirectory("mappings");
			}
			catch(IOException exception)
			{
				common_logger.logError(exception.getMessage());
			}
			common_logger.log("creating constants from mappings........");
			constants = new Constants(mappings);
		}
		else
		{
			common_logger.log("creating constants from cashes mapping......");
			constants = new Constants(new Mappings(new CashedMapping(
														new File(Defines.cacheMap_location, "classes.txt"),
														new File(Defines.cacheMap_location, "fields.txt"),
														new File(Defines.cacheMap_location, "methods.txt"))));
		}
		common_logger.log("start remapping classes.......");
//		readDir(new ResourceLocation("old"));
	}

	public static void readDir(File in)
	{
		for(File s: Objects.requireNonNull(in.listFiles()))
		{
			if(s.isDirectory()) readDir(s);
			else if(s.isFile()) if(!s.getName().equals("package-info.java"))
				new Thread(new Process(s)).start();
		}
	}
}

