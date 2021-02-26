package com.minecolonies.coremod.commands.generalcommands;



public class CommandResetPlayerSupplies implements IMCOPCommand
{
    /**
     * What happens when the command is executed after preConditions are successful.
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSource> context)
    {
        final String username = StringArgumentType.getString(context, PLAYERNAME_ARG);
        final PlayerEntity player = context.getSource().getServer().getPlayerList().getPlayerByUsername(username);
        if (player == null)
        {
            if (context.getSource().getEntity() instanceof PlayerEntity)
            {
                // could not find player with given name.
                LanguageHandler.sendPlayerMessage((PlayerEntity) context.getSource().getEntity(), "com.minecolonies.command.playernotfound", username);
            }
            else
            {
                context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.playernotfound", username), true);
            }
            return 0;
        }

        player.addStat(Stats.ITEM_USED.get(ModItems.supplyChest), -1);
        context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.resetsupply"), true);
        LanguageHandler.sendPlayerMessage(player, "com.minecolonies.command.resetsupply");
        return 1;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> build()
    {
        return IMCCommand.newLiteral(getName()).then(IMCCommand.newArgument(PLAYERNAME_ARG, StringArgumentType.string()).executes(this::checkPreConditionAndExecute));
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "resetsupplies";
    }
}
