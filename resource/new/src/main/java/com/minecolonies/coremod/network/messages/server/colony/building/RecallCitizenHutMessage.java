package com.minecolonies.coremod.network.messages.server.colony.building;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Used to handle citizen recalls to their hut.
 */
public class RecallCitizenHutMessage extends AbstractBuildingServerMessage<IBuilding>
{
    /**
     * Empty public constructor.
     */
    public RecallCitizenHutMessage()
    {
        super();
    }

    /**
     * Creates a message to recall all citizens to their hut.
     *
     * @param building {@link AbstractBuildingView}
     */
    public RecallCitizenHutMessage(@NotNull final AbstractBuildingView building)
    {
        super(building);
    }

    @Override
    protected void onExecute(@NotNull final NetworkEvent.Context ctxIn, final boolean isLogicalServer, @NotNull final IColony colony, @NotNull final IBuilding building)
    {
        final BlockPos location = building.getPosition();
        final World world = colony.getWorld();
        for (final ICitizenData citizenData : building.getAssignedCitizen())
        {
            Optional<AbstractEntityCitizen> optionalEntityCitizen = citizenData.getEntity();
            if (!optionalEntityCitizen.isPresent())
            {
                Log.getLogger().warn(String.format("Citizen  %d:%d has gone AWOL, respawning them!", colony.getID(), citizenData.getId()));
                citizenData.updateEntityIfNecessary();
                optionalEntityCitizen = citizenData.getEntity();
            }

            if (optionalEntityCitizen.isPresent() && !TeleportHelper.teleportCitizen(optionalEntityCitizen.get(), world, location))
            {
                final PlayerEntity player = ctxIn.getSender();
                if (player == null)
                {
                    return;
                }

                LanguageHandler.sendPlayerMessage(player, "com.minecolonies.coremod.workerhuts.recallFail");
            }
        }
    }

    @Override
    protected void toBytesOverride(final PacketBuffer buf)
    {

    }

    @Override
    protected void fromBytesOverride(final PacketBuffer buf)
    {

    }
}
