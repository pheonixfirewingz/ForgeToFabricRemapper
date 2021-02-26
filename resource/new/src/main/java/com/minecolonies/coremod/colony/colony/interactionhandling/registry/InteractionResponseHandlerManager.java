package com.minecolonies.coremod.colony.colony.interactionhandling.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manager creating and loading an instance of the interactionResponseHandler from NBT.
 */
public final class InteractionResponseHandlerManager implements IInteractionResponseHandlerDataManager
{
    @Nullable
    @Override
    public IInteractionResponseHandler createFrom(@NotNull final ICitizen citizen, @NotNull final CompoundNBT compound)
    {
        final ResourceLocation handlerType =
          compound.keySet().contains(NbtTagConstants.TAG_HANDLER_TYPE)
            ? new ResourceLocation(Constants.MOD_ID, compound.getString(NbtTagConstants.TAG_HANDLER_TYPE))
            : ModInteractionResponseHandlers.STANDARD;
        final IInteractionResponseHandler handler = IInteractionResponseHandlerRegistry.getInstance().getValue(handlerType).getProducer().apply(citizen);
        if (handler != null)
        {
            try
            {
                handler.deserializeNBT(compound);
            }
            catch (final RuntimeException ex)
            {
                Log.getLogger().error(String.format("An Interaction %s has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
                  handlerType), ex);
                return null;
            }
        }
        else
        {
            Log.getLogger().warn(String.format("Unknown Interaction type '%s' or missing constructor of proper format.", handlerType));
        }

        return handler;
    }
}
