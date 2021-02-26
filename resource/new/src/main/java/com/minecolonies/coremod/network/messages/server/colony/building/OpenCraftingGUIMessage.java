package com.minecolonies.coremod.network.messages.server.colony.building;

import org.jetbrains.annotations.NotNull;

/**
 * Message sent to open an inventory.
 */
public class OpenCraftingGUIMessage extends AbstractBuildingServerMessage<IBuilding>
{
    /**
     * Empty public constructor.
     */
    public OpenCraftingGUIMessage()
    {
        super();
    }

    /**
     * Creates an open inventory message for a building.
     *
     * @param building {@link AbstractBuildingView}
     */
    public OpenCraftingGUIMessage(@NotNull final AbstractBuildingView building)
    {
        super(building);
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
    }

    @Override
    protected void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony, final IBuilding building)
    {
        final ServerPlayerEntity player = ctxIn.getSender();
        if (player == null)
        {
            return;
        }
        building.openCraftingContainer(player);
    }
}
