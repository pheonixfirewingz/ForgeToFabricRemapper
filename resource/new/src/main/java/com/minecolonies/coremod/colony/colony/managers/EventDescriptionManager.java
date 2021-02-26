package com.minecolonies.coremod.colony.colony.managers;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;


/**
 * Manager for all colony related events.
 */
{
    /**
     * NBT tags
     */
    private static final String TAG_EVENT_DESC_LIST    = "event_descs_list";

    /**
     * Colony reference
     */
    private final IColony colony;

    /**
     * The event descriptions of this colony.
     */
    private final LinkedList<IColonyEventDescription> eventDescs = new LinkedList<>();

    public EventDescriptionManager(final IColony colony)
    {
        this.colony = colony;
    }

    
    public void addEventDescription(IColonyEventDescription colonyEventDescription)
    {
        if (eventDescs.size() >= MAX_COLONY_EVENTS)
        {
            eventDescs.removeFirst();
        }
        eventDescs.add(colonyEventDescription);
        if (colony.getBuildingManager().getTownHall() != null)
        {
            colony.getBuildingManager().getTownHall().markDirty();
        }
        else
        {
            colony.markDirty();
        }
    }

    
    public List<IColonyEventDescription> getEventDescriptions()
    {
        return eventDescs;
    }

    
    public void deserializeNBT(@NotNull final CompoundNBT eventManagerNBT)
    {
        final ListNBT eventDescListNBT = eventManagerNBT.getList(TAG_EVENT_DESC_LIST, Constants.NBT.TAG_COMPOUND);
        for (final INBT event : eventDescListNBT)
        {
            final CompoundNBT eventCompound = (CompoundNBT) event;
            final ResourceLocation eventTypeID = new ResourceLocation(MOD_ID, eventCompound.getString(TAG_NAME));

            final ColonyEventDescriptionTypeRegistryEntry registryEntry = MinecoloniesAPIProxy.getInstance().getColonyEventDescriptionRegistry().getValue(eventTypeID);
            if (registryEntry == null)
            {
                Log.getLogger().warn("Event is missing registryEntry!:" + eventTypeID.getPath());
                continue;
            }

            final IColonyEventDescription eventDescription = registryEntry.deserializeEventDescriptionFromNBT(eventCompound);
            eventDescs.add(eventDescription);
        }
    }

    
    public CompoundNBT serializeNBT()
    {
        final CompoundNBT eventManagerNBT = new CompoundNBT();
        final ListNBT eventDescsListNBT = new ListNBT();
        for (final IColonyEventDescription event : eventDescs)
        {
            final CompoundNBT eventNBT = event.serializeNBT();
            eventNBT.putString(TAG_NAME, event.getEventTypeId().getPath());
            eventDescsListNBT.add(eventNBT);
        }

        eventManagerNBT.put(TAG_EVENT_DESC_LIST, eventDescsListNBT);
        return eventManagerNBT;
    }
}
