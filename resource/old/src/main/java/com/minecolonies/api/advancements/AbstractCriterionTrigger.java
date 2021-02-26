package com.minecolonies.api.advancements;

import com.google.common.collect.Maps;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.*;
import net.minecraft.client.sound.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

/**
 * The base class of a Trigger that tracks listeners and defines criterion
 * related to the trigger, and how that is fetched from JSON
 * @param <T> the class of the individual listener that will perform the trigger
 * @param <U> the criterion the individual listener is checking
 */
public abstract class AbstractCriterionTrigger<T extends CriterionListeners<U>, U extends CriterionConditions> implements Criterion<U>
{
    /** The designation for this trigger. Used by JSON advancement data. */
    private final Identifier                id;

    /** The factory method to create a listener each time this trigger is utilised */
    private final Function<PlayerAdvancementTracker, T> createNew;

    /** A map tracking each of the listeners */
    private final Map<PlayerAdvancementTracker, T>      listeners = Maps.newHashMap();

    /**
     * The trigger constructor that will define the trigger to be called
     * @param id the designation for this trigger. Used by JSON.
     * @param createNew the factory method for the listener
     */
    protected AbstractCriterionTrigger(Identifier id, Function<PlayerAdvancementTracker, T> createNew)
    {
        this.id = id;
        this.createNew = createNew;
    }

    @NotNull
    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public void addListener(@NotNull PlayerAdvancementTracker playerAdvancements, @NotNull SoundListener<U> listener)
    {
        T listeners = this.listeners.get(playerAdvancements);
        if (listeners == null)
        {
            listeners = createNew.apply(playerAdvancements);
            this.listeners.put(playerAdvancements, listeners);
        }
        listeners.add(listener);
    }

    @Override
    public void removeListener(@NotNull PlayerAdvancementTracker playerAdvancements, @NotNull SoundListener<U> listener)
    {
        final T listeners = this.listeners.get(playerAdvancements);

        if (listeners != null)
        {
            listeners.remove(listener);
            if (listeners.isEmpty())
            {
                this.listeners.remove(playerAdvancements);
            }
        }
    }

    @Nullable
    protected T getListeners(PlayerAdvancementTracker playerAdvancements)
    {
        return this.listeners.get(playerAdvancements);
    }

    @Override
    public void removeAllListeners(@NotNull PlayerAdvancementTracker playerAdvancements)
    {
        this.listeners.remove(playerAdvancements);
    }
}
