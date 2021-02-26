package com.minecolonies.coremod.commands.colonycommands;



/**
 * Loads a colony by id from the backup file.
 */
public class CommandLoadBackup implements IMCOPCommand
{
    /**
     * What happens when the command is executed after preConditions are successful.
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSource> context)
    {
        final int colonyId = IntegerArgumentType.getInteger(context, COLONYID_ARG);
        BackUpHelper.loadColonyBackup(colonyId, context.getSource().getWorld().getDimensionKey(), true, true);
        context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.loadbackup.success"), true);
        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "loadBackup";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> build()
    {
        return IMCCommand.newLiteral(getName())
                 .then(IMCCommand.newArgument(COLONYID_ARG, IntegerArgumentType.integer(1)).executes(this::checkPreConditionAndExecute));
    }
}
