package com.minecolonies.coremod.blocks.huts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockHutEnchanter extends AbstractBlockHut<BlockHutEnchanter>
{
    @NotNull
    @Override
    public String getName()
    {
        return "blockhutenchanter";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.enchanter;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final BlockState state, final IBlockReader world)
    {
        final TileEntityEnchanter building = new TileEntityEnchanter();
        building.registryName = this.getBuildingEntry().getRegistryName();
        return building;
    }
}
