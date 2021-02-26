package com.minecolonies.coremod.colony.colony.requestsystem.resolvers.factory;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class DeliveryRequestResolverFactory implements IRequestResolverFactory<DeliveryRequestResolver>
{
    ////// --------------------------- NBTConstants --------------------------- \\\\\\
    private static final String NBT_TOKEN    = "Token";
    private static final String NBT_LOCATION = "Location";
    ////// --------------------------- NBTConstants --------------------------- \\\\\\

    @NotNull
    @Override
    public TypeToken<? extends DeliveryRequestResolver> getFactoryOutputType()
    {
        return TypeToken.of(DeliveryRequestResolver.class);
    }

    @NotNull
    @Override
    public TypeToken<? extends ILocation> getFactoryInputType()
    {
        return TypeConstants.ILOCATION;
    }

    @NotNull
    @Override
    public DeliveryRequestResolver getNewInstance(
      @NotNull final IFactoryController factoryController,
      @NotNull final ILocation iLocation,
      @NotNull final Object... context)
      throws IllegalArgumentException
    {
        return new DeliveryRequestResolver(iLocation, factoryController.getNewInstance(TypeConstants.ITOKEN));
    }

    @NotNull
    @Override
    public CompoundNBT serialize(
      @NotNull final IFactoryController controller, @NotNull final DeliveryRequestResolver deliveryRequestResolver)
    {
        final CompoundNBT compound = new CompoundNBT();
        compound.put(NBT_TOKEN, controller.serialize(deliveryRequestResolver.getId()));
        compound.put(NBT_LOCATION, controller.serialize(deliveryRequestResolver.getLocation()));
        return compound;
    }

    @NotNull
    @Override
    public DeliveryRequestResolver deserialize(@NotNull final IFactoryController controller, @NotNull final CompoundNBT nbt)
    {
        final IToken<?> token = controller.deserialize(nbt.getCompound(NBT_TOKEN));
        final ILocation location = controller.deserialize(nbt.getCompound(NBT_LOCATION));

        return new DeliveryRequestResolver(location, token);
    }

    @Override
    public void serialize(IFactoryController controller, DeliveryRequestResolver input, PacketBuffer packetBuffer)
    {
        controller.serialize(packetBuffer, input.getId());
        controller.serialize(packetBuffer, input.getLocation());
    }

    @Override
    public DeliveryRequestResolver deserialize(IFactoryController controller, PacketBuffer buffer) throws Throwable
    {
        final IToken<?> token = controller.deserialize(buffer);
        final ILocation location = controller.deserialize(buffer);

        return new DeliveryRequestResolver(location, token);
    }

    @Override
    public short getSerializationId()
    {
        return 15;
    }
}
