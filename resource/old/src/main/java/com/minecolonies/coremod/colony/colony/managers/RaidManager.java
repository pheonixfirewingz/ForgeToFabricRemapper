package com.minecolonies.coremod.colony.colony.managers;

import com.ldtteam.structurize.util.LanguageHandler;
import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.colonyEvents.EventStatus;
import com.minecolonies.api.colony.colonyEvents.IColonyEvent;
import com.minecolonies.api.colony.colonyEvents.IColonyRaidEvent;
import com.minecolonies.api.colony.managers.interfaces.IRaiderManager;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.coremod.colony.colony.Colony;
import com.minecolonies.coremod.colony.colony.buildings.modules.LivingBuildingModule;
import com.minecolonies.coremod.colony.colony.buildings.workerbuildings.BuildingGuardTower;
import com.minecolonies.coremod.colony.colony.buildings.workerbuildings.BuildingTownHall;
import com.minecolonies.coremod.colony.colony.colonyEvents.raidEvents.HordeRaidEvent;
import com.minecolonies.coremod.colony.colony.colonyEvents.raidEvents.amazonevent.AmazonRaidEvent;
import com.minecolonies.coremod.colony.colony.colonyEvents.raidEvents.barbarianEvent.BarbarianRaidEvent;
import com.minecolonies.coremod.colony.colony.colonyEvents.raidEvents.barbarianEvent.Horde;
import com.minecolonies.coremod.colony.colony.colonyEvents.raidEvents.egyptianevent.EgyptianRaidEvent;
import com.minecolonies.coremod.colony.colony.colonyEvents.raidEvents.norsemenevent.NorsemenRaidEvent;
import com.minecolonies.coremod.colony.colony.colonyEvents.raidEvents.norsemenevent.NorsemenShipRaidEvent;
import com.minecolonies.coremod.colony.colony.colonyEvents.raidEvents.pirateEvent.PirateGroundRaidEvent;
import com.minecolonies.coremod.colony.colony.colonyEvents.raidEvents.pirateEvent.PirateRaidEvent;
import com.minecolonies.coremod.colony.colony.colonyEvents.raidEvents.pirateEvent.ShipBasedRaiderUtils;
import com.minecolonies.coremod.colony.colony.colonyEvents.raidEvents.pirateEvent.ShipSize;
import com.minecolonies.coremod.colony.jobs.AbstractJobGuard;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.minecolonies.api.util.constant.ColonyConstants.BIG_HORDE_SIZE;
import static com.minecolonies.api.util.constant.Constants.DEFAULT_BARBARIAN_DIFFICULTY;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_NIGHTS_SINCE_LAST_RAID;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_RAIDABLE;

/**
 * Handles spawning hostile raid events.
 */
public class RaidManager
{
    /**
     * Spawn modifier to decrease the spawn-rate.
     */
    public static final double SPAWN_MODIFIER = 60;

    /**
     * Min distance to keep while spawning near buildings
     */
    private static final int MIN_BUILDING_SPAWN_DIST = 35;

    /**
     * Different biome ids.
     */
    private static final String DESERT_BIOME_ID = "desert";
    private static final String JUNGLE_BIOME_ID = "jungle";
    private static final String TAIGA_BIOME_ID  = "taiga";

    /**
     * Thresholds for reducing or increasing raid difficulty
     */
    private static final double LOST_CITIZEN_DIFF_REDUCE_PCT   = 0.15d;
    private static final double LOST_CITIZEN_DIFF_INCREASE_PCT = 0.05d;

    /**
     * Min and max for raid difficulty
     */
    private static final int MIN_RAID_DIFFICULTY = 1;
    private static final int MAX_RAID_DIFFICULTY = 14;

    /**
     * The minumum raid difficulty modifier
     */
    private static final double MIN_DIFFICULTY_MODIFIER = 0.2;

    /**
     * Difficulty nbt tag
     */
    private static final String TAG_RAID_DIFFICULTY = "difficulty";
    private static final String TAG_LOST_CITIZENS   = "lostCitizens";

    /**
     * Min required raidlevel
     */
    private static final int MIN_REQUIRED_RAIDLEVEL = 75;

    /**
     * Percentage increased amount of spawns per player
     */
    private static final double INCREASE_PER_PLAYER = 0.05;

