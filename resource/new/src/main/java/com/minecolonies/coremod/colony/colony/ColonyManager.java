package com.minecolonies.coremod.colony.colony;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;


/**
 * Singleton class that links colonies to minecraft.
 */
@SuppressWarnings("PMD.ExcessiveClassLength")
public final class ColonyManager
{
    /**
     * The list of colony views.
     */
    @NotNull
    private final Map<RegistryKey<World>, ColonyList<ColonyView>> colonyViews = new HashMap<>();

    /**
     * Recipemanager of this server.
     */
    private final IRecipeManager recipeManager = new StandardRecipeManager();

    /**
     * Pseudo unique id for the server
     */
    private UUID serverUUID = null;

    /**
     * Indicate if a schematic have just been downloaded. Client only
     */
    private boolean schematicDownloaded = false;

    /**
     * If the manager finished loading already.
     */
    private boolean loaded = false;

    
    public void createColony(@NotNull final World w, final BlockPos pos, @NotNull final PlayerEntity player, @NotNull final String style)
    {
        final IColonyManagerCapability cap = w.getCapability(COLONY_MANAGER_CAP, null).resolve().orElse(null);
        if (cap == null)
        {
            Log.getLogger().warn(MISSING_WORLD_CAP_MESSAGE);
            return;
        }

        final Colony colony = cap.createColony(w, pos);
        colony.setStyle(style);

        final String colonyName = LanguageHandler.format("com.minecolonies.coremod.gui.townHall.defaultName", player.getName().getString());
        colony.setName(colonyName);
        colony.getPermissions().setOwner(player);

        colony.getPackageManager().addImportantColonyPlayer((ServerPlayerEntity) player);
        colony.getPackageManager().addCloseSubscriber((ServerPlayerEntity) player);

        Log.getLogger().info(String.format("New Colony Id: %d by %s", colony.getID(), player.getName().getString()));

        if (colony.getWorld() == null)
        {
            Log.getLogger().error("Unable to claim chunks because of the missing world in the colony, please report this to the mod authors!", new Exception());
            return;
        }

        ChunkDataHelper.claimColonyChunks(colony.getWorld(), true, colony.getID(), colony.getCenter(), colony.getDimension());
    }

    
    public void deleteColonyByWorld(final int id, final boolean canDestroy, final World world)
    {
        deleteColony(getColonyByWorld(id, world), canDestroy);
    }

    
    public void deleteColonyByDimension(final int id, final boolean canDestroy, final RegistryKey<World> dimension)
    {
        deleteColony(getColonyByDimension(id, dimension), canDestroy);
    }

