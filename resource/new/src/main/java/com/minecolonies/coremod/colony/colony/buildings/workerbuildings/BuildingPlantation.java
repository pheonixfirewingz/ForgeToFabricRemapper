package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import org.jetbrains.annotations.NotNull;

import java.util.*;


/**
 * Class of the plantation building. Worker will grow sugarcane/bamboo/cactus + craft paper and books.
 */
public class BuildingPlantation extends AbstractBuildingCrafter
{
    /**
     * Description string of the building.
     */
    private static final String PLANTATION = "plantation";

    /**
     * List of sand blocks to grow onto.
     */
    private final List<BlockPos> sand = new ArrayList<>();

    /**
     * The current phase (default sugarcane).
     */
    private Item currentPhase = Items.SUGAR_CANE;

    /**
     * The setting in the hut. If it's in the "pick 2 mode" this is the disabled mode else this is the only used one.
     */
    private Item setting = Items.SUGAR_CANE;

    /**
     * All the possible settings.
     */
    private final List<Item> settings = Arrays.asList(Items.SUGAR_CANE, Items.CACTUS, Items.BAMBOO);

    /**
     * Instantiates a new plantation building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingPlantation(final IColony c, final BlockPos l)
    {
        super(c, l);
        keepX.put(itemStack -> ItemStackUtils.hasToolLevel(itemStack, ToolType.AXE, TOOL_LEVEL_WOOD_OR_GOLD, getMaxToolLevel()), new Tuple<>(1, true));
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return PLANTATION;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    @Override
    public void registerBlockPosition(@NotNull final Block block, @NotNull final BlockPos pos, @NotNull final World world)
    {
        super.registerBlockPosition(block, pos, world);
        if (block == Blocks.SAND)
        {
            final Block down = world.getBlockState(pos.down()).getBlock();
            if (down == Blocks.COBBLESTONE || down == Blocks.STONE_BRICKS)
            {
                sand.add(pos);
            }
        }
    }

    @Override
    public void deserializeNBT(final CompoundNBT compound)
    {
        super.deserializeNBT(compound);
        final ListNBT sandPos = compound.getList(TAG_PLANTGROUND, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < sandPos.size(); ++i)
        {
            sand.add(NBTUtil.readBlockPos(sandPos.getCompound(i).getCompound(TAG_POS)));
        }
        this.currentPhase = ItemStack.read(compound.getCompound(TAG_CURRENT_PHASE)).getItem();
        if (compound.keySet().contains(TAG_SETTING))
        {
            this.setting = ItemStack.read(compound.getCompound(TAG_SETTING)).getItem();
        }
        else
        {
            this.setting = this.currentPhase;
        }
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        final CompoundNBT compound = super.serializeNBT();
        @NotNull final ListNBT sandCompoundList = new ListNBT();
        for (@NotNull final BlockPos entry : sand)
        {
            @NotNull final CompoundNBT sandCompound = new CompoundNBT();
            sandCompound.put(TAG_POS, NBTUtil.writeBlockPos(entry));
            sandCompoundList.add(sandCompound);
        }
        compound.put(TAG_PLANTGROUND, sandCompoundList);
        compound.put(TAG_CURRENT_PHASE, new ItemStack(currentPhase).write(new CompoundNBT()));
        compound.put(TAG_SETTING, new ItemStack(setting).write(new CompoundNBT()));
        return compound;
    }

    /**
     * Get a list of positions to check for crops for the current phase.
     *
     * @param world the world.
     * @return the list of positions.
     */
    public List<BlockPos> getPosForPhase(final World world)
    {
        final List<BlockPos> filtered = new ArrayList<>();
        if (tileEntity != null && !tileEntity.getPositionedTags().isEmpty())
        {
            for (final Map.Entry<BlockPos, List<String>> entry : tileEntity.getPositionedTags().entrySet())
            {
                if ((entry.getValue().contains("bamboo") && currentPhase == Items.BAMBOO)
                      || (entry.getValue().contains("sugar") && currentPhase == Items.SUGAR_CANE)
                      || (entry.getValue().contains("cactus") && currentPhase == Items.CACTUS))
                {
                    filtered.add(getPosition().add(entry.getKey()));
                }
            }
        }

        //todo remove as soon as all schematics got tag support
        if (filtered.isEmpty())
        {
            for (final BlockPos pos : sand)
            {
                if (currentPhase == Items.SUGAR_CANE)
                {
                    if (world.getBlockState(pos.down()).getBlock() == Blocks.COBBLESTONE
                          && (world.getBlockState(pos.north()).getBlock() == Blocks.WATER
                                || world.getBlockState(pos.south()).getBlock() == Blocks.WATER
                                || world.getBlockState(pos.east()).getBlock() == Blocks.WATER
                                || world.getBlockState(pos.west()).getBlock() == Blocks.WATER))
                    {
                        filtered.add(pos);
                    }
                }
                else if (currentPhase == Items.CACTUS)
                {
                    if (world.getBlockState(pos.down()).getBlock() == Blocks.COBBLESTONE
                          && world.getBlockState(pos.north()).getBlock() != Blocks.WATER
                          && world.getBlockState(pos.south()).getBlock() != Blocks.WATER
                          && world.getBlockState(pos.east()).getBlock() != Blocks.WATER
                          && world.getBlockState(pos.west()).getBlock() != Blocks.WATER)
                    {
                        filtered.add(pos);
                    }
                }
                else
                {
                    //Bamboo
                    if (world.getBlockState(pos.down()).getBlock() == Blocks.STONE_BRICKS)
                    {
                        filtered.add(pos);
                    }
                }
            }
        }
        return filtered;
    }

