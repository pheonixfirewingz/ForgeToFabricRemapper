package com.minecolonies.coremod.colony.colony;

import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.*;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_ID;
import static com.minecolonies.api.util.constant.SchematicTagConstants.TAG_SITTING;

/**
 * Data for visitors
 */
public class VisitorData extends CitizenData
{
    /**
     * Recruit nbt tag
     */
    private static final String TAG_RECRUIT_COST = "rcost";

    /**
     * The position the citizen is sitting at
     */
    private BlockPos sittingPosition = BlockPos.ZERO;

    /**
     * The recruitment level, used for stats/equipment and costs
     */
    private ItemStack recruitCost = ItemStack.EMPTY;

    /**
     * Create a CitizenData given an ID. Used as a super-constructor or during loading.
     *
     * @param id     ID of the Citizen.
     * @param colony Colony the Citizen belongs to.
     */
    public VisitorData(final int id, final Colony colony)
    {
        super(id, colony);
    }

    
    public CompoundTag serializeNBT()
    {
        CompoundTag compoundNBT = super.serializeNBT();
        CompoundTag item = new CompoundTag();
        recruitCost.write(item);
        compoundNBT.put(TAG_RECRUIT_COST, item);
        BlockPosUtil.write(compoundNBT, TAG_SITTING, sittingPosition);
        return compoundNBT;
    }

    
    public void deserializeNBT(final CompoundTag nbtTagCompound)
    {
        super.deserializeNBT(nbtTagCompound);
        sittingPosition = BlockPosUtil.read(nbtTagCompound, TAG_SITTING);
        recruitCost = ItemStack.read(nbtTagCompound.getCompound(TAG_RECRUIT_COST));
    }

    
    public void setRecruitCosts(final ItemStack item)
    {
        this.recruitCost = item;
    }

    
    public ItemStack getRecruitCost()
    {
        return recruitCost;
    }

    /**
     * Loads this citizen data from nbt
     *
     * @param colony colony to load for
     * @param nbt    nbt compound to read from
     * @return new CitizenData
     */
    public static VisitorData loadVisitorFromNBT(final Colony colony, final CompoundTag nbt)
    {
        final IVisitorData data = new VisitorData(nbt.getInt(TAG_ID), colony);
        data.deserializeNBT(nbt);
        return data;
    }

    
    public void serializeViewNetworkData(@NotNull final PacketByteBuf buf)
    {
        super.serializeViewNetworkData(buf);
        buf.writeItemStack(recruitCost);
    }

    
    public BlockPos getSittingPosition()
    {
        return sittingPosition;
    }

    
    public void setSittingPosition(final BlockPos pos)
    {
        this.sittingPosition = pos;
    }

    
    public void updateEntityIfNecessary()
    {
        if (getEntity().isPresent())
        {
            final Entity entity = getEntity().get();
            if (entity.isAlive() && entity.addedToChunk && WorldUtil.isEntityBlockLoaded(entity.world, entity.getPosition()))
            {
                return;
            }
        }

        if (getLastPosition() != BlockPos.ZERO && (getLastPosition().getX() != 0 && getLastPosition().getZ() != 0) && WorldUtil.isEntityBlockLoaded(getColony().getWorld(),
          getLastPosition()))
        {
            getColony().getVisitorManager().spawnOrCreateCivilian(this, getColony().getWorld(), getLastPosition(), true);
        }
        else if (getHomeBuilding() != null)
        {
            if (WorldUtil.isEntityBlockLoaded(getColony().getWorld(), getHomeBuilding().getID()))
            {
                final BlockPos spawnPos = BlockPosUtil.findAround(getColony().getWorld(), getHomeBuilding().getID(), 1, 1, bs -> bs.getMaterial() == Material.AIR);
                if (spawnPos != null)
                {
                    getColony().getVisitorManager().spawnOrCreateCivilian(this, getColony().getWorld(), spawnPos, true);
                }
            }
        }
    }

    
    public void applyResearchEffects()
    {
        // no research effects for now
    }
}
