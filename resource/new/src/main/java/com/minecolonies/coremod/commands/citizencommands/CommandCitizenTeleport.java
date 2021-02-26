package com.minecolonies.coremod.commands.citizencommands;


import java.util.Optional;


/**
 * Teleports a chosen citizen to a chosen positoin..
 */
public class CommandCitizenTeleport implements IMCColonyOfficerCommand
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
        // Colony
        final int colonyID = IntegerArgumentType.getInteger(context, COLONYID_ARG);
        final IColony colony = IColonyManager.getInstance().getColonyByDimension(colonyID, sender == null ? World.OVERWORLD : context.getSource().getWorld().getDimensionKey());
        if (colony == null)
        {
            context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.colonyidnotfound", colonyID), true);
            return 0;
        }

        final ICitizenData citizenData = colony.getCitizenManager().getCivilian(IntegerArgumentType.getInteger(context, CITIZENID_ARG));

        if (citizenData == null)
        {
            context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.citizeninfo.notfound"), true);
            return 0;
        }

        final Optional<AbstractEntityCitizen> optionalEntityCitizen = citizenData.getEntity();

        if (!optionalEntityCitizen.isPresent())
        {
            context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.citizeninfo.notloaded"), true);
            return 0;
        }

        final AbstractEntityCitizen entityCitizen = optionalEntityCitizen.get();
        final ILocationArgument targetLocation = Vec3Argument.getLocation(context, POS_ARG);
        final BlockPos targetPos = targetLocation.getBlockPos(context.getSource());

        if (context.getSource().getWorld() == entityCitizen.world)
        {
            entityCitizen.setLocationAndAngles(targetPos.getX(), targetPos.getY(), targetPos.getZ(), entityCitizen.getYaw(1F), entityCitizen.getPitch(1F));
            entityCitizen.getNavigator().clearPath();
        }

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
                 .then(IMCCommand.newArgument(COLONYID_ARG, IntegerArgumentType.integer(1))
                         .then(IMCCommand.newArgument(CITIZENID_ARG, IntegerArgumentType.integer(1))
                                 .then(IMCCommand.newArgument(POS_ARG, Vec3Argument.vec3())
                                         .executes(this::checkPreConditionAndExecute))));
    }
}