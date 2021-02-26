package com.minecolonies.coremod.colony.colony;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


/**
 * Client side representation of the Colony.
 */
@SuppressWarnings("PMD.ExcessiveClassLength")
public final class ColonyView
{
    /**
     * Max allowed CompoundNBT in bytes
     */
    private static final int REQUEST_MANAGER_MAX_SIZE = 700000;

    //  General Attributes
    private final int                            id;
    private final Map<Integer, WorkOrderView>    workOrders  = new HashMap<>();
    //  Administration/permissions
    @NotNull
    private final PermissionsView                permissions = new PermissionsView();
    @NotNull
    private final Map<BlockPos, BuildingView>   buildings   = new HashMap<>();
    //  Citizenry
    @NotNull
    private final Map<nteger, CitizenDataView> citizens    = new HashMap<>();
    private       Map<Integer, VisitorViewData> visitors    = new HashMap<>();
    private       String                         name        = "Unknown";
    private       RegistryKey<World>                            dimensionId;

    /**
     * Colony team color.
     */
    private TextFormatting teamColonyColor = TextFormatting.WHITE;

    /**
     * The colony flag (set to plain white as default)
     */
    private ListNBT        colonyFlag      = new BannerPattern.Builder()
        .setPatternWithColor(BannerPattern.BASE, DyeColor.WHITE)
        .buildNBT();

    private BlockPos       center          = BlockPos.ZERO;

    /**
     * Defines if workers are hired manually or automatically.
     */
    private boolean manualHiring = false;

    /**
     * Defines if workers are housed manually or automatically.
     */
    private boolean manualHousing = false;

    /**
     * Defines if citizens can move in or not.
     */
    private boolean moveIn = true;

    //  Buildings
    @Nullable
    private ITownHallView townHall;

    /**
     * The max citizen count.
     */
    private int citizenCount = 0;

    /**
     * The max citizen count considering guard towers.
     */
    private int citizenCountWithEmptyGuardTowers = 0;

    /**
     * Check if the colony has a warehouse.
     */
    private boolean hasColonyWarehouse;

    /**
     * Last barbarian spawnpoints.
     */
    private final List<BlockPos> lastSpawnPoints = new ArrayList<>();

    /**
     * The Positions which players can freely interact.
     */
    private final Set<BlockPos> freePositions = new HashSet<>();

    /**
     * The Blocks which players can freely interact with.
     */
    private final Set<Block> freeBlocks = new HashSet<>();

    /**
     * The Set of waypoints.
     */
    private final Map<BlockPos, BlockState> wayPoints = new HashMap<>();

    /**
     * The overall happiness of the colony.
     */
    private double overallHappiness = 5;

    /**
     * The hours the colony is without contact with its players.
     */
    private int lastContactInHours = 0;

    /**
     * The request manager on the colony view side.
     */
    private IRequestManager requestManager;

    /**
     * Wether the colony is raided
     */
    private boolean isUnderRaid;

    /**
     * The world.
     */
    private World world;

    /**
     * Print progress.
     */
    private boolean printProgress;

    /**
     * The last use time of the mercenaries.
     */
    private long mercenaryLastUseTime = 0;

    /**
     * The default style.
     */
    private String style = "";

    /**
     * The list of allies.
     */
    private List<CompactColonyReference> allies;

    /**
     * The list of feuds.
     */
    private List<CompactColonyReference> feuds;

    /**
     * The research effects of the colony.
     */
    private final ResearchManager manager = new ResearchManager();

    /**
     * Whether spies are active and highlight enemy positions.
     */
    private boolean spiesEnabled;

    /**
     * Base constructor for a colony.
     *
     * @param id The current id for the colony.
     */
    private ColonyView(final int id)
    {
        this.id = id;
    }

    /**
     * Create a ColonyView given a UUID and CompoundNBT.
     *
     * @param id Id of the colony view.
     * @return the new colony view.
     */
    @NotNull
    public static ColonyView createFromNetwork(final int id)
    {
        return new ColonyView(id);
    }

