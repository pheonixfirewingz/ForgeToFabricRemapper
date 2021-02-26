package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;


/**
 * Class of the sawmill building.
 */
public class BuildingSawmill extends AbstractBuildingCrafter
{
    /**
     * Description string of the building.
     */
    private static final String SAWMILL = "sawmill";

    /**
     * The min percentage something has to have out of wood to be craftable by this worker.
     */
    private static final double MIN_PERCENTAGE_TO_CRAFT = 0.75;

    /**
     * Instantiates a new sawmill building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingSawmill(final IColony c, final BlockPos l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return SAWMILL;
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
        return new JobSawmill(citizen);
    }

    @NotNull
    @Override
    public String getJobName()
    {
        return SAWMILL;
    }

    @NotNull
    @Override
    public Skill getPrimarySkill()
    {
        return Skill.Knowledge;
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

            double amountOfValidBlocks = 0;
            double blocks = 0;
            for (final ItemStack stack : storage.getInput())
            {
                if (!ItemStackUtils.isEmpty(stack))
                {
                    if (stack.getItem().isIn(ItemTags.PLANKS) || stack.getItem().isIn(ItemTags.LOGS))
                    {
                        amountOfValidBlocks++;
                        continue;
                    }
                    for (final ResourceLocation tag : stack.getItem().getTags())
                    {
                        if (tag.getPath().contains("wood"))
                        {
                            amountOfValidBlocks++;
                            break;
                        }
                    }
                    blocks++;
                }
            }

            return amountOfValidBlocks > 0 && amountOfValidBlocks / blocks > MIN_PERCENTAGE_TO_CRAFT;

            // End Additional recipe rules
        }
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.sawmill;
    }

    @Override
    public void requestUpgrade(final PlayerEntity player, final BlockPos builder)
    {
        final UnlockBuildingResearchEffect effect = colony.getResearchManager().getResearchEffects().getEffect(ResearchInitializer.SAWMILL_RESEARCH, UnlockBuildingResearchEffect.class);
        if (effect == null)
        {
            player.sendMessage(new TranslationTextComponent("com.minecolonies.coremod.research.havetounlock"), player.getUniqueID());
            return;
        }
        super.requestUpgrade(player, builder);
    }

    /**
     * Sawmill View.
     */
    public static class View extends AbstractBuildingCrafter.View
    {

        /**
         * Instantiate the sawmill view.
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
            return new WindowHutCrafter(this, SAWMILL);
        }
    }
}
