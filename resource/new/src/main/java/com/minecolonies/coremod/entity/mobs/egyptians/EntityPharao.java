package com.minecolonies.coremod.entity.mobs.egyptians;



/**
 * Class for the Pharao entity.
 */
public class EntityPharao extends AbstractEntityEgyptian implements IPharaoEntity
{

    /**
     * Constructor of the entity.
     *
     * @param type    the entity type.
     * @param worldIn world to construct it in.
     */
    public EntityPharao(final EntityType<? extends EntityPharao> type, final World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        final double chiefArmor = difficulty * CHIEF_BONUS_ARMOR;
        this.getAttribute(Attributes.ARMOR).setBaseValue(chiefArmor);
        this.getAttribute(MOB_ATTACK_DAMAGE).setBaseValue(baseDamage + 1.0);
        this.setEnvDamageInterval((int) (BASE_ENV_DAMAGE_RESIST * 2 * difficulty));
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(baseHealth * 4.5);
        this.setHealth(this.getMaxHealth());
    }
}
