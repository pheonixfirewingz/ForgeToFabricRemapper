package com.minecolonies.coremod.commands.colonycommands;



public class CommandSetDeletable implements IMCOPCommand
{

    private static final String DELETEABLE_ARG = "deletable";

    /**
     * What happens when the command is executed after preConditions are successful.
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSource> context)
    {
        final int colonyID = IntegerArgumentType.getInteger(context, COLONYID_ARG);
        final IColony colony = IColonyManager.getInstance().getColonyByDimension(colonyID, context.getSource().getWorld().getDimensionKey());
        if (colony == null)
        {
            context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.colonyidnotfound", colonyID), true);
            return 0;
        }

        colony.setCanBeAutoDeleted(BoolArgumentType.getBool(context, DELETEABLE_ARG));
        context.getSource()
          .sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.deleteable.success", colonyID, BoolArgumentType.getBool(context, DELETEABLE_ARG)), true);
        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "setDeleteable";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> build()
    {
        return IMCCommand.newLiteral(getName())
                 .then(IMCCommand.newArgument(COLONYID_ARG, IntegerArgumentType.integer(1))
                         .then(IMCCommand.newArgument(DELETEABLE_ARG, BoolArgumentType.bool()).executes(this::checkPreConditionAndExecute)));
    }
}
