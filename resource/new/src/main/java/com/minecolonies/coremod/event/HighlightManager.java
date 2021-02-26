package com.minecolonies.coremod.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
{
    /**
     * A position to highlight with a unique id.
     */
    @Nullable
    public static final Map<String, Tuple<BlockPos, Long>> HIGHLIGHT_MAP = new HashMap<>();

    /**
     * Render buffers.
     */
    public static final  RenderTypeBuffers        renderBuffers            = new RenderTypeBuffers();
    private static final IRenderTypeBuffer.Impl   renderBuffer             = renderBuffers.getBufferSource();
    private static final Supplier<IVertexBuilder> linesWithoutCullAndDepth = () -> renderBuffer.getBuffer(RenderUtils.LINES_GLINT);

    /**
     * Used to catch the renderWorldLastEvent in order to draw the debug nodes for pathfinding.
     *
     * @param event the catched event.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void renderWorldLastEvent(@NotNull final RenderWorldLastEvent event)
    {
        if (!HIGHLIGHT_MAP.isEmpty())
        {
            final long worldTime = Minecraft.getInstance().world.getGameTime();
            for (final Map.Entry<String, Tuple<BlockPos, Long>> entry : new ArrayList<>(HIGHLIGHT_MAP.entrySet()))
            {
                if (entry.getValue().getB() <= worldTime)
                {
                    HIGHLIGHT_MAP.remove(entry.getKey());
                }
                else
                {
                    RenderUtils.renderBox(entry.getValue().getA(), entry.getValue().getA(), 0, 1, 0, 1.0F, 0.002D, event.getMatrixStack(), linesWithoutCullAndDepth.get());
                }
            }
        }
        renderBuffer.finish();
    }
}