package com.minecolonies.coremod.colony.colony.jobs;

import org.jetbrains.annotations.NotNull;

public class JobComposter extends AbstractJob<EntityAIWorkComposter, JobComposter>
{

    /**
     * Initialize citizen data.
     *
     * @param entity the citizen data.
     */
    public JobComposter(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.composter;
    }

    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.composter";
    }

    /**
     * Get the RenderBipedCitizen.Model to use when the Citizen performs this job role.
     *
     * @return Model of the citizen.
     */
    @NotNull
    @Override
    public BipedModelType getModel()
    {
        return BipedModelType.COMPOSTER;
    }

    @Override
    public EntityAIWorkComposter generateAI()
    {
        return new EntityAIWorkComposter(this);
    }

    @Override
    public int getDiseaseModifier()
    {
        final int skill = getCitizen().getCitizenSkillHandler().getLevel(getCitizen().getWorkBuilding().getPrimarySkill());
        return (int) ((100 - skill)/25.0);
    }
}
