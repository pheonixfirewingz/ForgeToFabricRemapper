package com.minecraftabnormals.endergetic.core.registry;

import java.util.function.BiPredicate;

import com.google.common.collect.Sets;
import com.minecraftabnormals.abnormals_core.common.world.modification.*;
import com.minecraftabnormals.abnormals_core.core.util.BiomeUtil;
import com.minecraftabnormals.abnormals_core.core.util.registry.BiomeSubRegistryHelper;
import com.minecraftabnormals.endergetic.common.world.features.EEFeatures;
import com.minecraftabnormals.endergetic.common.world.surfacebuilders.EESurfaceBuilders;
import com.minecraftabnormals.endergetic.core.EndergeticExpansion;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EndergeticExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EEBiomes {
	private static final BiomeSubRegistryHelper HELPER = EndergeticExpansion.REGISTRY_HELPER.getBiomeSubHelper();

	public static final BiomeSubRegistryHelper.KeyedBiome POISE_FOREST = createPoiseForest();

	@SuppressWarnings("unchecked")
	public static void setupBiomeInfo() {
		BiomeDictionary.addTypes(POISE_FOREST.getKey(), BiomeDictionary.Type.END, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.FOREST);
		BiomeUtil.addEndBiome(POISE_FOREST.getKey(), 6);

		BiomeModificationManager modificationManager = BiomeModificationManager.INSTANCE;
		BiPredicate<RegistryKey<Biome>, Biome> poiseOnly = BiomeModificationPredicates.forBiomeKey(POISE_FOREST.getKey());
		modificationManager.addModifier(BiomeSpawnsModifier.createMultiSpawnAdder(poiseOnly, EEEntities.END_CREATURE,
				Sets.newHashSet(
						new BiomeSpawnsModifier.SpawnInfo(EEEntities.BOOFLO_ADOLESCENT, 5, 1, 2),
						new BiomeSpawnsModifier.SpawnInfo(EEEntities.BOOFLO, 15, 1, 3),
						new BiomeSpawnsModifier.SpawnInfo(EEEntities.PUFF_BUG, 10, 2, 4)
				)
		));
		modificationManager.addModifier(BiomeFeatureModifier.createMultiFeatureAdder(poiseOnly, GenerationStage.Decoration.SURFACE_STRUCTURES,
				Sets.newHashSet(
						() -> EEFeatures.Configured.POISE_DOME,
						() -> EEFeatures.Configured.POISE_TREE,
						() -> EEFeatures.Configured.END_GATEWAY
				)
		));
		modificationManager.addModifier(BiomeFeatureModifier.createMultiFeatureAdder(poiseOnly, GenerationStage.Decoration.VEGETAL_DECORATION,
				Sets.newHashSet(
						() -> EEFeatures.Configured.POISE_CLUSTER,
						() -> EEFeatures.Configured.PUFFBUG_HIVE,
						() -> EEFeatures.Configured.BOLLOOM_BUD,
						() -> EEFeatures.Configured.TALL_POISE_GRASS,
						() -> EEFeatures.Configured.POISE_GRASS
				)
		));
		modificationManager.addModifier(BiomeAmbienceModifier.createAmbienceReplacer(poiseOnly, () -> new BiomeAmbience.Builder()
				.withSkyColor(0)
				.setWaterColor(4159204)
				.setWaterFogColor(329011)
				.setFogColor(10518688)
				.setAmbientSound(EESounds.POISE_FOREST_LOOP.get())
				.setAdditionsSound(new SoundAdditionsAmbience(EESounds.POISE_FOREST_ADDITIONS.get(), 0.01D))
				.setMoodSound(new MoodSoundAmbience(EESounds.POISE_FOREST_MOOD.get(), 6000, 8, 2.0D))
				.build()));
	}

	public static BiomeSubRegistryHelper.KeyedBiome createPoiseForest() {
		Biome.Builder builder = new Biome.Builder();
		builder
				.withGenerationSettings(
						new BiomeGenerationSettings.Builder()
								.withSurfaceBuilder(() -> EESurfaceBuilders.Configs.POISE_FOREST)
								.build()
				)
				.withMobSpawnSettings(
						new MobSpawnInfo.Builder()
								.withCreatureSpawnProbability(0.9F)
								.copy()
				)
				.setEffects(
						new BiomeAmbience.Builder()
								.withSkyColor(0)
								.setWaterColor(4159204)
								.setWaterFogColor(329011)
								.setFogColor(10518688)
								.setMoodSound(MoodSoundAmbience.DEFAULT_CAVE)
								.build()
				)
				.precipitation(Biome.RainType.NONE)
				.category(Biome.Category.THEEND)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(0.5F)
				.downfall(0.5F);
		return HELPER.createBiome("poise_forest", builder::build);
	}
}