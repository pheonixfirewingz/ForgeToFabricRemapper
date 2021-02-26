package com.minecolonies.coremod.network.messages.server.colony.building;

import org.jetbrains.annotations.NotNull;

/**
 * Message to execute the renaiming of the townHall.
 */
public class HutRenameMessage extends AbstractBuildingServerMessage<IBuilding>
{
    /**
     * The custom name to set.
     */
    private String name;

    /**
     * Empty public constructor.
     */
    public HutRenameMessage()
    {
        super();
    }

    /**
     * Object creation for the town hall rename
     *
     * @param name     New name of the town hall.
     * @param building the building we're executing on.
     */
    public HutRenameMessage(@NotNull final IBuildingView building, final String name)
    {
        super(building);
        this.name = name;
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {

        name = buf.readString(32767);
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {

        buf.writeString(name);
    }

    @Override
    protected void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony, final IBuilding building)
    {
        building.setCustomBuildingName(name);
    }
}
