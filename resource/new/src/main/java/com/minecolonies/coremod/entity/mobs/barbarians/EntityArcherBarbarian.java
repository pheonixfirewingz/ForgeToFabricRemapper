package com.minecolonies.coremod.entity.mobs.barbarians;


/**
 * Class for the Archer Barbarian entity.
 */
public class EntityArcherBarbarian extends AbstractEntityBarbarian implements IArcherBarbarianEntity
{

    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     * @param type    the entity type.
     */
    public EntityArcherBarbarian(final EntityType<? extends EntityArcherBarbarian> type, final World worldIn)
    {
        super(type, worldIn);
    }
}