    /**
     * Chance to ignore biome selection
     */
    private static final int IGNORE_BIOME_CHANCE = 2;

    /**
     * The dynamic difficulty of raids for this colony
     */
    private int raidDifficulty = MIN_RAID_DIFFICULTY;

    /**
     * Whether there will be a raid in this colony tonight.
     */
    private boolean raidTonight = false;

    /**
     * Whether or not this colony may have Raider events. (set via command)
     */
    private boolean haveBarbEvents = true;

    /**
     * The amount of nights since the last raid.
     */
    private int nightsSinceLastRaid = 0;

    /**
     * Last raider spawnpoints.
     */
    private final List<BlockPos> lastSpawnPoints = new ArrayList<>();

    /**
     * The colony of the manager.
     */
    private final Colony colony;

    /**
     * Whether the spies are currently active, active spies mark enemies with glow.
     */
    private boolean spiesEnabled;

    /**
     * The last building position for raiders to walk to
     */
    private BlockPos lastBuilding;

    /**
     * The time the last building pos was used.
     */
    private int buildingPosUsage = 0;

    /**
     * The amount of citizens lost in a raid, two for normal citizens one for guards
     */
    private int lostCitizens = 0;

    /**
     * The next raidType, or "" if the next raid should be determined from biome.
     */
    private String nextForcedType = "";

