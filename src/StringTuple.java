import org.jetbrains.annotations.Nullable;

import java.util.Objects;
public class StringTuple
{
	private final String a;
	private final String b;

	public StringTuple(@Nullable final String aIn, @Nullable final String bIn)
	{
		this.a = aIn;
		this.b = bIn;
	}

	@Nullable
	public String getA()
	{
		return this.a;
	}

	@Nullable
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