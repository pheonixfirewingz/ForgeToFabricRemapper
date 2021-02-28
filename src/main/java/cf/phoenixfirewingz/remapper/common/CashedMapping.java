package cf.phoenixfirewingz.remapper.common;

import cf.phoenixfirewingz.remapper.utils.*;

import java.io.File;
import java.util.Vector;

public class CashedMapping
{
	public final Vector<StringTuple> classes = new Vector<>();
	public final Vector<StringTuple> fields = new Vector<>();
	public final Vector<StringTuple> methods = new Vector<>();

	public CashedMapping(File classes_in,File fields_in,File methods_in)
	{
		String classes_raw = FileHandler.read(classes_in);
		String fields_raw = FileHandler.read(fields_in);
		String methods_raw = FileHandler.read(methods_in);

		String[] split1 = classes_raw.split("\n");
		String[] split2 = fields_raw.split("\n");
		String[] split3 = methods_raw.split("\n");

		for(String s:split1)
		{
			String[] data = s.split("->");
			classes.add(new StringTuple(data[0],data[1]));
		}
		for(String s:split2)
		{
			String[] data = s.split("->");
			fields.add(new StringTuple(data[0],data[1]));
		}
		for(String s:split3)
		{
			String[] data = s.split("->");
			methods.add(new StringTuple(data[0],data[1]));
		}
	}
}
