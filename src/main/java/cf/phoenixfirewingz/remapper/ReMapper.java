package cf.phoenixfirewingz.remapper;

public class ReMapper
{
	//fabric
	public static String maptoFabric(String data)
	{
		StringBuilder ret_string = new StringBuilder("");
		String[] process_string = data.replace(' ','#').split("\n");
		for(String s:process_string)
		{
			if(s.isEmpty()) ret_string.append("\n");
			else if(s.startsWith("package"))
				ret_string.append(s.replace('#',' ')).append("\n");
			else if(s.startsWith("import"))
			{
				if(checkImport(s.split("#")[1]))
					ret_string.append(s.replace('#', ' ')).append("\n");
			}
			else if(s.replace('#',' ').contains("public class") || s.replace('#',' ').contains("public abstract class"))
			{
				if(checkExtORImp(s)) ret_string.append(fixExtOrImp(s)).append("\n");
			}
			else
					ret_string.append(fixOther(s.replace('#',' '))).append("\n");
		}
		System.out.println(ret_string.toString() + "\n=================================================================");
		return ret_string.toString();
	}

	private static String fixOther(String s)
	{
		if(s.startsWith("{") || s.startsWith("}")) return s;

		if(s.contains("public") || s.contains("private") || s.contains("protected"))
		{
			String result = Main.constansts.CheckForClassConstMatchFabricClassIfSoRetIt(s);
			if(result != null) return result;
		}
		//fixme: need to add function remapping
		return s;
	}

	private static String fixExtOrImp(String s)
	{
		String[] data = s.split("#");

		int i = 0;
		while(i < data.length)
		{
			if(data[i].contains("extends") || data[i].contains("implements"))
			{
				i++;
				String result = Main.constansts.CheckForClassConstMatchFabricClassIfSoRetIt(data[i]);
				if(result != null) data[i] = result;
			}
			i++;
		}
		return s.replace('#',' ');
	}

	//forge
	public static String maptoForge(String data)
	{
		System.out.println("fabric to forge is not implemented");
		return null;
	}

	//shared functions
	private static final String[] ok_starts = {"java","com.google","org.jetbrains"};

	private static boolean checkImport(String s)
	{
		boolean ok = false;
		for(String comp:ok_starts) if(s.startsWith(comp)) ok = true;
		return ok;
	}

	private static boolean checkExtORImp(String s)
	{
		String[] data = s.split("#");

		for(String comp:data)
			if(comp.contains("implements") || comp.contains("extends")) return true;

		return false;
	}
}
