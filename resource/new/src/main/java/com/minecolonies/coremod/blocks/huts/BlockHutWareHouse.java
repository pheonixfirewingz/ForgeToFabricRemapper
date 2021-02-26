package com.minecolonies.coremod.blocks.huts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Hut for the warehouse. No different from {@link AbstractBlockHut}
 */

public class BlockHutWareHouse extends AbstractBlockHut<BlockHutWareHouse>
{
    public BlockHutWareHouse()
    {
        //No different from Abstract parent
        super();
    }

    @NotNull
    @Override
    public String getName()
    {
        return "blockhutwarehouse";
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final BlockState state, final IBlockReader world)
    {
        final TileEntityWareHouse building = (TileEntityWareHouse) MinecoloniesTileEntities.WAREHOUSE.create();
        building.registryName = this.getBuildingEntry().getRegistryName();
        return building;
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.wareHouse;
    }
}
