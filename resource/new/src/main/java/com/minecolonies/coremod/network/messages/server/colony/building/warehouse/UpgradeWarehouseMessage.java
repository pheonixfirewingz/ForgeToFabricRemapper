package com.minecolonies.coremod.network.messages.server.colony.building.warehouse;


/**
 * Issues the upgrade of the warehouse pos level 5.
 */
public class UpgradeWarehouseMessage extends AbstractBuildingServerMessage<BuildingWareHouse>
{
    /**
     * Empty constructor used when registering the
     */
    public UpgradeWarehouseMessage()
    {
        super();
    }

    @Override
    protected void toBytesOverride(final PacketBuffer buf)
    {

    }

    @Override
    protected void fromBytesOverride(final PacketBuffer buf)
    {

    }

    public UpgradeWarehouseMessage(final IBuildingView building)
    {
        super(building);
    }

    @Override
    protected void onExecute(
      final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony, final BuildingWareHouse building)
    {
        final PlayerEntity player = ctxIn.getSender();
        if (player == null)
        {
            return;
        }

        building.upgradeContainers(player.world);

        final boolean isCreative = player.isCreative();
        if (!isCreative)
        {
            final int slot = InventoryUtils.
                                             findFirstSlotInItemHandlerWith(new InvWrapper(player.inventory),
                                               itemStack -> itemStack.isItemEqual(new ItemStack(Blocks.EMERALD_BLOCK)));
            player.inventory.decrStackSize(slot, 1);
        }
    }
}