    /**
     * Creates the RaidManager for a colony.
     *
     * @param colony the colony.
     */
    public RaidManager(final Colony colony)
    {
        this.colony = colony;
    }

    
    public boolean canHaveRaiderEvents()
    {
        return this.haveBarbEvents;
    }

    
    public boolean willRaidTonight()
    {
        return this.raidTonight;
    }

    
    public void setCanHaveRaiderEvents(final boolean canHave)
    {
        this.haveBarbEvents = canHave;
    }

    
    public void addRaiderSpawnPoint(final BlockPos pos)
    {
        lastSpawnPoints.add(pos);
    }

    
    public void setRaidNextNight(final boolean willRaid)
    {
        this.raidTonight = willRaid;
    }

    
    public void setRaidNextNight(final boolean willRaid, final String raidType)
    {
        this.raidTonight = true;
        this.nextForcedType = raidType;
    }

    
    public boolean areSpiesEnabled()
    {
        return spiesEnabled;
    }

    
    public void setSpiesEnabled(final boolean enabled)
    {
        if (spiesEnabled != enabled)
        {
            colony.markDirty();
        }
        spiesEnabled = enabled;
    }

    
    public void raiderEvent()
    {
        raiderEvent("");
    }

    
    public void raiderEvent(final String raidType)
    {
        if (colony.getWorld() == null || !canRaid() || raidType == null)
        {
            return;
        }

        final int raidLevel = getColonyRaidLevel();
        int amount = calculateRaiderAmount(raidLevel);
        if (amount <= 0 || raidLevel < MIN_REQUIRED_RAIDLEVEL)
        {
            return;
        }

        // Splits into multiple raids if too large
        final int raidCount = Math.max(1, amount / BIG_HORDE_SIZE);

        final Set<BlockPos> spawnPoints = new HashSet<>();

        for (int i = 0; i < raidCount; i++)
        {
            final BlockPos targetSpawnPoint = calculateSpawnLocation();
            if (targetSpawnPoint == null || targetSpawnPoint.equals(colony.getCenter()) || targetSpawnPoint.getY() > MineColonies.getConfig().getServer().maxYForBarbarians.get())
            {
                continue;
            }

            spawnPoints.add(targetSpawnPoint);
        }

        if (spawnPoints.isEmpty())
        {
            return;
        }

        nightsSinceLastRaid = 0;
        amount = (int) Math.ceil((float) amount / spawnPoints.size());

        for (final BlockPos targetSpawnPoint : spawnPoints)
        {
            if (MineColonies.getConfig().getServer().enableInDevelopmentFeatures.get())
            {
                LanguageHandler.sendPlayersMessage(
                  colony.getMessagePlayerEntities(),
                  "Horde Spawn Point: " + targetSpawnPoint);
            }

            // No rotation till spawners are moved into schematics
            final int shipRotation = new Random().nextInt(3);
            final String homeBiomePath = colony.getWorld().getBiome(colony.getCenter()).getCategory().getName();
            final int rand = colony.getWorld().rand.nextInt(100);
            if ((raidType.isEmpty() && (homeBiomePath.contains(TAIGA_BIOME_ID) || rand < IGNORE_BIOME_CHANCE)
                  || raidType.equals(NorsemenRaidEvent.NORSEMEN_RAID_EVENT_TYPE_ID.getPath()))
                  && ShipBasedRaiderUtils.canSpawnShipAt(colony,
              targetSpawnPoint,
              amount,
              shipRotation,
              NorsemenShipRaidEvent.SHIP_NAME))
            {
                final NorsemenShipRaidEvent event = new NorsemenShipRaidEvent(colony);
                event.setSpawnPoint(targetSpawnPoint);
                event.setShipSize(ShipSize.getShipForRaiderAmount(amount));
                event.setShipRotation(shipRotation);
                colony.getEventManager().addEvent(event);
            }
            else if (ShipBasedRaiderUtils.canSpawnShipAt(colony, targetSpawnPoint, amount, shipRotation, PirateRaidEvent.SHIP_NAME)
                     && (raidType.isEmpty() || raidType.equals(PirateRaidEvent.PIRATE_RAID_EVENT_TYPE_ID.getPath())))
            {
                final PirateRaidEvent event = new PirateRaidEvent(colony);
                event.setSpawnPoint(targetSpawnPoint);
                event.setShipSize(ShipSize.getShipForRaiderAmount(amount));
                event.setShipRotation(shipRotation);
                colony.getEventManager().addEvent(event);
            }
            else
            {
                final String biomePath = colony.getWorld().getBiome(targetSpawnPoint).getCategory().getName().toLowerCase();
                final HordeRaidEvent event;
                if (((biomePath.contains(DESERT_BIOME_ID) || (rand > IGNORE_BIOME_CHANCE && rand < IGNORE_BIOME_CHANCE * 2))
                      && raidType.isEmpty()) || raidType.equals(EgyptianRaidEvent.EGYPTIAN_RAID_EVENT_TYPE_ID.getPath()))
                {
                    event = new EgyptianRaidEvent(colony);
                }
                else if (((biomePath.contains(JUNGLE_BIOME_ID) || (rand > IGNORE_BIOME_CHANCE * 2 && rand < IGNORE_BIOME_CHANCE * 3)
                           && raidType.isEmpty())) || (raidType.equals(AmazonRaidEvent.AMAZON_RAID_EVENT_TYPE_ID.getPath())))
                {
                    event = new AmazonRaidEvent(colony);
                }
                else if (((biomePath.contains(TAIGA_BIOME_ID) || (rand > IGNORE_BIOME_CHANCE * 3 && rand < IGNORE_BIOME_CHANCE * 4))
                           && raidType.isEmpty()) || raidType.equals(NorsemenRaidEvent.NORSEMEN_RAID_EVENT_TYPE_ID.getPath()))
                {
                    event = new NorsemenRaidEvent(colony);
                }
                else if(raidType.equals(PirateRaidEvent.PIRATE_RAID_EVENT_TYPE_ID.getPath()))
                {
                    event = new PirateGroundRaidEvent(colony);
                }
                else
                {
                    event = new BarbarianRaidEvent(colony);
                }

                event.setSpawnPoint(targetSpawnPoint);
                event.setHorde(new Horde(amount));
                colony.getEventManager().addEvent(event);
            }

            addRaiderSpawnPoint(targetSpawnPoint);
        }
        colony.markDirty();
    }

    private static final int MIN_RAID_CHUNK_DIST_CENTER = 5;

    /**
     * Calculate a random spawn point along the colony's border
     *
     * @return Returns the random blockPos
     */
    
