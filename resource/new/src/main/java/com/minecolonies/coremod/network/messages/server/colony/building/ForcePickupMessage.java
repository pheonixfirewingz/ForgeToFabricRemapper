package com.minecolonies.coremod.network.messages.server.colony.building;

import org.jetbrains.annotations.NotNull;


/**
 * Message class which manages the messages to request an immediate pickup
 */
public class ForcePickupMessage extends AbstractBuildingServerMessage<IBuilding>
{
    /**
     * Empty public constructor.
     */
    public ForcePickupMessage()
    {
        super();
    }

    /**
     * Creates message for player to force a pickup.
     *
     * @param building view of the building to read data from
     */
    public ForcePickupMessage(@NotNull final IBuildingView building)
    {
        super(building);
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
        // Noop
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        // Noop
    }

    @Override
    protected void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony, final IBuilding building)
    {
        if (building.createPickupRequest(getPlayerActionPriority(true)))
        {
            building.markDirty();
        }
        else
        {
            final PlayerEntity player = ctxIn.getSender();
            if (player == null)
            {
                return;
            }

            LanguageHandler.sendPlayerMessage(player, COM_MINECOLONIES_COREMOD_ENTITY_DELIVERYMAN_FORCEPICKUP_FAILED);
        }
    }
}
