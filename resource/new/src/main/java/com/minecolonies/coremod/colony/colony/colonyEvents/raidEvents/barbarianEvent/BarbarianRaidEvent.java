package com.minecolonies.coremod.colony.colony.colonyEvents.raidEvents.barbarianEvent;



/**
 * Barbarian raid event for the colony, triggers a horde of barbarians which spawn and attack the colony.
 */
public class BarbarianRaidEvent extends HordeRaidEvent
{
    /**
     * This raids event id, registry entries use res locations as ids.
     */
    public static final ResourceLocation BARBARIAN_RAID_EVENT_TYPE_ID = new ResourceLocation(Constants.MOD_ID, "barbarian_raid");

    public BarbarianRaidEvent(IColony colony)
    {
        super(colony);
    }

    @Override
    public ResourceLocation getEventTypeID()
    {
        return BARBARIAN_RAID_EVENT_TYPE_ID;
    }

    @Override
    public void registerEntity(final Entity entity)
    {
        if (!(entity instanceof AbstractEntityMinecoloniesMob) || !entity.isAlive())
        {
            entity.remove();
            return;
        }

        if (entity instanceof EntityChiefBarbarian && boss.keySet().size() < horde.numberOfBosses)
        {
            boss.put(entity, entity.getUniqueID());
            return;
        }

        if (entity instanceof EntityArcherBarbarian && archers.keySet().size() < horde.numberOfArchers)
        {
            archers.put(entity, entity.getUniqueID());
            return;
        }

        if (entity instanceof EntityBarbarian && normal.keySet().size() < horde.numberOfRaiders)
        {
            normal.put(entity, entity.getUniqueID());
            return;
        }

        entity.remove();
    }

    @Override
    public void onEntityDeath(final LivingEntity entity)
    {
        if (!(entity instanceof AbstractEntityMinecoloniesMob))
        {
            return;
        }

        if (entity instanceof EntityChiefBarbarian)
        {
            boss.remove(entity);
            horde.numberOfBosses--;
        }

        if (entity instanceof EntityArcherBarbarian)
        {
            archers.remove(entity);
            horde.numberOfArchers--;
        }

        if (entity instanceof EntityBarbarian)
        {
            normal.remove(entity);
            horde.numberOfRaiders--;
        }

        horde.hordeSize--;

        if (horde.hordeSize == 0)
        {
            status = EventStatus.DONE;
        }

        sendHordeMessage();
    }

    /**
     * Loads the event from the nbt compound.
     *
     * @param colony   colony to load into
     * @param compound NBTcompound with saved values
     * @return the raid event.
     */
    public static BarbarianRaidEvent loadFromNBT(final IColony colony, final CompoundNBT compound)
    {
        BarbarianRaidEvent event = new BarbarianRaidEvent(colony);
        event.deserializeNBT(compound);
        return event;
    }

    @Override
    public EntityType<?> getNormalRaiderType()
    {
        return BARBARIAN;
    }

    @Override
    public EntityType<?> getArcherRaiderType()
    {
        return ARCHERBARBARIAN;
    }

    @Override
    public EntityType<?> getBossRaiderType()
    {
        return CHIEFBARBARIAN;
    }

    @Override
    protected IFormattableTextComponent getDisplayName()
    {
        return new StringTextComponent(LanguageHandler.format(RAID_BARBARIAN));
    }
}