    public BlockPos calculateSpawnLocation()
    {
        List<IBuilding> loadedBuildings = new ArrayList<>();
        BlockPos locationSum = new BlockPos(0, 0, 0);
        int amount = 0;

        for (final IBuilding building : colony.getBuildingManager().getBuildings().values())
        {
            if (WorldUtil.isEntityBlockLoaded(colony.getWorld(), building.getPosition()))
            {
                loadedBuildings.add(building);
                amount++;
                locationSum = locationSum.add(building.getPosition());
            }
        }

        if (amount == 0)
        {
            Log.getLogger().info("Trying to spawn raid on colony with no loaded buildings, aborting!");
            return null;
        }

        // Calculate center on loaded buildings, to find a nice distance for raiders
        BlockPos calcCenter = new BlockPos(locationSum.getX() / amount, locationSum.getY() / amount, locationSum.getZ() / amount);

        final Random random = colony.getWorld().rand;

        BlockPos spawnPos = null;

        Direction direction1 = random.nextInt(2) < 1 ? Direction.EAST : Direction.WEST;
        Direction direction2 = random.nextInt(2) < 1 ? Direction.NORTH : Direction.SOUTH;

        for (int i = 0; i < 4; i++)
        {
            if (i > 0)
            {
                direction1 = direction1.rotateY();
                direction2 = direction2.rotateY();
            }

            spawnPos = findSpawnPointInDirections(calcCenter, direction1, direction2, loadedBuildings);
            if (spawnPos != null)
            {
                break;
            }
        }

        if (spawnPos == null)
        {
            return null;
        }

        return BlockPosUtil.findLand(spawnPos, colony.getWorld());
    }

    private final static int RAID_SPAWN_SEARCH_CHUNKS = 10;

    /**
     * Finds a spawnpoint randomly in a circular shape around the center Advances
     *
     * @param center          the center of the area to search for a spawn point
     * @param dir1            the first of the directions to look in
     * @param dir2            the second of the directions to look in
     * @param loadedBuildings a list of loaded buildings
     * @return the calculated position
     */
    private BlockPos findSpawnPointInDirections(final BlockPos center, final Direction dir1, final Direction dir2, final List<IBuilding> loadedBuildings)
    {
        final Random random = colony.getWorld().rand;

        BlockPos spawnPos = new BlockPos(center);

        // Do the min offset
        for (int i = 1; i <= MIN_RAID_CHUNK_DIST_CENTER; i++)
        {
            if (random.nextBoolean())
            {
                spawnPos = spawnPos.offset(dir1, 16);
            }
            else
            {
                spawnPos = spawnPos.offset(dir2, 16);
            }
        }

        BlockPos tempPos = new BlockPos(spawnPos);

        // Check if loaded
        if (WorldUtil.isBlockLoaded(colony.getWorld(), spawnPos))
        {
            for (int i = 1; i <= random.nextInt(RAID_SPAWN_SEARCH_CHUNKS - 3) + 3; i++)
            {
                // Choose random between our two directions
                if (random.nextBoolean())
                {
                    if (WorldUtil.isBlockLoaded(colony.getWorld(), tempPos.offset(dir1, 16))
                          && WorldUtil.isBlockLoaded(colony.getWorld(), tempPos.offset(dir1, 32))
                          && WorldUtil.isBlockLoaded(colony.getWorld(), tempPos.offset(dir2, 16)))
                    {
                        if (isValidSpawnPoint(tempPos.offset(dir1, 16)))
                        {
                            spawnPos = tempPos.offset(dir1, 16);
                        }
                        tempPos = tempPos.offset(dir1, 16);
                    }
                    else
                    {
                        break;
                    }
                }
                else
                {
                    if (WorldUtil.isBlockLoaded(colony.getWorld(), tempPos.offset(dir2, 16))
                          && WorldUtil.isBlockLoaded(colony.getWorld(), tempPos.offset(dir2, 32))
                          && WorldUtil.isBlockLoaded(colony.getWorld(), tempPos.offset(dir1, 16)))
                    {
                        if (isValidSpawnPoint(tempPos.offset(dir2, 16)))
                        {
                            spawnPos = tempPos.offset(dir2, 16);
                        }
                        tempPos = tempPos.offset(dir2, 16);
                    }
                    else
                    {
                        break;
                    }
                }
            }

            if (isValidSpawnPoint(spawnPos))
            {
                return spawnPos;
            }
        }

        return null;
    }