    /**
     * Populate an NBT compound for a network packet representing a ColonyView.
     *
     * @param colony            Colony to write data about.
     * @param buf               {@link PacketBuffer} to write data in.
     * @param hasNewSubscribers true if there is a new subscription.
     */
    public static void serializeNetworkData(@NotNull Colony colony, @NotNull PacketBuffer buf, boolean hasNewSubscribers)
    {
        //  General Attributes
        buf.writeString(colony.getName());
        buf.writeString(colony.getDimension().getLocation().toString());
        buf.writeBlockPos(colony.getCenter());
        buf.writeBoolean(colony.isManualHiring());
        //  Citizenry
        buf.writeInt(colony.getCitizenManager().getMaxCitizens());
        buf.writeInt(colony.getCitizenManager().getPotentialMaxCitizens());

        final Set<Block> freeBlocks = colony.getFreeBlocks();
        final Set<BlockPos> freePos = colony.getFreePositions();
        final Map<BlockPos, BlockState> waypoints = colony.getWayPoints();

        buf.writeInt(freeBlocks.size());
        for (final Block block : freeBlocks)
        {
            buf.writeString(block.getRegistryName().toString());
        }

        buf.writeInt(freePos.size());
        for (final BlockPos block : freePos)
        {
            buf.writeBlockPos(block);
        }
        buf.writeDouble(colony.getOverallHappiness());
        buf.writeBoolean(colony.hasWarehouse());

        buf.writeInt(waypoints.size());
        for (final Map.Entry<BlockPos, BlockState> block : waypoints.entrySet())
        {
            buf.writeBlockPos(block.getKey());
            buf.writeInt(Block.getStateId(block.getValue()));
        }

        buf.writeInt(colony.getLastContactInHours());
        buf.writeBoolean(colony.isManualHousing());
        buf.writeBoolean(colony.canMoveIn());
        //  Citizens are sent as a separate packet

        if (colony.getRequestManager() != null && (colony.getRequestManager().isDirty() || hasNewSubscribers))
        {
            final int preSize = buf.writerIndex();
            buf.writeBoolean(true);
            colony.getRequestManager().serialize(StandardFactoryController.getInstance(), buf);
            final int postSize = buf.writerIndex();
            if ((postSize - preSize) >= ColonyView.REQUEST_MANAGER_MAX_SIZE)
            {
                Log.getLogger().warn("Colony " + colony.getID() + " has a very big memory imprint, this could be a memory leak, please contact the mod author!");
            }
        }
        else
        {
            buf.writeBoolean(false);
        }

        buf.writeInt(colony.getRaiderManager().getLastSpawnPoints().size());
        for (final BlockPos block : colony.getRaiderManager().getLastSpawnPoints())
        {
            buf.writeBlockPos(block);
        }

        buf.writeInt(colony.getTeamColonyColor().ordinal());

        CompoundNBT flagNBT = new CompoundNBT();
        flagNBT.put(TAG_BANNER_PATTERNS, colony.getColonyFlag());
        buf.writeCompoundTag(flagNBT);

        buf.writeBoolean(colony.getProgressManager().isPrintingProgress());

        buf.writeLong(colony.getMercenaryUseTime());

        buf.writeString(colony.getStyle());
        buf.writeBoolean(colony.getRaiderManager().isRaided());
        buf.writeBoolean(colony.getRaiderManager().areSpiesEnabled());
        final List<IColony> allies = new ArrayList<>();
        for (final Player player : colony.getPermissions().getPlayersByRank(Rank.OFFICER))
        {
            final IColony col = IColonyManager.getInstance().getIColonyByOwner(colony.getWorld(), player.getID());
            if (col != null)
            {
                for (final Player owner : colony.getPermissions().getPlayersByRank(Rank.OWNER))
                {
                    if (col.getPermissions().getRank(owner.getID()) == Rank.OFFICER)
                    {
                        allies.add(col);
                    }
                }
            }
        }

        buf.writeInt(allies.size());
        for (final IColony col : allies)
        {
            buf.writeString(col.getName());
            buf.writeBlockPos(col.getCenter());
            buf.writeInt(col.getID());
            buf.writeBoolean(col.hasTownHall());
            buf.writeString(col.getDimension().getLocation().toString());
        }

        final List<IColony> feuds = new ArrayList<>();
        for (final Player player : colony.getPermissions().getPlayersByRank(Rank.HOSTILE))
        {
            final IColony col = IColonyManager.getInstance().getIColonyByOwner(colony.getWorld(), player.getID());
            if (col != null)
            {
                for (final Player owner : colony.getPermissions().getPlayersByRank(Rank.OWNER))
                {
                    if (col.getPermissions().getRank(owner.getID()) == Rank.HOSTILE)
                    {
                        feuds.add(col);
                    }
                }
            }
        }

        buf.writeInt(feuds.size());
        for (final IColony col : feuds)
        {
            buf.writeString(col.getName());
            buf.writeBlockPos(col.getCenter());
            buf.writeInt(col.getID());
            buf.writeString(col.getDimension().getLocation().toString());
        }

        final CompoundNBT treeTag = new CompoundNBT();
        colony.getResearchManager().writeToNBT(treeTag);
        buf.writeCompoundTag(treeTag);
    }

