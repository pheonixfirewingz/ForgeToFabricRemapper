package com.minecolonies.api.entity.mobs.pirates;

import net.minecraft.command.*;
import net.minecraft.entity.monster.*;
import net.minecraft.nbt.*;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IPirateEntity extends IMob, ICommandSource, ICapabilitySerializable<CompoundNBT>
{
}
