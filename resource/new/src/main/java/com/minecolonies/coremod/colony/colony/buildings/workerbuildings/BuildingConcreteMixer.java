package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


/**
 * Class of the concrete mason building.
 */
public class BuildingConcreteMixer extends AbstractBuildingCrafter
{
    /**
     * Description string of the building.
     */
    private static final String CONCRETE_MIXER = "concretemixer";

    /**
     * How deep the water can max be to place concrete in it.
     */
    private static final int WATER_DEPTH_SUPPORT = 5;

    /**
     * Water position list.
     */
    private final Map<Integer, List<BlockPos>> waterPos = new HashMap<>();

    /**
     * The minimum found water level
     */
    private int minWaterLevel = WATER_DEPTH_SUPPORT;

    /**
     * Instantiates a new concrete mason building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingConcreteMixer(final IColony c, final BlockPos l)
    {
        super(c, l);
        keepX.put(itemStack -> ItemStackUtils.hasToolLevel(itemStack, ToolType.PICKAXE, TOOL_LEVEL_WOOD_OR_GOLD, getMaxToolLevel()), new Tuple<>(1, true));
    }

    @Override
    public void checkForWorkerSpecificRecipes()
    {
        super.checkForWorkerSpecificRecipes();

        final List<ItemStack> input = new ArrayList<>();
        input.add(new ItemStack(Items.SAND, 4));
        input.add(new ItemStack(Items.GRAVEL, 4));

        for (final Item item : ModTags.concretePowder.getAllElements())
        {
            final List<ItemStack> customInput = new ArrayList<>(input);

            final Item dye = DyeItem.getItem(DyeColor.byTranslationKey(item.getRegistryName().getPath().replace("_concrete_powder", ""), DyeColor.WHITE));
            customInput.add(new ItemStack(dye, 1));

            final IRecipeStorage storage = StandardFactoryController.getInstance().getNewInstance(
              TypeConstants.RECIPE,
              StandardFactoryController.getInstance().getNewInstance(TypeConstants.ITOKEN),
              customInput,
              3,
              new ItemStack(item, 8),
              Blocks.AIR);

            addRecipeToList(IColonyManager.getInstance().getRecipeManager().checkOrAddRecipe(storage));

            final Block block = item instanceof BlockItem ? ((BlockItem) item).getBlock() : null;
            if (block instanceof ConcretePowderBlock)
            {
                final IRecipeStorage storage2 = StandardFactoryController.getInstance().getNewInstance(
                  TypeConstants.RECIPE,
                  StandardFactoryController.getInstance().getNewInstance(TypeConstants.ITOKEN),
                  Collections.singletonList(new ItemStack(item, 1)),
                  3,
                  new ItemStack(((ConcretePowderBlock) block).solidifiedState.getBlock(), 1),
                  Blocks.AIR);

                addRecipeToList(IColonyManager.getInstance().getRecipeManager().checkOrAddRecipe(storage2));
            }
        }
    }

    @Override
    public void registerBlockPosition(@NotNull final BlockState blockState, @NotNull final BlockPos pos, @NotNull final World world)
    {
        if (!blockState.getFluidState().isEmpty())
        {
            if (blockState.getFluidState().getFluid() == Fluids.FLOWING_WATER && blockState.getFluidState().getLevel() <= WATER_DEPTH_SUPPORT)
            {
                final List<BlockPos> fluidPos = waterPos.getOrDefault(blockState.getFluidState().getLevel(), new ArrayList<>());
                if (!fluidPos.contains(pos))
                {
                    fluidPos.add(pos);
                }
                waterPos.put(blockState.getFluidState().getLevel(), fluidPos);
                minWaterLevel = Math.min(minWaterLevel, blockState.getFluidState().getLevel());
            }
        }

        super.registerBlockPosition(blockState, pos, world);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        final CompoundNBT compound = super.serializeNBT();

        @NotNull final ListNBT waterMap = new ListNBT();
        for (@NotNull final Map.Entry<Integer, List<BlockPos>> entry : waterPos.entrySet())
        {
            final CompoundNBT waterCompound = new CompoundNBT();

            waterCompound.putInt(TAG_LEVEL, entry.getKey());

            @NotNull final ListNBT waterList = new ListNBT();
            for (@NotNull final BlockPos pos : entry.getValue())
            {
                waterList.add(NBTUtil.writeBlockPos(pos));
            }
            waterCompound.put(TAG_WATER, waterList);
            waterMap.add(waterCompound);
        }
        compound.put(TAG_WATER, waterMap);
        return compound;
    }

    @Override
    public void deserializeNBT(final CompoundNBT compound)
    {
        super.deserializeNBT(compound);

        waterPos.clear();
        final ListNBT waterMapList = compound.getList(TAG_WATER, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < waterMapList.size(); ++i)
        {
            final CompoundNBT waterCompound = waterMapList.getCompound(i);
            final int level = waterCompound.getInt(TAG_LEVEL);
            minWaterLevel = Math.min(minWaterLevel, level);

            final ListNBT waterTagList = waterCompound.getList(TAG_WATER, Constants.NBT.TAG_COMPOUND);
            final List<BlockPos> water = new ArrayList<>();
            for (int j = 0; j < waterTagList.size(); ++j)
            {
                final CompoundNBT waterSubCompound = waterTagList.getCompound(j);

                final BlockPos waterPos = NBTUtil.readBlockPos(waterSubCompound);
                if (!water.contains(waterPos))
                {
                    water.add(waterPos);
                }
            }
            waterPos.put(level, water);
        }
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return CONCRETE_MIXER;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    @NotNull
    @Override
    public IJob<?> createJob(final ICitizenData citizen)
    {
        return new JobConcreteMixer(citizen);
    }

    @NotNull
    @Override
    public String getJobName()
    {
        return CONCRETE_MIXER;
    }

    @NotNull
    @Override
    public Skill getPrimarySkill()
    {
        return Skill.Stamina;
    }

    @NotNull
    @Override
    public Skill getSecondarySkill()
    {
        return Skill.Dexterity;
    }

    @Override
    public boolean canRecipeBeAdded(final IToken<?> token)
    {
        return false;
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.concreteMixer;
    }

    @Override
    public void requestUpgrade(final PlayerEntity player, final BlockPos builder)
    {
        final UnlockBuildingResearchEffect effect = colony.getResearchManager().getResearchEffects().getEffect(ResearchInitializer.CONCRETE_MIXER_RESEARCH, UnlockBuildingResearchEffect.class);
        if (effect == null)
        {
            player.sendMessage(new TranslationTextComponent("com.minecolonies.coremod.research.havetounlock"), player.getUniqueID());
            return;
        }
        super.requestUpgrade(player, builder);
    }

    /**
     * Check if there are open positions to mine.
     *
     * @return the open position if so.
     */
    @Nullable
    public BlockPos getBlockToMine()
    {
        for (int i = 1; i <= minWaterLevel; i++)
        {
            for (final BlockPos pos : waterPos.getOrDefault(i, Collections.emptyList()))
            {
                if (ModTags.concreteBlock.contains(colony.getWorld().getBlockState(pos).getBlock()))
                {
                    return pos;
                }
            }
        }
        
        return null;
    }

