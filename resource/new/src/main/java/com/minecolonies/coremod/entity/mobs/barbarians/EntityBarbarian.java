package com.minecolonies.coremod.entity.mobs.barbarians;


/**
 * Class for the Barbarian entity.
 */
public class EntityBarbarian extends AbstractEntityBarbarian implements IMeleeBarbarianEntity
{

    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     * @param type    the entity type.
     */
    public EntityBarbarian(final EntityType<? extends EntityBarbarian> type, final World worldIn)
    {
        super(type, worldIn);
    }
}
