package com.minecolonies.coremod.network.messages.server.colony.building;

import org.jetbrains.annotations.NotNull;


/**
 * Message to set the style of a building.
 */
public class BuildingSetStyleMessage extends AbstractBuildingServerMessage<IBuilding>
{
    /**
     * The style to set.
     */
    private String style;

    /**
     * Empty constructor used when registering the
     */
    public BuildingSetStyleMessage()
    {
        super();
    }

    /**
     * Creates object for the style of a building.
     *
     * @param building View of the building to read data from.
     * @param style    style of the building.
     */
    public BuildingSetStyleMessage(@NotNull final IBuildingView building, final String style)
    {
        super(building);
        this.style = style;
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
        style = buf.readString(32767);
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        buf.writeString(style);
    }

    @Override
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony, final IBuilding building)
    {
        building.setStyle(style);
        if (building.getTileEntity() != null && !building.getStyle().equals(style))
        {
            building.getTileEntity().setStyle(style);

            final WorkOrderBuildBuilding workOrder = new WorkOrderBuildBuilding(building, building.getBuildingLevel() + 1);
            final LoadOnlyStructureHandler structure = new LoadOnlyStructureHandler(colony.getWorld(), building.getPosition(), workOrder.getStructureName(), new PlacementSettings(), true);
            structure.getBluePrint().rotateWithMirror(BlockPosUtil.getRotationFromRotations(workOrder.getRotation(colony.getWorld())), workOrder.isMirrored() ? Mirror.FRONT_BACK : Mirror.NONE, colony.getWorld());

            CompoundNBT teData = structure.getBluePrint().getTileEntityData(building.getTileEntity().getPos(), structure.getBluePrint().getPrimaryBlockOffset());
            if (teData != null && teData.contains(TAG_BLUEPRINTDATA))
            {
                building.getTileEntity().readSchematicDataFromNBT(teData);
                Chunk chunk = (Chunk) building.getTileEntity().getWorld().getChunk(building.getTileEntity().getPos());
                PacketDistributor.TRACKING_CHUNK.with(() -> chunk).send(building.getTileEntity().getUpdatePacket());
                building.getTileEntity().markDirty();
                building.calculateCorners();
            }
        }
    }
}