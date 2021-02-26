package com.minecolonies.coremod.network.messages.server.colony.building.lumberjack;



/**
 * Message to set the lumberjack scepter in the player inventory.
 */
public class LumberjackScepterMessage extends AbstractBuildingServerMessage<BuildingLumberjack>
{
    /**
     * Empty standard constructor.
     */
    public LumberjackScepterMessage()
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

    public LumberjackScepterMessage(final IBuildingView building)
    {
        super(building);
    }

    @Override
    protected void onExecute(
      final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony, final BuildingLumberjack building)
    {
        final PlayerEntity player = ctxIn.getSender();
        if (player == null)
        {
            return;
        }

        final ItemStack scepter;
        boolean giveToPlayer = true;
        if (player.getHeldItemMainhand().getItem() == ModItems.scepterLumberjack)
        {
            scepter = player.getHeldItemMainhand();
            giveToPlayer = false;
        }
        else
        {
            scepter = new ItemStack(ModItems.scepterLumberjack);
        }

        if (!scepter.hasTag())
        {
            scepter.setTag(new CompoundNBT());
        }
        final CompoundNBT compound = scepter.getTag();

        //Should never happen.
        if (compound == null)
        {
            return;
        }

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
