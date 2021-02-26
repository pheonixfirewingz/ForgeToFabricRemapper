package com.minecolonies.coremod.entity.citizen.citizenhandlers;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * The citizen skill handler of the citizen.
 */
public class CitizenSkillHandler implements ICitizenSkillHandler
{
    /**
     * Chance to level up intelligence.
     */
    private static final int CHANCE_TO_LEVEL = 50;

    /**
     * Defines how much child stats very from parents average(+ or -).
     */
    private static final int CHILD_STATS_VARIANCE = 3;

    /**
     * Max inheritance of stats.
     */
    private static final int MAX_INHERITANCE = 10;

    /**
     * Skill map.
     */
    public Map<Skill, Tuple<Integer, Double>> skillMap = new HashMap<>();

    @Override
    public void init(final int levelCap)
    {
        if (levelCap <= 1)
        {
            for (final Skill skill : Skill.values())
            {
                skillMap.put(skill, new Tuple<>(1, 0.0D));
            }
        }
        else
        {
            final Random random = new Random();
            for (final Skill skill : Skill.values())
            {
                skillMap.put(skill, new Tuple<>(random.nextInt(levelCap - 1) + 1, 0.0D));
            }
        }
    }

    @Override
    public void init(@NotNull final ICitizenData mom, @NotNull final ICitizenData dad, final Random rand)
    {
        final int levelCap = (int) mom.getColony().getOverallHappiness();
        init(levelCap);

        final int bonusPoints = 25 + rand.nextInt(25);

        int totalPoints = 0;
        for (final Skill skill : Skill.values())
        {
            final int momLevel = mom.getCitizenSkillHandler().getSkills().get(skill).getA();
            final int dadLevel = dad.getCitizenSkillHandler().getSkills().get(skill).getA();
            totalPoints += momLevel + dadLevel;
        }

        for (final Skill skill : Skill.values())
        {
            final double momLevel = mom.getCitizenSkillHandler().getSkills().get(skill).getA();
            final double dadLevel = dad.getCitizenSkillHandler().getSkills().get(skill).getA();

            int newPoints = (int) (((momLevel + dadLevel) / totalPoints) * bonusPoints);

            skillMap.put(skill, new Tuple<>(skillMap.get(skill).getA() + newPoints, 0.0D));
        }
    }

    @NotNull
    @Override
    public CompoundNBT write()
    {
        final CompoundNBT compoundNBT = new CompoundNBT();

        @NotNull final ListNBT levelTagList = new ListNBT();
        for (@NotNull final Map.Entry<Skill, Tuple<Integer, Double>> entry : skillMap.entrySet())
        {
            if (entry.getKey() != null && entry.getValue() != null)
            {
                @NotNull final CompoundNBT levelCompound = new CompoundNBT();
                levelCompound.putInt(TAG_SKILL, entry.getKey().ordinal());
                levelCompound.putInt(TAG_LEVEL, entry.getValue().getA());
                levelCompound.putDouble(TAG_EXPERIENCE, entry.getValue().getB());
                levelTagList.add(levelCompound);
            }
        }
        compoundNBT.put(TAG_LEVEL_MAP, levelTagList);

        return compoundNBT;
    }

    @Override
    public void read(@NotNull final CompoundNBT compoundNBT)
    {
        final ListNBT levelTagList = compoundNBT.getList(TAG_LEVEL_MAP, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < levelTagList.size(); ++i)
        {
            final CompoundNBT levelExperienceAtJob = levelTagList.getCompound(i);
            skillMap.put(Skill.values()[levelExperienceAtJob.getInt(TAG_SKILL)],
              new Tuple<>(Math.max(1, Math.min(levelExperienceAtJob.getInt(TAG_LEVEL), MAX_CITIZEN_LEVEL)), levelExperienceAtJob.getDouble(TAG_EXPERIENCE)));
        }
    }

    @Override
    public void tryLevelUpIntelligence(@NotNull final Random random, final int customChance, @NotNull final ICitizenData citizen)
    {
        if ((customChance > 0 && random.nextInt(customChance) > 0) || (customChance < 1 && random.nextInt(CHANCE_TO_LEVEL) > 0))
        {
            return;
        }

        final int levelCap = (int) citizen.getCitizenHappinessHandler().getHappiness(citizen.getColony());
        if (skillMap.get(Skill.Intelligence).getB() < levelCap * 9)
        {
            addXpToSkill(Skill.Intelligence, 10, citizen);
        }
    }

