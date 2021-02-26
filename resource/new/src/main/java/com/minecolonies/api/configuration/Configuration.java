package com.minecolonies.api.configuration;


/**
 * Mod root configuration.
 */
{
    /**
     * Loaded clientside, not synced
     */
    private final ClientConfiguration clientConfig;

    /**
     * Loaded serverside, synced on connection
     */
    private final ServerConfiguration serverConfig;

    /**
     * Builds configuration tree.
     */
    public Configuration()
    {
        final Pair<ClientConfiguration, ForgeConfigSpec> cli = new ForgeConfigSpec.Builder().configure(ClientConfiguration::new);
        final Pair<ServerConfiguration, ForgeConfigSpec> ser = new ForgeConfigSpec.Builder().configure(ServerConfiguration::new);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, cli.getRight());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ser.getRight());

        clientConfig = cli.getLeft();
        serverConfig = ser.getLeft();
    }

    public ClientConfiguration getClient()
    {
        return clientConfig;
    }

    public ServerConfiguration getServer()
    {
        return serverConfig;
    }
}
