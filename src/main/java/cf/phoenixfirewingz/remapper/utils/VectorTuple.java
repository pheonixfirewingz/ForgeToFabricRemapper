package cf.phoenixfirewingz.remapper.utils;

import java.util.*;

public class VectorTuple<E>
{
	private final Vector<E> a;
	private final Vector<E> b;

	public VectorTuple(Vector<E> a, Vector<E> b)
	{
		this.a = a;
		this.b = b;
	}

	public Vector<E> getA()
	{
		return this.a;
	}

	public Vector<E> getB()
	{
		return this.b;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(a, b);
	}

}