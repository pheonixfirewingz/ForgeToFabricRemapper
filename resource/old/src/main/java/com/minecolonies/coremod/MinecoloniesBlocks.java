package com.minecolonies.coremod;

import com.minecolonies.coremod.blocks.BlockBarrel;
import com.minecolonies.coremod.blocks.abstracts.AbstractBlockHut;
import com.minecolonies.coremod.blocks.decorative.BlockGate;
import com.minecolonies.coremod.util.FabricWrapper;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import org.jetbrains.annotations.NotNull;

public class MinecoloniesBlocks
{
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutTownHall;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutHome;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutMiner;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutLumberjack;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutBaker;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutBuilder;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutDeliveryman;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutBlacksmith;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutStonemason;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutFarmer;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutFisherman;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutGuardTower;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutWareHouse;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutShepherd;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutCowboy;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutSwineHerder;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutChickenHerder;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutBarracks;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutBarracksTower;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutCook;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutSmeltery;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutComposter;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutLibrary;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutArchery;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutCombatAcademy;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutSawmill;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutStoneSmeltery;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutCrusher;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutSifter;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockPostBox;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutFlorist;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutEnchanter;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutUniversity;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutHospital;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockStash;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutSchool;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutGlassblower;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutDyer;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutFletcher;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutMechanic;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutPlantation;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutTavern;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutRabbitHutch;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutConcreteMixer;
	public static AbstractBlockHut<? extends AbstractBlockHut<?>> blockHutBeekeeper;
	public static Block barrel_block;
	public static BlockEntityType<TEBarrel> barrel_entity_block;
	public static Block blockConstructionTape;
	public static Block blockWayPoint;
	public static Block blockDecorationPlaceholder;
	public static Block blockScarecrow;
	public static Block blockBarracksTowerSubstitution;
	public static Block blockCompostedDirt;
	public static Block blockColonyBanner;
	public static Block blockColonyWallBanner;

	public static void registerBlocks()
	{
		barrel_block = FabricWrapper.regBlockWithRet(new BlockBarrel(),"barrel_block");
		barrel_entity_block = FabricWrapper.regBlockEntity(
				BlockEntityType.Builder.create(TEBarrel::new, barrel_block).build(null),"barrel_entity_block");
		FabricWrapper.regBlock(new BlockGate(9),"gate_wood");
		FabricWrapper.regBlock(new BlockGate(9),"gate_iron");
	}

	@NotNull
	public static AbstractBlockHut<?>[] getHuts()
	{
		return new AbstractBlockHut[]{
				blockHutStoneSmeltery,
				blockHutStonemason,
				blockHutGuardTower,
				blockHutArchery,
				blockHutBaker,
				blockHutBarracks,
				blockHutBarracksTower,
				blockHutBlacksmith,
				blockHutBuilder,
				blockHutChickenHerder,
				blockHutHome,
				blockHutCombatAcademy,
				blockHutComposter,
				blockHutCook,
				blockHutCowboy,
				blockHutCrusher,
				blockHutDeliveryman,
				blockHutFarmer,
				blockHutFisherman,
				blockHutLibrary,
				blockHutLumberjack,
				blockHutMiner,
				blockHutSawmill,
				blockHutSifter,
				blockHutShepherd,
				blockHutSmeltery,
				blockHutSwineHerder,
				blockHutTownHall,
				blockHutUniversity,
				blockHutHospital,
				blockHutSchool,
				blockHutEnchanter,
				blockHutGlassblower,
				blockHutDyer,
				blockHutFletcher,
				blockHutMechanic,
				blockHutPlantation,
				blockHutTavern,
				blockHutRabbitHutch,
				blockHutConcreteMixer,
				blockHutBeekeeper,
				blockHutFlorist,
				blockPostBox};
	}

}
