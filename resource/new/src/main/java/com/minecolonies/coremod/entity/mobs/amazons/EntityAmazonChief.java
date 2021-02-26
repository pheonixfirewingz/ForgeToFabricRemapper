package com.minecolonies.coremod.entity.mobs.amazons;



/**
 * Class for the Amazon Chief entity.
 */
public class EntityAmazonChief extends AbstractEntityAmazon implements IAmazonChief
{

    /**
     * Constructor of the entity.
     *
     * @param type    the entity type.
     * @param worldIn world to construct it in.
     */
    public EntityAmazonChief(final EntityType<? extends EntityAmazonChief> type, final World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        final double chiefArmor = difficulty * CHIEF_BONUS_ARMOR * 2;
        this.getAttribute(Attributes.ARMOR).setBaseValue(chiefArmor);
        this.getAttribute(MOB_ATTACK_DAMAGE).setBaseValue(baseDamage + 1.0);
        this.setEnvDamageInterval((int) (BASE_ENV_DAMAGE_RESIST * 2 * difficulty));
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(baseHealth * 1.5);
        this.setHealth(this.getMaxHealth());
    }
}
