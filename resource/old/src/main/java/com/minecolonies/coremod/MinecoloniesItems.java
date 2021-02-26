package com.minecolonies.coremod;


import com.minecolonies.coremod.items.*;
import com.minecolonies.coremod.util.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;

public class MinecoloniesItems
{
	private static final ModArmorMaterial pirate = new ModArmorMaterial(new int[]{13, 15, 16, 11}, new int[]{1,1,1,1},
																		25, "pirate");
	//private static final ModToolMaterial pirate_tool = new ModToolMaterial(500,3,5.0f,3);
	public static Item supplyChest;
	public static Item permTool;
	public static Item scepterGuard;
	public static Item bannerRallyGuards;
	public static Item supplyCamp;
	public static Item ancientTome;
	public static Item scimitar;
	public static Item scepterLumberjack;
	public static Item pharaoscepter;
	public static Item firearrow;
	public static Item scepterBeekeeper;

	public static Item clipboard;

	public static Item resourceScroll;

	public static Item flagBanner;

	public static Item scrollColonyTP;
	public static Item scrollColonyAreaTP;
	public static Item scrollBuff;
	public static Item scrollGuardHelp;
	public static Item scrollHighLight;

	public static Item compost;
	public static Item builders_wand;

	public static void registerItems()
	{
		FabricWrapper.regItem(new ArmorItem(pirate, EquipmentSlot.HEAD,new MineColoniesItemSettings()),"santa_hat");
		FabricWrapper.regItem(new ArmorItem(pirate, EquipmentSlot.HEAD,new MineColoniesItemSettings()),"pirate_hat");
		FabricWrapper.regItem(new ArmorItem(pirate, EquipmentSlot.HEAD,new MineColoniesItemSettings()),"pirate_cap");
		FabricWrapper.regItem(new ArmorItem(pirate, EquipmentSlot.CHEST,new MineColoniesItemSettings()),"pirate_chest");
		FabricWrapper.regItem(new ArmorItem(pirate, EquipmentSlot.CHEST,new MineColoniesItemSettings()),"pirate_top");
		FabricWrapper.regItem(new ArmorItem(pirate, EquipmentSlot.LEGS,new MineColoniesItemSettings()),"pirate_leggins");
		FabricWrapper.regItem(new ArmorItem(pirate, EquipmentSlot.LEGS,new MineColoniesItemSettings()),"pirate_legs");
		FabricWrapper.regItem(new ArmorItem(pirate, EquipmentSlot.FEET,new MineColoniesItemSettings()),"pirate_shoes");
		//FabricWrapper.regItem(new SwordItem(pirate_tool, 3,2,new MineColoniesItemSettings()),"chief_sword");
		FabricWrapper.regItem(new Item(new MineColoniesItemSettings()),"bread_dough");
		FabricWrapper.regItem(new Item(new MineColoniesItemSettings()),"cake_batter");
		FabricWrapper.regItem(new Food(1,16F),"cookie_dough");
		FabricWrapper.regItem(new Food(1,1F),"raw_pumpkin_pie");
		FabricWrapper.regItem(new Food(2,4F),"milky_bread");
		FabricWrapper.regItem(new Food(4,5F),"sugary_bread");
		FabricWrapper.regItem(new Food(5,8F),"golden_bread");
		FabricWrapper.regItem(new Food(20,-0.6f),"chorus_bread");
		compost = FabricWrapper.regItemWithRet(new ItemCompost(),"compost");
		builders_wand = FabricWrapper.regItemWithRet(new BuildWand(),"sceptergold");
	}
}
