package com.minecolonies.coremod.entity.ai.citizen.school;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;


public class EntityAIWorkPupil extends AbstractEntityAIInteract<JobPupil, BuildingSchool>
{
    /**
     * How often the kid studies for one recess.
     */
    private static final int STUDY_TO_RECESS_RATIO = 10;

    /**
     * To be consumed from the inv.
     */
    private final Predicate<ItemStack> PAPER = stack -> stack.getItem() == Items.PAPER;

    /**
     * The max time to sit.
     */
    private int maxSittingTicks = 0;

    /**
     * The current sitting time.
     */
    private int sittingTicks = 0;

    /**
     * The pos to study at.
     */
    private BlockPos studyPos;

    /**
     * Next recess pos to run to.
     */
    private BlockPos recessPos;

    /**
     * Constructor for the AI
     *
     * @param job the job to fulfill
     */
    public EntityAIWorkPupil(@NotNull final JobPupil job)
    {
        super(job);
        super.registerTargets(
          new AITarget(IDLE, START_WORKING, 1),
          new AITarget(START_WORKING, this::startWorkingAtOwnBuilding, TICKS_SECOND),
          new AITarget(DECIDE, this::decide, TICKS_SECOND),
          new AITarget(STUDY, this::study, TICKS_SECOND),
          new AITarget(RECESS, this::recess, TICKS_SECOND)
        );
        worker.setCanPickUpLoot(true);
    }

    /**
     * Decide between recess and studying.
     *
     * @return next state to go to.
     */
    private IAIState decide()
    {
        if (worker.getRandom().nextInt(STUDY_TO_RECESS_RATIO) < 1)
        {
            recessPos = getOwnBuilding().getPosition();
            return RECESS;
        }

        final BuildingSchool school = getOwnBuilding();
        final BlockPos pos = school.getRandomPlaceToSit();
        if (pos == null)
        {
            worker.getCitizenData().triggerInteraction(new StandardInteraction(new TranslationTextComponent(PUPIL_NO_CARPET), ChatPriority.BLOCKING));
            return DECIDE;
        }

        studyPos = pos;
        return STUDY;
    }

    /**
     * Run around a bit until it's time for studying again.
     *
     * @return next state to go to.
     */
    private IAIState recess()
    {
        if (recessPos == null || worker.getRandom().nextInt(STUDY_TO_RECESS_RATIO) < 1)
        {
            return START_WORKING;
        }

        if (walkToBlock(recessPos))
        {
            return getState();
        }

        final BlockPos newRecessPos = findRandomPositionToWalkTo(10);
        if (newRecessPos != null)
        {
            recessPos = newRecessPos;
        }
        return getState();
    }

    /**
     * Sit down a bit and study. If has paper consume it.
     *
     * @return next state to go to.
     */
    private IAIState study()
    {
        if (studyPos == null)
        {
            return DECIDE;
        }

        if (walkToBlock(studyPos))
        {
            return getState();
        }

        if (!world.getLoadedEntitiesWithinAABB(EntityCitizen.class,
          new AxisAlignedBB(studyPos.getX(), studyPos.getY(), studyPos.getZ(), studyPos.getX(), studyPos.getY(), studyPos.getZ())).isEmpty())
        {
            studyPos = null;
            return DECIDE;
        }

        if (sittingTicks == 0 || worker.ridingEntity == null)
        {
            // Sit for 60-120 seconds.
            maxSittingTicks = worker.getRandom().nextInt(120 / 2) + 60;
            SittingEntity.sitDown(studyPos, worker, maxSittingTicks * 20);
        }

        final int slot = InventoryUtils.findFirstSlotInItemHandlerWith(worker.getInventoryCitizen(), PAPER);

        if (slot != -1)
        {
            worker.setItemStackToSlot(EquipmentSlotType.MAINHAND, worker.getInventoryCitizen().getStackInSlot(slot));
            Network.getNetwork().sendToTrackingEntity(new CircleParticleEffectMessage(worker.getPositionVec().add(0, 1, 0), ParticleTypes.ENCHANT, sittingTicks), worker);
        }
        else
        {
            worker.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
            Network.getNetwork().sendToTrackingEntity(new CircleParticleEffectMessage(worker.getPositionVec().add(0, 1, 0), ParticleTypes.HAPPY_VILLAGER, sittingTicks), worker);
        }

        sittingTicks++;
        if (sittingTicks < maxSittingTicks)
        {
            return getState();
        }

        worker.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
        if (worker.ridingEntity != null)
        {
            worker.stopRiding();
            worker.setPosition(worker.getPosX(), worker.getPosY() + 1, worker.getPosZ());
        }

        if (slot != -1)
        {
            InventoryUtils.reduceStackInItemHandler(worker.getInventoryCitizen(), new ItemStack(Items.PAPER), 1);
            double bonus = 50.0;
            final MultiplierModifierResearchEffect effect =
              worker.getCitizenColonyHandler().getColony().getResearchManager().getResearchEffects().getEffect(TEACHING, MultiplierModifierResearchEffect.class);
            if (effect != null)
            {
                bonus *= (1 + effect.getEffect());
            }

            worker.getCitizenData().getCitizenSkillHandler().addXpToSkill(Skill.Intelligence, bonus, worker.getCitizenData());
        }

        worker.decreaseSaturationForContinuousAction();

        maxSittingTicks = 0;
        sittingTicks = 0;
        return null;
    }

    @Override
    public Class<BuildingSchool> getExpectedBuildingClass()
    {
        return BuildingSchool.class;
    }

    /**
     * Redirects the student to his library.
     *
     * @return the next state.
     */
    private IAIState startWorkingAtOwnBuilding()
    {
        if (walkToBuilding())
        {
            return getState();
        }
        return STUDY;
    }
}
