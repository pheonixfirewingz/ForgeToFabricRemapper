package com.minecolonies.coremod.commands.killcommands;


public class CommandKillAnimal implements IMCOPCommand
{
    private int entitiesKilled = 0;

    /**
     * What happens when the command is executed
     *
     * @param context the context of the command execution
     */
    @Override
    public final int onExecute(final CommandContext<CommandSource> context)
    {
        entitiesKilled = 0;

        context.getSource().getWorld().getEntities().forEach(entity ->
        {
            if (entity instanceof AnimalEntity)
            {
                entity.remove();
                entitiesKilled++;
            }
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
        return "animals";
    }
}
