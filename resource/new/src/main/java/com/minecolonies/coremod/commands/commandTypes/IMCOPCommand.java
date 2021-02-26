package com.minecolonies.coremod.commands.commandTypes;


/**
 * Interface for commands requiring OP rights to execute.
 */
public interface IMCOPCommand extends IMCCommand
{
    /**
     * Executes pre-checks before issuing the command. Checks for the senders type and OP rights.
     */
    @Override
    default boolean checkPreCondition(final CommandContext<CommandSource> context)
    {
        if (context.getSource().hasPermissionLevel(OP_PERM_LEVEL))
        {
            return true;
        }

        final Entity sender = context.getSource().getEntity();
        if (!(sender instanceof PlayerEntity))
        {
            return false;
        }

        if (!IMCCommand.isPlayerOped((PlayerEntity) sender))
        {
            LanguageHandler.sendPlayerMessage((PlayerEntity) sender, "com.minecolonies.command.notop");
            return false;
        }
        return true;
    }
}
