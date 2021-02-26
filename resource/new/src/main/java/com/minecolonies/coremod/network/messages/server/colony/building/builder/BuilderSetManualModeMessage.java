package com.minecolonies.coremod.network.messages.server.colony.building.builder;

import org.jetbrains.annotations.NotNull;




public class BuilderSetManualModeMessage extends AbstractBuildingServerMessage<BuildingBuilder>
{
    private boolean manualMode;

    /**
     * Empty standard constructor.
     */
    public BuilderSetManualModeMessage()
    {
        super();
    }

    /**
     * Creates a new BuilderSetManualModeMessage.
     *
     * @param building View of the building to read data from.
     * @param manualMode Whether the builder should accept build requests automatically.
     */
    public BuilderSetManualModeMessage(@NotNull final BuildingBuilder.View building, final boolean manualMode)
    {
        super(building);
        this.manualMode = manualMode;
    }

    @Override
    public void fromBytesOverride(final PacketBuffer buf)
    {
        manualMode = buf.readBoolean();
    }

    @Override
    public void toBytesOverride(final PacketBuffer buf)
    {
        buf.writeBoolean(manualMode);
    }

    @Override
    protected void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony, final BuildingBuilder building)
    {
        building.setManualMode(manualMode);
        if (manualMode && building.getMainCitizen() != null)
        {
            ctxIn.getSender().sendMessage(new TranslationTextComponent(COM_MINECOLONIES_COREMOD_BUILDER_MANUAL_MODE, building.getMainCitizen().getName()), ctxIn.getSender().getUniqueID());
        }
    }
}
