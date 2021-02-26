package com.minecolonies.coremod.colony.colony.requestsystem.resolvers.factory;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class PublicWorkerCraftingRequestResolverFactory implements IRequestResolverFactory<PublicWorkerCraftingRequestResolver>
{
    ////// --------------------------- NBTConstants --------------------------- \\\\\\
    private static final String NBT_TOKEN    = "Token";
    private static final String NBT_LOCATION = "Location";
    ////// --------------------------- NBTConstants --------------------------- \\\\\\

    @NotNull
    @Override
    public TypeToken<? extends PublicWorkerCraftingRequestResolver> getFactoryOutputType()
    {
        return TypeToken.of(PublicWorkerCraftingRequestResolver.class);
    }

    @NotNull
    @Override
    public TypeToken<? extends ILocation> getFactoryInputType()
    {
        return TypeConstants.ILOCATION;
    }

    @NotNull
    @Override
    public PublicWorkerCraftingRequestResolver getNewInstance(
      @NotNull final IFactoryController factoryController,
      @NotNull final ILocation iLocation,
      @NotNull final Object... context)
    {
        return new PublicWorkerCraftingRequestResolver(iLocation, factoryController.getNewInstance(TypeConstants.ITOKEN));
    }

    @NotNull
    @Override
    public CompoundNBT serialize(@NotNull final IFactoryController controller, @NotNull final PublicWorkerCraftingRequestResolver publicWorkerCraftingRequestResolverFactory)
    {
        final CompoundNBT compound = new CompoundNBT();
        compound.put(NBT_TOKEN, controller.serialize(publicWorkerCraftingRequestResolverFactory.getId()));
        compound.put(NBT_LOCATION, controller.serialize(publicWorkerCraftingRequestResolverFactory.getLocation()));
        return compound;
    }

    @NotNull
    @Override
    public PublicWorkerCraftingRequestResolver deserialize(@NotNull final IFactoryController controller, @NotNull final CompoundNBT nbt)
    {
        final IToken<?> token = controller.deserialize(nbt.getCompound(NBT_TOKEN));
        final ILocation location = controller.deserialize(nbt.getCompound(NBT_LOCATION));

        return new PublicWorkerCraftingRequestResolver(location, token);
    }

    @Override
    public void serialize(IFactoryController controller, PublicWorkerCraftingRequestResolver input, PacketBuffer packetBuffer)
    {
        controller.serialize(packetBuffer, input.getId());
        controller.serialize(packetBuffer, input.getLocation());
    }

    @Override
    public PublicWorkerCraftingRequestResolver deserialize(IFactoryController controller, PacketBuffer buffer) throws Throwable
    {
        final IToken<?> token = controller.deserialize(buffer);
        final ILocation location = controller.deserialize(buffer);

        return new PublicWorkerCraftingRequestResolver(location, token);
    }

    @Override
    public short getSerializationId()
    {
        return 20;
    }
}