    /**
     * Get a copy of the freePositions list.
     *
     * @return the list of free to interact positions.
     */
    
    public List<BlockPos> getFreePositions()
    {
        return new ArrayList<>(freePositions);
    }

    /**
     * Get a copy of the freeBlocks list.
     *
     * @return the list of free to interact blocks.
     */
    
    public List<Block> getFreeBlocks()
    {
        return new ArrayList<>(freeBlocks);
    }

    /**
     * Add a new free to interact position.
     *
     * @param pos position to add.
     */
    
    public void addFreePosition(@NotNull final BlockPos pos)
    {
        freePositions.add(pos);
    }

    /**
     * Add a new free to interact block.
     *
     * @param block block to add.
     */
    
    public void addFreeBlock(@NotNull final Block block)
    {
        freeBlocks.add(block);
    }

    /**
     * Remove a free to interact position.
     *
     * @param pos position to remove.
     */
    
    public void removeFreePosition(@NotNull final BlockPos pos)
    {
        freePositions.remove(pos);
    }

    /**
     * Remove a free to interact block.
     *
     * @param block state to remove.
     */
    
    public void removeFreeBlock(@NotNull final Block block)
    {
        freeBlocks.remove(block);
    }

    
    public void setCanBeAutoDeleted(final boolean canBeDeleted)
    {

    }

    /**
     * Returns the dimension ID of the view.
     *
     * @return dimension ID of the view.
     */
    
    public RegistryKey<World> getDimension()
    {
        return dimensionId;
    }

    /**
     * Getter for the manual hiring or not.
     *
     * @return the boolean true or false.
     */
    
    public boolean isManualHiring()
    {
        return manualHiring;
    }

    /**
     * Sets if workers should be hired manually.
     *
     * @param manualHiring true if manually.
     */
    
    public void setManualHiring(final boolean manualHiring)
    {
        this.manualHiring = manualHiring;
    }

    
    public CompoundNBT write(final CompoundNBT colonyCompound)
    {
        return new CompoundNBT();
    }

    
    public void read(final CompoundNBT compound)
    {
        //Noop
    }

    /**
     * Getter for the manual housing or not.
     *
     * @return the boolean true or false.
     */
    
    public boolean isManualHousing()
    {
        return manualHousing;
    }

    /**
     * Sets if houses should be assigned manually.
     *
     * @param manualHousing true if manually.
     */
    
    public void setManualHousing(final boolean manualHousing)
    {
        this.manualHousing = manualHousing;
    }

    
    public void addWayPoint(final BlockPos pos, final BlockState newWayPointState)
    {

    }

    
    public boolean isValidAttackingGuard(final AbstractEntityCitizen entity)
    {
        return false;
    }

