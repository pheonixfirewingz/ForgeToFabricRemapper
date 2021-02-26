package com.minecolonies.api.entity.pathfinding.registry;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.entity.pathfinding.AbstractAdvancedPathNavigate;
import net.minecraft.entity.*;

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
