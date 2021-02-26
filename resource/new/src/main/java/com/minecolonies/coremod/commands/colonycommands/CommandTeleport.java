package com.minecolonies.coremod.commands.colonycommands;



/**
 * Teleports the user to the given colony
 */
public class CommandTeleport implements IMCColonyOfficerCommand
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

        if (!IMCCommand.isPlayerOped((PlayerEntity) sender) && !MineColonies.getConfig().getServer().canPlayerUseColonyTPCommand.get())
        {
            LanguageHandler.sendPlayerMessage((PlayerEntity) sender, "com.minecolonies.command.notenabledinconfig");
            return 0;
        }

        // Colony
        final int colonyID = IntegerArgumentType.getInteger(context, COLONYID_ARG);
        final IColony colony = IColonyManager.getInstance().getColonyByDimension(colonyID, context.getSource().getWorld().getDimensionKey());
        if (colony == null)
        {
            LanguageHandler.sendPlayerMessage((PlayerEntity) sender, "com.minecolonies.command.colonyidnotfound", colonyID);
            return 0;
        }

        final ServerPlayerEntity player = (ServerPlayerEntity) sender;
        TeleportHelper.colonyTeleport(player, colony);
        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "teleport";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> build()
    {
        return IMCCommand.newLiteral(getName())
                 .then(IMCCommand.newArgument(COLONYID_ARG, IntegerArgumentType.integer(1)).executes(this::checkPreConditionAndExecute));
    }
}
