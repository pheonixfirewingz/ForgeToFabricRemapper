package com.minecolonies.coremod.items;

import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.sound.*;

public class ModArmorMaterial implements ArmorMaterial
{
	private int[] BASE_DURABILITY;
	private int[] PROTECTION_VALUES;
	private int x_factor;
	String name;

	public ModArmorMaterial(int[] base,int[] protect,int x_factor_in,String name_in)
	{
		name = name_in;
		x_factor = x_factor_in;
		BASE_DURABILITY = base;
		PROTECTION_VALUES = protect;
	}

	@Override
	public int getDurability(EquipmentSlot slot)
	{
		return BASE_DURABILITY[slot.getEntitySlotId()]*x_factor;
	}

	@Override
	public int getProtectionAmount(EquipmentSlot slot)
	{
		return PROTECTION_VALUES[slot.getEntitySlotId()];
	}

	@Override
	public int getEnchantability()
	{
		return x_factor;
	}

	@Override
	public SoundEvent getEquipSound()
	{
		return SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND;
	}

	@Override public Ingredient getRepairIngredient()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public float getToughness()
	{
		return (float)x_factor;
	}

	@Override
	public float getKnockbackResistance()
	{
		return (float) x_factor;
	}
}
