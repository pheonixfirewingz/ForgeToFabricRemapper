package cf.phoenixfirewingz.remapper.common;

import cf.phoenixfirewingz.remapper.utils.StringTuple;

import java.util.Vector;

public class ChangeMapping
{
	private final Vector<StringTuple> names = new Vector<>();

	public ChangeMapping(Mappings mappings)
	{
		mappings.classes.forEach(this::getData);
		mappings.methods.forEach(this::getData);
		mappings.fields.forEach(this::getData);
	}

	private void getData(String s,String t)
	{
		String[] r = s.split("/");
		String[] i = t.split("/");
		int count = 0,count_2 = 0;
		for(;count != (r.length - 1);count++);
		for(;count_2 != (i.length - 1) ; count_2++);
		if(!r[count].equals(i[count_2])) names.add(new StringTuple(r[count],i[count_2]));
	}

	public Vector<StringTuple> getNames()
	{
		return names;
	}
}
