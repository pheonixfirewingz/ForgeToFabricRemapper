package com.minecolonies.api.entity.citizen;

import org.jetbrains.annotations.Nullable;

public abstract class AbstractCivilianEntity extends PassiveEntity implements Npc, IStuckHandlerEntity
{

    /**
     * Whether this entity can be stuck for stuckhandling
     */
    private boolean canBeStuck = true;

    protected AbstractCivilianEntity(final EntityType<? extends PassiveEntity> type, final World worldIn)
    {
        super(type, worldIn);
    }

    /**
     * Setter for the citizen data.
     *
     * @param data the data to set.
     */
    public abstract void setCivilianData(@Nullable ICivilianData data);

    /**
     * Mark the citizen dirty to synch the data with the client.
     */
    public abstract void markDirty();

    /**
     * Getter for the citizen id.
     *
     * @return the id.
     */
    public abstract int getCivilianID();

    /**
     * Setter for the citizen id.
     *
     * @param id the id to set.
     */
    public abstract void setCitizenId(int id);

    @Override
    public boolean canBeStuck()
    {
        return canBeStuck;
    }

    /**
     * Sets whether the entity currently can be stuck
     *
     * @param canBeStuck
     */
    public void setCanBeStuck(final boolean canBeStuck)
    {
        this.canBeStuck = canBeStuck;
    }
}
