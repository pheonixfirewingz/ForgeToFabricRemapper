package com.minecolonies.api.entity.pathfinding.registry;


import java.util.function.*;

public interface IPathNavigateRegistry
{

    static IPathNavigateRegistry getInstance()
    {
        return IMinecoloniesAPI.getInstance().getPathNavigateRegistry();
    }

    IPathNavigateRegistry registerNewPathNavigate(Predicate<MobEntity> selectionPredicate, Function<MobEntity, AbstractAdvancedPathNavigate> navigateProducer);

    AbstractAdvancedPathNavigate getNavigateFor(MobEntity entityLiving);
}
