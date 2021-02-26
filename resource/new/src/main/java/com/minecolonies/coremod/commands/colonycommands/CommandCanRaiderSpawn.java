package com.minecolonies.coremod.commands.colonycommands;



public class CommandCanRaiderSpawn implements IMCOPCommand
{
    private static final String CANSPAWN_ARG = "canSpawn";

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

        final boolean canHaveBarbEvents = BoolArgumentType.getBool(context, CANSPAWN_ARG);

        colony.getRaiderManager().setCanHaveRaiderEvents(canHaveBarbEvents);
        colony.markDirty();
        context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.canspawnraider.success", colony.getName(), canHaveBarbEvents), true);
        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "canSpawnRaiders";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> build()
    {
        return IMCCommand.newLiteral(getName())
                 .then(IMCCommand.newArgument(COLONYID_ARG, IntegerArgumentType.integer(1))
                         .then(IMCCommand.newArgument(CANSPAWN_ARG, BoolArgumentType.bool()).executes(this::checkPreConditionAndExecute)));
    }
}
