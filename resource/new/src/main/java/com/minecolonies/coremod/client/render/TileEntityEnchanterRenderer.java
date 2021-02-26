package com.minecolonies.coremod.client.render;

import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TileEntityEnchanterRenderer extends TileEntityRenderer<TileEntityColonyBuilding>
{
    public static final RenderMaterial TEXTURE_BOOK;
    static
    {
        TEXTURE_BOOK = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(Constants.MOD_ID, "blocks/enchanting_table_book"));
    }

    /**
     * The book model to be rendered.
     */
    private final BookModel modelBook = new BookModel();

    /**
     * Create the renderer.
     *
     * @param dispatcher the dispatcher.
     */
    public TileEntityEnchanterRenderer(final TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(
      @NotNull final TileEntityColonyBuilding ent,
      float partialTicks,
      @NotNull final MatrixStack matrixStack,
      @NotNull final IRenderTypeBuffer renderTypeBuffer,
      final int lightA,
      final int lightB)
    {
        if (ent instanceof TileEntityEnchanter)
        {
            final TileEntityEnchanter entity = (TileEntityEnchanter) ent;
            matrixStack.push();
            matrixStack.translate(0.5D, 0.75D, 0.5D);
            float tick = (float) entity.tickCount + partialTicks;
            matrixStack.translate(0.0D, (0.1F + MathHelper.sin(tick * 0.1F) * 0.01F), 0.0D);

            double rotVPrev = entity.bookRotation - entity.bookRotationPrev;
            float circleRot = (float) ((rotVPrev + Math.PI % (2 * Math.PI)) - Math.PI);

            float tickBasedRot = entity.bookRotationPrev + circleRot * partialTicks;
            matrixStack.rotate(Vector3f.YP.rotation(-tickBasedRot));
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(80.0F));
            float pageFlip = MathHelper.lerp(partialTicks, entity.pageFlipPrev, entity.pageFlip);
            float flipA = MathHelper.frac(pageFlip + 0.25F) * 1.6F - 0.3F;
            float flipB = MathHelper.frac(pageFlip + 0.75F) * 1.6F - 0.3F;
            float bookSpread = MathHelper.lerp(partialTicks, entity.bookSpreadPrev, entity.bookSpread);
            this.modelBook.setBookState(tick, MathHelper.clamp(flipA, 0.0F, 1.0F), MathHelper.clamp(flipB, 0.0F, 1.0F), bookSpread);
            IVertexBuilder vertexConsumer = TEXTURE_BOOK.getBuffer(renderTypeBuffer, RenderType::getEntitySolid);
            this.modelBook.render(matrixStack, vertexConsumer, lightA, lightB, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.pop();
        }
    }
}