    /**
     * Delete a colony and purge all buildings and citizens.
     *
     * @param iColony    the colony to destroy.
     * @param canDestroy if the building outlines should be destroyed as well.
     */
    private void deleteColony(@Nullable final Colony iColony, final boolean canDestroy)
    {
        if (!(iColony instanceof Colony))
        {
            return;
        }

        final Colony colony = (Colony) iColony;
        final int id = colony.getID();
        final World world = colony.getWorld();

        if (world == null)
        {
            Log.getLogger().warn("Deleting Colony " + id + " errored: World is Null");
            return;
        }

        try
        {
            ChunkDataHelper.claimColonyChunks(world, false, id, colony.getCenter(), colony.getDimension());
            Log.getLogger().info("Removing citizens for " + id);
            for (final ICitizenData citizenData : new ArrayList<>(colony.getCitizenManager().getCitizens()))
            {
                Log.getLogger().info("Kill Citizen " + citizenData.getName());
                citizenData.getEntity().ifPresent(entityCitizen -> entityCitizen.onDeath(CONSOLE_DAMAGE_SOURCE));
            }

            Log.getLogger().info("Removing buildings for " + id);
            for (final IBuilding building : new ArrayList<>(colony.getBuildingManager().getBuildings().values()))
            {
                try
                {
                    final BlockPos location = building.getPosition();
                    Log.getLogger().info("Delete Building at " + location);
                    if (canDestroy)
                    {
                        building.deconstruct();
                    }
                    building.destroy();
                    if (world.getBlockState(location).getBlock() instanceof AbstractBlockHut)
                    {
                        Log.getLogger().info("Found Block, deleting " + world.getBlockState(location).getBlock());
                        world.removeBlock(location, false);
                    }
                }
                catch (final Exception ex)
                {
                    Log.getLogger().warn("Something went wrong deleting a building while deleting the colony!", ex);
                }
            }

            try
            {
                MinecraftForge.EVENT_BUS.unregister(colony.getEventHandler());
            }
            catch (final NullPointerException e)
            {
                Log.getLogger().warn("Can't unregister the event handler twice");
            }

            Log.getLogger().info("Deleting colony: " + colony.getID());

            final IColonyManagerCapability cap = world.getCapability(COLONY_MANAGER_CAP, null).resolve().orElse(null);
            if (cap == null)
            {
                Log.getLogger().warn(MISSING_WORLD_CAP_MESSAGE);
                return;
            }

            cap.deleteColony(id);
            BackUpHelper.markColonyDeleted(colony.getID(), colony.getDimension());
            colony.getImportantMessageEntityPlayers()
              .forEach(player -> Network.getNetwork().sendToPlayer(new ColonyViewRemoveMessage(colony.getID(), colony.getDimension()), (ServerPlayerEntity) player));
            Log.getLogger().info("Successfully deleted colony: " + id);
        }
        catch (final RuntimeException e)
        {
            Log.getLogger().warn("Deleting Colony " + id + " errored:", e);
        }
    }

    
    public void removeColonyView(final int id, final RegistryKey<World> dimension)
    {
        if (colonyViews.containsKey(dimension))
        {
            colonyViews.get(dimension).remove(id);
        }
    }

    
    @Nullable
    public Colony getColonyByWorld(final int id, final World world)
    {
        final IColonyManagerCapability cap = world.getCapability(COLONY_MANAGER_CAP, null).resolve().orElse(null);
        if (cap == null)
        {
            Log.getLogger().warn(MISSING_WORLD_CAP_MESSAGE);
            return null;
        }
        return cap.getColony(id);
    }

    
    @Nullable
    public Colony getColonyByDimension(final int id, final RegistryKey<World> registryKey)
    {
        final World world = ServerLifecycleHooks.getCurrentServer().getWorld(registryKey);
        if (world == null)
        {
            return null;
        }
        final IColonyManagerCapability cap = world.getCapability(COLONY_MANAGER_CAP, null).resolve().orElse(null);
        if (cap == null)
        {
            Log.getLogger().warn(MISSING_WORLD_CAP_MESSAGE);
            return null;
        }
        return cap.getColony(id);
    }

    
    public IBuilding getBuilding(@NotNull final World w, @NotNull final BlockPos pos)
    {
        @Nullable final Colony colony = getColonyByPosFromWorld(w, pos);
        if (colony != null)
        {
            final IBuilding building = colony.getBuildingManager().getBuilding(pos);
            if (building != null)
            {
                return building;
            }
        }

        //  Fallback - there might be a AbstractBuilding for this block, but it's outside of it's owning colony's radius.
        for (@NotNull final Colony otherColony : getColonies(w))
        {
            final IBuilding building = otherColony.getBuildingManager().getBuilding(pos);
            if (building != null)
            {
                return building;
            }
        }

        return null;
    }

    
    public Colony getColonyByPosFromWorld(@Nullable final World w, @NotNull final BlockPos pos)
    {
        if (w == null)
        {
            return null;
        }
        final Chunk centralChunk = w.getChunkAt(pos);
        final int id = centralChunk.getCapability(CLOSE_COLONY_CAP, null).map(IColonyTagCapability::getOwningColony).orElse(0);
        if (id == 0)
        {
            return null;
        }
        return getColonyByWorld(id, w);
    }

    
    public Colony getColonyByPosFromDim(final RegistryKey<World> registryKey, @NotNull final BlockPos pos)
    {
        return getColonyByPosFromWorld(ServerLifecycleHooks.getCurrentServer().getWorld(registryKey), pos);
    }

    
    public boolean isTooCloseToColony(@NotNull final World w, @NotNull final BlockPos pos)
    {
        return !ChunkDataHelper.canClaimChunksInRange(w,
          pos,
          Math.max(MineColonies.getConfig().getServer().minColonyDistance.get(), getConfig().getServer().initialColonySize.get()));
    }

    
    @NotNull
    public List<Colony> getColonies(@NotNull final World w)
    {
        final IColonyManagerCapability cap = w.getCapability(COLONY_MANAGER_CAP, null).resolve().orElse(null);
        if (cap == null)
        {
            Log.getLogger().warn(MISSING_WORLD_CAP_MESSAGE);
            return Collections.emptyList();
        }
        return cap.getColonies();
    }

    
    @NotNull
    public List<Colony> getAllColonies()
    {
        final List<Colony> allColonies = new ArrayList<>();
        for (final World world : ServerLifecycleHooks.getCurrentServer().getWorlds())
        {
            world.getCapability(COLONY_MANAGER_CAP, null).ifPresent(c -> allColonies.addAll(c.getColonies()));
        }
        return allColonies;
    }

    
    @NotNull
    public List<Colony> getColoniesAbandonedSince(final int abandonedSince)
    {
        final List<Colony> sortedList = new ArrayList<>();
        for (final Colony colony : getAllColonies())
        {
            if (colony.getLastContactInHours() >= abandonedSince)
            {
                sortedList.add(colony);
            }
        }

        return sortedList;
    }

    
    public IBuildingView getBuildingView(final RegistryKey<World> dimension, final BlockPos pos)
    {
        if (colonyViews.containsKey(dimension))
        {
            //  On client we will just check all known views
            for (@NotNull final ColonyView colony : colonyViews.get(dimension))
            {
                final IBuildingView building = colony.getBuilding(pos);
                if (building != null)
                {
                    return building;
                }
            }
        }

        return null;
    }

    
    @Nullable
    public Colony getIColony(@NotNull final World w, @NotNull final BlockPos pos)
    {
        return w.isRemote ? getColonyView(w, pos) : getColonyByPosFromWorld(w, pos);
    }