    @NotNull
    @Override
    public IJob<?> createJob(final ICitizenData citizen)
    {
        return new JobPlanter(citizen);
    }

    @NotNull
    @Override
    public String getJobName()
    {
        return PLANTATION;
    }

    @NotNull
    @Override
    public Skill getPrimarySkill()
    {
        return Skill.Agility;
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

        Optional<Boolean> isRecipeAllowed;

        if (!super.canRecipeBeAdded(token))
        {
            return false;
        }

        isRecipeAllowed = super.canRecipeBeAddedBasedOnTags(token);
        if (isRecipeAllowed.isPresent())
        {
            return isRecipeAllowed.get();
        }
        else
        {
            // Additional recipe rules

            final IRecipeStorage storage = IColonyManager.getInstance().getRecipeManager().getRecipes().get(token);

            // End Additional recipe rules
        }

        return false;
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.plantation;
    }

    @Override
    public void requestUpgrade(final PlayerEntity player, final BlockPos builder)
    {
        final UnlockBuildingResearchEffect effect = colony.getResearchManager().getResearchEffects().getEffect(ResearchInitializer.PLANTATION_RESEARCH, UnlockBuildingResearchEffect.class);
        if (effect == null)
        {
            player.sendMessage(new TranslationTextComponent("com.minecolonies.coremod.research.havetounlock"), player.getUniqueID());
            return;
        }
        super.requestUpgrade(player, builder);
    }

    /**
     * Set the current phase.
     *
     * @param phase the phase to set.
     */
    public void setSetting(final Item phase)
    {
        this.setting = phase;
    }

    @Override
    public void serializeToView(@NotNull final PacketBuffer buf)
    {
        super.serializeToView(buf);
        buf.writeItemStack(new ItemStack(setting));
    }

    /**
     * Get the current phase.
     *
     * @return the current phase item.
     */
    public Item getCurrentPhase()
    {
        return currentPhase;
    }

    /**
     * Iterates over available plants
     * @return the item of the new or unchanged plant phase
     */
    public Item nextPlantPhase()
    {
        final UnlockAbilityResearchEffect researchEffect = getColony().getResearchManager().getResearchEffects().getEffect(PLANT_2, UnlockAbilityResearchEffect.class);
        if (researchEffect != null && researchEffect.getEffect())
        {
            int next = settings.indexOf(currentPhase);

            do
            {
                next = (next + 1) % settings.size();
            }
            while (settings.get(next) == setting);

            currentPhase = settings.get(next);
        }

        return currentPhase;
    }

    /**
     * Plantation View.
     */
    public static class View extends AbstractBuildingCrafter.View
    {
        /**
         * All possible phases.
         */
        private final List<Item> phases = Arrays.asList(Items.SUGAR_CANE, Items.CACTUS, Items.BAMBOO);

        /**
         * The current phase.
         */
        private Item currentPhase;

        /**
         * Instantiate the plantation view.
         *
         * @param c the colonyview to put it in
         * @param l the positon
         */
        public View(final IColonyView c, final BlockPos l)
        {
            super(c, l);
        }

        /**
         * Get the list of all phases.
         *
         * @return the list.
         */
        public List<Item> getPhases()
        {
            return phases;
        }

        @Override
        public void deserialize(@NotNull final PacketBuffer buf)
        {
            super.deserialize(buf);
            this.currentPhase = buf.readItemStack().getItem();
        }

        @NotNull
        @Override
        public Window getWindow()
        {
            return new WindowHutPlantation(this);
        }

        /**
         * Get the current phase.
         *
         * @return the phase.
         */
        public Item getCurrentPhase()
        {
            return currentPhase;
        }

        /**
         * Set a new phase.
         *
         * @param phase the phase to set.
         */
        public void setPhase(final Item phase)
        {
            this.currentPhase = phase;
            Network.getNetwork().sendToServer(new PlantationSetPhaseMessage(this, phase));
        }
    }
}