package com.minecolonies.coremod.commands.colonycommands;



public class CommandSetAbandoned implements IMCColonyOfficerCommand
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

        final int colonyID = IntegerArgumentType.getInteger(context, COLONYID_ARG);
        final IColony colony = IColonyManager.getInstance().getColonyByDimension(colonyID, context.getSource().getWorld().getDimensionKey());
        if (colony == null)
        {
            context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.colonyidnotfound", colonyID), true);
            return 0;
        }

        boolean addOfficer = false;
        if (sender != null && (colony.getPermissions().getRank((PlayerEntity) sender) == Rank.OFFICER
                                 || colony.getPermissions().getRank((PlayerEntity) sender) == Rank.OWNER))
        {
            addOfficer = true;
        }

        colony.getPermissions().setOwnerAbandoned();

        if (addOfficer)
        {
            colony.getPermissions().addPlayer(((PlayerEntity) sender).getGameProfile(), Rank.OFFICER);
        }

        context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.ownerchange.success", "[abandoned]", colony.getName()), true);
        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "setAbandoned";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> build()
    {
        return IMCCommand.newLiteral(getName())
                 .then(IMCCommand.newArgument(COLONYID_ARG, IntegerArgumentType.integer(1)).executes(this::checkPreConditionAndExecute));
    }
}
