package cf.phoenixfirewingz.remapper.common;

import cf.phoenixfirewingz.remapper.utils.StringTuple;

import java.util.Vector;

public class StringMapping
{
	private final Vector<StringTuple> names = new Vector<>();
	public StringMapping(Mappings mappings)
	{
		mappings.classes.forEach((s,t) ->
				names.add(new StringTuple(s.replace("/","."),t.replace("/","."))));
	}
	public Vector<StringTuple> getNames()
	{
		return names;
	}
}
