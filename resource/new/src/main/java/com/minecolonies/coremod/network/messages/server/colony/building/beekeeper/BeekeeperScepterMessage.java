package com.minecolonies.coremod.network.messages.server.colony.building.beekeeper;



/**
 * Message to set the beekeeper scepter in the player inventory.
 */
public class BeekeeperScepterMessage extends AbstractBuildingServerMessage<BuildingBeekeeper>
{
    /**
     * Empty standard constructor.
     */
    public BeekeeperScepterMessage()
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

    public BeekeeperScepterMessage(final IBuildingView building)
    {
        super(building);
    }

    @Override
    protected void onExecute(
      final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony, final BuildingBeekeeper building)
    {
        final PlayerEntity player = ctxIn.getSender();
        if (player == null)
        {
            return;
        }

        final ItemStack scepter;
        boolean giveToPlayer = true;
        if (player.getHeldItemMainhand().getItem() == ModItems.scepterBeekeeper)
        {
            scepter = player.getHeldItemMainhand();
            giveToPlayer = false;
        }
        else
        {
            scepter = new ItemStack(ModItems.scepterBeekeeper);
        }
        final CompoundNBT compound = scepter.getOrCreateTag();

        final int emptySlot = player.inventory.getFirstEmptyStack();
        BlockPosUtil.write(compound, TAG_POS, building.getID());
        compound.putInt(TAG_ID, colony.getID());

        if (giveToPlayer)
        {
            final ItemStack item = player.inventory.getStackInSlot(player.inventory.currentItem);
            player.inventory.setInventorySlotContents(emptySlot, item);
            player.inventory.setInventorySlotContents(player.inventory.currentItem, scepter);
        }
        player.inventory.markDirty();
    }
}
