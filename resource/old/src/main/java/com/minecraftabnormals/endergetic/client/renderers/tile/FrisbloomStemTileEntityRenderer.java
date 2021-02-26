package com.minecraftabnormals.endergetic.client.renderers.tile;

import java.util.Random;

import com.minecraftabnormals.endergetic.client.models.frisbloom.FrisbloomFlowerModel;
import com.minecraftabnormals.endergetic.client.models.frisbloom.FrisbloomStemModel;
import com.minecraftabnormals.endergetic.common.tileentities.FrisbloomStemTileEntity;
import com.minecraftabnormals.endergetic.core.EndergeticExpansion;
import com.minecraftabnormals.endergetic.core.registry.EEBlocks;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("unused")
public class FrisbloomStemTileEntityRenderer extends TileEntityRenderer<FrisbloomStemTileEntity> {
	private FrisbloomStemModel model;

	private FrisbloomFlowerModel modelFlower;
	private static final ResourceLocation TEXTURE = new ResourceLocation(EndergeticExpansion.MOD_ID + ":textures/tile/frisbloom_stem.png");
	private static final ResourceLocation TEXTURE_FLOWER = new ResourceLocation(EndergeticExpansion.MOD_ID + ":textures/tile/frisbloom_flower.png");

	public FrisbloomStemTileEntityRenderer(TileEntityRendererDispatcher renderDispatcher) {
		super(renderDispatcher);
		this.model = new FrisbloomStemModel();
		this.modelFlower = new FrisbloomFlowerModel();
	}

	@Override
	public void render(FrisbloomStemTileEntity te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		Random rnd = new Random(((this.getLongForPos(te.getPos()) / 12)) ^ 5);
		int rndRot = rnd.nextInt(12) - 6;
		BlockState state = te.hasWorld() ? te.getBlockState() : (BlockState) EEBlocks.FRISBLOOM_STEM.getDefaultState();

//		GlStateManager.pushMatrix();
//		
//		if(state.get(BlockFrisbloomStem.LAYER) == 4) {
//			this.bindTexture(TEXTURE_FLOWER);
//		} else {
//			this.bindTexture(TEXTURE);
//		}
//		
//		GlStateManager.translatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
//		GlStateManager.enableRescaleNormal();
//		GlStateManager.scalef(1.0F, -1.0F, -1.0F);
//		if(state.get(BlockFrisbloomStem.LAYER) != 4) { model.renderStem(); }
//		GlStateManager.disableRescaleNormal();
//		GlStateManager.popMatrix();
//		
//		if(state.get(BlockFrisbloomStem.LAYER) == 0 || state.get(BlockFrisbloomStem.LAYER) == 1) {
//			GlStateManager.pushMatrix();
//			GlStateManager.translatef((float) x + 0.5F, (float) y - 0.5F, (float) z + 0.5F);
//			GlStateManager.rotatef(rndRot, 1, 0, 0);
//			model.renderFrisbloom(0, false);
//			GlStateManager.disableLighting();
//			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
//			GlStateManager.translatef(0, (float) -0.015F, 0);
//			model.renderFrisbloom(0, true);
//			GlStateManager.popMatrix();
//		} else if(state.get(BlockFrisbloomStem.LAYER) == 2) {
//			GlStateManager.pushMatrix();
//			GlStateManager.translatef((float) x + 0.5F, (float) y - 0.5F, (float) z + 0.5F);
//			GlStateManager.rotatef(rndRot, 1, 0, 0);
//			model.renderFrisbloom(2, false);
//			GlStateManager.disableLighting();
//			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
//			GlStateManager.translatef(0, (float) -0.015F, 0);
//			model.renderFrisbloom(2, true);
//			GlStateManager.popMatrix();
//		} else if(state.get(BlockFrisbloomStem.LAYER) == 3) {
//			GlStateManager.pushMatrix();
//			GlStateManager.translatef((float) x + 0.5F, (float) y - 0.5F, (float) z + 0.5F);
//			model.renderFrisbloom(3, false);
//			GlStateManager.disableLighting();
//			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
//			GlStateManager.translatef(0, (float) -0.015F, 0);
//			model.renderFrisbloom(3, true);
//			GlStateManager.popMatrix();
//		} else {
//			GlStateManager.pushMatrix();
//			GlStateManager.translatef((float) x + 0.5F, (float) y - 1, (float) z + 0.5F);
//			modelFlower.renderAll(false);
//			GlStateManager.disableLighting();
//			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
//			GlStateManager.translatef(0, 0.81F, 0);
//			modelFlower.renderAll(true);
//			GlStateManager.popMatrix();
//		}
	}

	public long getLongForPos(BlockPos pos) {
		return (pos.getX() + pos.getY() + pos.getZ() * pos.toLong());
	}
}