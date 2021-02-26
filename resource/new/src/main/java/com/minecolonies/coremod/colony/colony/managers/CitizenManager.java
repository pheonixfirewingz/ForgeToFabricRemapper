package com.minecolonies.coremod.colony.colony.managers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


{
    /**
     * Map of citizens with ID,CitizenData
     */
    @NotNull
    private final Map<Integer, ICitizenData> citizens = new HashMap<>();

    /**
     * Variables to determine if citizens have to be updated on the client side.
     */
    private boolean isCitizensDirty = false;

    /**
     * The highest citizen id.
     */
    private int topCitizenId = 0;

    /**
     * Max citizens without housing.
     */
    private int maxCitizens = 0;

    /**
     * Max citizens considering the spot in the empty guard tower.
     */
    private int potentialMaxCitizens;

    /**
     * The colony of the manager.
     */
    private final Colony colony;

    /**
     * The initial citizen spawn interval
     */
    private int respawnInterval = 30 * TICKS_SECOND;

    /**
     * Random obj.
     */
    private final Random random = new Random();

    /**
     * Whether all citizens excluding guards are sleeping
     */
    private boolean areCitizensSleeping;

    /**
     * Creates the Citizenmanager for a colony.
     *
     * @param colony the colony.
     */
    public CitizenManager(final Colony colony)
    {
        this.colony = colony;
    }

    
    public void registerCivilian(final AbstractCivilianEntity entity)
    {
        if (entity.getCivilianID() == 0 || citizens.get(entity.getCivilianID()) == null)
        {
            entity.remove();
            return;
        }

        final ICitizenData data = citizens.get(entity.getCivilianID());
        final Optional<AbstractEntityCitizen> existingCitizen = data.getEntity();

        if (!existingCitizen.isPresent())
        {
            data.setEntity(entity);
            colony.getWorld().getScoreboard().addPlayerToTeam(entity.getScoreboardName(), colony.getTeam());
            return;
        }

        if (existingCitizen.get() == entity)
        {
            colony.getWorld().getScoreboard().addPlayerToTeam(entity.getScoreboardName(), colony.getTeam());
            return;
        }

        if (entity.isAlive())
        {
            existingCitizen.get().remove();
            data.setEntity(entity);
            entity.setCivilianData(data);
            colony.getWorld().getScoreboard().addPlayerToTeam(entity.getScoreboardName(), colony.getTeam());
            return;
        }

        entity.remove();
    }

    
    public void unregisterCivilian(final AbstractCivilianEntity entity)
    {
        final ICitizenData data = citizens.get(entity.getCivilianID());
        if (data != null && data.getEntity().isPresent() && data.getEntity().get() == entity)
        {
            try
            {
                if (colony.getWorld().getScoreboard().getPlayersTeam(entity.getScoreboardName()) == colony.getTeam())
                {
                    colony.getWorld().getScoreboard().removePlayerFromTeam(entity.getScoreboardName(), colony.getTeam());
                }
            }
            catch (Exception ignored)
            {
                // For some weird reason we can get an exception here, though the exception is thrown for team != colony team which we check == on before
            }

            citizens.get(entity.getCivilianID()).setEntity(null);
        }
    }

    
    public void read(@NotNull final CompoundNBT compound)
    {
        citizens.clear();
        //  Citizens before Buildings, because Buildings track the Citizens
        citizens.putAll(NBTUtils.streamCompound(compound.getList(TAG_CITIZENS, Constants.NBT.TAG_COMPOUND))
                          .map(this::deserializeCitizen)
                          .collect(Collectors.toMap(ICitizenData::getId, Function.identity())));

        // Update child state after loading citizen data
        colony.updateHasChilds();
    }

    /**
     * Creates a citizen data from NBT
     *
     * @param compound NBT
     * @return citizen data
     */
    private ICitizenData deserializeCitizen(@NotNull final CompoundNBT compound)
    {
        final ICitizenData data = ICitizenDataManager.getInstance().createFromNBT(compound, colony);
        topCitizenId = Math.max(topCitizenId, data.getId());
        return data;
    }

    
    public void write(@NotNull final CompoundNBT compoundNBT)
    {
        @NotNull final ListNBT citizenTagList = citizens.values().stream().map(citizen -> citizen.serializeNBT()).collect(NBTUtils.toListNBT());
        compoundNBT.put(TAG_CITIZENS, citizenTagList);
    }

    
    public void sendPackets(
      @NotNull final Set<ServerPlayerEntity> closeSubscribers,
      @NotNull final Set<ServerPlayerEntity> newSubscribers)
    {
        if (isCitizensDirty || !newSubscribers.isEmpty())
        {
            final Set<ServerPlayerEntity> players = new HashSet<>();
            if (isCitizensDirty)
            {
                players.addAll(closeSubscribers);
            }
            players.addAll(newSubscribers);
            for (@NotNull final ICitizenData citizen : citizens.values())
            {
                if (citizen.getEntity().isPresent())
                {
                    if (citizen.isDirty() || !newSubscribers.isEmpty())
                    {
                        players.forEach(player -> Network.getNetwork().sendToPlayer(new ColonyViewCitizenViewMessage(colony, citizen), player));
                    }
                }
            }
        }
    }

    
    public ICitizenData spawnOrCreateCivilian(@Nullable final ICivilianData data, final World world, final BlockPos spawnPos, final boolean force)
    {
        if (!colony.getBuildingManager().hasTownHall() || (!colony.canMoveIn() && !force))
        {
            return (ICitizenData) data;
        }

        BlockPos spawnLocation = spawnPos;
        if (colony.hasTownHall() && (spawnLocation == null || spawnLocation.equals(BlockPos.ZERO)))
        {
            spawnLocation = colony.getBuildingManager().getTownHall().getPosition();
        }

        if (WorldUtil.isEntityBlockLoaded(world, spawnLocation))
        {
            BlockPos calculatedSpawn = EntityUtils.getSpawnPoint(world, spawnLocation);
            if (calculatedSpawn != null)
            {
                return spawnCitizenOnPosition((ICitizenData) data, world, force, calculatedSpawn);
            }
            else
            {
                if (colony.hasTownHall())
                {
                    calculatedSpawn = EntityUtils.getSpawnPoint(world, colony.getBuildingManager().getTownHall().getID());
                    if (calculatedSpawn != null)
                    {
                        return spawnCitizenOnPosition((ICitizenData) data, world, force, calculatedSpawn);
                    }
                }

                LanguageHandler.sendPlayersMessage(colony.getMessagePlayerEntities(),
                  "com.minecolonies.coremod.citizens.nospace",
                  spawnLocation.getX(),
                  spawnLocation.getY(),
                  spawnLocation.getZ());
            }
        }

        return (ICitizenData) data;
    }

    @NotNull
    private ICitizenData spawnCitizenOnPosition(
      @Nullable final ICitizenData data,
      @NotNull final World world,
      final boolean force,
      final BlockPos spawnPoint)
    {
        ICitizenData citizenData = data;
        if (citizenData == null)
        {
            citizenData = createAndRegisterCivilianData();

            if (getMaxCitizens() == getCitizens().size() && !force)
            {
                if (maxCitizensFromResearch() <= getCitizens().size())
                {
                    LanguageHandler.sendPlayersMessage(
                      colony.getMessagePlayerEntities(),
                      "block.blockhuttownhall.messagemaxsize.research",
                      colony.getName());
                }
                else
                {
                    LanguageHandler.sendPlayersMessage(
                      colony.getMessagePlayerEntities(),
                      "block.blockhuttownhall.messagemaxsize.config",
                      colony.getName());
                }
            }

            colony.getEventDescriptionManager().addEventDescription(new CitizenSpawnedEvent(spawnPoint, citizenData.getName()));
        }
        final EntityCitizen entity = (EntityCitizen) ModEntities.CITIZEN.create(world);

        entity.setPosition(spawnPoint.getX() + HALF_BLOCK, spawnPoint.getY() + SLIGHTLY_UP, spawnPoint.getZ() + HALF_BLOCK);
        world.addEntity(entity);

        entity.getCitizenColonyHandler().registerWithColony(citizenData.getColony().getID(), citizenData.getId());

        colony.getProgressManager()
          .progressCitizenSpawn(citizens.size(), citizens.values().stream().filter(tempDate -> tempDate.getJob() != null).collect(Collectors.toList()).size());
        markDirty();
        return citizenData;
    }

    
    public ICitizenData createAndRegisterCivilianData()
    {
        //This ensures that citizen IDs are getting reused.
        //That's needed to prevent bugs when calling IDs that are not used.
        for (int i = 1; i <= this.getCurrentCitizenCount() + 1; i++)
        {
            if (this.getCivilian(i) == null)
            {
                topCitizenId = i;
                break;
            }
        }

        final CitizenData citizenData = new CitizenData(topCitizenId, colony);
        citizenData.initForNewCivilian();
        citizens.put(citizenData.getId(), citizenData);

        return citizenData;
    }

    
    public void removeCivilian(@NotNull final ICivilianData citizen)
    {
        if (!(citizen instanceof ICitizenData))
        {
            return;
        }

        //Remove the Citizen
        citizens.remove(citizen.getId());

        for (@NotNull final IBuilding building : colony.getBuildingManager().getBuildings().values())
        {
            building.removeCitizen((ICitizenData) citizen);
        }

        colony.getWorkManager().clearWorkForCitizen((ICitizenData) citizen);

        //  Inform Subscribers of removed citizen
        for (final ServerPlayerEntity player : colony.getPackageManager().getCloseSubscribers())
        {
            Network.getNetwork().sendToPlayer(new ColonyViewRemoveCitizenMessage(colony, citizen.getId()), player);
        }

        calculateMaxCitizens();
        markDirty();
        colony.markDirty();
    }

    
    public ICitizenData getJoblessCitizen()
    {
        for (@NotNull final ICitizenData citizen : citizens.values())
        {
            if (citizen.getWorkBuilding() == null && !citizen.isChild())
            {
                return citizen;
            }
        }

        return null;
    }

    
    public void calculateMaxCitizens()
    {
        int newMaxCitizens = 0;
        int potentialMax = 0;

        for (final IBuilding b : colony.getBuildingManager().getBuildings().values())
        {
            if (b.getBuildingLevel() > 0)
            {
                if (b.hasModule(BedHandlingModule.class) && b instanceof AbstractBuildingWorker)
                {
                    newMaxCitizens += b.getAssignedCitizen().size();
                    if ((b instanceof AbstractBuildingGuards) && b.getAssignedCitizen().size() == 0 && b.getBuildingLevel() > 0)
                    {
                        potentialMax += 1;
                    }
                }
                else if (b.hasModule(LivingBuildingModule.class))
                {
                    newMaxCitizens += b.getMaxInhabitants();
                }
            }
        }
        if (getMaxCitizens() != newMaxCitizens)
        {
            setMaxCitizens(newMaxCitizens);
            setPotentialMaxCitizens(potentialMax + newMaxCitizens);
            colony.markDirty();
        }
    }

    /**
     * Spawn a brand new Citizen.
     */
    public void spawnOrCreateCitizen()
    {
        spawnOrCreateCitizen(null, colony.getWorld(), null);
    }

    @NotNull
    
    public Map<Integer, ICivilianData> getCivilianDataMap()
    {
        return Collections.unmodifiableMap(citizens);
    }

    
    public void markDirty()
    {
        colony.markDirty();
        isCitizensDirty = true;
    }

    
    public ICitizenData getCivilian(final int citizenId)
    {
        return citizens.get(citizenId);
    }

    
    public void clearDirty()
    {
        isCitizensDirty = false;
        citizens.values().forEach(ICitizenData::clearDirty);
    }

    
    public List<ICitizenData> getCitizens()
    {
        return new ArrayList<>(citizens.values());
    }

    
    public int getMaxCitizens()
    {
        return (int) Math.min(maxCitizens, Math.min(maxCitizensFromResearch(), MineColonies.getConfig().getServer().maxCitizenPerColony.get()));
    }

    
    public int getPotentialMaxCitizens()
    {
        return (int) Math.min(potentialMaxCitizens, Math.min(maxCitizensFromResearch(), MineColonies.getConfig().getServer().maxCitizenPerColony.get()));
    }

    /**
     * Get the max citizens based on the research.
     *
     * @return the max.
     */
    private double maxCitizensFromResearch()
    {
        double max = 25;
        final AdditionModifierResearchEffect effect = colony.getResearchManager().getResearchEffects().getEffect(CAP, AdditionModifierResearchEffect.class);
        if (effect != null)
        {
            max += effect.getEffect();
            // TODO research data rework
            if (max >= MineColonies.getConfig().getServer().maxCitizenPerColony.get())
            {
                return MineColonies.getConfig().getServer().maxCitizenPerColony.get();
            }
        }
        return max;
    }

    /**
     * Get the current amount of citizens, might be bigger then {@link  getMaxCitizens()}
     *
     * @return The current amount of citizens in the colony.
     */
    
    public int getCurrentCitizenCount() { return citizens.size(); }

    
    public void setMaxCitizens(final int newMaxCitizens)
    {
        this.maxCitizens = newMaxCitizens;
    }

    
    public void setPotentialMaxCitizens(final int newPotentialMax)
    {
        this.potentialMaxCitizens = newPotentialMax;
    }

    
    public void updateModifier(final String id)
    {
        for (final ICitizenData citizenData : citizens.values())
        {
            citizenData.getCitizenHappinessHandler().getModifier(id).reset();
        }
    }

    
    public void checkCitizensForHappiness()
    {
        for (final ICitizenData citizenData : citizens.values())
        {
            citizenData.getCitizenHappinessHandler().processDailyHappiness(citizenData);
        }
    }

    
    public void tickCitizenData()
    {
        this.getCitizens().forEach(ICitizenData::tick);
    }

    /**
     * Updates the citizen entities when needed and spawn the initial citizens on colony tick.
     *
     * @param colony the colony being ticked.
     */
    
    public void onColonyTick(final IColony colony)
    {
        if (colony.hasTownHall())
        {
            getCitizens().stream().filter(Objects::nonNull).forEach(ICitizenData::updateEntityIfNecessary);
        }

        //  Spawn initial Citizens
        if (colony.canMoveIn() && colony.hasTownHall() && getCitizens().size() < MineColonies.getConfig().getServer().initialCitizenAmount.get())
        {
            respawnInterval -= 500 + (SECONDS_A_MINUTE * colony.getBuildingManager().getTownHall().getBuildingLevel());

            if (respawnInterval <= 0)
            {
                respawnInterval = MineColonies.getConfig().getServer().citizenRespawnInterval.get() * TICKS_SECOND;
                int femaleCount = 0;
                for (ICitizenData citizens : getCitizens())
                {
                    femaleCount += citizens.isFemale() ? 1 : 0;
                }
                final ICitizenData newCitizen = createAndRegisterCivilianData();

                // For first citizen, give a random chance of male or female.
                if (getCitizens().size() == 1)
                {
                    newCitizen.setIsFemale(random.nextBoolean());
                }
                // Otherwise, set the new colonist's gender to whatever gender is less common.
                // Use double division to avoid getting two male colonists in a row for the first set.
                else newCitizen.setIsFemale(femaleCount < (getCitizens().size() - 1) / 2.0);

                spawnOrCreateCivilian(newCitizen, colony.getWorld(), null, true);

                colony.getEventDescriptionManager().addEventDescription(new CitizenSpawnedEvent(colony.getBuildingManager().getTownHall().getPosition(),
                      newCitizen.getName()));
            }
        }
    }

    
    public void updateCitizenMourn(final boolean mourn)
    {
        for (final ICitizenData citizen : getCitizens())
        {
            if (citizen.getEntity().isPresent() && !(citizen.getJob() instanceof AbstractJobGuard))
            {
                citizen.getEntity().get().setMourning(mourn);
            }
        }
    }

    
    public ICitizenData getRandomCitizen()
    {
        return (ICitizenData) citizens.values().toArray()[random.nextInt(citizens.values().size())];
    }

    
    public void updateCitizenSleep(final boolean sleep)
    {
        this.areCitizensSleeping = sleep;
    }

    
    public void onCitizenSleep()
    {
        for (final ICitizenData citizenData : citizens.values())
        {
            if (!(citizenData.isAsleep() || citizenData.getJob() instanceof AbstractJobGuard))
            {
                return;
            }
        }

        if (!this.areCitizensSleeping)
        {
            LanguageHandler.sendPlayersMessage(colony.getMessagePlayerEntities(), ALL_CITIZENS_ARE_SLEEPING);
        }

        this.areCitizensSleeping = true;
    }
}
