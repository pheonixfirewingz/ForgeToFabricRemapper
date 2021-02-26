package com.minecolonies.coremod.commands.killcommands;


public class CommandKillRaider implements IMCOPCommand
{
    private int entitiesKilled = 0;

    /**
     * What happens when the command is executed
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSource> context)
    {
        entitiesKilled = 0;

        context.getSource().getWorld().getEntities().forEach(entity ->
        {
            if (entity instanceof AbstractEntityMinecoloniesMob)
            {
                final AbstractEntityMinecoloniesMob mob = (AbstractEntityMinecoloniesMob) entity;
                mob.onDeath(new DamageSource("despawn"));
                mob.remove();

                final IColonyEvent event = mob.getColony().getEventManager().getEventByID(mob.getEventID());

                if (event != null)
                {
                    event.setStatus(EventStatus.DONE);
                }

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
        return "raider";
    }
}
