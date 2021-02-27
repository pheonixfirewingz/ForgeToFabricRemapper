package cf.phoenixfirewingz.remapper;

import cf.phoenixfirewingz.remapper.common.*;
import cf.phoenixfirewingz.remapper.utils.StringTuple;

public class Constansts
{
	public final Mappings mappings;
	public final ChangeMapping changeMapping;

	public Constansts(Mappings mappings)
	{
		this.mappings = mappings;
		changeMapping = new ChangeMapping(mappings);
	}

	public String mapToFabric(String line)
	{
		String current = line;
		for(StringTuple s:changeMapping.getNames())
		{
			String temp;
			if(line.contains(s.getA()))
			{
				temp = current.replace(s.getA(), s.getB());
				current = temp;
			}
		}
		return line;
	}

	public String mapToForge(String line)
	{
		String current = line;
		for(StringTuple s:changeMapping.getNames())
		{
			String temp;
			if(line.contains(s.getB()))
			{
				temp = current.replace(s.getB(), s.getA());
				current = temp;
			}
		}
		return line;
	}
}
