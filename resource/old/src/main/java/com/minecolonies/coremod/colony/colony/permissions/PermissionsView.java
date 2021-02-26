package com.minecolonies.coremod.colony.colony.permissions;

import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.colony.permissions.IPermissions;
import com.minecolonies.api.colony.permissions.Player;
import com.minecolonies.api.colony.permissions.Rank;
import com.minecolonies.api.network.PacketUtils;
import com.minecolonies.api.util.Utils;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A client side representation of the permissions.
 */
public class PermissionsView implements IPermissions
{
    @NotNull
    private final Map<UUID, Player>  players     = new HashMap<>();
    @NotNull
    private final Map<Rank, Integer> permissions = new EnumMap<>(Rank.class);
    private       Rank               userRank    = Rank.NEUTRAL;

    private UUID   colonyOwner;
    private String ownerName = "";

    /**
     * Getter for the user rank.
     *
     * @return the rank.
     */
    public Rank getUserRank()
    {
        return userRank;
    }

    /**
     * Gets all player by a certain rank.
     *
     * @param rank the rank.
     * @return set of players.
     */
    @NotNull
    public Set<Player> getPlayersByRank(final Rank rank)
    {
        return Collections.unmodifiableSet(
          this.players.values()
            .stream()
            .filter(player -> player.getRank() == rank)
            .collect(Collectors.toSet()));
    }

    @NotNull
    public Map<UUID, Player> getPlayers()
    {
        return Collections.unmodifiableMap(players);
    }

    @Override
    public boolean setPlayerRank(final UUID id, final Rank rank, final World world)
    {
        return false;
    }

    @Override
    public boolean addPlayer(@NotNull final GameProfile gameprofile, final Rank rank)
    {
        return false;
    }

    /**
     * Gets all player by a certain set of rank.
     *
     * @param ranks the set of rank.
     * @return set of players.
     */
    @NotNull
    public Set<Player> getPlayersByRank(@NotNull final Set<Rank> ranks)
    {
        return Collections.unmodifiableSet(
          this.players.values()
            .stream()
            .filter(player -> ranks.contains(player.getRank()))
            .collect(Collectors.toSet()));
    }

    @NotNull
    public Map<Rank, Integer> getPermissions()
    {
        return permissions;
    }

    /**
     * Checks if the player has the permission to do an action.
     *
     * @param id     the id of the player.
     * @param action the action he is trying to execute.
     * @return true if so.
     */
    public boolean hasPermission(final UUID id, @NotNull final Action action)
    {
        return hasPermission(getRank(id), action);
    }

    /**
     * Checks if the rank has the permission to do an action.
     *
     * @param rank   the rank of the player.
     * @param action the action he is trying to execute.
     * @return true if so.
     */
    public boolean hasPermission(final Rank rank, @NotNull final Action action)
    {
        return (rank == Rank.OWNER && action != Action.GUARDS_ATTACK)
                 || (permissions != null && action != null && permissions.containsKey(rank) && Utils.testFlag(permissions.get(rank), action.getFlag()));
    }

    /**
     * Sets if the rank has the permission to do an action.
     *
     * @param rank   the rank to set.
     * @param action the action he is trying to execute.
     * @return true if so.
     */
    public boolean setPermission(final Rank rank, @NotNull final Action action)
    {
        final int flags = permissions.get(rank);

        //check that flag isn't set
        if (!Utils.testFlag(flags, action.getFlag()))
        {
            permissions.put(rank, Utils.setFlag(flags, action.getFlag()));
            return true;
        }
        return false;
    }

    /**
     * Remove if the rank has the permission to do an action.
     *
     * @param rank   the rank to set.
     * @param action the action he is trying to execute.
     * @return true if so.
     */
    public boolean removePermission(final Rank rank, @NotNull final Action action)
    {
        final int flags = permissions.get(rank);
        if (Utils.testFlag(flags, action.getFlag()))
        {
            permissions.put(rank, Utils.unsetFlag(flags, action.getFlag()));
            return true;
        }
        return false;
    }

    @Override
    public boolean removePlayer(final UUID playerID)
    {
        return false;
    }

    /**
     * Toggle a permission flag.
     *
     * @param rank   the rank.
     * @param action the action.
     */
    public void togglePermission(final Rank rank, @NotNull final Action action)
    {
        permissions.put(rank, Utils.toggleFlag(permissions.get(rank), action.getFlag()));
    }

    @Nullable
    @Override
    public Map.Entry<UUID, Player> getOwnerEntry()
    {
        for (@NotNull final Map.Entry<UUID, Player> entry : players.entrySet())
        {
            if (entry.getValue().getRank().equals(Rank.OWNER))
            {
                return entry;
            }
        }
        return null;
    }

    @Override
    public boolean setOwner(final PlayerEntity player)
    {
        return false;
    }

    @Override
    public void setOwnerAbandoned()
    {
    }

    @NotNull
    @Override
    public UUID getOwner()
    {
        if (colonyOwner == null)
        {
            final Map.Entry<UUID, Player> owner = getOwnerEntry();
            if (owner != null)
            {
                colonyOwner = owner.getKey();
            }
            else
            {
                restoreOwnerIfNull();
            }
        }
        return colonyOwner;
    }

    /**
     * Deserialize content of class to a buffer.
     *
     * @param buf the buffer.
     */
    public void deserialize(@NotNull final PacketByteBuf buf)
    {
        userRank = Rank.valueOf(buf.readString(32767));

        //  Owners
        players.clear();
        final int numOwners = buf.readInt();
        for (int i = 0; i < numOwners; ++i)
        {
            final UUID id = PacketUtils.readUUID(buf);
            final String name = buf.readString(32767);
            final Rank rank = Rank.valueOf(buf.readString(32767));
            if (rank == Rank.OWNER)
            {
                colonyOwner = id;
            }

            players.put(id, new Player(id, name, rank));
        }

        //Permissions
        permissions.clear();
        final int numPermissions = buf.readInt();
        for (int i = 0; i < numPermissions; ++i)
        {
            final Rank rank = Rank.valueOf(buf.readString(32767));
            final int flags = buf.readInt();
            permissions.put(rank, flags);
        }
    }

    /**
     * Get the rank of a certain player.
     *
     * @param player the player.
     * @return the rank.
     */
    @NotNull
    public Rank getRank(@NotNull final PlayerEntity player)
    {
        return getRank(player.getUniqueID());
    }

    @Override
    public void restoreOwnerIfNull()
    {
        //Noop happens on the server side.
    }

    @NotNull
    @Override
    public Rank getRank(final UUID id)
    {
        final Player player = players.get(id);
        return player == null ? Rank.NEUTRAL : player.getRank();
    }

    @Override
    public boolean hasPermission(@NotNull final PlayerEntity player, @NotNull final Action action)
    {
        return hasPermission(getRank(player), action);
    }

    @Override
    public boolean addPlayer(@NotNull final String player, final Rank rank, final World world)
    {
        return false;
    }

    @Override
    public boolean addPlayer(@NotNull final UUID id, final String name, final Rank rank)
    {
        return false;
    }

    @Nullable
    @Override
    public String getOwnerName()
    {
        if (ownerName.isEmpty())
        {
            final Map.Entry<UUID, Player> owner = getOwnerEntry();
            if (owner != null)
            {
                ownerName = owner.getValue().getName();
            }
        }
        return ownerName;
    }

    @Override
    public boolean isSubscriber(@NotNull final PlayerEntity player)
    {
        return false;
    }

    @Override
    public boolean isColonyMember(@NotNull final PlayerEntity player)
    {
        return players.containsKey(player.getUniqueID());
    }
}
