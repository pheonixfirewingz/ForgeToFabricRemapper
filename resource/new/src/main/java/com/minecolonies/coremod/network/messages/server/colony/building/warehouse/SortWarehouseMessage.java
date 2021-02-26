package com.minecolonies.coremod.network.messages.server.colony.building.warehouse;


/**
 * Sort the warehouse if level bigger than 3.
 */
public class SortWarehouseMessage extends AbstractBuildingServerMessage<BuildingWareHouse>
{
    /**
     * The required level to sort a warehouse.
     */
    private static final int REQUIRED_LEVEL_TO_SORT_WAREHOUSE = 3;

    /**
     * Empty constructor used when registering the
     */
    public SortWarehouseMessage()
    {
        super();
    }

    public SortWarehouseMessage(final IBuildingView building)
    {
        super(building);
    }

    @Override
    protected void toBytesOverride(final PacketBuffer buf)
    {

    }

    @Override
    protected void fromBytesOverride(final PacketBuffer buf)
    {

    }

    @Override
    protected void onExecute(
      final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony, final BuildingWareHouse building)
    {
        if (building.getBuildingLevel() >= REQUIRED_LEVEL_TO_SORT_WAREHOUSE)
        {
            building.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(inv -> SortingUtils.sort((CombinedItemHandler) inv));
        }
    }
}
