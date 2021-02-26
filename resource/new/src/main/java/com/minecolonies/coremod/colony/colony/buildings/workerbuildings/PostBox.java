package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

/**
 * Class used to manage the postbox building block.
 */
public class PostBox extends AbstractBuilding implements IRSComponent
{
    /**
     * Description of the block used to set this block.
     */
    private static final String POST_BOX = "postbox";

    /**
     * Instantiates the building.
     *
     * @param c the colony.
     * @param l the location.
     */
    public PostBox(final IColony c, final BlockPos l)
    {
        super(c, l);
    }

    @Override
    public ImmutableCollection<IRequestResolver<?>> createResolvers()
    {
        return ImmutableList.of();
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return POST_BOX;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return 0;
    }

    @Override
    public boolean canBeGathered()
    {
        return false;
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.postBox;
    }

    @Override
    public void onRequestedRequestCancelled(@NotNull final IRequestManager manager, @NotNull final IRequest<?> request)
    {
        super.onRequestedRequestCancelled(manager, request);
        if (request.getState() == RequestState.FAILED && request.getRequest() instanceof Stack)
        {
            final IDeliverable req = ((Stack) request.getRequest()).copyWithCount(((Stack) request.getRequest()).getCount());
            createRequest(req, false);
        }
    }

    @Override
    public Tuple<BlockPos, BlockPos> getCorners()
    {
        this.setCorners(this.getPosition(), this.getPosition());
        return super.getCorners();
    }

    @Override
    public int getRotation()
    {
        return 0;
    }

    /**
     * ClientSide representation of the building.
     */
    public static class View extends AbstractBuildingView
    {
        /**
         * Instantiates the view of the building.
         *
         * @param c the colonyView.
         * @param l the location of the block.
         */
        public View(final IColonyView c, final BlockPos l)
        {
            super(c, l);
        }

        @NotNull
        @Override
        public Window getWindow()
        {
            return new WindowPostBox(this);
        }

        @NotNull
        @Override
        public IFormattableTextComponent getRequesterDisplayName(@NotNull final IRequestManager manager, @NotNull final IRequest<?> request)
        {
            return new TranslationTextComponent("block.minecolonies.blockpostbox.name");
        }
    }
}
