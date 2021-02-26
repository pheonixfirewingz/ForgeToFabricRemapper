package com.minecolonies.coremod.commands.generalcommands;


/**
 * Cleanup task to remove the force flag from all loaded chunks.
 */
public class CommandUnloadForcedChunks implements IMCCommand
{
    /**
     * What happens when the command is executed
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSource> context)
    {
        final Entity sender = context.getSource().getEntity();
        if (sender instanceof PlayerEntity)
        {
            final World world = sender.world;
            for (long chunk : ((ServerChunkProvider) sender.world.getChunkProvider()).chunkManager.immutableLoadedChunks.keySet())
            {
                ((ServerWorld) world).forceChunk(ChunkPos.getX(chunk), ChunkPos.getZ(chunk), false);
            }
            sender.sendMessage(new StringTextComponent("Successfully removed forceload flag!"), sender.getUniqueID());
            return 1;
        }
        return 0;
    }

    @Override
    public boolean checkPreCondition(final CommandContext<CommandSource> context)
    {
        final Entity sender = context.getSource().getEntity();
        return sender instanceof PlayerEntity && ((PlayerEntity) sender).isCreative();
    }

    /**
     * Name string of the command.
     *
     * @return this commands name.
     */
    @Override
    public String getName()
    {
        return "forceunloadchunks";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> build()
    {
        return IMCCommand.newLiteral(getName()).executes(this::checkPreConditionAndExecute);
    }
}
