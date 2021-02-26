package com.minecolonies.coremod.network.messages.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Removes a guard tower from the rallying list
 */
public class RemoveFromRallyingListMessage implements IMessage
{
    /**
     * The banner to be modified.
     */
    private ItemStack banner;

    /**
     * The position of the guard tower that should be removed.
     */
    private ILocation location;

    /**
     * Empty constructor used when registering the message
     */
    public RemoveFromRallyingListMessage()
    {
        super();
    }

    /**
     * Remove the guard tower from the rallying list
     *
     * @param banner   The banner to be modified.
     * @param location The position of the guard tower
     */
    public RemoveFromRallyingListMessage(final ItemStack banner, final ILocation location)
    {
        super();
        this.banner = banner;
        this.location = location;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        banner = buf.readItemStack();
        location = StandardFactoryController.getInstance().deserialize(buf.readCompoundTag());
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeItemStack(banner);
        buf.writeCompoundTag(StandardFactoryController.getInstance().serialize(location));
    }

    @Nullable
    @Override
    public LogicalSide getExecutionSide()
    {
        return LogicalSide.SERVER;
    }

    @Override
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer)
    {
        final ServerPlayerEntity player = ctxIn.getSender();
        final int slot = InventoryUtils.findFirstSlotInItemHandlerWith(new InvWrapper(player.inventory),
          (itemStack -> ItemStackUtils.compareItemStacksIgnoreStackSize(itemStack, banner)));

        if (slot == -1)
        {
            LanguageHandler.sendPlayerMessage(player, TranslationConstants.COM_MINECOLONIES_BANNER_RALLY_GUARDS_GUI_ERROR);
            return;
        }

        removeGuardTowerAtLocation(player.inventory.getStackInSlot(slot), location);
    }
}
