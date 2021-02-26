import java.util.Vector;

public class Constansts
{
	public static final Vector<StringTuple> constans_classes = new Vector<StringTuple>();

	/*
		forge mapping names must go on the left
		fabric mapping names must go on the right
		it will break the remapper if not
	 */
	public Constansts()
	{
		constans_classes.add(new StringTuple("ResourceLocation", "Identifier"));
		constans_classes.add(new StringTuple("PacketBuffer","PacketByteBuf"));
		constans_classes.add(new StringTuple("IRecipeType","RecipeType"));
	}

	public  String CheckForClassConstMatchFabricIfSoRetIt(String s)
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
}
