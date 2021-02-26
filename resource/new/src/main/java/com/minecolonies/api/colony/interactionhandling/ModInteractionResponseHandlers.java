package com.minecolonies.api.colony.interactionhandling;


/**
 * List of mod interaction handlers.
 */
public final class ModInteractionResponseHandlers
{
    /**
     * List of IDs.
     */
    public static final ResourceLocation STANDARD            = new ResourceLocation(Constants.MOD_ID, "standard");
    public static final ResourceLocation SIMPLE_NOTIFICATION = new ResourceLocation(Constants.MOD_ID, "simplenotification");
    public static final ResourceLocation POS                 = new ResourceLocation(Constants.MOD_ID, "pos");
    public static final ResourceLocation REQUEST             = new ResourceLocation(Constants.MOD_ID, "request");
    public static final ResourceLocation RECRUITMENT         = new ResourceLocation(Constants.MOD_ID, "recruitment");

    /**
     * List of entries.
     */
    public static InteractionResponseHandlerEntry standard;
    public static InteractionResponseHandlerEntry simpleNotification;
    public static InteractionResponseHandlerEntry pos;
    public static InteractionResponseHandlerEntry request;
    public static InteractionResponseHandlerEntry recruitment;

    private ModInteractionResponseHandlers()
    {
        throw new IllegalStateException("Tried to initialize: ModJobs but this is a Utility class.");
    }
}
