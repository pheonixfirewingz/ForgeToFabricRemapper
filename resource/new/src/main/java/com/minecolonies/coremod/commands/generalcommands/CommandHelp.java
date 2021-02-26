package com.minecolonies.coremod.commands.generalcommands;


public class CommandHelp implements IMCCommand
{

    private static final String wikiUrl    = "https://wiki.minecolonies.ldtteam.com";
    private static final String discordUrl = "https://discord.minecolonies.com";

    /**
     * What happens when the command is executed
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSource> context)
    {
        final Entity sender = context.getSource().getEntity();
        if (!(sender instanceof PlayerEntity))
        {
            return 0;
        }

        context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.help.wiki"), true);
        context.getSource().sendFeedback(((IFormattableTextComponent) ForgeHooks.newChatWithLinks(wikiUrl)).append(new StringTextComponent("\n")), true);
        context.getSource().sendFeedback(LanguageHandler.buildChatComponent("com.minecolonies.command.help.discord"), true);
        context.getSource().sendFeedback(ForgeHooks.newChatWithLinks(discordUrl), true);

        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "help";
    }
}
