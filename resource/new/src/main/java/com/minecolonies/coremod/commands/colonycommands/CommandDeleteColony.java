package com.minecolonies.coremod.commands.colonycommands;



public class CommandDeleteColony implements IMCColonyOfficerCommand
{
    /**
     * Formatable string to use the command
     */
    private static final String DELETE_BUILDNGS_ARG = "delete Buildings?";

    /**
     * What happens when the command is executed after preConditions are successful.
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSource> context)
    {
        if (!context.getSource().hasPermissionLevel(OP_PERM_LEVEL) && !MineColonies.getConfig().getServer().canPlayerUseDeleteColonyCommand.get())
        {
            context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.notenabledinconfig"), true);
            return 0;
        }

        // Colony
        final int colonyID = IntegerArgumentType.getInteger(context, COLONYID_ARG);
        final IColony colony = IColonyManager.getInstance().getColonyByDimension(colonyID, context.getSource().getWorld().getDimensionKey());
        if (colony == null)
        {
            context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.colonyidnotfound", colonyID), true);
            return 0;
        }

        final boolean deleteBuildings = BoolArgumentType.getBool(context, DELETE_BUILDNGS_ARG);

        BackUpHelper.backupColonyData();
        IColonyManager.getInstance().deleteColonyByDimension(colonyID, deleteBuildings, context.getSource().getWorld().getDimensionKey());
        context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.delete.success", colony.getName()), true);
        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "delete";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> build()
    {
        String[] s = new String[2];
        s[0] = "true delete buildings";
        s[1] = "false keep buildings";

        return IMCCommand.newLiteral(getName())
                 .then(IMCCommand.newArgument(COLONYID_ARG, IntegerArgumentType.integer(1))
                         .then(IMCCommand.newArgument(DELETE_BUILDNGS_ARG, BoolArgumentType.bool())
                                 .suggests((ctx, builder) -> ISuggestionProvider.suggest(s, builder))
                                 .then(IMCCommand.newArgument("", StringArgumentType.string())
                                         .then(IMCCommand.newArgument("", StringArgumentType.string())
                                                 .executes(this::checkPreConditionAndExecute)))));
    }
}
