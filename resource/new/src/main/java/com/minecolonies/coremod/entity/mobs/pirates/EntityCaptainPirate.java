package com.minecolonies.coremod.entity.mobs.pirates;



/**
 * Class for the Chief Pirate entity.
 */
public class EntityCaptainPirate extends AbstractEntityPirate implements ICaptainPirateEntity
{

    /**
     * Constructor of the entity.
     *
     * @param type    the entity type.
     * @param worldIn world to construct it in.
     */
    public EntityCaptainPirate(final EntityType<? extends EntityCaptainPirate> type, final World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        this.getAttribute(Attributes.ARMOR).setBaseValue(-1);
        this.getAttribute(MOB_ATTACK_DAMAGE).setBaseValue(baseDamage + 2.0);
        this.setEnvDamageInterval((int) (BASE_ENV_DAMAGE_RESIST * 2 * difficulty));
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(baseHealth * 1.3);
        this.setHealth(this.getMaxHealth());
    }
}