    @Override
    public int getLevel(@NotNull final Skill skill)
    {
        return skillMap.get(skill).getA();
    }

    @Override
    public void incrementLevel(@NotNull final Skill skill, final int level)
    {
        final Tuple<Integer, Double> current = skillMap.get(skill);
        skillMap.put(skill, new Tuple<>(Math.min(MAX_CITIZEN_LEVEL, Math.max(current.getA() + level, 1)), current.getB()));
    }

    @Override
    public void addXpToSkill(final Skill skill, final double xp, final ICitizenData data)
    {
        final Tuple<Integer, Double> tuple = skillMap.getOrDefault(skill, new Tuple<>(0, 0.0D));
        int level = tuple.getA();
        final double currentXp = tuple.getB();

        final IBuilding home = data.getHomeBuilding();

        final double citizenHutLevel = home == null ? 0 : home.getBuildingLevel();
        final double citizenHutMaxLevel = home == null ? 5 : home.getMaxBuildingLevel();

        if ((citizenHutLevel < citizenHutMaxLevel && citizenHutLevel * 10 <= level) || level >= MAX_CITIZEN_LEVEL)
        {
            return;
        }

        double xpToLevelUp = Math.min(Double.MAX_VALUE, currentXp + xp);
        while (xpToLevelUp > 0)
        {
            final double nextLevel = ExperienceUtils.getXPNeededForNextLevel(level);
            if (nextLevel > xpToLevelUp)
            {
                skillMap.put(skill, new Tuple<>(Math.min(MAX_CITIZEN_LEVEL, level), xpToLevelUp));
                xpToLevelUp = 0;
            }
            else
            {
                xpToLevelUp = xpToLevelUp - nextLevel;
                level++;
            }
        }

        if (level > tuple.getA())
        {
            levelUp(data);
            data.markDirty();
        }
    }

    @Override
    public void removeXpFromSkill(@NotNull final Skill skill, final double xp, @NotNull final ICitizenData data)
    {
        final Tuple<Integer, Double> tuple = skillMap.get(skill);
        int level = tuple.getA();
        double currentXp = tuple.getB();

        double xpToDiscount = xp;
        while (xpToDiscount > 0)
        {
            if (currentXp >= xpToDiscount || level <= 1)
            {
                skillMap.put(skill, new Tuple<>(Math.max(1, level), Math.max(0, currentXp - xpToDiscount)));
                break;
            }
            else
            {
                xpToDiscount -= currentXp;
                currentXp = ExperienceUtils.getXPNeededForNextLevel(level - 1);
                level--;
            }
        }

        if (level < tuple.getA())
        {
            data.markDirty();
        }
    }

    @Override
    public void levelUp(final ICitizenData data)
    {
        // Show level-up particles
        if (data.getEntity().isPresent())
        {
            final AbstractEntityCitizen citizen = data.getEntity().get();
            Network.getNetwork()
              .sendToTrackingEntity(new VanillaParticleMessage(citizen.getPosX(), citizen.getPosY(), citizen.getPosZ(), ParticleTypes.HAPPY_VILLAGER),
                data.getEntity().get());
        }

        if (data.getJob() != null)
        {
            data.getJob().onLevelUp();
        }
    }

    @Override
    public int getJobModifier(@NotNull final ICitizenData data)
    {
        final IBuilding workBuilding = data.getWorkBuilding();
        if (workBuilding instanceof AbstractBuildingWorker)
        {
            final Skill primary = ((AbstractBuildingWorker) workBuilding).getPrimarySkill();
            final Skill secondary = ((AbstractBuildingWorker) workBuilding).getSecondarySkill();
            return (getLevel(primary) + getLevel(secondary)) / 4;
        }
        return 0;
    }

    @Override
    public int getJobModifier(@NotNull final IBuildingView workBuilding)
    {
        if (workBuilding instanceof AbstractBuildingWorker.View)
        {
            final Skill primary = ((AbstractBuildingWorker.View) workBuilding).getPrimarySkill();
            final Skill secondary = ((AbstractBuildingWorker.View) workBuilding).getSecondarySkill();
            return (getLevel(primary) + getLevel(secondary)) / 4;
        }
        return 0;
    }

    @Override
    public double getTotalXP()
    {
        double totalXp = 0;
        for (final Tuple<Integer, Double> tuple : skillMap.values())
        {
            totalXp += tuple.getB();
        }
        return totalXp;
    }

    @Override
    public Map<Skill, Tuple<Integer, Double>> getSkills()
    {
        return ImmutableMap.copyOf(skillMap);
    }
}