import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main
{
	public static Constansts constansts = new Constansts();
	public static void main(String[] args)
	{
		try
		{
			readDir(new File("C:\\Users\\luket\\Desktop\\programing\\MCPToFabric\\resource\\old"));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void readDir(File in) throws IOException
	{
		for(File s: Objects.requireNonNull(in.listFiles()))
		{
			if(s.isDirectory()) readDir(s);
			else if(s.isFile())
				if(!s.getName().contains("package-info.java")) new Thread(new Process(s, true)).run();
		}
	}
}

