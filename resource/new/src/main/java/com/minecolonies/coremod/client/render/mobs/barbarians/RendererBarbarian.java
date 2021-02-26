package com.minecolonies.coremod.client.render.mobs.barbarians;


/**
 * Renderer used for Barbarians And Archer Barbarians.
 */
public class RendererBarbarian extends AbstractRendererBarbarian<AbstractEntityBarbarian, BipedModel<AbstractEntityBarbarian>>
{
    /**
     * Texture of the entity.
     */
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecolonies:textures/entity/raiders/barbarian1.png");

    /**
     * Constructor method for renderer
     *
     * @param renderManagerIn the renderManager
     */
    public RendererBarbarian(final EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new BipedModel<>(1.0F), 0.5F);
    }

    @Override
    public ResourceLocation getEntityTexture(final AbstractEntityBarbarian entity)
    {
        return TEXTURE;
    }
}
