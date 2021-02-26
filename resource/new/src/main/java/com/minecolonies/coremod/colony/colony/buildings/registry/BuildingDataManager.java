package com.minecolonies.coremod.colony.colony.buildings.registry;



public class BuildingDataManager implements IBuildingDataManager
{
    @Override
    public IBuilding createFrom(final IColony colony, final CompoundNBT compound)
    {
        final ResourceLocation type = new ResourceLocation(compound.getString(TAG_BUILDING_TYPE));
        final BlockPos pos = BlockPosUtil.read(compound, TAG_LOCATION);

        IBuilding building = this.createFrom(colony, pos, type);

        try
        {
            building.deserializeNBT(compound);
        }
        catch (final RuntimeException ex)
        {
            Log.getLogger().error(String.format("A Building %s(%s) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
              type, building.getClass().getName()), ex);
            building = null;
        }

        return building;
    }

    @Override
    public IBuilding createFrom(final IColony colony, final AbstractTileEntityColonyBuilding tileEntityColonyBuilding)
    {
        return this.createFrom(colony, tileEntityColonyBuilding.getPosition(), tileEntityColonyBuilding.getBuildingName());
    }

    @Override
    public IBuilding createFrom(final IColony colony, final BlockPos position, final ResourceLocation buildingName)
    {
        final BuildingEntry entry = IBuildingRegistry.getInstance().getValue(buildingName);

        if (entry == null)
        {
            Log.getLogger().error(String.format("Unknown building type '%s'.", buildingName), new Exception());
            return null;
        }
        return entry.produceBuilding(position, colony);
    }

    @Override
    public IBuildingView createViewFrom(
      final IColonyView colony, final BlockPos position, final PacketBuffer networkBuffer)
    {
        final ResourceLocation buildingName = new ResourceLocation(networkBuffer.readString(32767));
        final BuildingEntry entry = IBuildingRegistry.getInstance().getValue(buildingName);

        if (entry == null)
        {
            Log.getLogger().error(String.format("Unknown building type '%s'.", buildingName), new Exception());
            return null;
        }

        final IBuildingView view = entry.getBuildingViewProducer().get().apply(colony, position);

        if (view != null)
        {
            view.deserialize(networkBuffer);
        }

        return view;
    }
}
