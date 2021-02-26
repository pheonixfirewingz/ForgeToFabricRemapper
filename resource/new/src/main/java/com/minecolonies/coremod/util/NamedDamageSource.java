package com.minecolonies.coremod.util;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Specific named damage source.
 */
public class NamedDamageSource extends EntityDamageSource
{
    /**
     * Create a specific named damage source.
     *
     * @param damageTypeIn         the string to print.
     * @param damageSourceEntityIn the inflicting entity.
     */
    public NamedDamageSource(final String damageTypeIn, @Nullable final Entity damageSourceEntityIn)
    {
        super(damageTypeIn, damageSourceEntityIn);
    }

    @NotNull
    @Override
    public ITextComponent getDeathMessage(LivingEntity entityLivingBaseIn)
    {
        return new TranslationTextComponent(this.damageType, entityLivingBaseIn.getName());
    }

    /**
     * World difficulty scaling of damage against players, disabled as we already do take world difficulty into account.
     *
     * @return false
     */
    public boolean isDifficultyScaled()
    {
        return false;
    }
}