    /**
     * Getter for letting citizens move in or not.
     *
     * @return the boolean true or false.
     */
    
    public boolean canMoveIn()
    {
        return moveIn;
    }

    /**
     * Tries to use a given amount of additional growth-time for childs.
     *
     * @param amount amount to use
     * @return true if used up.
     */
    
    public boolean useAdditionalChildTime(final int amount)
    {
        return false;
    }

    
    public void updateHasChilds()
    {
    }

    
    public void addLoadedChunk(final long chunkPos)
    {

    }

    
    public void removeLoadedChunk(final long chunkPos)
    {

    }

    
    public int getLoadedChunkCount()
    {
        return 0;
    }

    
    public ColonyState getState()
    {
        return null;
    }

    
    public boolean isActive()
    {
        return true;
    }

    /**
     * Sets if citizens can move in.
     *
     * @param newMoveIn true if citizens can move in.
     */
    
    public void setMoveIn(final boolean newMoveIn) { this.moveIn = newMoveIn; }

    /**
     * Get the town hall View for this ColonyView.
     *
     * @return {@link BuildingTownHall.View} of the colony.
     */
    
    @Nullable
    public ITownHallView getTownHall()
    {
        return townHall;
    }

    /**
     * Get a AbstractBuilding.View for a given building (by coordinate-id) using raw x,y,z.
     *
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @param z z-coordinate.
     * @return {@link AbstractBuildingView} of a AbstractBuilding for the given Coordinates/ID, or null.
     */
    
    public IBuildingView getBuilding(final int x, final int y, final int z)
    {
        return getBuilding(new BlockPos(x, y, z));
    }

    /**
     * Get a AbstractBuilding.View for a given building (by coordinate-id) using ChunkCoordinates.
     *
     * @param buildingId Coordinates/ID of the AbstractBuilding.
     * @return {@link AbstractBuildingView} of a AbstractBuilding for the given Coordinates/ID, or null.
     */
    
    public IBuildingView getBuilding(final BlockPos buildingId)
    {
        return buildings.get(buildingId);
    }

    /**
     * Returns a map of players in the colony. Key is the UUID, value is {@link Player}
     *
     * @return Map of UUID's and {@link Player}
     */
    
    @NotNull
    public Map<UUID, Player> getPlayers()
    {
        return permissions.getPlayers();
    }

    /**
     * Sets a specific permission to a rank. If the permission wasn't already set, it sends a message to the server.
     *
     * @param rank   Rank to get the permission.
     * @param action Permission to get.
     */
    
    public void setPermission(final Rank rank, @NotNull final Action action)
    {
        if (permissions.setPermission(rank, action))
        {
            Network.getNetwork().sendToServer(new PermissionsMessage.Permission(this, PermissionsMessage.MessageType.SET_PERMISSION, rank, action));
        }
    }

    /**
     * removes a specific permission to a rank. If the permission was set, it sends a message to the server.
     *
     * @param rank   Rank to remove permission from.
     * @param action Action to remove permission of.
     */
    
    public void removePermission(final Rank rank, @NotNull final Action action)
    {
        if (permissions.removePermission(rank, action))
        {
            Network.getNetwork().sendToServer(new PermissionsMessage.Permission(this, PermissionsMessage.MessageType.REMOVE_PERMISSION, rank, action));
        }
    }

    /**
     * Toggles a specific permission to a rank. Sends a message to the server.
     *
     * @param rank   Rank to toggle permission of.
     * @param action Action to toggle permission of.
     */
    
    public void togglePermission(final Rank rank, @NotNull final Action action)
    {
        permissions.togglePermission(rank, action);
        Network.getNetwork().sendToServer(new PermissionsMessage.Permission(this, PermissionsMessage.MessageType.TOGGLE_PERMISSION, rank, action));
    }

    /**
     * Returns the maximum amount of citizen in the colony.
     *
     * @return maximum amount of citizens.
     */
    
