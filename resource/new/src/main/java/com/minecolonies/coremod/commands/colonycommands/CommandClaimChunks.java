package com.minecolonies.coremod.commands.colonycommands;



public class CommandClaimChunks implements IMCOPCommand
{
    /**
     * What happens when the command is executed after preConditions are successful.
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSource> context)
    {
        final Entity sender = context.getSource().getEntity();

        if (!(sender instanceof PlayerEntity))
        {
            return 0;
        }

        // Colony
        final int colonyID = IntegerArgumentType.getInteger(context, COLONYID_ARG);

        // Range
        final int range = IntegerArgumentType.getInteger(context, RANGE_ARG);
        if (range > MineColonies.getConfig().getServer().maxColonySize.get())
        {
            LanguageHandler.sendPlayerMessage((PlayerEntity) sender, "com.minecolonies.command.claim.toolarge", colonyID);
            return 0;
        }

        // Added/removed
        final boolean add = BoolArgumentType.getBool(context, ADD_ARG);

        final IChunkmanagerCapability chunkManager = sender.world.getCapability(CHUNK_STORAGE_UPDATE_CAP, null).resolve().orElse(null);
        if (chunkManager == null)
        {
            Log.getLogger().error(UNABLE_TO_FIND_WORLD_CAP_TEXT, new Exception());
            return 0;
        }

        if (chunkManager.getAllChunkStorages().size() > CHUNKS_TO_CLAM_THRESHOLD)
        {
            LanguageHandler.sendPlayerMessage((PlayerEntity) sender, "com.minecolonies.command.claim.maxchunks");
            return 0;
        }

        ChunkDataHelper.claimChunksInRange(colonyID, context.getSource().getWorld().getDimensionKey(), add, new BlockPos(sender.getPositionVec()), range, 0, sender.world);
        LanguageHandler.sendPlayerMessage((PlayerEntity) sender, "com.minecolonies.command.claim.success");
        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "claim";
    }

    public LiteralArgumentBuilder<CommandSource> build()
    {
        return IMCCommand.newLiteral(getName())
                 .then(IMCCommand.newArgument(COLONYID_ARG, IntegerArgumentType.integer(1))
                         .then(IMCCommand.newArgument(RANGE_ARG, IntegerArgumentType.integer(1, 10))
                                 .then(IMCCommand.newArgument(ADD_ARG, BoolArgumentType.bool()).executes(this::checkPreConditionAndExecute))));
    }
}
