package com.minecolonies.coremod.util;

import net.fabricmc.fabric.api.biome.v1.*;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.enchantment.*;
import net.minecraft.item.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.surfacebuilder.*;

public class FabricWrapper
{
	/* -----------------------------------------------------------------------------------------------------------
	 *											biome block
	 * -----------------------------------------------------------------------------------------------------------
	 * -----------------------------------------------------------------------------------------------------------
	 *									this is use to build the biome
	 * -----------------------------------------------------------------------------------------------------------
	 */
	public static Biome buildHot(float depth,float scale,float rain,BiomeEffects.Builder builder,SpawnSettings.Builder spawn,
								 GenerationSettings.Builder gen)
	{
		return new Biome.Builder().precipitation(Biome.Precipitation.RAIN).category(Biome.Category.NONE).depth(depth)
				.scale(scale).temperature(1.0f).downfall(rain).effects(builder.build())
				.spawnSettings(spawn.build()).generationSettings(gen.build()).build();
	}

	public static Biome buildMild(float depth,float scale,float rain,BiomeEffects.Builder builder,SpawnSettings.Builder spawn,
								  GenerationSettings.Builder gen)
	{
		return new Biome.Builder().precipitation(Biome.Precipitation.RAIN).category(Biome.Category.NONE).depth(depth)
				.scale(scale).temperature(0.5f).downfall(rain).effects(builder.build())
				.spawnSettings(spawn.build()).generationSettings(gen.build()).build();
	}

	public static Biome buildCold(float depth,float scale,float rain,BiomeEffects.Builder builder,SpawnSettings.Builder spawn,
								  GenerationSettings.Builder gen)
	{
		return new Biome.Builder().precipitation(Biome.Precipitation.SNOW).category(Biome.Category.NONE).depth(depth)
				.scale(scale).temperature(0.1f).downfall(rain).effects(builder.build())
				.spawnSettings(spawn.build()).generationSettings(gen.build()).build();
	}
	/* -----------------------------------------------------------------------------------------------------------
	 *								this is use to build the Biome effects
	 * -----------------------------------------------------------------------------------------------------------
	 */
	public static BiomeEffects.Builder buildEffects(int sky_colour, int ground_colour, int water_colour)
	{
		return buildEffects(sky_colour,sky_colour,ground_colour,ground_colour,water_colour,water_colour);
	}

	public static BiomeEffects.Builder buildEffects(int sky_colour,int sky_colour_two,int ground_colour,
													int ground_colour_two, int water_colour,int water_colour_two)
	{
		return new BiomeEffects.Builder()	.waterColor(water_colour).waterFogColor(water_colour_two)
				.foliageColor(ground_colour).grassColor(ground_colour_two)
				.skyColor(sky_colour).fogColor(sky_colour_two);
	}
	/* -----------------------------------------------------------------------------------------------------------
	 *									this is use to register biomes
	 * -----------------------------------------------------------------------------------------------------------
	 */
	public static void regBiome(Biome biome, ConfiguredSurfaceBuilder<TernarySurfaceConfig> surface_builder,
								OverworldClimate climate, String name)
	{
		RegistryKey<Biome> registryKey = RegistryKey.of(Registry.BIOME_KEY, new ModIdentifier(name));
		Registry.register(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, new ModIdentifier(name), surface_builder);
		Registry.register(BuiltinRegistries.BIOME, registryKey.getValue(), biome);
		OverworldBiomes.addContinentalBiome(registryKey,climate, 2D);
	}
	/* -----------------------------------------------------------------------------------------------------------
	 *										biome block end
	 * -----------------------------------------------------------------------------------------------------------
	 * -----------------------------------------------------------------------------------------------------------
	 *											world block
	 * -----------------------------------------------------------------------------------------------------------
	 * -----------------------------------------------------------------------------------------------------------
	 *								  this is use to register ore gen
	 * -----------------------------------------------------------------------------------------------------------
	 */
	public static void regEndOre(ConfiguredFeature<?, ?> reg_ore, String name)
	{
		RegistryKey<ConfiguredFeature<?, ?>> ore = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN,new ModIdentifier(name));
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, ore.getValue(), reg_ore);
		BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES, ore);
	}

	public static void regNetherOre(ConfiguredFeature<?, ?> reg_ore, String name)
	{
		RegistryKey<ConfiguredFeature<?, ?>> ore = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN,new ModIdentifier(name));
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, ore.getValue(), reg_ore);
		BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(), GenerationStep.Feature.UNDERGROUND_ORES, ore);
	}

	public static void regOverworldOre(ConfiguredFeature<?, ?> reg_ore, String name)
	{
		RegistryKey<ConfiguredFeature<?, ?>> ore = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN,new ModIdentifier(name));
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, ore.getValue(), reg_ore);
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ore);
	}
	/* -----------------------------------------------------------------------------------------------------------
	 *										world block end
	 * -----------------------------------------------------------------------------------------------------------
	 * -----------------------------------------------------------------------------------------------------------
	 *								  this is use to register blocks
	 * -----------------------------------------------------------------------------------------------------------
	 */
	public static void regBlock(Block block, String name)
	{
		Registry.register(Registry.BLOCK,new ModIdentifier(name), block);
		Registry.register(Registry.ITEM,new ModIdentifier(name), new BlockItem(block, new MineColoniesItemSettings()));
	}

	public static Block regBlockWithRet(Block block, String name)
	{
		Registry.register(Registry.BLOCK,new ModIdentifier(name), block);
		Registry.register(Registry.ITEM,new ModIdentifier(name), new BlockItem(block, new MineColoniesItemSettings()));
		return block;
	}

	public static BlockEntityType<?> regBlockEntity(BlockEntityType<?> type, String name)
	{
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, new ModIdentifier(name), type);
	}
	/* -----------------------------------------------------------------------------------------------------------
	 *								  this is use to register item
	 * -----------------------------------------------------------------------------------------------------------
	 */
	public static void regItem(Item item, String name)
	{
		Registry.register(Registry.ITEM,new ModIdentifier(name),item);
	}

	public static Item regItemWithRet(Item item, String name)
	{
		Registry.register(Registry.ITEM,new ModIdentifier(name),item);
		return item;
	}

	public static void regItemAsBurnable(Item item,int cook_time)
	{
		FuelRegistry.INSTANCE.add(item,cook_time);
	}
	/* -----------------------------------------------------------------------------------------------------------
	 *								this is use to register enchantment
	 * -----------------------------------------------------------------------------------------------------------
	 */
	public static Enchantment regEnchantment(Enchantment enchantment,String name)
	{
		return Registry.register(Registry.ENCHANTMENT,new ModIdentifier(name),enchantment);
	}
}
