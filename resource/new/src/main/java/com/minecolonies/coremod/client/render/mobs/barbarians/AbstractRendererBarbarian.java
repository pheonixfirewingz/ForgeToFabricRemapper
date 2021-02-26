package com.minecolonies.coremod.client.render.mobs.barbarians;


/**
 * Abstract for rendering Barbarians.
 */
public abstract class AbstractRendererBarbarian<T extends AbstractEntityBarbarian, M extends BipedModel<T>> extends BipedRenderer<T, M>
{
    public AbstractRendererBarbarian(final EntityRendererManager renderManagerIn, final M modelBipedIn, final float shadowSize)
    {
        super(renderManagerIn, modelBipedIn, shadowSize);
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
    }
}
