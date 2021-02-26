package com.minecolonies.coremod.colony.colony.requestsystem.data;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StandardDataStoreManager implements IDataStoreManager
{

    private final Map<IToken<?>, IDataStore> storeMap;

    public StandardDataStoreManager(final Map<IToken<?>, IDataStore> storeMap) {this.storeMap = storeMap;}

    public StandardDataStoreManager()
    {
        this(new HashMap<>());
    }

    @Override
    public <T extends IDataStore> T get(final IToken<?> id, final TypeToken<T> type)
    {
        return get(id, () -> StandardFactoryController.getInstance().getNewInstance(type));
    }

    @Override
    public <T extends IDataStore> T get(final IToken<?> id, final Supplier<T> factory)
    {
        if (!storeMap.containsKey(id))
        {
            final T defaultInstance = factory.get();

            defaultInstance.setId(id);
            storeMap.put(id, defaultInstance);
        }

        return (T) storeMap.get(id);
    }

    @Override
    public void remove(final IToken<?> id)
    {
        storeMap.remove(id);
    }

    @Override
    public void removeAll()
    {
        storeMap.clear();
    }

    public static class Factory implements IFactory<FactoryVoidInput, StandardDataStoreManager>
    {

        @NotNull
        @Override
        public TypeToken<? extends StandardDataStoreManager> getFactoryOutputType()
        {
            return TypeToken.of(StandardDataStoreManager.class);
        }

        @NotNull
        @Override
        public TypeToken<? extends FactoryVoidInput> getFactoryInputType()
        {
            return TypeConstants.FACTORYVOIDINPUT;
        }

        @NotNull
        @Override
        public StandardDataStoreManager getNewInstance(
          @NotNull final IFactoryController factoryController, @NotNull final FactoryVoidInput factoryVoidInput, @NotNull final Object... context) throws IllegalArgumentException
        {
            return new StandardDataStoreManager();
        }

        @NotNull
        @Override
        public CompoundNBT serialize(@NotNull final IFactoryController controller, @NotNull final StandardDataStoreManager standardDataStoreManager)
        {
            final CompoundNBT compound = new CompoundNBT();

            compound.put(NbtTagConstants.TAG_LIST, standardDataStoreManager.storeMap.keySet().stream().map(iToken -> {
                final CompoundNBT entryCompound = new CompoundNBT();

                entryCompound.put(NbtTagConstants.TAG_TOKEN, controller.serialize(iToken));
                entryCompound.put(NbtTagConstants.TAG_VALUE, controller.serialize(standardDataStoreManager.storeMap.get(iToken)));

                return entryCompound;
            }).collect(NBTUtils.toListNBT()));

            return compound;
        }

        @NotNull
        @Override
        public StandardDataStoreManager deserialize(@NotNull final IFactoryController controller, @NotNull final CompoundNBT nbt) throws Throwable
        {
            final Map<IToken<?>, IDataStore> storeMap = NBTUtils.streamCompound(nbt.getList(NbtTagConstants.TAG_LIST, Constants.NBT.TAG_COMPOUND)).map(CompoundNBT -> {
                final IToken<?> token = controller.deserialize(CompoundNBT.getCompound(NbtTagConstants.TAG_TOKEN));
                final IDataStore store = controller.deserialize(CompoundNBT.getCompound(NbtTagConstants.TAG_VALUE));

                return new Tuple<>(token, store);
            }).collect(Collectors.toMap(Tuple::getA, Tuple::getB));

            return new StandardDataStoreManager(storeMap);
        }

        @Override
        public void serialize(IFactoryController controller, StandardDataStoreManager input, PacketBuffer packetBuffer)
        {
            packetBuffer.writeInt(input.storeMap.size());
            input.storeMap.forEach((key, value) -> {
                controller.serialize(packetBuffer, key);
                controller.serialize(packetBuffer, value);
            });
        }

        @Override
        public StandardDataStoreManager deserialize(IFactoryController controller, PacketBuffer buffer)
          throws Throwable
        {
            final Map<IToken<?>, IDataStore> storeMap = new HashMap<>();
            final int storeSize = buffer.readInt();
            for (int i = 0; i < storeSize; ++i)
            {
                storeMap.put(controller.deserialize(buffer), controller.deserialize(buffer));
            }

            return new StandardDataStoreManager(storeMap);
        }

        @Override
        public short getSerializationId()
        {
            return 40;
        }
    }
}
