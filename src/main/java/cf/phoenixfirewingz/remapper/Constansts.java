package cf.phoenixfirewingz.remapper;

import cf.phoenixfirewingz.remapper.common.*;
import cf.phoenixfirewingz.remapper.utils.*;

import java.util.Vector;

public class Constansts
{
	public final Vector<StringTuple> constans_classes = new Vector<>();
	public final Vector<StringTuple> constans_methods = new Vector<>();
	/*
		forge mapping names must go on the left
		fabric mapping names must go on the right
		it will break the remapper if not
	 */
	public Constansts(String path)
	{
		Mapping mapping = JsonData.readJsonMap(false,path);
		for(MapEntry entry:mapping.getMappings())
		{
			if(entry.getType().equals("class"))
				constans_classes.add(new StringTuple(entry.getForge(), entry.getFabric()));
			else
				constans_methods.add(new StringTuple(entry.getForge(), entry.getFabric()));
		}
		int i = 0;
	}

	public  String CheckForClassConstMatchFabricClassIfSoRetIt(String s)
	{
		String[] data = s.split("#");
		int i = 0;
		for(String comp:data)
		{
			for(StringTuple constant:constans_classes)
				if(comp.contains(constant.getA())) data[i] = constant.getB();

			i++;
		}
		return null;
	}

	public  String CheckForClassConstMatchFabricMethodIfSoRetIt(String s)
	{
		String[] data = s.split("#");
		int i = 0;
		for(String comp:data)
		{
			for(StringTuple constant:constans_methods)
				if(comp.contains(constant.getA())) data[i] = constant.getB();

			i++;
		}
		return null;
	}
}
