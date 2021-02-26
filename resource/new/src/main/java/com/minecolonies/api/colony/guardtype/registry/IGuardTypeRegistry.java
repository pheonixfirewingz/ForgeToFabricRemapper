package com.minecolonies.api.colony.guardtype.registry;


public interface IGuardTypeRegistry
{

    static IForgeRegistry<GuardType> getInstance()
    {
        return IMinecoloniesAPI.getInstance().getGuardTypeRegistry();
    }
}
