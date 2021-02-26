package com.minecolonies.coremod.entity;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Custom arrow entity for the fire arrows.
 */
public class FireArrowEntity extends CustomArrowEntity
{
    public FireArrowEntity(EntityType<? extends ArrowEntity> entity, World world)
    {
        super(entity, world);
    }

    @Override
    public void setShooter(@Nullable final Entity shooter)
    {
        super.setShooter(shooter);
        this.setPosition(shooter.getPosX(), shooter.getPosYEye() - (double) 0.1F, shooter.getPosZ());
    }

    @NotNull
    @Override
    protected ItemStack getArrowStack()
    {
        return new ItemStack(ModItems.firearrow, 1);
    }
}
