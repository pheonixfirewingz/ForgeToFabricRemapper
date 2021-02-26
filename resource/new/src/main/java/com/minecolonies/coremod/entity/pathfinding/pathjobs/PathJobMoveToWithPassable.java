package com.minecolonies.coremod.entity.pathfinding.pathjobs;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Moves to the given location, with a special passable check.
 */
public class PathJobMoveToWithPassable extends PathJobMoveToLocation
{
    /**
     * Function which tests if the given blockstate is passable
     */
    private final Function<BlockState, Boolean> isPassable;

    /**
     * Prepares the PathJob for the path finding system.
     *
     * @param world      world the entity is in.
     * @param start      starting location.
     * @param end        target location.
     * @param range      max search range.
     * @param entity     the entity.
     * @param isPassable passable check
     */
    public PathJobMoveToWithPassable(
      final World world,
      @NotNull final BlockPos start,
      @NotNull final BlockPos end, final int range, final LivingEntity entity, final Function<BlockState, Boolean> isPassable)
    {
        super(world, start, end, range, entity);
        this.isPassable = isPassable;
    }

    @Override
    protected boolean isPassable(@NotNull final BlockState block, final BlockPos pos)
    {
        return super.isPassable(block, pos) || isPassable.apply(block);
    }
}