    /**
     * Get Colony that contains a given (x, y, z).
     *
     * @param w   World.
     * @param pos coordinates.
     * @return returns the view belonging to the colony at x, y, z.
     */
    private ColonyView getColonyView(@NotNull final World w, @NotNull final BlockPos pos)
    {
        final Chunk centralChunk = w.getChunkAt(pos);

        final int id = centralChunk.getCapability(CLOSE_COLONY_CAP, null).map(IColonyTagCapability::getOwningColony).orElse(0);
        if (id == 0)
        {
            return null;
        }
        return getColonyView(id, w.getDimensionKey());
    }

    
    @Nullable
    public Colony getClosestIColony(@NotNull final World w, @NotNull final BlockPos pos)
    {
        return w.isRemote ? getClosestColonyView(w, pos) : getClosestColony(w, pos);
    }

    
    @Nullable
    public ColonyView getClosestColonyView(@Nullable final World w, @Nullable final BlockPos pos)
    {
        if (w == null || pos == null)
        {
            return null;
        }

        final Chunk chunk = w.getChunkAt(pos);
        final IColonyTagCapability cap = chunk.getCapability(CLOSE_COLONY_CAP, null).resolve().orElse(null);
        if (cap == null)
        {
            return null;
        }

        if (cap.getOwningColony() != 0)
        {
            return getColonyView(cap.getOwningColony(), w.getDimensionKey());
        }
        else if (!cap.getAllCloseColonies().isEmpty())
        {
            @Nullable ColonyView closestColony = null;
            long closestDist = Long.MAX_VALUE;

            for (final int cId : cap.getAllCloseColonies())
            {
                final ColonyView c = getColonyView(cId, w.getDimensionKey());
                if (c != null && c.getDimension() == w.getDimensionKey())
                {
                    final long dist = c.getDistanceSquared(pos);
                    if (dist < closestDist)
                    {
                        closestColony = c;
                        closestDist = dist;
                    }
                }
            }
            return closestColony;
        }

        @Nullable ColonyView closestColony = null;
        long closestDist = Long.MAX_VALUE;

        if (colonyViews.containsKey(w.getDimensionKey()))
        {
            for (@NotNull final ColonyView c : colonyViews.get(w.getDimensionKey()))
            {
                if (c.getDimension() == w.getDimensionKey() && c.getCenter() != null)
                {
                    final long dist = c.getDistanceSquared(pos);
                    if (dist < closestDist)
                    {
                        closestColony = c;
                        closestDist = dist;
                    }
                }
            }
        }

        return closestColony;
    }

    
    public Colony getClosestColony(@NotNull final World w, @NotNull final BlockPos pos)
    {
        final Chunk chunk = w.getChunkAt(pos);
        final IColonyTagCapability cap = chunk.getCapability(CLOSE_COLONY_CAP, null).resolve().orElse(null);
        if (cap == null)
        {
            return null;
        }

        if (cap.getOwningColony() != 0)
        {
            return getColonyByWorld(cap.getOwningColony(), w);
        }
        else if (!cap.getAllCloseColonies().isEmpty())
        {
            @Nullable Colony closestColony = null;
            long closestDist = Long.MAX_VALUE;

            for (final int cId : cap.getAllCloseColonies())
            {
                final Colony c = getColonyByWorld(cId, w);
                if (c != null && c.getDimension() == w.getDimensionKey())
                {
                    final long dist = c.getDistanceSquared(pos);
                    if (dist < closestDist)
                    {
                        closestColony = c;
                        closestDist = dist;
                    }
                }
            }
            return closestColony;
        }

        @Nullable Colony closestColony = null;
        long closestDist = Long.MAX_VALUE;

        for (@NotNull final Colony c : getColonies(w))
        {
            if (c.getDimension() == w.getDimensionKey())
            {
                final long dist = c.getDistanceSquared(pos);
                if (dist < closestDist)
                {
                    closestColony = c;
                    closestDist = dist;
                }
            }
        }

        return closestColony;
    }

    
    @Nullable
    public Colony getIColonyByOwner(@NotNull final World w, @NotNull final PlayerEntity owner)
    {
        return getIColonyByOwner(w, w.isRemote ? owner.getUniqueID() : owner.getGameProfile().getId());
    }

    
    @Nullable
    public Colony getIColonyByOwner(@NotNull final World w, final UUID owner)
    {
        return w.isRemote ? getColonyViewByOwner(owner, w.getDimensionKey()) : getColonyByOwner(owner);
    }

