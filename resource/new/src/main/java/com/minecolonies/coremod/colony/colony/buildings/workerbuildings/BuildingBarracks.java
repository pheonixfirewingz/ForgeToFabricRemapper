package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Building class for the Barracks.
 */
public class BuildingBarracks extends AbstractBuilding
{
    /**
     * Name of our building's Schematics.
     */
    private static final String SCHEMATIC_NAME = "barracks";

    /**
     * Max hut level of the Barracks.
     */
    private static final int BARRACKS_HUT_MAX_LEVEL = 5;

    /**
     * The tag to store the tower list to NBT.
     */
    private static final String TAG_TOWERS = "towers";

    /**
     * The list of barracksTowers.
     */
    private final List<BlockPos> towers = new ArrayList<>();

    /**
     * The goldcost for spies
     */
    public static int SPIES_GOLD_COST = 5;

    /**
     * Constructor for a AbstractBuilding.
     *
     * @param colony Colony the building belongs to.
     * @param pos    Location of the building (it's Hut Block).
     */
    public BuildingBarracks(@NotNull final IColony colony, final BlockPos pos)
    {
        super(colony, pos);
        keepX.put((stack) -> stack.getItem() == Items.GOLD_INGOT, new Tuple<>(STACKSIZE, true));
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return SCHEMATIC_NAME;
    }

    @SuppressWarnings("squid:S109")
    @Override
    public int getMaxBuildingLevel()
    {
        return BARRACKS_HUT_MAX_LEVEL;
    }

    @Override
    public void onDestroyed()
    {
        final World world = getColony().getWorld();

        if (world != null)
        {
            for (final BlockPos tower : towers)
            {
                world.setBlockState(tower, Blocks.AIR.getDefaultState());
            }
        }
        super.onDestroyed();
        colony.getBuildingManager().guardBuildingChangedAt(this, 0);
    }

    @Override
    public void requestUpgrade(final PlayerEntity player, final BlockPos builder)
    {
        final UnlockBuildingResearchEffect effect = colony.getResearchManager().getResearchEffects().getEffect(ResearchInitializer.BARRACKS_RESEARCH, UnlockBuildingResearchEffect.class);
        if (effect == null)
        {
            player.sendMessage(new TranslationTextComponent("com.minecolonies.coremod.research.havetounlock"), player.getUniqueID());
            return;
        }
        super.requestUpgrade(player, builder);
    }

    @Override
    public void onUpgradeComplete(final int newLevel)
    {
        super.onUpgradeComplete(newLevel);
        colony.getBuildingManager().guardBuildingChangedAt(this, newLevel);
    }

    @Override
    public void registerBlockPosition(@NotNull final BlockState block, @NotNull final BlockPos pos, @NotNull final World world)
    {
        super.registerBlockPosition(block, pos, world);
        if (block.getBlock() == ModBlocks.blockBarracksTowerSubstitution || block.getBlock() == ModBlocks.blockHutBarracksTower)
        {
            if (world.getBlockState(pos).getBlock() != ModBlocks.blockHutBarracksTower)
            {
                world.setBlockState(pos, ModBlocks.blockHutBarracksTower.getDefaultState().with(BlockBarracksTowerSubstitution.FACING, block.get(AbstractBlockHut.FACING)));
                final TileEntity tile = world.getTileEntity(pos);
                if (tile instanceof TileEntityColonyBuilding)
                {
                    ((TileEntityColonyBuilding) tile).setMirror(this.isMirrored());
                    ((TileEntityColonyBuilding) tile).setStyle(this.getStyle());
                }
                getColony().getBuildingManager().addNewBuilding((TileEntityColonyBuilding) world.getTileEntity(pos), world);
            }
            final IBuilding building = getColony().getBuildingManager().getBuilding(pos);
            if (building instanceof BuildingBarracksTower)
            {
                building.setStyle(this.getStyle());
                ((BuildingBarracksTower) building).addBarracks(getPosition());
                if (!towers.contains(pos))
                {
                    towers.add(pos);
                }
            }
        }
    }

    @Override
    public void onColonyTick(@NotNull final IColony colony)
    {
        if (colony.getWorld().isRemote)
        {
            return;
        }

        if (colony.getRaiderManager().isRaided())
        {
            if (!colony.getRaiderManager().areSpiesEnabled())
            {
                final int amount = InventoryUtils.getItemCountInItemHandler(this.getTileEntity().getInventory(), Items.GOLD_INGOT);
                if (amount >= SPIES_GOLD_COST)
                {
                    InventoryUtils.removeStackFromItemHandler(tileEntity.getInventory(), new ItemStack(Items.GOLD_INGOT, SPIES_GOLD_COST), SPIES_GOLD_COST);
                    colony.getRaiderManager().setSpiesEnabled(true);
                    colony.markDirty();
                }
            }
        }
        else
        {
            colony.getRaiderManager().setSpiesEnabled(false);
        }
    }

    @Override
    public int getClaimRadius(final int newLevel)
    {
        if (newLevel <= 0)
        {
            return 0;
        }

        // tower levels must all be 4+ to get increased radius of 3 
        int barracksClaimRadius = 3;
        for (final BlockPos pos : towers)
        {
            final IBuilding building = colony.getBuildingManager().getBuilding(pos);
            if (building != null)
            {
                if (building.getBuildingLevel() < 4) 
                { 
                    barracksClaimRadius = 2;
                    break;
                }
            }
        }
        return barracksClaimRadius;
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.barracks;
    }

    @Override
    public void deserializeNBT(final CompoundNBT compound)
    {
        super.deserializeNBT(compound);
        towers.clear();
        towers.addAll(NBTUtils.streamCompound(compound.getList(TAG_TOWERS, Constants.NBT.TAG_COMPOUND))
                        .map(resultCompound -> BlockPosUtil.read(resultCompound, TAG_POS))
                        .collect(Collectors.toList()));
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        final CompoundNBT compound = super.serializeNBT();
        final ListNBT towerTagList = towers.stream().map(pos -> BlockPosUtil.write(new CompoundNBT(), TAG_POS, pos)).collect(NBTUtils.toListNBT());
        compound.put(TAG_TOWERS, towerTagList);

        return compound;
    }

    public List<BlockPos> getTowers()
    {
        return towers;
    }

    /**
     * Barracks building View.
     */
    public static class View extends AbstractBuildingView
    {
        /**
         * Instantiate the barracks view.
         *
         * @param c the colonyview to put it in
         * @param l the positon
         */
        public View(final IColonyView c, final BlockPos l)
        {
            super(c, l);
        }

        @NotNull
        @Override
        public Window getWindow()
        {
            return new WindowBarracksBuilding(this);
        }
    }
}
