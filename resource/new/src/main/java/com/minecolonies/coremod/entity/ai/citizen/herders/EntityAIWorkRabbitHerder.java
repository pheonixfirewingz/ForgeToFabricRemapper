package com.minecolonies.coremod.entity.ai.citizen.herders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * The AI behind the {@link JobRabbitHerder} for Breeding and Killing Rabbits.
 */
public class EntityAIWorkRabbitHerder extends AbstractEntityAIHerder<JobRabbitHerder, BuildingRabbitHutch, RabbitEntity>
{
    /**
     * Max amount of animals per Hut Level.
     */
    private static final int MAX_ANIMALS_PER_LEVEL = 2;

    /**
     * Creates the abstract part of the AI. Always use this constructor!
     *
     * @param job the job to fulfill
     */
    public EntityAIWorkRabbitHerder(@NotNull final JobRabbitHerder job)
    {
        super(job);
    }

    @Override
    public Class<BuildingRabbitHutch> getExpectedBuildingClass()
    {
        return BuildingRabbitHutch.class;
    }

    @Override
    public ItemStack getBreedingItem()
    {
        final ItemStack stack = new ItemStack(Items.CARROT);
        stack.setCount(2);
        return stack;
    }

    @Override
    protected boolean canFeedChildren()
    {
        return getSecondarySkillLevel() >= LIMIT_TO_FEED_CHILDREN;
    }

    @Override
    public int getMaxAnimalMultiplier()
    {
        return MAX_ANIMALS_PER_LEVEL;
    }

    @Override
    public Class<RabbitEntity> getAnimalClass()
    {
        return RabbitEntity.class;
    }

    @Override
    protected void butcherAnimal(@Nullable final AnimalEntity animal)
    {
        worker.getCitizenStatusHandler().setLatestStatus(new TranslationTextComponent(TranslationConstants.COM_MINECOLONIES_COREMOD_STATUS_HERDER_BUTCHERING));
        if (animal != null && !walkingToAnimal(animal) && !ItemStackUtils.isEmpty(worker.getHeldItemMainhand()))
        {
            worker.swingArm(Hand.MAIN_HAND);

            if (worker.getRandom().nextInt(1 + (ONE_HUNDRED_PERCENT - getPrimarySkillLevel()) / 5) <= 1)
            {
                final FakePlayer fp = FakePlayerFactory.getMinecraft((ServerWorld) worker.getEntityWorld());
                final DamageSource ds = DamageSource.causePlayerDamage(fp);
                animal.attackEntityFrom(ds, (float) getButcheringAttackDamage());
                worker.getCitizenItemHandler().damageItemInHand(Hand.MAIN_HAND, 1);
            }
        }
    }
}
