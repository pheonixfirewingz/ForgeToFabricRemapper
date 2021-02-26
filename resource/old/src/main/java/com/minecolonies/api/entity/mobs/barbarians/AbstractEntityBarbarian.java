package com.minecolonies.api.entity.mobs.barbarians;

import com.minecolonies.api.entity.mobs.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import static com.minecolonies.api.util.constant.RaiderConstants.*;

/**
 * Abstract for all Barbarian entities.
 */
public abstract class AbstractEntityBarbarian extends AbstractEntityMinecoloniesMob
{
    /**
     * Constructor method for Abstract Barbarians.
     *
     * @param type  the type.
     * @param world the world.
     */
    public AbstractEntityBarbarian(final EntityType<? extends AbstractEntityBarbarian> type, final World world)
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
    public RaiderType getRaiderType()
    {
        return RaiderType.BARBARIAN;
    }
}
