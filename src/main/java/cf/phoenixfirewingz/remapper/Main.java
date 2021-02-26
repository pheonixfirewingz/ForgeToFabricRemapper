package cf.phoenixfirewingz.remapper;

import cf.phoenixfirewingz.remapper.common.Config;
import cf.phoenixfirewingz.remapper.utils.JsonData;

import java.io.File;
import java.util.Objects;

public class Main
{

	public static Constansts constansts;
	public static final Config config = JsonData.readJsonConfig();

	public static void main(String[] args)
	{
		System.out.println("getting lastest ForgeToFabricRemapper mapping....\n");
		constansts = new Constansts("C:\\Users\\sindr\\Dokumenter\\Development\\Misc\\ForgeToFabricRemapper\\mappings.json");
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

