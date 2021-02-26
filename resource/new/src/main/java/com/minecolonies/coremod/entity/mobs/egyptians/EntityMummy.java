package com.minecolonies.coremod.entity.mobs.egyptians;


/**
 * Class for the Mummy entity.
 */
public class EntityMummy extends AbstractEntityEgyptian implements IMeleeMummyEntity
{

    /**
     * Constructor of the entity.
     *
     * @param type    the entity type.
     * @param worldIn world to construct it in.
     */
    public EntityMummy(final EntityType<? extends EntityMummy> type, final World worldIn)
    {
        super(type, worldIn);
        this.moveController = new MovementHandler(this);
    }
}
