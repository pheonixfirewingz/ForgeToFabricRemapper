package com.minecolonies.coremod.entity.ai.citizen.mechanic;

import org.jetbrains.annotations.NotNull;


/**
 * Crafts everything else basically (redstone stuff etc)
 */
public class EntityAIWorkMechanic extends AbstractEntityAICrafting<JobMechanic, BuildingMechanic>
{
    /**
     * Initialize the mechanic and add all his tasks.
     *
     * @param mechanic the job he has.
     */
    public EntityAIWorkMechanic(@NotNull final JobMechanic mechanic)
    {
        super(mechanic);
    }

    @Override
    public Class<BuildingMechanic> getExpectedBuildingClass()
    {
        return BuildingMechanic.class;
    }

    @Override
    protected void updateRenderMetaData()
    {
        worker.setRenderMetadata(getState() == CRAFT ? "mask" : "");
    }
}
