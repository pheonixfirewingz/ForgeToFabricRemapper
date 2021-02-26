package com.minecolonies.coremod.colony.colony.requestsystem.resolvers.factory;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public class PrivateWorkerCraftingProductionResolverFactory implements IRequestResolverFactory<PrivateWorkerCraftingProductionResolver>
{
    ////// --------------------------- NBTConstants --------------------------- \\\\\\
    private static final String NBT_TOKEN    = "Token";
    private static final String NBT_LOCATION = "Location";
    ////// --------------------------- NBTConstants --------------------------- \\\\\\

    @NotNull
    @Override
    public TypeToken<? extends PrivateWorkerCraftingProductionResolver> getFactoryOutputType()
    {
        return TypeToken.of(PrivateWorkerCraftingProductionResolver.class);
    }

    @NotNull
    @Override
    public TypeToken<? extends ILocation> getFactoryInputType()
    {
        return TypeConstants.ILOCATION;
    }

    @NotNull
    @Override
    public PrivateWorkerCraftingProductionResolver getNewInstance(
      @NotNull final IFactoryController factoryController,
      @NotNull final ILocation iLocation,
      @NotNull final Object... context)
    {
        return new PrivateWorkerCraftingProductionResolver(iLocation, factoryController.getNewInstance(TypeConstants.ITOKEN));
    }

    @NotNull
    @Override
    public CompoundNBT serialize(
      @NotNull final IFactoryController controller, @NotNull final PrivateWorkerCraftingProductionResolver privateWorkerCraftingProductionResolverFactory)
    {
        final CompoundNBT compound = new CompoundNBT();
        compound.put(NBT_TOKEN, controller.serialize(privateWorkerCraftingProductionResolverFactory.getId()));
        compound.put(NBT_LOCATION, controller.serialize(privateWorkerCraftingProductionResolverFactory.getLocation()));
        return compound;
    }

    @NotNull
    @Override
    public PrivateWorkerCraftingProductionResolver deserialize(@NotNull final IFactoryController controller, @NotNull final CompoundNBT nbt)
    {
        final IToken<?> token = controller.deserialize(nbt.getCompound(NBT_TOKEN));
        final ILocation location = controller.deserialize(nbt.getCompound(NBT_LOCATION));

        return new PrivateWorkerCraftingProductionResolver(location, token);
    }

    @Override
    public void serialize(IFactoryController controller, PrivateWorkerCraftingProductionResolver input, PacketBuffer packetBuffer)
    {
        controller.serialize(packetBuffer, input.getId());
        controller.serialize(packetBuffer, input.getLocation());
    }

    @Override
    public PrivateWorkerCraftingProductionResolver deserialize(IFactoryController controller, PacketBuffer buffer) throws Throwable
    {
        final IToken<?> token = controller.deserialize(buffer);
        final ILocation location = controller.deserialize(buffer);

        return new PrivateWorkerCraftingProductionResolver(location, token);
    }

    @Override
    public short getSerializationId()
    {
        return 21;
    }
}
