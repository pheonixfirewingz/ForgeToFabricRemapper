package com.minecraftabnormals.endergetic.core.mixin.chorus;

import com.minecraftabnormals.endergetic.core.registry.other.EETags;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ChorusPlantFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ChorusPlantFeature.class)
public final class ChorusPlantFeatureMixin {

	@Inject(at = @At("HEAD"), method = "generate", cancellable = true)
	private void allowGenerationOnTag(ISeedReader seedReader, ChunkGenerator p_230362_3_, Random p_230362_4_, BlockPos pos, NoFeatureConfig p_230362_6_, CallbackInfoReturnable<Boolean> info) {
		if (seedReader.isAirBlock(pos) && seedReader.getBlockState(pos.down()).isIn(EETags.Blocks.CHORUS_PLANTABLE)) {
			ChorusFlowerBlock.generatePlant(seedReader, pos, p_230362_4_, 8);
			info.setReturnValue(true);
		}
	}

}
