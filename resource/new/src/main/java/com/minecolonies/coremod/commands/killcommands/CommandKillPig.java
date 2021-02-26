package com.minecolonies.coremod.commands.killcommands;


public class CommandKillPig implements IMCOPCommand
{
    int entitiesKilled = 0;

    /**
     * What happens when the command is executed
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSource> context)
    {
        entitiesKilled = 0;

        context.getSource().getWorld().getEntities(EntityType.PIG, entity -> true).forEach(entity ->
        {
            entity.remove();
            entitiesKilled++;
        });
        context.getSource().sendFeedback(new StringTextComponent(entitiesKilled + " entities killed"), true);
        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "pig";
    }
}