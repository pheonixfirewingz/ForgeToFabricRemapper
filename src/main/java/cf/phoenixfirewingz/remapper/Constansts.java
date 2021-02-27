package cf.phoenixfirewingz.remapper;

import cf.phoenixfirewingz.remapper.common.Mappings;
import java.util.concurrent.atomic.*;

public class Constansts
{
	public final Mappings mappings;

	public Constansts(Mappings mappings)
	{
		this.mappings = mappings;
	}

	public boolean isForgeClass(String s)
	{
		AtomicBoolean isClass = new AtomicBoolean(false);
		forge.classes.forEach((String t,String i) -> {
			if(s.equals(i)) isClass.set(true);
		});
		return isClass.get();
	}

	public boolean isFabricClass(String s)
	{
		AtomicBoolean isClass = new AtomicBoolean(false);
		fabric_intermediary.classes.forEach((String t, String i) -> {
			if(s.equals(i)) isClass.set(true);
		});
		return isClass.get();
	}

	public boolean isForgeName(String s)
	{
		AtomicBoolean isMethod = new AtomicBoolean(false);
		forge.methods.forEach((String t,String i) -> {
			if(s.equals(i)) isMethod.set(true);
		});
		return isMethod.get();
	}

	public boolean isFabricName(String s)
	{
		AtomicBoolean isMethod = new AtomicBoolean(false);
		fabric_intermediary.methods.forEach((String t, String i) -> {
			if(s.equals(i)) isMethod.set(true);
		});
		return isMethod.get();
	}

	public String convertIntermittencyClassToFabric(String s)
	{
		AtomicReference<String> newName = new AtomicReference<>();
		fabric_intermediary.classes.forEach((String t, String r) -> {
			if(s.equals(t)) newName.set(r);
		});
		return newName.get();
	}

	public String convertIntermittencyMethodToFabric(String s)
	{
		AtomicReference<String> newName = new AtomicReference<>();
		fabric_intermediary.methods.forEach((String t, String r) -> {
			if(s.equals(t)) newName.set(r);
		});
		return newName.get();
	}
}
