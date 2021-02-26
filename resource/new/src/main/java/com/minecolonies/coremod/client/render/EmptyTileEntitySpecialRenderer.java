package com.minecolonies.coremod.client.render;

import org.jetbrains.annotations.NotNull;

/**
 * Renderer for a normal tile entity (Nothing special with rendering).
 */
@OnlyIn(Dist.CLIENT)
public class EmptyTileEntitySpecialRenderer extends TileEntityRenderer<AbstractTileEntityColonyBuilding>
{

    public EmptyTileEntitySpecialRenderer(final TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(
      @NotNull final AbstractTileEntityColonyBuilding tileEntity,
      final float v,
      @NotNull final MatrixStack matrixStack,
      @NotNull final IRenderTypeBuffer iRenderTypeBuffer,
      final int i,
      final int i1)
    {

    }
}