    /**
     * Check if there are open positions to place.
     *
     * @return the open position if so.
     */
    @Nullable
    public BlockPos getBlockToPlace()
    {
        for (int i = 1; i <= minWaterLevel; i++)
        {
            for (final BlockPos pos : waterPos.getOrDefault(i, Collections.emptyList()))
            {
                if (!colony.getWorld().getBlockState(pos).getFluidState().isEmpty() && !colony.getWorld().getBlockState(pos).getMaterial().isSolid())
                {
                    return pos;
                }
            }
        }
 
        return null;
    }

    /**
     * Get how much of an itemStack we already placed in the world.
     *
     * @param primaryOutput the block to check for.
     * @return the total count.
     */
    public int outputBlockCountInWorld(final ItemStack primaryOutput)
    {
        int count = 0;
        if (primaryOutput.getItem() instanceof BlockItem)
        {
            for (int i = 1; i <= minWaterLevel; i++)
            {
                for (final BlockPos pos : waterPos.getOrDefault(i, Collections.emptyList()))
                {
                    if (((BlockItem) primaryOutput.getItem()).getBlock() == colony.getWorld().getBlockState(pos).getBlock())
                    {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    /**
     * Concrete Mason View.
     */
    public static class View extends AbstractBuildingCrafter.View
    {

        /**
         * Instantiate the Concrete Mason view.
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
            return new WindowHutCrafter(this, CONCRETE_MIXER);
        }
    }
}
