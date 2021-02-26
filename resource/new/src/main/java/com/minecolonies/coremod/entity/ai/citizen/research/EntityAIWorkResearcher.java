package com.minecolonies.coremod.entity.ai.citizen.research;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class EntityAIWorkResearcher extends AbstractEntityAIInteract<JobResearch, BuildingUniversity>
{
    /**
     * Delay for each subject study.
     */
    public static final int STUDY_DELAY = 60;

    /**
     * XP gained per study position
     */
    private static final double XP_PER_STUDYPOS = 0.7;

    /**
     * The current pos to study at.
     */
    private BlockPos studyPos = null;

    /**
     * Constructor for the AI
     *
     * @param job the job to fulfill
     */
    public EntityAIWorkResearcher(@NotNull final JobResearch job)
    {
        super(job);
        super.registerTargets(
          new AITarget(IDLE, START_WORKING, 1),
          new AITarget(START_WORKING, this::startWorkingAtOwnBuilding, TICKS_SECOND),
          new AITarget(STUDY, this::study, STUDY_DELAY)
        );
        worker.setCanPickUpLoot(true);
    }

    @Override
    public Class<BuildingUniversity> getExpectedBuildingClass()
    {
        return BuildingUniversity.class;
    }

    /**
     * The AI task for the student to study. For this he should walk between the different bookcase hit them once and then stand around for a while.
     *
     * @return the next IAIState.
     */
    private IAIState study()
    {
        final IColony colony = getOwnBuilding().getColony();
        final List<ILocalResearch> inProgress = colony.getResearchManager().getResearchTree().getResearchInProgress();
        if (!inProgress.isEmpty() && job.getCurrentMana() > 0)
        {
            final ILocalResearch research = inProgress.get(worker.getRandom().nextInt(inProgress.size()));

            if (colony.getResearchManager()
                  .getResearchTree()
                  .getResearch(research.getBranch(), research.getId())
                  .research(colony.getResearchManager().getResearchEffects(), colony.getResearchManager().getResearchTree()))
            {
                getOwnBuilding().onSuccess(research);
            }
            job.reduceCurrentMana();
        }

        if (studyPos == null)
        {
            studyPos = getOwnBuilding().getRandomBookShelf();
        }

        if (walkToBlock(studyPos))
        {
            return getState();
        }

        worker.decreaseSaturationForContinuousAction();
        worker.getCitizenData().getCitizenSkillHandler().addXpToSkill(getOwnBuilding().getPrimarySkill(), XP_PER_STUDYPOS, worker.getCitizenData());

        studyPos = null;
        return getState();
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
