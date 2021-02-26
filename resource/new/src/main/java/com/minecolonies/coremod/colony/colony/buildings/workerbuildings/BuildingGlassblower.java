package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Class of the glassblower building.
 */
public class BuildingGlassblower extends AbstractBuildingSmelterCrafter
{
    /**
     * Description string of the building.
     */
    private static final String GLASS_BLOWER = "glassblower";

    /**
     * Instantiates a new stone smeltery building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingGlassblower(final IColony c, final BlockPos l)
    {
        super(c, l);
        keepX.put(stack -> isAllowedFuel(stack), new Tuple<>(STACKSIZE, true));
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return GLASS_BLOWER;
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
        return new JobGlassblower(citizen);
    }

    @NotNull
    @Override
    public String getJobName()
    {
        return GLASS_BLOWER;
    }

    @NotNull
    @Override
    public Skill getPrimarySkill()
    {
        return Skill.Creativity;
    }

    @NotNull
    @Override
    public Skill getSecondarySkill()
    {
        return Skill.Focus;
    }

    @Override
    public boolean canRecipeBeAdded(final IToken<?> token)
    {
        Optional<Boolean> isRecipeAllowed;

        if (!super.canRecipeBeAdded(token))
        {
            return false;
        }

        checkForWorkerSpecificRecipes();

        isRecipeAllowed = super.canRecipeBeAddedBasedOnTags(token);
        if (isRecipeAllowed.isPresent())
        {
            return isRecipeAllowed.get();
        }

        return false;
    }

    @Override
    public void onColonyTick(@NotNull final IColony colony)
    {
        super.onColonyTick(colony);
    }

    @Override
    public void checkForWorkerSpecificRecipes()
    {
        super.checkForWorkerSpecificRecipes();

        final List<IToken<?>> tokens = new ArrayList<>();
        for (final Item item : Tags.Items.SAND.getAllElements())
        {
            final ItemStack stack = new ItemStack(item);
            final ItemStack output = FurnaceRecipes.getInstance().getSmeltingResult(stack);
            if (Tags.Items.GLASS.contains(output.getItem()))
            {
                final List<ItemStack> list = new ArrayList<>();
                list.add(stack);

                final IRecipeStorage storage = StandardFactoryController.getInstance().getNewInstance(
                  TypeConstants.RECIPE,
                  StandardFactoryController.getInstance().getNewInstance(TypeConstants.ITOKEN),
                  list,
                  1,
                  output,
                  Blocks.FURNACE);
                final IToken<?> token = IColonyManager.getInstance().getRecipeManager().checkOrAddRecipe(storage);
                if (recipes.contains(token))
                {
                    return;
                }
                tokens.add(token);
            }
        }
        recipes.addAll(tokens);
    }

    @Override
    public boolean canCraftComplexRecipes()
    {
        return true;
    }

    @Override
    public void openCraftingContainer(final ServerPlayerEntity player)
    {
        NetworkHooks.openGui(player, new INamedContainerProvider()
        {
            @Override
            public ITextComponent getDisplayName()
            {
                return new StringTextComponent("Crafting GUI");
            }

            @NotNull
            @Override
            public Container createMenu(final int id, @NotNull final PlayerInventory inv, @NotNull final PlayerEntity player)
            {
                final PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                buffer.writeBoolean(canCraftComplexRecipes());
                buffer.writeBlockPos(getID());
                return new ContainerCrafting(id, inv, buffer);
            }
        }, buffer -> new PacketBuffer(buffer.writeBoolean(canCraftComplexRecipes())).writeBlockPos(getID()));
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.glassblower;
    }

    @Override
    public void requestUpgrade(final PlayerEntity player, final BlockPos builder)
    {
        super.requestUpgrade(player, builder);
        final UnlockBuildingResearchEffect effect = colony.getResearchManager().getResearchEffects().getEffect(ResearchInitializer.GLASSBLOWER_RESEARCH, UnlockBuildingResearchEffect.class);
        if (effect == null)
        {
            player.sendMessage(new TranslationTextComponent("com.minecolonies.coremod.research.havetounlock"), player.getUniqueID());
            return;
        }
        super.requestUpgrade(player, builder);
    }

    /**
     * Stone smeltery View.
     */
    public static class View extends AbstractBuildingSmelterCrafter.View
    {
        /**
         * Instantiate the stone smeltery view.
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
            return new WindowHutGlassblower(this);
        }
    }
}
