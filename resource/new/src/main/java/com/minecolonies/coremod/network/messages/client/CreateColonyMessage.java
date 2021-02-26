package com.minecolonies.coremod.network.messages.client;

import org.jetbrains.annotations.Nullable;


/**
 * Message for trying to create a new colony.
 */
public class CreateColonyMessage implements IMessage
{
    /**
     * Townhall position to create building on
     */
    BlockPos townHall;

    public CreateColonyMessage()
    {
        super();
    }

    public CreateColonyMessage(final BlockPos townHall)
    {
        this.townHall = townHall;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeBlockPos(townHall);
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        townHall = buf.readBlockPos();
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
        final ServerPlayerEntity sender = ctxIn.getSender();
        final World world = ctxIn.getSender().world;

        if (sender == null)
        {
            return;
        }

        if (sender.getStats().getValue(Stats.ITEM_USED.get(ModItems.supplyChest)) <= 0 && !sender.isCreative())
        {
            LanguageHandler.sendPlayerMessage(sender, "com.minecolonies.coremod.supplyneed");
            return;
        }

        final IColony colony = IColonyManager.getInstance().getClosestColony(world, townHall);

        String style = Constants.DEFAULT_STYLE;
        final TileEntity tileEntity = world.getTileEntity(townHall);

        if (!(tileEntity instanceof TileEntityColonyBuilding))
        {
            LanguageHandler.sendPlayerMessage(sender, "com.minecolonies.coremod.gui.colony.create.notileentity");
            return;
        }

        if (!((AbstractTileEntityColonyBuilding) tileEntity).getStyle().isEmpty())
        {
            style = ((AbstractTileEntityColonyBuilding) tileEntity).getStyle();
        }

        if (MineColonies.getConfig().getServer().restrictColonyPlacement.get())
        {
            final double spawnDistance = Math.sqrt(BlockPosUtil.getDistanceSquared2D(townHall, ((ServerWorld) world).getSpawnPoint()));
            if (spawnDistance < MineColonies.getConfig().getServer().minDistanceFromWorldSpawn.get())
            {
                if (!world.isRemote)
                {
                    LanguageHandler.sendPlayerMessage(sender, CANT_PLACE_COLONY_TOO_CLOSE_TO_SPAWN, MineColonies.getConfig().getServer().minDistanceFromWorldSpawn.get());
                }
                return;
            }
            else if (spawnDistance > MineColonies.getConfig().getServer().maxDistanceFromWorldSpawn.get())
            {
                if (!world.isRemote)
                {
                    LanguageHandler.sendPlayerMessage(sender, CANT_PLACE_COLONY_TOO_FAR_FROM_SPAWN, MineColonies.getConfig().getServer().maxDistanceFromWorldSpawn.get());
                }
                return;
            }
        }

        if (colony == null || !IColonyManager.getInstance().isTooCloseToColony(world, townHall))
        {
            final IColony ownedColony = IColonyManager.getInstance().getIColonyByOwner(world, sender);

            if (ownedColony == null)
            {
                IColonyManager.getInstance().createColony(world, townHall, sender, style);
                IColonyManager.getInstance().getIColonyByOwner(world, sender).getBuildingManager().addNewBuilding((TileEntityColonyBuilding) tileEntity, world);
                LanguageHandler.sendPlayerMessage((PlayerEntity) sender, "com.minecolonies.coremod.progress.colony_founded");
                return;
            }
        }

        LanguageHandler.sendPlayerMessage((PlayerEntity) sender, "com.minecolonies.coremod.gui.colony.create.failed");
    }
}
