package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;


/**
 * Class of the warehouse building.
 */
public class BuildingDeliveryman extends AbstractBuildingWorker implements IBuildingDeliveryman
{

    private static final String DELIVERYMAN = "deliveryman";

    /**
     * Instantiates a new warehouse building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingDeliveryman(final IColony c, final BlockPos l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return DELIVERYMAN;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.deliveryman;
    }

    @NotNull
    @Override
    public IJob<?> createJob(final ICitizenData citizen)
    {
        return new JobDeliveryman(citizen);
    }

    @NotNull
    @Override
    public String getJobName()
    {
        return DELIVERYMAN;
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
        return Skill.Adaptability;
    }

    @Override
    public void removeCitizen(final ICitizenData citizen)
    {
        if (citizen != null)
        {
            final Optional<AbstractEntityCitizen> optCitizen = citizen.getEntity();
            optCitizen.ifPresent(entityCitizen -> AttributeModifierUtils.removeModifier(entityCitizen, SKILL_BONUS_ADD, Attributes.MOVEMENT_SPEED));
        }
        super.removeCitizen(citizen);
    }

    @Override
    public boolean canEat(final ItemStack stack)
    {
        final ICitizenData citizenData = getMainCitizen();
        if (citizenData != null)
        {
            final JobDeliveryman job = (JobDeliveryman) citizenData.getJob();
            final IRequest<? extends IRequestable> currentTask = job.getCurrentTask();
            if (currentTask == null)
            {
                return super.canEat(stack);
            }
            final IRequestable request = currentTask.getRequest();
            if (request instanceof Delivery && ((Delivery) request).getStack().isItemEqual(stack))
            {
                return false;
            }
        }
        return super.canEat(stack);
    }

    /**
     * BuildingDeliveryman View.
     */
    public static class View extends AbstractBuildingWorker.View
    {
        /**
         * Instantiate the deliveryman view.
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
            return new WindowHutDeliveryman(this);
        }
    }
}
