package cf.phoenixfirewingz.remapper;

import java.io.File;
import java.util.Objects;

public class Main
{

	public static Constansts constansts;

	public static void main(String[] args)
	{
		System.out.println("getting lastest ForgeToFabricRemapper mapping....\n");
		constansts = new Constansts("https://github.com/pheonixfirewingz/ForgeToFabricRemapper/blob/main/mappings.json");
		System.out.println("start remapping classes.......\n");
		readDir(new File(System.getProperty("user.dir") + "\\resource\\old"));
	}

	public static void readDir(File in)
	{
		for(File s: Objects.requireNonNull(in.listFiles()))
		{
			if(s.isDirectory()) readDir(s);
			else if(s.isFile())
				if(!s.getName().contains("package-info.java")) new Thread(new Process(s, true)).start();
		}
	}
}