    /**
     * Determines whether the given spawn point is allowed.
     *
     * @param spawnPos        the spawn point to check
     * @return true if valid
     */
    private boolean isValidSpawnPoint(final BlockPos spawnPos)
    {
        for (final IBuilding building : colony.getBuildingManager().getBuildings().values())
        {
            if (building.getBuildingLevel() == 0)
            {
                continue;
            }

            int minDist = MIN_BUILDING_SPAWN_DIST;

            // Additional raid protection for certain buildings, towers can be used now to deal with unlucky - inwall spawns
            if (building instanceof BuildingGuardTower)
            {
                minDist += building.getBuildingLevel() * 7;
            }
            else if (building.hasModule(LivingBuildingModule.class))
            {
                minDist += building.getBuildingLevel() * 4;
            }
            else if (building instanceof BuildingTownHall)
            {
                minDist += building.getBuildingLevel() * 8;
            }
            else
            {
                minDist += building.getBuildingLevel() * 2;
            }

            if (BlockPosUtil.getDistance2D(building.getPosition(), spawnPos) < minDist)
            {
                return false;
            }
        }

        return true;
    }

    
    public List<BlockPos> getLastSpawnPoints()
    {
        return new ArrayList<>(lastSpawnPoints);
    }

    /**
     * Returns the colonies barbarian level
     *
     * @return the amount of barbarians.
     */
    
    public int calculateRaiderAmount(final int raidLevel)
    {
        return 1 + Math.min(MineColonies.getConfig().getServer().maxBarbarianSize.get(),
          (int) ((raidLevel / SPAWN_MODIFIER) * getRaidDifficultyModifier() * (1.0 + colony.getMessagePlayerEntities().size() * INCREASE_PER_PLAYER) * ((
            colony.getWorld().rand.nextDouble() * 0.5d) + 0.75)));
    }

    
    public boolean isRaided()
    {
        for (final IColonyEvent event : colony.getEventManager().getEvents().values())
        {
            if (event instanceof IColonyRaidEvent && (event.getStatus() == EventStatus.PROGRESSING || event.getStatus() == EventStatus.PREPARING))
            {
                return true;
            }
        }
        return false;
    }

    
    public void onNightFall()
    {
        if (!isRaided())
        {
            if (nightsSinceLastRaid == 0)
            {
                final double lostPct = (double) lostCitizens / colony.getCitizenManager().getMaxCitizens();
                if (lostPct > LOST_CITIZEN_DIFF_REDUCE_PCT)
                {
                    raidDifficulty = Math.max(MIN_RAID_DIFFICULTY, raidDifficulty - (int) (lostPct / LOST_CITIZEN_DIFF_REDUCE_PCT));
                }
                else if (lostPct < LOST_CITIZEN_DIFF_INCREASE_PCT)
                {
                    raidDifficulty = Math.min(MAX_RAID_DIFFICULTY, raidDifficulty + 1);
                }
            }

            nightsSinceLastRaid++;
            lostCitizens = 0;
        }
        else
        {
            nightsSinceLastRaid = 0;
        }

        if (raidTonight)
        {
            raidTonight = false;
            raiderEvent(nextForcedType);
            nextForcedType = "";
        }
        else
        {
            determineRaidForNextDay();
        }
    }

    
    public int getNightsSinceLastRaid()
    {
        return nightsSinceLastRaid;
    }

    
    public void setNightsSinceLastRaid(final int nightsSinceLastRaid)
    {
        this.nightsSinceLastRaid = nightsSinceLastRaid;
    }

    /**
     * Checks if a raid is possible
     *
     * @return whether a raid is possible
     */
    
    public boolean canRaid()
    {
        return !WorldUtil.isPeaceful(colony.getWorld())
                 && MineColonies.getConfig().getServer().doBarbariansSpawn.get()
                 && colony.getRaiderManager().canHaveRaiderEvents()
                 && !colony.getPackageManager().getImportantColonyPlayers().isEmpty();
    }

    /**
     * Determines whether we raid on the next day
     */
    private void determineRaidForNextDay()
    {
        final boolean raid =
          canRaid()
            &&
            (
              raidThisNight(colony.getWorld(), colony)
                || colony.getWorld().getBiome(colony.getCenter()).getCategory().getName().contains("desert") && colony.getWorld().isRaining()
            );

        if (MineColonies.getConfig().getServer().enableInDevelopmentFeatures.get())
        {
            LanguageHandler.sendPlayersMessage(
              colony.getImportantMessageEntityPlayers(),
              "Will raid tomorrow: " + raid);
        }

        setRaidNextNight(raid);
    }

