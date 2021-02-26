package com.minecolonies.coremod.entity.mobs.aitasks;


import java.util.EnumSet;


/**
 * Break door entity AI with mutex.
 */
public class EntityAIBreakDoor extends BreakDoorGoal
{
    /**
     * Previous break pos
     */
    private BlockPos prevDoorPos = BlockPos.ZERO;

    /**
     * The door's hardness we're breaking
     */
    private int hardness = 0;

    /**
     * Amount of nearby raiders
     */
    private int breakChance = 1;

    public EntityAIBreakDoor(final MobEntity entityIn)
    {
        super(entityIn, difficulty -> difficulty.getId() > 0);
        setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return super.shouldContinueExecuting() && !entity.world.isAirBlock(doorPosition);
    }

    @Override
    public void startExecuting()
    {
        super.startExecuting();
        if (!prevDoorPos.equals(doorPosition))
        {
            this.breakingTime = 0;
        }
        prevDoorPos = doorPosition;
        hardness = (int) (1 + entity.world.getBlockState(doorPosition).getBlockHardness(entity.world, doorPosition));

        // No stuck during door break
        if (entity instanceof AbstractEntityMinecoloniesMob)
        {
            ((AbstractEntityMinecoloniesMob) entity).setCanBeStuck(false);
        }
    }

    public void resetTask()
    {
        super.resetTask();
        this.entity.world.sendBlockBreakProgress(this.entity.getEntityId(), this.doorPosition, -1);
        if (entity instanceof AbstractEntityMinecoloniesMob)
        {
            ((AbstractEntityMinecoloniesMob) entity).setCanBeStuck(true);
        }
    }

    @Override
    public void tick()
    {
        if (entity.getEntityWorld().getDifficulty().getId() < 2 || !MineColonies.getConfig().getServer().shouldRaidersBreakDoors.get())
        {
            breakingTime = 0;
        }

        // Only advances breaking time in relation to hardness
        if (this.entity.getRNG().nextInt(breakChance) != 0)
        {
            this.breakingTime--;
        }
        else
        {
            int fasterBreakPerXNearby = 5;

            if (entity instanceof AbstractEntityMinecoloniesMob && !entity.world.isRemote())
            {
                final IColony colony = ((AbstractEntityMinecoloniesMob) entity).getColony();
                final AdditionModifierResearchEffect effect =
                  colony.getResearchManager().getResearchEffects().getEffect(MECHANIC_ENHANCED_GATES, AdditionModifierResearchEffect.class);
                if (effect != null)
                {
                    fasterBreakPerXNearby += effect.getEffect().intValue();
                }
            }
            breakChance = Math.max(1,
              hardness / (1 + (entity.world.getLoadedEntitiesWithinAABB(AbstractEntityMinecoloniesMob.class, entity.getBoundingBox().grow(5)).size() / fasterBreakPerXNearby)));
        }

        if (this.breakingTime == this.func_220697_f() - 1)
        {
            final BlockState toBreak = entity.world.getBlockState(doorPosition);
            if (toBreak.getBlock() instanceof AbstractBlockGate)
            {
                ((AbstractBlockGate) toBreak.getBlock()).removeGate(entity.world, doorPosition, toBreak.get(BlockStateProperties.HORIZONTAL_FACING).rotateY());
            }
        }

        super.tick();
    }
}
