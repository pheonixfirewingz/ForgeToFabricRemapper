package com.minecolonies.coremod.colony.colony.jobs;


/**
 * The Knight's Training Job class
 */
public class JobCombatTraining extends AbstractJob<EntityAICombatTraining, JobCombatTraining>
{
    /**
     * Initialize citizen data.
     *
     * @param entity the citizen data.
     */
    public JobCombatTraining(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.combat;
    }

    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.combattraining";
    }

    @Override
    public String getExperienceTag()
    {
        return JobKnight.DESC;
    }

    @Override
    public BipedModelType getModel()
    {
        return BipedModelType.KNIGHT_GUARD;
    }

    @Override
    public EntityAICombatTraining generateAI()
    {
        return new EntityAICombatTraining(this);
    }
}
