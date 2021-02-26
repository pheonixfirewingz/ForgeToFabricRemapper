package com.minecolonies.coremod.colony.colony.jobs;



/**
 * The Knight's job class
 *
 * @author Asherslab
 */
public class JobKnight extends AbstractJobGuard<JobKnight>
{
    /**
     * Desc of knight job.
     */
    public static final String DESC = "com.minecolonies.coremod.job.Knight";

    /**
     * Initialize citizen data.
     *
     * @param entity the citizen data.
     */
    public JobKnight(final ICitizenData entity)
    {
        super(entity);
    }

    /**
     * Generates the {@link AbstractEntityAIGuard} job for our knight.
     *
     * @return The AI.
     */
    @Override
    public EntityAIKnight generateGuardAI()
    {
        return new EntityAIKnight(this);
    }

    /**
     * Custom Action on Levelup, increases Knight HP
     */
    @Override
    public void onLevelUp()
    {
        // Bonus Health for knights(gets reset upon Firing)
        if (getCitizen().getEntity().isPresent())
        {
            final AbstractEntityCitizen citizen = getCitizen().getEntity().get();

            // +1 Heart every 2 level
            final AttributeModifier healthModLevel =
              new AttributeModifier(GUARD_HEALTH_MOD_LEVEL_NAME,
                getCitizen().getCitizenSkillHandler().getLevel(Skill.Stamina) + KNIGHT_HP_BONUS,
                AttributeModifier.Operation.ADDITION);
            AttributeModifierUtils.addHealthModifier(citizen, healthModLevel);
        }
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.knight;
    }

    /**
     * Gets the name of our knight.
     *
     * @return The name.
     */
    @Override
    public String getName()
    {
        return DESC;
    }

    /**
     * Gets the {@link BipedModelType} to use for our ranger.
     *
     * @return The model to use.
     */
    @Override
    public BipedModelType getModel()
    {
        return BipedModelType.KNIGHT_GUARD;
    }
}
