package com.minecolonies.coremod.colony.colony.buildings.registry;


public final class GuardTypeDataManager implements IGuardTypeDataManager
{
    @Override
    public GuardType getFrom(final ResourceLocation jobName)
    {
        if (jobName == null)
        {
            return null;
        }

        return IGuardTypeRegistry.getInstance().getValue(jobName);
    }
}
