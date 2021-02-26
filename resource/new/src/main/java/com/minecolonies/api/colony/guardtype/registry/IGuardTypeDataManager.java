package com.minecolonies.api.colony.guardtype.registry;


public interface IGuardTypeDataManager
{

    static IGuardTypeDataManager getInstance()
    {
        return IMinecoloniesAPI.getInstance().getGuardTypeDataManager();
    }

    GuardType getFrom(ResourceLocation jobName);
}
