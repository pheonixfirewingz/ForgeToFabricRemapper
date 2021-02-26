package com.minecolonies.api.entity.mobs.egyptians;

import com.minecolonies.api.entity.mobs.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import static com.minecolonies.api.util.constant.RaiderConstants.*;

/**
 * Abstract for all egyptian entities.
 */
public abstract class AbstractEntityEgyptian extends AbstractEntityMinecoloniesMob
{
    /**
     * Constructor method for Abstract egyptian..
     *
     * @param type  the type.
     * @param world the world.
     */
    public AbstractEntityEgyptian(final EntityType<? extends AbstractEntityEgyptian> type, final World world)
    {
        super(type, world);
    }

    @Override
    public void playAmbientSound()
    {
        final SoundEvent soundevent = this.getAmbientSound();

        if (soundevent != null && world.rand.nextInt(OUT_OF_ONE_HUNDRED) <= ONE)
        {
            this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public boolean canSpawn(final IWorld worldIn, final SpawnReason spawnReasonIn)
    {
        return true;
    }

    @Override
    public RaiderType getRaiderType()
    {
        return RaiderType.EGYPTIAN;
    }
}
