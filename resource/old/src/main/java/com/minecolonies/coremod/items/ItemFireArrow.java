package com.minecolonies.coremod.items;

import com.minecolonies.api.entity.ModEntities;
import com.minecolonies.coremod.MinecoloniesEntitys;
import com.minecolonies.coremod.util.MineColoniesItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Class handling the Scepter for the Pharao.
 */
public class ItemFireArrow extends ArrowItem
{
    /**
     * Constructor method for the Chief Sword Item
     *
     * @param properties the properties.
     */
    public ItemFireArrow()
    {
        super(new MineColoniesItemSettings());
    }

    @NotNull
    @Override
    public PersistentProjectileEntity createArrow(@NotNull final World worldIn, @NotNull final ItemStack stack, final LivingEntity shooter)
    {
        //fixme: need entity code
        return null;
    }
}
