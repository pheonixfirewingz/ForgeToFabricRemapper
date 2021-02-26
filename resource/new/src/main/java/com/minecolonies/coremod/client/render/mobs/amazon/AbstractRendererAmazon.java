package com.minecolonies.coremod.client.render.mobs.amazon;


/**
 * Abstract for rendering amazons.
 */
public abstract class AbstractRendererAmazon<T extends AbstractEntityAmazon, M extends BipedModel<T>> extends BipedRenderer<T, M>
{
    public AbstractRendererAmazon(final EntityRendererManager renderManagerIn, final M modelBipedIn, final float shadowSize)
    {
        super(renderManagerIn, modelBipedIn, shadowSize);
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
    }
}
