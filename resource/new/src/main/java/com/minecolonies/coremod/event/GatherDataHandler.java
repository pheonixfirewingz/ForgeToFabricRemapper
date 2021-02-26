package com.minecolonies.coremod.event;


{
    /**
     * This method is for adding datagenerators. this does not run during normal client operations, only during building.
     *
     * @param event event sent when you run the "runData" gradle task
     */
    public static void dataGeneratorSetup(final GatherDataEvent event)
    {
        event.getGenerator().addProvider(new DefaultBlockLootTableProvider(event.getGenerator()));
        event.getGenerator().addProvider(new DefaultSoundProvider(event.getGenerator()));
    }
}
