package cf.phoenixfirewingz.remapper;

import cf.phoenixfirewingz.remapper.utils.*;

import java.util.Vector;

public class Constansts
{
	public final Vector<StringTuple> constans_classes;
	public final Vector<StringTuple> constans_methods;
	/*
		forge mapping names must go on the left
		fabric mapping names must go on the right
		it will break the remapper if not
	 */
	public Constansts(String path)
	{
		VectorTuple<StringTuple> mapping = JsonData.readJsonMap(false,path);
		constans_classes = mapping.getA();
		constans_methods = mapping.getB();
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
