package com.minecolonies.coremod.event;

import org.jetbrains.annotations.NotNull;

/**
 * Event handler used to catch various forge events.
 */
{
    @SubscribeEvent
    public static void onServerTick(final TickEvent.ServerTickEvent event)
    {
        IColonyManager.getInstance().onServerTick(event);
    }

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        IColonyManager.getInstance().onClientTick(event);
    }

    @SubscribeEvent
    public static void onWorldTick(final TickEvent.WorldTickEvent event)
    {
        IColonyManager.getInstance().onWorldTick(event);
    }

    @SubscribeEvent
    public static void onPlayerLogin(@NotNull final PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayerEntity)
        {
            Network.getNetwork().sendToPlayer(new ServerUUIDMessage(), (ServerPlayerEntity) event.getPlayer());
            Network.getNetwork().sendToPlayer(new ColonyStylesMessage(), (ServerPlayerEntity) event.getPlayer());

            // This automatically reloads the owner of the colony if failed.
            IColonyManager.getInstance().getIColonyByOwner(((ServerPlayerEntity) event.getPlayer()).getServerWorld(), event.getPlayer());
            //ColonyManager.syncAllColoniesAchievements();
        }
    }

    @SubscribeEvent
    public static void onServerAboutToStart(@NotNull final AddReloadListenerEvent event)
    {
        event.addListener(new CrafterRecipeListener());
    }

    public static void onServerStopped(final FMLServerStoppingEvent event)
    {
        Pathfinding.shutdown();
    }
}
