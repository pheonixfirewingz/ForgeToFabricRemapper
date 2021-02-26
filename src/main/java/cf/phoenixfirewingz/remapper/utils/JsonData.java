package cf.phoenixfirewingz.remapper.utils;

import com.google.gson.Gson;
import java.util.Vector;

public class JsonData
{
	private static final Gson gson = new Gson();

	public static VectorTuple<StringTuple> readJsonMap(boolean isLocal, String path)
	{
		String data = null;
		Vector<StringTuple> classes = new Vector<>();
		Vector<StringTuple> methods = new Vector<>();
		try
		{
			data = FIleHandler.read(isLocal,path).replaceAll("\n", "");
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		VectorTuple<StringTuple> ret = new VectorTuple<>(classes,methods);
		return ret;
	}
}
