package cf.phoenixfirewingz.remapper.common;

import cf.phoenixfirewingz.remapper.utils.StringTuple;

public class Constants
{
	public final StringMapping mappings;
	public final ChangeMapping changeMapping;

	public Constants(Mappings mappings_raw)
	{
		mappings = new StringMapping(mappings_raw);
		changeMapping = new ChangeMapping(mappings_raw);
	}

	public String mapToImportsFabric(String line)
	{
		for(StringTuple s:mappings.getNames())
		{
			if(line.equals(s.getForge()))
				return "import " + s.getFabric() + ";";
		}
		return line;
	}

	public String mapToImportsForge(String line)
	{
		for(StringTuple s:mappings.getNames())
		{
			if(line.contains(s.getForge()))
				return "import " + s.getFabric() + ";";
		}
		return line;
	}

	public String mapToFabric(String line)
	{
		String current = line;
		for(StringTuple s:changeMapping.getNames())
		{
			String temp;
			if(line.contains(s.getForge()))
			{
				temp = current.replace(s.getForge(), s.getFabric());
				current = temp;
			}
		}
		return current;
	}

	public String mapToForge(String line)
	{
		String current = line;
		for(StringTuple s:changeMapping.getNames())
		{
			String temp;
			if(line.contains(s.getFabric()))
			{
				temp = current.replace(s.getFabric(), s.getForge());
				current = temp;
			}
		}
		return current;
	}
}