    public int getCitizenCount()
    {
        return citizenCount;
    }

    
    public int getCitizenCountLimit()
    {
        return citizenCountWithEmptyGuardTowers;
    }

    /**
     * Getter for the citizens map.
     *
     * @return a unmodifiable Map of the citizen.
     */
    
    public Map<Integer, ICitizenDataView> getCitizens()
    {
        return Collections.unmodifiableMap(citizens);
    }

    /**
     * Getter for the workOrders.
     *
     * @return a unmodifiable Collection of the workOrders.
     */
    
    public Collection<WorkOrderView> getWorkOrders()
    {
        return Collections.unmodifiableCollection(workOrders.values());
    }

    /**
     * Gets the CitizenDataView for a citizen id.
     *
     * @param id the citizen id.
     * @return CitizenDataView for the citizen.
     */
    
    public ICitizenDataView getCitizen(final int id)
    {
        if (id > 0)
        {
            return citizens.get(id);
        }
        else
        {
            return visitors.get(id);
        }
    }

    /**
     * Populate a ColonyView from the network data.
     *
     * @param buf               {@link PacketBuffer} to read from.
     * @param isNewSubscription Whether this is a new subscription of not.
     * @return null == no response.
     */
    
    @Nullable
    public IMessage handleColonyViewMessage(@NotNull final PacketBuffer buf, @NotNull final World world, final boolean isNewSubscription)
    {
        this.world = world;
        //  General Attributes
        name = buf.readString(32767);
        dimensionId = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(buf.readString(32767)));
        center = buf.readBlockPos();
        manualHiring = buf.readBoolean();
        //  Citizenry
        citizenCount = buf.readInt();
        citizenCountWithEmptyGuardTowers = buf.readInt();

        if (isNewSubscription)
        {
            citizens.clear();
            townHall = null;
            buildings.clear();
        }

        freePositions.clear();
        freeBlocks.clear();
        wayPoints.clear();
        lastSpawnPoints.clear();

