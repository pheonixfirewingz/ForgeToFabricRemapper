package com.minecraftabnormals.endergetic.common.tileentities;

import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.minecraftabnormals.abnormals_core.core.endimator.ControlledEndimation;
import com.minecraftabnormals.abnormals_core.core.util.MathUtil;
import com.minecraftabnormals.endergetic.api.util.StringUtils;
import com.minecraftabnormals.endergetic.common.blocks.poise.BolloomBudBlock;
import com.minecraftabnormals.endergetic.common.entities.bolloom.BolloomFruitEntity;
import com.minecraftabnormals.endergetic.common.entities.puffbug.PuffBugEntity;
import com.minecraftabnormals.endergetic.core.registry.EEBlocks;
import com.minecraftabnormals.endergetic.core.registry.EETileEntities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BolloomBudTileEntity extends TileEntity implements ITickableTileEntity {
	public final ControlledEndimation pedalAnimation = new ControlledEndimation(20, 20);
	private EnumMap<BudSide, SideData> sideData = Util.make(new EnumMap<>(BudSide.class), (side) -> {
		side.put(BudSide.NORTH, new SideData());
		side.put(BudSide.EAST, new SideData());
		side.put(BudSide.SOUTH, new SideData());
		side.put(BudSide.WEST, new SideData());
	});
	private int maxFruitHeight = 7;
	private UUID teleportingBug;

	public BolloomBudTileEntity() {
		super(EETileEntities.BOLLOOM_BUD.get());
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return super.getRenderBoundingBox().grow(1.0F);
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return super.getMaxRenderDistanceSquared() * 2;
	}

	@Override
	public void tick() {
		Random rand = new Random();

		this.sideData.forEach((side, sideData) -> {
			if (sideData.growTimer > 0 && sideData.growing) sideData.growTimer--;

			if (sideData.growing && sideData.growTimer <= 0) {
				if (!this.world.isRemote() && this.getBlockState().get(BolloomBudBlock.OPENED)) {
					int height = rand.nextInt(this.maxFruitHeight) + 1;
					BolloomFruitEntity fruit = new BolloomFruitEntity(this.world, this.pos, side.offsetPosition(this.pos).up(height - 1), height, side.direction);
					this.world.addEntity(fruit);
					sideData.fruitUUID = fruit.getUniqueID();
				}
				sideData.growing = false;
				sideData.growTimer = 0;
			}
		});

		if (this.getBlockState().get(BolloomBudBlock.OPENED)) {
			if (!this.world.isRemote() && this.shouldShutBud() && rand.nextInt(200) == 0) {
				this.world.setBlockState(this.pos, this.getBlockState().with(BolloomBudBlock.OPENED, false), 2);
				this.resetGrowing();
			}
		}

		this.pedalAnimation.update();

		this.pedalAnimation.tick();

		if (this.world.isAreaLoaded(this.pos, 1)) {
			boolean opened = this.getBlockState().get(BolloomBudBlock.OPENED);
			if (this.pedalAnimation.isDecrementing() != opened) {
				this.pedalAnimation.setDecrementing(opened);
			}
		}

		if (!this.world.isRemote) {
			if (this.teleportingBug != null) {
				Entity entity = ((ServerWorld) this.world).getEntityByUuid(this.teleportingBug);
				if (entity != null && !entity.isAlive()) {
					this.teleportingBug = null;
				} else if (entity == null) {
					this.teleportingBug = null;
				}
			}
		}
	}

	public void startGrowing(Random rand, int maxHeight, boolean instant) {
		boolean didOneGrow = false;
		this.maxFruitHeight = maxHeight;

		if (instant) {
			this.pedalAnimation.setTick(0);
		}

		for (Entry<BudSide, SideData> data : this.sideData.entrySet()) {
			if (rand.nextBoolean()) {
				data.getValue().growing = true;
				data.getValue().growTimer = instant ? 0 : rand.nextInt(220) + 60;
				didOneGrow = true;
			}
		}

		if (!didOneGrow) {
			SideData sideData = this.sideData.get(BudSide.random(rand));
			sideData.growing = true;
			sideData.growTimer = instant ? 0 : rand.nextInt(220) + 60;
		}
	}

	public void resetGrowing() {
		this.sideData.forEach((side, sideData) -> {
			sideData.fruitUUID = null;
			sideData.growing = false;
			sideData.growTimer = 0;
		});
		this.maxFruitHeight = 7;
	}

	public void setTeleportingBug(@Nullable PuffBugEntity puffbug) {
		this.teleportingBug = puffbug != null ? puffbug.getUniqueID() : null;
	}

	public boolean hasTeleportingBug() {
		return this.teleportingBug != null;
	}

	public boolean canBeOpened() {
		Block block = EEBlocks.BOLLOOM_BUD.get();

		for (Direction directions : Direction.values()) {
			if (this.world.getBlockState(this.pos.offset(directions, 2)).getBlock() == block) {
				return false;
			}
		}

		BlockPos north = this.pos.offset(Direction.NORTH);
		BlockPos south = this.pos.offset(Direction.SOUTH);

		if (this.world.getBlockState(north.east()).getBlock() == block || this.world.getBlockState(south.east()).getBlock() == block || this.world.getBlockState(north.west()).getBlock() == block || this.world.getBlockState(south.west()).getBlock() == block) {
			return false;
		}

		for (BudSide sides : BudSide.values()) {
			BlockPos sidePos = sides.offsetPosition(this.pos);
			if (!this.world.getFluidState(sidePos).isEmpty() || !this.world.getBlockState(sidePos).getCollisionShape(this.world, sidePos).isEmpty()) {
				return false;
			}
		}

		return !this.getBlockState().get(BolloomBudBlock.OPENED) && this.calculateFruitMaxHeight() >= 3;
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) {
		super.read(state, compound);

		this.maxFruitHeight = compound.contains("MaxFruitHeight") ? MathHelper.clamp(compound.getInt("MaxFruitHeight"), 1, 7) : 7;

		this.sideData.forEach((side, sideData) -> {
			String sideName = StringUtils.capitaliseFirstLetter(side.direction.toString());
			String sideUUID = compound.contains(sideName + "FruitUUID", 8) ? compound.getString(sideName + "FruitUUID") : "";

			sideData.fruitUUID = !sideUUID.isEmpty() ? UUID.fromString(sideUUID) : null;

			sideData.growing = compound.getBoolean("Is" + sideName + "Growing");
			sideData.growTimer = compound.getInt(sideName + "GrowTime");
		});

		this.pedalAnimation.read(compound);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);

		if (compound.contains("MaxFruitHeight")) {
			compound.putInt("MaxFruitHeight", this.maxFruitHeight);
		}

		this.sideData.forEach((side, sideData) -> {
			String sideName = StringUtils.capitaliseFirstLetter(side.direction.toString());

			if (sideData.fruitUUID == null) {
				compound.putString(sideName + "FruitUUID", "");
			} else {
				compound.putString(sideName + "FruitUUID", sideData.fruitUUID.toString());
			}

			compound.putBoolean("Is" + sideName + "Growing", sideData.growing);
			compound.putInt(sideName + "GrowTime", sideData.growTimer);
		});

		this.pedalAnimation.write(compound);

		return compound;
	}

	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 100, this.getUpdateTag());
	}

	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override
	public boolean onlyOpsCanSetNbt() {
		return true;
	}

	private boolean shouldShutBud() {
		boolean hasAFruit = false;
		for (Entry<BudSide, SideData> data : this.sideData.entrySet()) {
			if (data.getValue().hasFruit(this.world)) {
				hasAFruit = true;
			}
		}
		return !hasAFruit;
	}

	public int calculateFruitMaxHeight() {
		int[] maxHeights = new int[4];

		for (BudSide sides : BudSide.values()) {
			for (int y = 1; y < 7; y++) {
				if (this.world.isAirBlock(sides.offsetPosition(this.pos.up(y)))) {
					maxHeights[sides.id] = y;
				} else {
					break;
				}
			}
		}

		return MathUtil.getLowestValueInIntArray(maxHeights);
	}

	public enum BudSide {
		NORTH(Direction.NORTH, 0),
		EAST(Direction.EAST, 1),
		SOUTH(Direction.SOUTH, 2),
		WEST(Direction.WEST, 3);

		private final Direction direction;
		public final int id;

		BudSide(Direction direction, int id) {
			this.direction = direction;
			this.id = id;
		}

		public BlockPos offsetPosition(BlockPos pos) {
			return pos.offset(this.direction);
		}

		public static BudSide random(Random rand) {
			return values()[rand.nextInt(values().length)];
		}
	}

	static class SideData {
		@Nullable
		private UUID fruitUUID;
		private int growTimer;
		private boolean growing;

		public BolloomFruitEntity getFruit(World world) {
			if (!world.isRemote() && this.fruitUUID != null) {
				Entity entity = ((ServerWorld) world).getEntityByUuid(this.fruitUUID);
				if (entity instanceof BolloomFruitEntity) {
					return (BolloomFruitEntity) entity;
				}
			}
			return null;
		}

		public boolean hasFruit(World world) {
			if (this.growing) {
				return true;
			}
			return this.getFruit(world) != null && this.getFruit(world).isAlive() && !this.getFruit(world).isUntied();
		}
	}
}