    /**
     * Returns a ColonyView with specific owner.
     *
     * @param owner     UUID of the owner.
     * @param dimension the dimension id.
     * @return ColonyView.
     */
    private Colony getColonyViewByOwner(final UUID owner, final RegistryKey<World> dimension)
    {
        if (colonyViews.containsKey(dimension))
        {
            for (@NotNull final ColonyView c : colonyViews.get(dimension))
            {
                final Player p = c.getPlayers().get(owner);
                if (p != null && p.getRank().equals(Rank.OWNER))
                {
                    return c;
                }
            }
        }

        return null;
    }

    @Nullable
    private Colony getColonyByOwner(@Nullable final UUID owner)
    {
        if (owner == null)
        {
            return null;
        }

        return getAllColonies().stream()
                 .filter(c -> owner.equals(c.getPermissions().getOwner()))
                 .findFirst()
                 .orElse(null);
    }

    
    public int getMinimumDistanceBetweenTownHalls()
    {
        //  [TownHall](Radius)+(Padding)+(Radius)[TownHall]
        return getConfig().getServer().minColonyDistance.get() * BLOCKS_PER_CHUNK;
    }

    
    public void onServerTick(@NotNull final TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            for (@NotNull final Colony c : getAllColonies())
            {
                c.onServerTick(event);
            }
        }
    }

    
    public void write(@NotNull final CompoundNBT compound)
    {
        //Get the colonies NBT tags and store them in a ListNBT.
        if (serverUUID != null)
        {
            compound.putUniqueId(TAG_UUID, serverUUID);
        }

        final CompoundNBT compCompound = new CompoundNBT();
        compatibilityManager.write(compCompound);
        compound.put(TAG_COMPATABILITY_MANAGER, compCompound);

        compound.putBoolean(TAG_DISTANCE, true);
        final CompoundNBT recipeCompound = new CompoundNBT();
        recipeManager.write(recipeCompound);

        compound.put(RECIPE_MANAGER_TAG, recipeCompound);
    }

    
    public void read(@NotNull final CompoundNBT compound)
    {
        if (compound.hasUniqueId(TAG_UUID))
        {
            serverUUID = compound.getUniqueId(TAG_UUID);
        }

        if (compound.keySet().contains(TAG_COMPATABILITY_MANAGER))
        {
            compatibilityManager.read(compound.getCompound(TAG_COMPATABILITY_MANAGER));
        }

        recipeManager.read(compound.getCompound(RECIPE_MANAGER_TAG));
    }

    
    public void onClientTick(@NotNull final TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().world == null && !colonyViews.isEmpty())
        {
            //  Player has left the game, clear the Colony View cache
            colonyViews.clear();
        }

        if (ModTags.tagsLoaded && !compatibilityManager.isDiscoveredAlready() && ItemStackUtils.ISFOOD != null && FurnaceRecipes.getInstance().loaded())
        {
            compatibilityManager.discover(false);
        }
    }

    
    public void onWorldTick(@NotNull final TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            getColonies(event.world).forEach(c -> c.onWorldTick(event));
        }

        if (ModTags.tagsLoaded && !compatibilityManager.isDiscoveredAlready() && FurnaceRecipes.getInstance().loaded())
        {
            compatibilityManager.discover(true);
        }
    }

    
    public void onWorldLoad(@NotNull final World world)
    {
        ModTagsInitializer.init();
        if (!world.isRemote)
        {
            if (!loaded)
            {
                @NotNull final File file = BackUpHelper.getSaveLocation((ServerWorld) world);
                @Nullable final CompoundNBT data = BackUpHelper.loadNBTFromPath(file);
                if (data != null)
                {
                    Log.getLogger().info("Loading Minecolonies Data");
                    read(data);
                    Log.getLogger().info("Load Complete");
                }

                if (serverUUID == null)
                {
                    serverUUID = UUID.randomUUID();
                    Log.getLogger().info(String.format("New Server UUID %s", serverUUID));
                }
                else
                {
                    Log.getLogger().info(String.format("Server UUID %s", serverUUID));
                }
                loaded = true;
                BackUpHelper.loadMissingColonies();
            }

            for (@NotNull final Colony c : getColonies(world))
            {
                c.onWorldLoad(world);
            }
        }
    }

    
    public UUID getServerUUID()
    {
        return serverUUID;
    }

    
    public void setServerUUID(final UUID uuid)
    {
        serverUUID = uuid;
    }

    
    public void onWorldUnload(@NotNull final World world)
    {
        if (!world.isRemote)
        {
            for (@NotNull final Colony c : getColonies(world))
            {
                c.onWorldUnload(world);
            }
            if (loaded)
            {
                BackUpHelper.backupColonyData();
                recipeManager.reset();
                loaded = false;
            }
        }
    }

    
    public void handleColonyViewMessage(final int colonyId, @NotNull final PacketBuffer colonyData, @NotNull final World world, final boolean isNewSubscription, final RegistryKey<World> dim)
    {
        ColonyView view = getColonyView(colonyId, dim);
        if (view == null)
        {
            view = ColonyView.createFromNetwork(colonyId);
            if (colonyViews.containsKey(dim))
            {
                colonyViews.get(dim).add(view);
            }
            else
            {
                final ColonyList<ColonyView> list = new ColonyList<>();
                list.add(view);
                colonyViews.put(dim, list);
            }
        }
        view.handleColonyViewMessage(colonyData, world, isNewSubscription);
    }

    
    public ColonyView getColonyView(final int id, final RegistryKey<World> dimension)
    {
        if (colonyViews.containsKey(dimension))
        {
            return colonyViews.get(dimension).get(id);
        }
        return null;
    }

    
    public void handlePermissionsViewMessage(final int colonyID, @NotNull final PacketBuffer data, final RegistryKey<World> dim)
    {
        final ColonyView view = getColonyView(colonyID, dim);
        if (view == null)
        {
            Log.getLogger().error(String.format("Colony view does not exist for ID  %d", colonyID), new Exception());
        }
        else
        {
            view.handlePermissionsViewMessage(data);
        }
    }

    
    public void handleColonyViewCitizensMessage(final int colonyId, final int citizenId, final PacketBuffer buf, final RegistryKey<World> dim)
    {
        final ColonyView view = getColonyView(colonyId, dim);
        if (view == null)
        {
            return;
        }
        view.handleColonyViewCitizensMessage(citizenId, buf);
    }

    
    public void handleColonyViewWorkOrderMessage(final int colonyId, final PacketBuffer buf, final RegistryKey<World> dim)
    {
        final ColonyView view = getColonyView(colonyId, dim);
        if (view == null)
        {
            return;
        }
        view.handleColonyViewWorkOrderMessage(buf);
    }

    
    public void handleColonyViewRemoveCitizenMessage(final int colonyId, final int citizenId, final RegistryKey<World> dim)
    {
        final ColonyView view = getColonyView(colonyId, dim);
        if (view != null)
        {
            //  Can legitimately be NULL, because (to keep the code simple and fast), it is
            //  possible to receive a 'remove' notice before receiving the View.
            view.handleColonyViewRemoveCitizenMessage(citizenId);
        }
    }

    
    public void handleColonyBuildingViewMessage(final int colonyId, final BlockPos buildingId, @NotNull final PacketBuffer buf, final RegistryKey<World> dim)
    {
        final ColonyView view = getColonyView(colonyId, dim);
        if (view != null)
        {
            view.handleColonyBuildingViewMessage(buildingId, buf);
        }
        else
        {
            Log.getLogger().error(String.format("Colony view does not exist for ID  %d", colonyId), new Exception());
        }
    }

    
    public void handleColonyViewRemoveBuildingMessage(final int colonyId, final BlockPos buildingId, final RegistryKey<World> dim)
    {
        final ColonyView view = getColonyView(colonyId, dim);
        if (view != null)
        {
            //  Can legitimately be NULL, because (to keep the code simple and fast), it is
            //  possible to receive a 'remove' notice before receiving the View.
            view.handleColonyViewRemoveBuildingMessage(buildingId);
        }
    }

    
    public void handleColonyViewRemoveWorkOrderMessage(final int colonyId, final int workOrderId, final RegistryKey<World> dim)
    {
        final ColonyView view = getColonyView(colonyId, dim);
        if (view != null)
        {
            //  Can legitimately be NULL, because (to keep the code simple and fast), it is
            //  possible to receive a 'remove' notice before receiving the View.
            view.handleColonyViewRemoveWorkOrderMessage(workOrderId);
        }
    }

    
    public boolean isSchematicDownloaded()
    {
        return schematicDownloaded;
    }

    
    public void setSchematicDownloaded(final boolean downloaded)
    {
        schematicDownloaded = downloaded;
    }

    
    public boolean isCoordinateInAnyColony(@NotNull final World world, final BlockPos pos)
    {
        final Chunk centralChunk = world.getChunkAt(pos);
        return centralChunk.getCapability(CLOSE_COLONY_CAP, null).map(IColonyTagCapability::getOwningColony).orElse(0) != 0;
    }

    
    public ICompatibilityManager getCompatibilityManager()
    {
        return compatibilityManager;
    }

    
    public IRecipeManager getRecipeManager()
    {
        return recipeManager;
    }

    
    public int getTopColonyId()
    {
        int top = 0;
        for (final World world : ServerLifecycleHooks.getCurrentServer().getWorlds())
        {
            final int tempTop = world.getCapability(COLONY_MANAGER_CAP, null).map(IColonyManagerCapability::getTopID).orElse(0);
            if (tempTop > top)
            {
                top = tempTop;
            }
        }
        return top;
    }

    
    public void resetColonyViews()
    {
        colonyViews.clear();
    }
}
