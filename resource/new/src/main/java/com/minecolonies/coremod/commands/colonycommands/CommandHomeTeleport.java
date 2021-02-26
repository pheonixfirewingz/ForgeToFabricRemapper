package com.minecolonies.coremod.commands.colonycommands;


public class CommandHomeTeleport implements IMCCommand
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

        if (!MineColonies.getConfig().getServer().canPlayerUseHomeTPCommand.get())
        {
            LanguageHandler.sendPlayerMessage((PlayerEntity) sender, "com.minecolonies.command.notenabledinconfig");
            return 0;
        }

        TeleportHelper.homeTeleport((ServerPlayerEntity) sender);
        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "home";
    }
}
