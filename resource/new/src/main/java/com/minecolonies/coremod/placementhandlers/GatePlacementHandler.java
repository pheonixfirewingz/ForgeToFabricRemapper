package com.minecolonies.coremod.placementhandlers;

import org.jetbrains.annotations.NotNull;

/**
 * Placement handler for special gate blocks
 */
public class GatePlacementHandler extends GeneralBlockPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState)
    {
        return blockState.getBlock() instanceof AbstractBlockGate;
    }
}