    /**
     * Takes a colony and spits out that colony's RaidLevel.
     *
     * @return an int describing the raid level
     */
    public int getColonyRaidLevel()
    {
        int levels = colony.getCitizenManager().getCitizens().size() * 10;

        for (final IBuilding building : colony.getBuildingManager().getBuildings().values())
        {
            levels += building.getBuildingLevel() * 2;
        }

        return levels;
    }

    /**
     * Returns whether a raid should happen depending on the Config
     *
     * @param world  The world in which the raid is possibly happening (Used to get a random number easily)
     * @param colony The colony to raid
     * @return Boolean value on whether to act this night
     */
    private boolean raidThisNight(final World world, final IColony colony)
    {
        if (nightsSinceLastRaid < MineColonies.getConfig().getServer().minimumNumberOfNightsBetweenRaids.get())
        {
            return false;
        }

        if (nightsSinceLastRaid > MineColonies.getConfig().getServer().averageNumberOfNightsBetweenRaids.get() + 2)
        {
            return true;
        }

        return world.rand.nextDouble() < 1.0 / (MineColonies.getConfig().getServer().averageNumberOfNightsBetweenRaids.get() - MineColonies.getConfig()
                                                                                                                                 .getServer().minimumNumberOfNightsBetweenRaids.get());
    }

    
    @NotNull
    public BlockPos getRandomBuilding()
    {
        buildingPosUsage++;
        if (buildingPosUsage > 3 || lastBuilding == null)
        {
            buildingPosUsage = 0;
            final Collection<IBuilding> buildingList = colony.getBuildingManager().getBuildings().values();
            final Object[] buildingArray = buildingList.toArray();
            if (buildingArray.length != 0)
            {
                final int rand = colony.getWorld().rand.nextInt(buildingArray.length);
                final IBuilding building = (IBuilding) buildingArray[rand];
                lastBuilding = building.getPosition();
            }
            else
            {
                lastBuilding = colony.getCenter();
            }
        }

        return lastBuilding;
    }

    
    public double getRaidDifficultyModifier()
    {
        return ((raidDifficulty / (double) 10) + MIN_DIFFICULTY_MODIFIER) * (MinecoloniesAPIProxy.getInstance().getConfig().getServer().barbarianHordeDifficulty.get()
                                                                       / (double) DEFAULT_BARBARIAN_DIFFICULTY) * (colony.getWorld().getDifficulty().getId() / 2d);
    }

    
    public void onLostCitizen(final ICitizenData citizen)
    {
        if (!isRaided())
        {
            return;
        }

        if (citizen.getJob() instanceof AbstractJobGuard)
        {
            lostCitizens++;
        }
        else
        {
            lostCitizens += 2;
        }

        if (((double) lostCitizens / colony.getCitizenManager().getMaxCitizens()) > 0.5)
        {
            for (final IColonyEvent event : colony.getEventManager().getEvents().values())
            {
                event.setStatus(EventStatus.DONE);
            }
        }
    }

    
    public void write(final CompoundNBT compound)
    {
        compound.putBoolean(TAG_RAIDABLE, canHaveRaiderEvents());
        compound.putInt(TAG_NIGHTS_SINCE_LAST_RAID, getNightsSinceLastRaid());
        compound.putInt(TAG_RAID_DIFFICULTY, raidDifficulty);
        compound.putInt(TAG_LOST_CITIZENS, lostCitizens);
    }

    
    public void read(final CompoundNBT compound)
    {
        if (compound.keySet().contains(TAG_RAIDABLE))
        {
            setCanHaveRaiderEvents(compound.getBoolean(TAG_RAIDABLE));
        }
        else
        {
            setCanHaveRaiderEvents(true);
        }

        if (compound.contains(TAG_NIGHTS_SINCE_LAST_RAID))
        {
            setNightsSinceLastRaid(compound.getInt(TAG_NIGHTS_SINCE_LAST_RAID));
        }

        raidDifficulty = MathHelper.clamp(compound.getInt(TAG_RAID_DIFFICULTY), MIN_RAID_DIFFICULTY, MAX_RAID_DIFFICULTY);
        lostCitizens = compound.getInt(TAG_LOST_CITIZENS);
    }

    
    public int getLostCitizen()
    {
        return lostCitizens;
    }
}