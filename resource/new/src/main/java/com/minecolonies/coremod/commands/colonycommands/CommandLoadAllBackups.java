package com.minecolonies.coremod.commands.colonycommands;


public class CommandLoadAllBackups implements IMCOPCommand
{
    /**
     * What happens when the command is executed after preConditions are successful.
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSource> context)
    {
        BackUpHelper.loadAllBackups();
        context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.loadbackup.success"), true);
        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "loadAllColoniesFromBackup";
    }
}
