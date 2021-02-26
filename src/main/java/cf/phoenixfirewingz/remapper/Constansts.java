package cf.phoenixfirewingz.remapper;

import cf.phoenixfirewingz.remapper.common.*;
import cf.phoenixfirewingz.remapper.utils.*;

import java.util.Vector;

public class Constansts
{
	public final Vector<StringTuple> constans_imports = new Vector<>();
	public final Vector<StringTuple> constans_classes = new Vector<>();
	public final Vector<StringTuple> constans_methods = new Vector<>();

	public Constansts(String path)
	{
		Mapping mapping = JsonData.readJsonMap(true,path);
		for(MapEntry entry:mapping.mappings)
		{
			if(entry.type.equals("class"))
				constans_classes.add(new StringTuple(entry.forge, entry.fabric));
			else if(entry.type.equals("import"))
				constans_imports.add(new StringTuple(entry.forge, entry.fabric));
			else
				constans_methods.add(new StringTuple(entry.forge, entry.fabric));
		}
		int i = 0;
	}

	public  String CheckForClassConstMatchFabricImportIfSoRetIt(String s)
	{
		String[] data = s.split("#");
		int i = 0;
		for(String comp:data)
		{
			for(StringTuple constant:constans_imports)
				if(comp.contains(constant.getA())) data[i] = constant.getB();

			i++;
		}
		return null;
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
