package cf.phoenixfirewingz.remapper.utils;

import java.util.Objects;

public class StringTuple
{
	private final String a;
	private final String b;

	public StringTuple(final String aIn, final String bIn)
	{
		this.a = aIn;
		this.b = bIn;
	}

	public String getA()
	{
		return this.a;
	}

	public String getB()
	{
		return this.b;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(a, b);
	}

}