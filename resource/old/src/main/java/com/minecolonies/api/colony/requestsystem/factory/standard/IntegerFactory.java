package com.minecolonies.api.colony.requestsystem.factory.standard;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.factory.*;
import com.minecolonies.api.util.constant.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import org.jetbrains.annotations.NotNull;

public class IntegerFactory implements IFactory<FactoryVoidInput, Integer>
{
    @NotNull
    @Override
    public TypeToken<? extends Integer> getFactoryOutputType()
    {
        return TypeConstants.INTEGER;
    }

    @NotNull
    @Override
    public TypeToken<? extends FactoryVoidInput> getFactoryInputType()
    {
        return TypeConstants.FACTORYVOIDINPUT;
    }

    @NotNull
    @Override
    public Integer getNewInstance(
      @NotNull final IFactoryController factoryController, @NotNull final FactoryVoidInput factoryVoidInput, @NotNull final Object... context) throws IllegalArgumentException
    {
        return new Integer(0);
    }

    @NotNull
    @Override
    public CompoundNBT serialize(@NotNull final IFactoryController controller, @NotNull final Integer integer)
    {
        CompoundNBT compound = new CompoundNBT();

        compound.putInt(NbtTagConstants.TAG_VALUE, integer);

        return compound;
    }

    @NotNull
    @Override
    public Integer deserialize(@NotNull final IFactoryController controller, @NotNull final CompoundNBT nbt)
    {
        return nbt.getInt(NbtTagConstants.TAG_VALUE);
    }

    @Override
    public void serialize(IFactoryController controller, Integer input, PacketBuffer packetBuffer)
    {
        packetBuffer.writeInt(input);
    }

    @Override
    public Integer deserialize(IFactoryController controller, PacketBuffer buffer) throws Throwable
    {
        return buffer.readInt();
    }

    @Override
    public short getSerializationId()
    {
        return 30;
    }
}
