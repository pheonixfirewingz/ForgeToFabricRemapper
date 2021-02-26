package com.minecolonies.coremod.commands.citizencommands;



/**
 * Force-spawns a new citizen in the given colony.
 */
public class CommandCitizenSpawnNew implements IMCOPCommand
{
    /**
     * What happens when the command is executed after preConditions are successful.
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSource> context)
    {
        // Colony
        final int colonyID = IntegerArgumentType.getInteger(context, COLONYID_ARG);
        final IColony colony = IColonyManager.getInstance().getColonyByDimension(colonyID, context.getSource().getWorld().getDimensionKey());
        if (colony == null)
        {
            context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.colonyidnotfound", colonyID), true);
            return 0;
        }

        context.getSource()
          .sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.citizenspawn.success",
            colony.getCitizenManager().spawnOrCreateCivilian(null, colony.getWorld(), null, true).getName()), true);
        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "spawnNew";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> build()
    {
        return IMCCommand.newLiteral(getName())
                 .then(IMCCommand.newArgument(COLONYID_ARG, IntegerArgumentType.integer(1)).executes(this::checkPreConditionAndExecute));
    }
}
