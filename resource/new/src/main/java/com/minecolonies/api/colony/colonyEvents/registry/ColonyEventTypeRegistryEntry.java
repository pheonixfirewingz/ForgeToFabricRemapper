package com.minecolonies.api.colony.colonyEvents.registry;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

/**
 * This is the colonies event registry entry class, used for registering any colony related events. Takes a function of colony, nbt to create the right event object.
 */
public class ColonyEventTypeRegistryEntry extends ForgeRegistryEntry<ColonyEventTypeRegistryEntry>
{
    /**
     * Function for creating the event objects.
     */
    private final BiFunction<IColony, CompoundNBT, IColonyEvent> eventCreator;

    /**
     * Creates a new registry entry for the given function and registry name
     *
     * @param eventCreator the event creator.
     * @param registryID   the registry id.
     */
    public ColonyEventTypeRegistryEntry(@NotNull final BiFunction<IColony, CompoundNBT, IColonyEvent> eventCreator, @NotNull final ResourceLocation registryID)
    {
        if (registryID.getPath().isEmpty())
        {
            Log.getLogger().warn("Created empty registry empty for event, supply a name for it!");
        }

        this.eventCreator = eventCreator;
        setRegistryName(registryID);
    }

    /**
     * Deserializes the event from nbt.
     * 
     * @param colony   the colony this event is part of.
     * @param compound the nbt to deserialize the event from.
     * @return the deserialized event.
     */
    public IColonyEvent deserializeEvent(@Nonnull final IColony colony, @Nonnull final CompoundNBT compound)
    {
        return eventCreator.apply(colony, compound);
    }
}