        final int blockListSize = buf.readInt();
        for (int i = 0; i < blockListSize; i++)
        {
            freeBlocks.add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation((buf.readString(32767)))));
        }

        final int posListSize = buf.readInt();
        for (int i = 0; i < posListSize; i++)
        {
            freePositions.add(buf.readBlockPos());
        }
        this.overallHappiness = buf.readDouble();
        this.hasColonyWarehouse = buf.readBoolean();

        final int wayPointListSize = buf.readInt();
        for (int i = 0; i < wayPointListSize; i++)
        {
            wayPoints.put(buf.readBlockPos(), Block.getStateById(buf.readInt()));
        }
        this.lastContactInHours = buf.readInt();
        this.manualHousing = buf.readBoolean();
        this.moveIn = buf.readBoolean();

        if (buf.readBoolean())
        {
            this.requestManager = new StandardRequestManager(this);
            this.requestManager.deserialize(StandardFactoryController.getInstance(), buf);
        }

        final int barbSpawnListSize = buf.readInt();
        for (int i = 0; i < barbSpawnListSize; i++)
        {
            lastSpawnPoints.add(buf.readBlockPos());
        }
        Collections.reverse(lastSpawnPoints);

        this.teamColonyColor = TextFormatting.values()[buf.readInt()];
        this.colonyFlag = buf.readCompoundTag().getList(TAG_BANNER_PATTERNS, Constants.TAG_COMPOUND);

        this.printProgress = buf.readBoolean();

        this.mercenaryLastUseTime = buf.readLong();

        this.style = buf.readString(32767);

        this.isUnderRaid = buf.readBoolean();
        this.spiesEnabled = buf.readBoolean();

        this.allies = new ArrayList<>();
        this.feuds = new ArrayList<>();

        final int noOfAllies = buf.readInt();
        for (int i = 0; i < noOfAllies; i++)
        {
            allies.add(new CompactColonyReference(buf.readString(32767), buf.readBlockPos(), buf.readInt(), buf.readBoolean(), RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(buf.readString(32767)))));
        }

        final int noOfFeuds = buf.readInt();
        for (int i = 0; i < noOfFeuds; i++)
        {
            feuds.add(new CompactColonyReference(buf.readString(32767), buf.readBlockPos(), buf.readInt(), false, RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(buf.readString(32767)))));
        }

        this.manager.readFromNBT(buf.readCompoundTag());
        if (isCoordInColony(world, Minecraft.getInstance().player.getPosition()))
        {
            ItemBlockHut.checkResearch(this);
        }
        return null;
    }

    /**
     * Update permissions.
     *
     * @param buf buffer containing permissions.
     * @return null == no response
     */
    
    @Nullable
    public IMessage handlePermissionsViewMessage(@NotNull final PacketBuffer buf)
    {
        permissions.deserialize(buf);
        return null;
    }

    /**
     * Update a ColonyView's workOrders given a network data ColonyView update packet. This uses a full-replacement - workOrders do not get updated and are instead overwritten.
     *
     * @param buf Network data.
     * @return null == no response.
     */
    
    @Nullable
    public IMessage handleColonyViewWorkOrderMessage(final PacketBuffer buf)
    {
        workOrders.clear();
        final int amount = buf.readInt();
        for (int i = 0; i < amount; i++)
        {
            @Nullable final WorkOrderView workOrder = AbstractWorkOrder.createWorkOrderView(buf);
            if (workOrder != null)
            {
                workOrders.put(workOrder.getId(), workOrder);
            }
        }

        return null;
    }

    /**
     * Update a ColonyView's citizens given a network data ColonyView update packet. This uses a full-replacement - citizens do not get updated and are instead overwritten.
     *
     * @param id  ID of the citizen.
     * @param buf Network data.
     * @return null == no response.
     */
    
    @Nullable
    public IMessage handleColonyViewCitizensMessage(final int id, final PacketBuffer buf)
    {
        final ICitizenDataView citizen = ICitizenDataManager.getInstance().createFromNetworkData(id, buf, this);
        if (citizen != null)
        {
            citizens.put(citizen.getId(), citizen);
        }

        return null;
    }

    
    public void handleColonyViewVisitorMessage(final boolean refresh, final Set<IVisitorViewData> visitorViewData)
    {
        if (refresh)
        {
            visitors = new HashMap<>();
        }

        for (final IVisitorViewData data : visitorViewData)
        {
            visitors.put(data.getId(), data);
        }
    }

    /**
     * Remove a citizen from the ColonyView.
     *
     * @param citizen citizen ID.
     * @return null == no response.
     */
    
    @Nullable
    public IMessage handleColonyViewRemoveCitizenMessage(final int citizen)
    {
        citizens.remove(citizen);
        return null;
    }

    /**
     * Remove a building from the ColonyView.
     *
     * @param buildingId location of the building.
     * @return null == no response.
     */
    
    @Nullable
    public IMessage handleColonyViewRemoveBuildingMessage(final BlockPos buildingId)
    {
        final IBuildingView building = buildings.remove(buildingId);
        if (townHall == building)
        {
            townHall = null;
        }
        return null;
    }

    /**
     * Remove a workOrder from the ColonyView.
     *
     * @param workOrderId id of the workOrder.
     * @return null == no response
     */
    
    @Nullable
    public IMessage handleColonyViewRemoveWorkOrderMessage(final int workOrderId)
    {
        workOrders.remove(workOrderId);
        return null;
    }

    /**
     * Update a ColonyView's buildings given a network data ColonyView update packet. This uses a full-replacement - buildings do not get updated and are instead overwritten.
     *
     * @param buildingId location of the building.
     * @param buf        buffer containing ColonyBuilding information.
     * @return null == no response.
     */
    
    @Nullable
    public IMessage handleColonyBuildingViewMessage(final BlockPos buildingId, @NotNull final PacketBuffer buf)
    {
        if (buildings.containsKey(buildingId))
        {
            //Read the string first to set up the buffer.
            buf.readString(32767);
            buildings.get(buildingId).deserialize(buf);
        }
        else
        {
            @Nullable final IBuildingView building = IBuildingDataManager.getInstance().createViewFrom(this, buildingId, buf);
            if (building != null)
            {
                buildings.put(building.getID(), building);

                if (building instanceof BuildingTownHall.View)
                {
                    townHall = (ITownHallView) building;
                }
            }
        }

        return null;
    }

    /**
     * Update a players permissions.
     *
     * @param player player username.
     */
    
    public void addPlayer(final String player)
    {
        Network.getNetwork().sendToServer(new PermissionsMessage.AddPlayer(this, player));
    }

    /**
     * Remove player from colony permissions.
     *
     * @param player the UUID of the player to remove.
     */
    
    public void removePlayer(final UUID player)
    {
        Network.getNetwork().sendToServer(new PermissionsMessage.RemovePlayer(this, player));
    }

    /**
     * Getter for the overall happiness.
     *
     * @return the happiness, a double.
     */
    
    public double getOverallHappiness()
    {
        return overallHappiness;
    }

    
    public BlockPos getCenter()
    {
        return center;
    }

    
    public String getName()
    {
        return name;
    }

    /**
     * Getter for the team colony color.
     *
     * @return the color.
     */
    
    public TextFormatting getTeamColonyColor()
    {
        return teamColonyColor;
    }

    /**
     * Getter for the pattern list of the colony flag
     *
     * @return the ListNBT of flag (banner) patterns
     */
    
    public ListNBT getColonyFlag() { return colonyFlag; }

    /**
     * Sets the name of the view.
     *
     * @param name Name of the view.
     */
    
    public void setName(final String name)
    {
        this.name = name;
        Network.getNetwork().sendToServer(new TownHallRenameMessage(this, name));
    }

    @NotNull
    
    public IPermissions getPermissions()
    {
        return permissions;
    }

    
    public boolean isCoordInColony(@NotNull final World w, @NotNull final BlockPos pos)
    {
        final Chunk chunk = w.getChunkAt(pos);
        final IColonyTagCapability cap = chunk.getCapability(CLOSE_COLONY_CAP, null).orElseGet(null);
        return cap.getOwningColony() == this.getID();
    }

    
    public long getDistanceSquared(@NotNull final BlockPos pos)
    {
        return BlockPosUtil.getDistanceSquared2D(center, pos);
    }

    
    public boolean hasTownHall()
    {
        return townHall != null;
    }

    /**
     * Returns the ID of the view.
     *
     * @return ID of the view.
     */
    
    public int getID()
    {
        return id;
    }

    
    public boolean hasWarehouse()
    {
        return hasColonyWarehouse;
    }

    
    public boolean isDay()
    {
        return false;
    }

    
    public ScorePlayerTeam getTeam()
    {
        return world.getScoreboard().getTeam(getTeamName());
    }

    
    public int getLastContactInHours()
    {
        return lastContactInHours;
    }

    
    public World getWorld()
    {
        return world;
    }

    @NotNull
    
    public IRequestManager getRequestManager()
    {
        //No request system on the client side.
        //At least for now.
        return requestManager;
    }

    
    public void markDirty()
    {
        /*
         * Nothing to do here.
         */
    }

    
    public boolean canBeAutoDeleted()
    {
        return false;
    }

    @Nullable
    
    public IRequester getRequesterBuildingForPosition(@NotNull final BlockPos pos)
    {
        return getBuilding(pos);
    }

    
    public void removeVisitingPlayer(final PlayerEntity player)
    {
        /*
         * Intentionally left empty.
         */
    }

    @NotNull
    
    public List<PlayerEntity> getMessagePlayerEntities()
    {
        return new ArrayList<>();
    }

    
    public void onBuildingUpgradeComplete(@Nullable final IBuilding building, final int level)
    {

    }

    
    public void addVisitingPlayer(final PlayerEntity player)
    {
        /*
         * Intentionally left empty.
         */
    }

    
    public void onWorldLoad(@NotNull final World w)
    {

    }

    
    public void onWorldUnload(@NotNull final World w)
    {

    }

    
    public void onServerTick(@NotNull final TickEvent.ServerTickEvent event)
    {

    }

    @NotNull
    
    public IWorkManager getWorkManager()
    {
        return null;
    }

    
    public void onWorldTick(@NotNull final TickEvent.WorldTickEvent event)
    {

    }

    
    public Map<BlockPos, BlockState> getWayPoints()
    {
        return wayPoints;
    }

    /**
     * Get a list of all barb spawn positions in the colony view.
     *
     * @return a copy of the list.
     */
    
    public List<BlockPos> getLastSpawnPoints()
    {
        return new ArrayList<>(lastSpawnPoints);
    }

    /**
     * Get if progress should be printed.
     *
     * @return true if so.
     */
    
    public boolean isPrintingProgress()
    {
        return printProgress;
    }

    
    public boolean isRemote()
    {
        return true;
    }

    
    public CompoundNBT getColonyTag()
    {
        return null;
    }

    
    public boolean isNeedToMourn()
    {
        return false;
    }

    
    public void setNeedToMourn(final boolean needToMourn, final String name)
    {

    }

    
    public boolean isMourning()
    {
        return false;
    }

    
    public boolean isColonyUnderAttack()
    {
        return false;
    }

    
    public boolean isValidAttackingPlayer(final PlayerEntity entity)
    {
        return false;
    }

    
    public void addGuardToAttackers(final AbstractEntityCitizen entityCitizen, final PlayerEntity followPlayer)
    {

    }

    
    public void setColonyColor(final TextFormatting color)
    {

    }

    
    public void setColonyFlag(ListNBT colonyFlag)
    {
        this.colonyFlag = colonyFlag;
        Network.getNetwork().sendToServer(new ColonyFlagChangeMessage(this, colonyFlag));
    }

    /**
     * Get a list of all buildings.
     *
     * @return a list of their views.
     */
    
    public List<IBuildingView> getBuildings()
    {
        return new ArrayList<>(buildings.values());
    }

    @NotNull
    
    public List<PlayerEntity> getImportantMessageEntityPlayers()
    {
        return new ArrayList<>();
    }

    /**
     * Get the style of the colony.
     *
     * @return the current default style.
     */
    
    public String getStyle()
    {
        return style;
    }

    
    public void setStyle(final String style)
    {
        // Not needed.
    }

    
    public IBuildingManager getBuildingManager()
    {
        return null;
    }

    
    public ICitizenManager getCitizenManager()
    {
        return null;
    }

    
    public IVisitorManager getVisitorManager()
    {
        return null;
    }

    
    public IRaiderManager getRaiderManager()
    {
        return null;
    }

    
    public IEventManager getEventManager()
    {
        return null;
    }

    
    public IEventDescriptionManager getEventDescriptionManager()
    {
        return null;
    }

    
    public IColonyPackageManager getPackageManager()
    {
        return null;
    }

    
    public IProgressManager getProgressManager()
    {
        return null;
    }

    
    public boolean isRaiding()
    {
        return this.isUnderRaid;
    }

    
    public long getMercenaryUseTime()
    {
        return mercenaryLastUseTime;
    }

    
    public void usedMercenaries()
    {
        mercenaryLastUseTime = world.getGameTime();
    }

    
    public List<CompactColonyReference> getAllies()
    {
        return allies;
    }

    
    public List<CompactColonyReference> getFeuds()
    {
        return feuds;
    }

    
    public IResearchManager getResearchManager()
    {
        return manager;
    }

    
    public boolean areSpiesEnabled()
    {
        return spiesEnabled;
    }

    
    public ICitizenDataView getVisitor(final int citizenId)
    {
        return visitors.get(citizenId);
    }
}
