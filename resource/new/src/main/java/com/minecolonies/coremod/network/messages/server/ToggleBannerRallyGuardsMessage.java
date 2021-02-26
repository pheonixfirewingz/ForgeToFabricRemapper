package com.minecolonies.coremod.network.messages.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Toggles a rallying banner
 */
public class ToggleBannerRallyGuardsMessage implements IMessage
{
    /**
     * The banner to be toggled.
     */
    private ItemStack banner;

    /**
     * Empty constructor used when registering the message
     */
    public ToggleBannerRallyGuardsMessage()
    {
        super();
    }

    /**
     * Toggle the banner
     *
     * @param banner The banner to be toggled.
     */
    public ToggleBannerRallyGuardsMessage(final ItemStack banner)
    {
        super();
        this.banner = banner;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        banner = buf.readItemStack();
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeItemStack(banner);
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

        toggleBanner(player.inventory.getStackInSlot(slot), player);
    }
}
