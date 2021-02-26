package com.minecraftabnormals.endergetic.common.entities.booflo;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.minecraftabnormals.abnormals_core.core.api.IAgeableEntity;
import com.minecraftabnormals.abnormals_core.core.endimator.ControlledEndimation;
import com.minecraftabnormals.abnormals_core.core.endimator.Endimation;
import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatedEntity;
import com.minecraftabnormals.abnormals_core.core.util.MathUtil;
import com.minecraftabnormals.abnormals_core.core.util.NetworkUtil;
import com.minecraftabnormals.endergetic.api.entity.pathfinding.EndergeticFlyingPathNavigator;
import com.minecraftabnormals.endergetic.api.entity.util.DetectionHelper;
import com.minecraftabnormals.endergetic.api.entity.util.EntityItemStackHelper;
import com.minecraftabnormals.endergetic.api.entity.util.RayTraceHelper;
import com.minecraftabnormals.endergetic.client.particles.EEParticles;
import com.minecraftabnormals.endergetic.common.advancement.EECriteriaTriggers;
import com.minecraftabnormals.endergetic.common.entities.bolloom.BolloomFruitEntity;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloAttackGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloBoofGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloBreedGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloEatFruitGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloEatPuffBugGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloFaceRandomGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloGiveBirthGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloGroundHopGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloHuntFruitGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloHuntPuffBugGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloNearestAttackableTargetGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloSinkGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloSlamGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloSwimGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.ai.BoofloTemptGoal;
import com.minecraftabnormals.endergetic.common.entities.puffbug.PuffBugEntity;
import com.minecraftabnormals.endergetic.core.registry.EEBlocks;
import com.minecraftabnormals.endergetic.core.registry.EEEntities;
import com.minecraftabnormals.endergetic.core.registry.EEItems;
import com.minecraftabnormals.endergetic.core.registry.EESounds;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BoofloEntity extends EndimatedEntity implements IAgeableEntity {
	public static final Predicate<Entity> IS_SCARED_BY = (entity) -> {
		if (entity instanceof PlayerEntity) {
			return !entity.isSpectator() && !((PlayerEntity) entity).isCreative();
		}
		return false;
	};
	private static final int BOOST_POWER_INCREMENT = 10;
	private static final int MAX_BOOST_POWER = 182;
	private static final int HALF_BOOST_POWER = 91;
	private static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<Optional<UUID>> LAST_FED_UNIQUE_ID = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<Boolean> ON_GROUND = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> TAMED = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> MOVING_IN_AIR = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> BOOFED = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> PREGNANT = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> HUNGRY = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> HAS_FRUIT = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> FRUITS_NEEDED = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> BOOST_STATUS = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> BOOST_POWER = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> LOVE_TICKS = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> ATTACK_TARGET = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> BRACELETS_COLOR = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Float> LOCKED_YAW = EntityDataManager.createKey(BoofloEntity.class, DataSerializers.FLOAT);

	public static final Endimation CROAK = new Endimation(55);
	public static final Endimation HOP = new Endimation(25);
	public static final Endimation HURT = new Endimation(15);
	public static final Endimation BIRTH = new Endimation(140);
	public static final Endimation INFLATE = new Endimation(10);
	public static final Endimation SWIM = new Endimation(20);
	public static final Endimation EAT = new Endimation(160);
	public static final Endimation CHARGE = new Endimation(75);
	public static final Endimation SLAM = new Endimation(10);
	public static final Endimation GROWL = new Endimation(60);

	private static final EntitySize BOOFED_SIZE = EntitySize.fixed(2.0F, 1.5F);
	public final ControlledEndimation OPEN_JAW = new ControlledEndimation(25, 0);
	public final ControlledEndimation FRUIT_HOVER = new ControlledEndimation(8, 0);

	private final EndergeticFlyingPathNavigator attackingNavigator;
	private UUID playerInLove;
	public int hopDelay;
	public int breedDelay;
	private int croakDelay;
	private int deflateDelay;
	public boolean wasBred;
	private boolean shouldPlayLandSound;
	private boolean wasOnGround;

	public BoofloEntity(EntityType<? extends BoofloEntity> type, World world) {
		super(type, world);
		this.attackingNavigator = new EndergeticFlyingPathNavigator(this, this.world);
		this.moveController = new GroundMoveHelperController(this);
		this.hopDelay = this.getDefaultGroundHopDelay();
		this.stepHeight = 1.0F;
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
		this.dataManager.register(LAST_FED_UNIQUE_ID, Optional.empty());
		this.dataManager.register(ON_GROUND, false);
		this.dataManager.register(TAMED, false);
		this.dataManager.register(MOVING_IN_AIR, false);
		this.dataManager.register(BOOFED, false);
		this.dataManager.register(PREGNANT, false);
		this.dataManager.register(HUNGRY, this.getRNG().nextFloat() < 0.6F);
		this.dataManager.register(HAS_FRUIT, false);
		this.dataManager.register(BOOST_STATUS, (byte) 0);
		this.dataManager.register(BOOST_POWER, 0);
		this.dataManager.register(FRUITS_NEEDED, this.getRNG().nextInt(3) + 2);
		this.dataManager.register(LOVE_TICKS, 0);
		this.dataManager.register(ATTACK_TARGET, 0);
		this.dataManager.register(BRACELETS_COLOR, DyeColor.YELLOW.getId());
		this.dataManager.register(LOCKED_YAW, 0.0F);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new BoofloGiveBirthGoal(this));
		this.goalSelector.addGoal(0, new BoofloEatPuffBugGoal(this));
		this.goalSelector.addGoal(1, new BoofloBoofGoal(this));
		this.goalSelector.addGoal(1, new BoofloSlamGoal(this));
		this.goalSelector.addGoal(1, new BoofloBreedGoal(this));
		this.goalSelector.addGoal(2, new BoofloEatFruitGoal(this));
		this.goalSelector.addGoal(3, new BoofloSinkGoal(this));
		this.goalSelector.addGoal(5, new BoofloTemptGoal(this));
		this.goalSelector.addGoal(6, new BoofloHuntPuffBugGoal(this));
		this.goalSelector.addGoal(6, new BoofloAttackGoal(this));
		this.goalSelector.addGoal(7, new BoofloHuntFruitGoal(this, 1.0F));
		this.goalSelector.addGoal(8, new BoofloSwimGoal(this, 1.0F, 15));
		this.goalSelector.addGoal(9, new BoofloFaceRandomGoal(this));
		this.goalSelector.addGoal(10, new BoofloGroundHopGoal(this));

		this.targetSelector.addGoal(1, new BoofloNearestAttackableTargetGoal<>(this, PuffBugEntity.class, 175, true, false));
		this.targetSelector.addGoal(2, new BoofloNearestAttackableTargetGoal<>(this, BolloomFruitEntity.class, true));
	}

	@Override
	public void tick() {
		super.tick();

		if (this.breedDelay > 0) this.breedDelay--;
		if (this.deflateDelay > 0) this.deflateDelay--;
		if (this.croakDelay > 0) this.croakDelay--;

		if (this.isBoofed()) {
			if (this.hasAggressiveAttackTarget()) {
				this.navigator = this.attackingNavigator;
			} else {
				if (this.navigator instanceof EndergeticFlyingPathNavigator) {
					this.navigator = new FlyingPathNavigator(this, this.world) {

						@Override
						public boolean canEntityStandOnPos(BlockPos pos) {
							return this.world.isAirBlock(pos);
						}

					};
				}

				if (this.getBoofloAttackTarget() == null && this.hasPath() && this.getMotion().length() < 0.25F && RayTraceHelper.rayTrace(this, 2.0D, 1.0F).getType() == Type.BLOCK) {
					this.getNavigator().clearPath();
				}
			}
		}

		if (!this.world.isRemote) {
			if (this.isEndimationPlaying(BoofloEntity.CHARGE) && this.getAnimationTick() >= 15) {
				this.addVelocity(0.0F, -0.225F, 0.0F);
			}

			this.setOnGround(!this.world.hasNoCollisions(DetectionHelper.checkOnGround(this.getBoundingBox())));

			int power = this.getBoostPower();
			if (power > 0 && !this.isBoostExpanding()) {
				this.setBoostPower(Math.max(0, power - (this.isOnGround() ? 3 : 2)));
				if (this.getBoostPower() <= 0) {
					this.setBoostLocked(false);
				}
			} else if (this.isBoostExpanding()) {
				if (power < MAX_BOOST_POWER) {
					if (this.isBoostLocked()) {
						int incremented = power + BOOST_POWER_INCREMENT;
						this.setBoostPower(Math.min(HALF_BOOST_POWER, incremented));
						if (incremented >= HALF_BOOST_POWER) {
							this.setBoostExpanding(false);
						}
					} else {
						this.setBoostPower(Math.min(MAX_BOOST_POWER, power + BOOST_POWER_INCREMENT));
					}
				} else {
					if (!this.isBoostLocked() && this.getControllingPassenger() instanceof PlayerEntity) {
						NetworkUtil.setPlayingAnimationMessage(this, INFLATE);
						this.playSound(this.getInflateSound(), 0.75F, 1.0F);
					}
					this.setBoostExpanding(false);
				}
			}

			if (this.isOnGround() && !this.isBoofed() && this.isBoostExpanding()) {
				this.setBoostExpanding(false);
				this.setBoostLocked(false);
			}

			/*
			 * Resends data to clients
			 */
			if (this.isBoofed() && !this.isOnGround()) {
				this.setBoofed(true);
			}

			if (this.isBoofed() && this.isNoEndimationPlaying() && this.isMovingInAir()) {
				if (RayTraceHelper.rayTrace(this, 2.0D, 1.0F).getType() != Type.BLOCK) {
					NetworkUtil.setPlayingAnimationMessage(this, BoofloEntity.SWIM);
				}
			}

			if (this.isEndimationPlaying(SWIM) && this.getAnimationTick() <= 15) {
				this.setMovingInAir(true);
			}

			if (this.isEndimationPlaying(EAT)) {
				if ((this.getAnimationTick() > 20 && this.getAnimationTick() <= 140)) {
					if (this.getAnimationTick() % 20 == 0) {
						if (this.world instanceof ServerWorld && this.hasCaughtFruit()) {
							((ServerWorld) this.world).spawnParticle(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(EEItems.BOLLOOM_FRUIT.get())), this.getPosX(), this.getPosY() + (double) this.getHeight() / 1.5D, this.getPosZ(), 10, (double) (this.getWidth() / 4.0F), (double) (this.getHeight() / 4.0F), (double) (this.getWidth() / 4.0F), 0.05D);
						}

						if (this.hasCaughtPuffBug()) {
							this.getPassengers().get(0).attackEntityFrom(DamageSource.causeMobDamage(this), 0.0F);
						}

						this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
					}
					if (this.getAnimationTick() == 140) {
						this.setCaughtFruit(false);
						this.heal(5.0F);

						if (this.hasCaughtPuffBug()) {
							this.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1.0F, 0.75F);
							this.getPassengers().get(0).remove();
						}
					}
				}
			}

			if (this.isEndimationPlaying(HOP)) {
				if (this.getAnimationTick() == 10) {
					this.playSound(this.getHopSound(false), 0.95F, this.getSoundPitch());
					this.shouldPlayLandSound = true;
				}
			}

			if (this.shouldPlayLandSound && this.onGround && !this.wasOnGround) {
				this.playSound(this.getHopSound(true), 0.95F, this.getSoundPitch());
				this.shouldPlayLandSound = false;
			}
		}

		if (this.isEndimationPlaying(INFLATE) && this.getAnimationTick() == 2) {
			this.boof(1.0F, 1.0F, false);
		}

		if (!this.isWorldRemote() && this.isEndimationPlaying(GROWL)) {
			if (this.getAnimationTick() == 10) {
				this.playSound(this.getGrowlSound(), 0.75F, this.getSoundPitch());
			}

			if (this.getAnimationTick() >= 20) {
				for (PlayerEntity players : this.getNearbyPlayers(0.4F)) {
					if (!this.hasAggressiveAttackTarget()) {
						this.setBoofloAttackTargetId(players.getEntityId());
					}
				}
			}
		}

		if (this.isEndimationPlaying(SLAM) && this.getAnimationTick() == 3) {
			this.boof(1.2F, 2.2F, true);
			this.playSound(this.getSlamSound(), 0.75F, 1.0F);
		}

		if (this.isInWater()) {
			if (!this.isBoofed()) {
				this.setBoofed(true);
			} else if (this.rand.nextFloat() < 0.7F) {
				this.addVelocity(0.0F, 0.05F, 0.0F);
			}
		}

		if (this.isOnGround() && this.isBoofed()) {
			if (this.hasAggressiveAttackTarget() && !this.hasCaughtPuffBug()) {
				if (!this.isWorldRemote()) {
					if (this.isNoEndimationPlaying()) {
						NetworkUtil.setPlayingAnimationMessage(this, INFLATE);
					} else if (this.isEndimationPlaying(CHARGE)) {
						NetworkUtil.setPlayingAnimationMessage(this, SLAM);
					}
				}
			} else {
				if (this.isBeingRidden() && this.isEndimationPlaying(CHARGE)) {
					NetworkUtil.setPlayingAnimationMessage(this, SLAM);
				} else {
					if (this.deflateDelay <= 0 && (!this.isEndimationPlaying(SLAM) && !this.isInWater())) {
						this.setBoofed(false);
					}
				}
			}
		}

		if (this.getRNG().nextInt(40000) < 10 && !this.hasCaughtFruit() && !this.hasCaughtPuffBug()) {
			this.setHungry(true);
		}

		if (this.isWorldRemote()) {
			if (this.isBoofed()) {
				this.OPEN_JAW.setDecrementing(this.getBoofloAttackTarget() == null || this.hasCaughtPuffBug() || (this.hasAggressiveAttackTarget() && !(this.getBoofloAttackTarget() instanceof PuffBugEntity)));

				this.OPEN_JAW.update();

				this.OPEN_JAW.tick();
			}
		}

		this.FRUIT_HOVER.update();

		if (this.isEndimationPlaying(EAT)) {
			if ((this.getAnimationTick() >= 20) && this.getAnimationTick() < 140) {
				if (this.getAnimationTick() % 10 == 0) {
					if (this.getAnimationTick() == 20) {
						this.FRUIT_HOVER.setDecrementing(false);
						this.FRUIT_HOVER.setTick(0);
					}
					this.FRUIT_HOVER.setDecrementing(!this.FRUIT_HOVER.isDecrementing());
				}
			} else if (this.getAnimationTick() >= 140) {
				this.FRUIT_HOVER.setDecrementing(false);
			}
		}

		this.FRUIT_HOVER.tick();

		this.wasOnGround = this.onGround;

		if (this.isEndimationPlaying(EAT) && !this.hasCaughtFruit()) {
			this.rotationYaw = this.rotationYawHead = this.renderYawOffset = this.getLockedYaw();
		}
	}

	@Override
	public void livingTick() {
		super.livingTick();

		if (this.hopDelay > 0) this.hopDelay--;

		if (this.getInLoveTicks() > 0) {
			this.setInLove(this.getInLoveTicks() - 1);
			if (this.getInLoveTicks() % 10 == 0) {
				double d0 = this.rand.nextGaussian() * 0.02D;
				double d1 = this.rand.nextGaussian() * 0.02D;
				double d2 = this.rand.nextGaussian() * 0.02D;
				this.world.addParticle(ParticleTypes.HEART, this.getPosX() + (this.rand.nextFloat() * this.getWidth() * 2.0F) - this.getWidth(), this.getPosY() + 0.5D + (this.rand.nextFloat() * this.getHeight()), this.getPosZ() + (this.rand.nextFloat() * this.getWidth() * 2.0F) - this.getWidth(), d0, d1, d2);
			}
		}

		if (!this.isWorldRemote() && this.croakDelay == 0 && !this.isTempted() && this.isAlive() && this.onGround && !this.isBoofed() && this.rand.nextInt(1000) < this.livingSoundTime++ && this.isNoEndimationPlaying() && this.getPassengers().isEmpty()) {
			this.livingSoundTime = -this.getTalkInterval();
			NetworkUtil.setPlayingAnimationMessage(this, CROAK);
		}

		if (this.isEndimationPlaying(CROAK) && this.getAnimationTick() == 5 && !this.isWorldRemote()) {
			this.playSound(this.getAmbientSound(), 1.25F, this.getSoundPitch());
		}

		if (this.hasAggressiveAttackTarget()) {
			this.rotationYaw = this.rotationYawHead;
			Entity attackTarget = this.getBoofloAttackTarget();
			if (!this.isWorldRemote() && (this.getDistanceSq(attackTarget) > 1152.0D || attackTarget.isInvisible() || (attackTarget instanceof PuffBugEntity && attackTarget.isPassenger()))) {
				this.setBoofloAttackTargetId(0);
			}
		}
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putBoolean("IsMovingInAir", this.isMovingInAir());
		compound.putBoolean("IsBoofed", this.isBoofed());
		compound.putBoolean("IsPregnant", this.isPregnant());
		compound.putBoolean("IsHungry", this.isHungry());
		compound.putBoolean("HasFruit", this.hasCaughtFruit());
		compound.putBoolean("WasBred", this.wasBred);
		compound.putInt("FruitsNeededTillTamed", this.getFruitsNeededTillTamed());
		compound.putInt("InLove", this.getInLoveTicks());
		compound.putInt("BoofloTargetId", this.getBoofloAttackTargetId());
		compound.putByte("BraceletsColor", (byte) this.getBraceletsColor().getId());
		compound.putFloat("BirthYaw", this.getLockedYaw());

		if (this.playerInLove != null) {
			compound.putUniqueId("LoveCause", this.playerInLove);
		}

		if (this.getOwnerId() != null) {
			compound.putString("Owner", this.getOwnerId().toString());
		}

		if (this.getLastFedId() != null) {
			compound.putString("LastFed", this.getLastFedId().toString());
		}
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.setMovingInAir(compound.getBoolean("IsMovingInAir"));
		this.setBoofed(compound.getBoolean("IsBoofed"));
		this.setPregnant(compound.getBoolean("IsPregnant"));
		this.setHungry(compound.getBoolean("IsHungry"));
		this.setCaughtFruit(compound.getBoolean("HasFruit"));
		this.setInLove(compound.getInt("InLove"));
		this.setBoofloAttackTargetId(compound.getInt("BoofloTargetId"));
		this.setLockedYaw(compound.getFloat("BirthYaw"));
		this.playerInLove = compound.hasUniqueId("LoveCause") ? compound.getUniqueId("LoveCause") : null;
		this.wasBred = compound.getBoolean("WasBred");

		if (compound.contains("BraceletsColor", 99)) {
			this.setBraceletsColor(DyeColor.byId(compound.getInt("BraceletsColor")));
		}

		if (compound.contains("FruitsNeededTillTamed")) {
			this.setFruitsNeeded(compound.getInt("FruitsNeededTillTamed"));
		}

		UUID ownerUUID = compound.hasUniqueId("Owner") ? compound.getUniqueId("Owner") : PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), compound.getString("Owner"));
		UUID lastFedUUID = compound.hasUniqueId("LastFed") ? compound.getUniqueId("LastFed") : PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), compound.getString("LastFed"));

		if (ownerUUID != null) {
			try {
				this.setOwnerId(ownerUUID);
				this.setTamed(true);
			} catch (Throwable throwable) {
				this.setTamed(false);
			}
		}

		if (lastFedUUID != null) {
			try {
				this.setLastFedId(lastFedUUID);
			} catch (Throwable exception) {
			}
		}
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (BOOFED.equals(key)) {
			this.recalculateSize();
			if (this.isBoofed()) {
				this.navigator = new FlyingPathNavigator(this, this.world) {

					@Override
					public boolean canEntityStandOnPos(BlockPos pos) {
						return this.world.isAirBlock(pos);
					}

				};
				this.moveController = new FlyingMoveController(this);
				this.lookController = new FlyingLookController(this, 10);

				if (!this.isWorldRemote() && this.ticksExisted > 5) {
					this.playSound(this.getInflateSound(), this.getSoundVolume(), this.getSoundPitch());
				}

				this.deflateDelay = 10;
			} else {
				this.navigator = this.createNavigator(this.world);
				this.moveController = new GroundMoveHelperController(this);
				this.lookController = new LookController(this);

				if (!this.isWorldRemote() && this.ticksExisted > 5) {
					this.playSound(this.getDeflateSound(), this.getSoundVolume(), this.getSoundPitch());
				}

				if (this.isWorldRemote()) {
					this.OPEN_JAW.setTick(0);
					this.setBoofloAttackTargetId(0);
				}
			}
		}
	}

	@Override
	public void travel(Vector3d vec3d) {
		if (this.isAlive() && this.isBeingRidden() && this.canBeSteered()) {
			LivingEntity rider = (LivingEntity) this.getControllingPassenger();
			this.rotationYaw = rider.rotationYaw;
			this.prevRotationYaw = this.rotationYaw;
			this.rotationPitch = 0.0F;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.renderYawOffset = this.rotationYaw;
			this.rotationYawHead = this.rotationYaw;

			float playerMoveFoward = rider.moveForward;

			if (!this.isWorldRemote() && playerMoveFoward > 0.0F) {
				if (this.isOnGround() && this.isNoEndimationPlaying() && !this.isBoofed()) {
					NetworkUtil.setPlayingAnimationMessage(this, HOP);
				} else if (!this.isOnGround() && this.isNoEndimationPlaying() && this.isBoofed()) {
					NetworkUtil.setPlayingAnimationMessage(this, SWIM);
				}
			}

			if (this.isBoofed()) {
				float gravity = this.getBoostPower() > 0 ? 0.01F : 0.035F;

				if (this.hasPath()) {
					this.getNavigator().clearPath();
				}

				if (this.getBoofloAttackTarget() != null) {
					this.setBoofloAttackTargetId(0);
				}

				this.move(MoverType.SELF, this.getMotion());
				this.setMotion(this.getMotion().scale(0.9D));
				if (!this.isInWater()) {
					this.setMotion(this.getMotion().subtract(0, gravity, 0));
				}
			} else {
				if (this.onGround && this.isEndimationPlaying(HOP) && this.getAnimationTick() == 10) {
					Vector3d motion = this.getMotion();
					EffectInstance jumpBoost = this.getActivePotionEffect(Effects.JUMP_BOOST);
					float boostPower = jumpBoost == null ? 1.0F : (float) (jumpBoost.getAmplifier() + 1);

					this.setMotion(motion.x, 0.55F * boostPower, motion.z);
					this.isAirBorne = true;

					float xMotion = -MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F)) * MathHelper.cos(1.0F * ((float) Math.PI / 180F));
					float zMotion = MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F)) * MathHelper.cos(1.0F * ((float) Math.PI / 180F));

					float multiplier = 0.35F + (float) this.getAttribute(Attributes.MOVEMENT_SPEED).getValue();

					this.setMotion(this.getMotion().add(xMotion * multiplier, 0.0F, zMotion * multiplier));
				}

				if (this.canPassengerSteer()) {
					super.travel(new Vector3d(0.0F, vec3d.y, 0.0F));
				} else {
					this.setMotion(Vector3d.ZERO);
				}
			}
		} else {
			if (this.isServerWorld() && this.isBoofed()) {
				this.moveRelative(0.0F, vec3d);
				this.move(MoverType.SELF, this.getMotion());
				this.setMotion(this.getMotion().scale(0.9D));
				if (!this.isMovingInAir()) {
					this.setMotion(this.getMotion().subtract(0, 0.01D, 0));
				}
			} else {
				super.travel(vec3d);
			}
		}
	}

	@Nullable
	@Override
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		if (reason == SpawnReason.NATURAL) {
			Random rand = new Random();
			if (rand.nextFloat() < 0.2F) {
				this.setPregnant(true);
			}
			this.setFruitsNeeded(rand.nextInt(3) + 2);
		}
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	protected void removePassenger(Entity passenger) {
		if (!this.world.isRemote && this.isBoostExpanding() && !this.isBoostLocked() && passenger instanceof PlayerEntity && this.getControllingPassenger() == passenger) {
			this.setBoostExpanding(false);
		}
		super.removePassenger(passenger);
	}

	@Nullable
	public UUID getOwnerId() {
		return this.dataManager.get(OWNER_UNIQUE_ID).orElse(null);
	}

	public void setOwnerId(@Nullable UUID ownerId) {
		this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(ownerId));
	}

	@Nullable
	public UUID getLastFedId() {
		return this.dataManager.get(LAST_FED_UNIQUE_ID).orElse(null);
	}

	public void setLastFedId(@Nullable UUID ownerId) {
		this.dataManager.set(LAST_FED_UNIQUE_ID, Optional.ofNullable(ownerId));
	}

	/*
	 * Minecraft's onGround boolean isn't synced correctly so this has its own
	 */
	public boolean isOnGround() {
		return this.dataManager.get(ON_GROUND);
	}

	public void setOnGround(boolean onGround) {
		this.dataManager.set(ON_GROUND, onGround);
	}

	public boolean isTamed() {
		return this.dataManager.get(TAMED);
	}

	public void setTamed(boolean tamed) {
		this.dataManager.set(TAMED, tamed);
	}

	public boolean isMovingInAir() {
		return this.dataManager.get(MOVING_IN_AIR);
	}

	public void setMovingInAir(boolean moving) {
		this.dataManager.set(MOVING_IN_AIR, moving);
	}

	public boolean isBoofed() {
		return this.dataManager.get(BOOFED);
	}

	public void setBoofed(boolean boofed) {
		this.dataManager.set(BOOFED, boofed);
		this.shouldPlayLandSound = false;
	}

	public boolean isPregnant() {
		return this.dataManager.get(PREGNANT);
	}

	public void setPregnant(boolean pregnant) {
		this.dataManager.set(PREGNANT, pregnant);
	}

	public boolean isHungry() {
		return this.dataManager.get(HUNGRY);
	}

	public void setHungry(boolean hungry) {
		this.dataManager.set(HUNGRY, hungry);
	}

	public boolean hasCaughtFruit() {
		return this.dataManager.get(HAS_FRUIT);
	}

	public boolean hasCaughtPuffBug() {
		return !this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof PuffBugEntity;
	}

	public void setCaughtFruit(boolean hasCaughtFruit) {
		this.dataManager.set(HAS_FRUIT, hasCaughtFruit);
	}

	public void setBoostStatus(int status, boolean add) {
		this.dataManager.set(BOOST_STATUS, (byte) (add ? this.dataManager.get(BOOST_STATUS) | status : this.dataManager.get(BOOST_STATUS) & ~status));
	}

	public void setBoostExpanding(boolean expanding) {
		this.setBoostStatus(1, expanding);
	}

	public boolean isBoostExpanding() {
		return (this.dataManager.get(BOOST_STATUS) & 1) != 0;
	}

	public void setBoostLocked(boolean expandingDelay) {
		this.setBoostStatus(2, expandingDelay);
	}

	public boolean isBoostLocked() {
		return (this.dataManager.get(BOOST_STATUS) & 2) != 0;
	}

	public float getLockedYaw() {
		return this.dataManager.get(LOCKED_YAW);
	}

	public void setLockedYaw(float yaw) {
		this.dataManager.set(LOCKED_YAW, yaw);
	}

	public int getBoofloAttackTargetId() {
		return this.dataManager.get(ATTACK_TARGET);
	}

	@Nullable
	public Entity getBoofloAttackTarget() {
		Entity entity = this.world.getEntityByID(this.getBoofloAttackTargetId());
		if (entity == null || entity != null && !entity.isAlive() || entity instanceof BoofloEntity) {
			this.setBoofloAttackTargetId(0);
		}

		if (this.getOwner() != null && this.getOwner() == entity) {
			this.setBoofloAttackTargetId(0);
		}
		return this.getBoofloAttackTargetId() > 0 ? entity : null;
	}

	public boolean hasAggressiveAttackTarget() {
		return this.getBoofloAttackTarget() instanceof LivingEntity;
	}

	public void setBoofloAttackTargetId(int id) {
		this.dataManager.set(ATTACK_TARGET, id);
	}

	public void setInLove(@Nullable PlayerEntity player) {
		this.setInLove(600);
		if (player != null) {
			this.playerInLove = player.getUniqueID();
		}

		this.world.setEntityState(this, (byte) 18);
	}

	public void setFruitsNeeded(int fruitsNeeded) {
		this.dataManager.set(FRUITS_NEEDED, fruitsNeeded);
	}

	public int getFruitsNeededTillTamed() {
		return this.dataManager.get(FRUITS_NEEDED);
	}

	public void setBoostPower(int power) {
		this.dataManager.set(BOOST_POWER, power);
	}

	public int getBoostPower() {
		return this.dataManager.get(BOOST_POWER);
	}

	public DyeColor getBraceletsColor() {
		return DyeColor.byId(this.dataManager.get(BRACELETS_COLOR));
	}

	public void setBraceletsColor(DyeColor color) {
		this.dataManager.set(BRACELETS_COLOR, color.getId());
	}

	public void setInLove(int ticks) {
		this.dataManager.set(LOVE_TICKS, ticks);
	}

	public int getInLoveTicks() {
		return this.dataManager.get(LOVE_TICKS);
	}

	public boolean canBreed() {
		return this.isTamed() && this.getInLoveTicks() <= 0 && !this.isPregnant() && this.breedDelay <= 0;
	}

	public boolean isInLove() {
		if (this.isPregnant()) {
			return false;
		}
		return this.getInLoveTicks() > 0;
	}

	public void resetInLove() {
		this.setInLove(0);
	}

	@Nullable
	public ServerPlayerEntity getLoveCause() {
		if (this.playerInLove == null) {
			return null;
		} else {
			PlayerEntity playerentity = this.world.getPlayerByUuid(this.playerInLove);
			return playerentity instanceof ServerPlayerEntity ? (ServerPlayerEntity) playerentity : null;
		}
	}

	public void setTamedBy(PlayerEntity player) {
		this.setTamed(true);
		this.setOwnerId(player.getUniqueID());
		if (player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			//Creates wolf to still trigger tamed - as booflo isn't an AnimalEntity
			CriteriaTriggers.TAME_ANIMAL.trigger(serverPlayer, EntityType.WOLF.create(this.world));
			if (!this.isWorldRemote()) {
				EECriteriaTriggers.TAME_BOOFLO.trigger(serverPlayer);
			}
		}
	}

	@Nullable
	public LivingEntity getOwner() {
		try {
			UUID uuid = this.getOwnerId();
			return uuid == null ? null : this.world.getPlayerByUuid(uuid);
		} catch (IllegalArgumentException exception) {
			return null;
		}
	}

	@Nullable
	public LivingEntity getLastFedPlayer() {
		try {
			UUID uuid = this.getLastFedId();
			return uuid == null ? null : this.world.getPlayerByUuid(uuid);
		} catch (IllegalArgumentException exception) {
			return null;
		}
	}

	public boolean canMateWith(BoofloEntity possibleMate) {
		if (possibleMate == this) {
			return false;
		} else {
			return this.isInLove() && possibleMate.isInLove();
		}
	}

	public int getDefaultGroundHopDelay() {
		return this.isInLove() ? this.rand.nextInt(10) + 25 : this.rand.nextInt(40) + 80;
	}

	public void boof(float internalStrength, float offensiveStrength, boolean slam) {
		float verticalStrength = 1.0F;

		if (this.isBeingRidden() && this.getControllingPassenger() instanceof PlayerEntity && !this.isEndimationPlaying(SLAM) && !this.isBoostLocked()) {
			float boostPower = MathHelper.clamp(this.getBoostPower() * 0.01F, 0.35F, 1.82F);
			offensiveStrength *= MathHelper.clamp(boostPower / 2, 0.5F, 1.85F);
			verticalStrength *= MathHelper.clamp(boostPower, 0.35F, 1.5F);

			float xMotion = -MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F)) * MathHelper.cos(this.rotationPitch * ((float) Math.PI / 180F));
			float zMotion = MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F)) * MathHelper.cos(this.rotationPitch * ((float) Math.PI / 180F));
			Vector3d boostFowardForce = new Vector3d(xMotion, 1.3F * verticalStrength, zMotion).normalize().scale(boostPower > 0.35 ? boostPower * 2.0F : boostPower);

			this.setMotion(boostFowardForce.getX(), 1.3F * verticalStrength, boostFowardForce.getZ());
		} else {
			this.addVelocity(-MathHelper.sin((float) (this.rotationYaw * Math.PI / 180.0F)) * ((4 * internalStrength) * (this.rand.nextFloat() + 0.1F)) * 0.1F, 1.3F * verticalStrength, MathHelper.cos((float) (this.rotationYaw * Math.PI / 180.0F)) * ((4 * internalStrength) * (this.rand.nextFloat() + 0.1F)) * 0.1F);
		}

		if (slam) {
			for (int i = 0; i < 12; i++) {
				double offsetX = MathUtil.makeNegativeRandomly(this.rand.nextFloat() * 0.25F, this.rand);
				double offsetZ = MathUtil.makeNegativeRandomly(this.rand.nextFloat() * 0.25F, this.rand);

				double x = this.getPosX() + 0.5D + offsetX;
				double y = this.getPosY() + 0.5D + (this.rand.nextFloat() * 0.05F);
				double z = this.getPosZ() + 0.5D + offsetZ;

				if (this.isWorldRemote()) {
					this.world.addParticle(EEParticles.POISE_BUBBLE.get(), x, y, z, MathUtil.makeNegativeRandomly((this.rand.nextFloat() * 0.3F), this.rand) + 0.025F, (this.rand.nextFloat() * 0.15F) + 0.1F, MathUtil.makeNegativeRandomly((this.rand.nextFloat() * 0.3F), this.rand) + 0.025F);
				}
			}
		}

		for (Entity entity : this.world.getEntitiesWithinAABB(Entity.class, this.getBoundingBox().grow(3.5F * Math.max(offensiveStrength / 2.0F, 1.0F)), entity -> entity != this && (entity instanceof ItemEntity || entity instanceof LivingEntity) && !(entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative() && ((PlayerEntity) entity).abilities.isFlying))) {
			float resistance = this.isResistantToBoof(entity) ? 0.15F : 1.0F;
			float amount = (0.2F * offensiveStrength) * resistance;
			if (offensiveStrength > 2.0F && resistance > 0.15F && entity != this.getControllingPassenger()) {
				entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
				entity.velocityChanged = false;
			}
			Vector3d result = entity.getPositionVec().subtract(this.getPositionVec());
			entity.addVelocity(result.x * amount, (this.rand.nextFloat() * 0.75D + 0.25D) * (offensiveStrength * 0.75F), result.z * amount);
		}
	}

	public LivingEntity growDown() {
		if (this.isAlive()) {
			BoofloAdolescentEntity boofloAdolescent = EEEntities.BOOFLO_ADOLESCENT.get().create(this.world);
			boofloAdolescent.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);

			if (this.hasCustomName()) {
				boofloAdolescent.setCustomName(this.getCustomName());
				boofloAdolescent.setCustomNameVisible(this.isCustomNameVisible());
			}

			if (this.getLeashed()) {
				boofloAdolescent.setLeashHolder(this.getLeashHolder(), true);
				this.clearLeashed(true, false);
			}

			if (this.getRidingEntity() != null) {
				boofloAdolescent.startRiding(this.getRidingEntity());
			}

			boofloAdolescent.wasBred = this.wasBred;
			boofloAdolescent.setHealth(boofloAdolescent.getMaxHealth());
			this.world.addEntity(boofloAdolescent);
			this.remove();
			return boofloAdolescent;
		}
		return this;
	}

	public void catchPuffBug(PuffBugEntity puffbug) {
		puffbug.startRiding(this, true);
	}

	@Override
	public boolean canDespawn(double distanceToClosestPlayer) {
		return !this.isTamed() && !this.wasBred && !this.isPregnant();
	}

	public boolean isTempted() {
		for (Object goals : this.goalSelector.getRunningGoals().toArray()) {
			if (goals instanceof PrioritizedGoal) {
				return ((PrioritizedGoal) goals).getGoal() instanceof BoofloTemptGoal;
			}
		}
		return false;
	}

	public List<PlayerEntity> getNearbyPlayers(float multiplier) {
		return this.world.getEntitiesWithinAABB(PlayerEntity.class, this.getBoundingBox().grow(8.0F * multiplier, 4.0F, 8.0F * multiplier), IS_SCARED_BY);
	}

	public boolean isPlayerNear(float multiplier) {
		return !this.getNearbyPlayers(multiplier).isEmpty();
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return this.isBoofed() ? 1.2F : 0.9F;
	}

	@Override
	public int getTalkInterval() {
		return 120;
	}

	@Override
	public Endimation[] getEndimations() {
		return new Endimation[]{
				CROAK,
				HOP,
				HURT,
				BIRTH,
				INFLATE,
				SWIM,
				EAT,
				CHARGE,
				SLAM,
				GROWL
		};
	}

	@Override
	public Endimation getHurtAnimation() {
		return HURT;
	}

	@Override
	public void onEndimationStart(Endimation endimation) {
		if (endimation == SWIM) {
			float pitch = this.isBeingRidden() ? 1.0F : this.rotationPitch;
			float xMotion = -MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
			float yMotion = -MathHelper.sin(pitch * ((float) Math.PI / 180F));
			float zMotion = MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));

			double motionScale = (this.hasAggressiveAttackTarget() && !this.hasCaughtPuffBug()) || (!this.getPassengers().isEmpty() && !this.hasCaughtPuffBug()) ? 0.85F : 0.5F;

			Vector3d motion = new Vector3d(xMotion, yMotion, zMotion).normalize().mul(motionScale, 0.5D, motionScale);

			this.addVelocity(motion.x * (this.getAttribute(Attributes.MOVEMENT_SPEED).getValue() - 0.05F), motion.y, motion.z * (this.getAttribute(Attributes.MOVEMENT_SPEED).getValue() - 0.05F));
		}
	}

	@Override
	protected void jump() {
		Vector3d vec3d = this.getMotion();
		this.setMotion(vec3d.x, 0.55D, vec3d.z);
		this.isAirBorne = true;
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		if (entity instanceof BoofloBabyEntity && (((BoofloBabyEntity) (entity)).isBeingBorn() || ((BoofloBabyEntity) (entity)).getMotherNoClipTicks() > 0))
			return;
		super.collideWithEntity(entity);
	}

	@Override
	protected ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		Item item = itemstack.getItem();

		if (item instanceof SpawnEggItem && ((SpawnEggItem) item).hasType(itemstack.getTag(), this.getType())) {
			if (!this.isWorldRemote()) {
				BoofloBabyEntity baby = EEEntities.BOOFLO_BABY.get().create(this.world);
				baby.setGrowingAge(-24000);
				baby.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), 0.0F, 0.0F);
				this.world.addEntity(baby);
				if (itemstack.hasDisplayName()) {
					baby.setCustomName(itemstack.getDisplayName());
				}

				EntityItemStackHelper.consumeItemFromStack(player, itemstack);
			}
			return ActionResultType.func_233537_a_(this.world.isRemote);
		} else if (item == EEBlocks.POISE_CLUSTER.get().asItem() && this.canBreed()) {
			EntityItemStackHelper.consumeItemFromStack(player, itemstack);
			this.setInLove(player);
			return ActionResultType.func_233537_a_(this.world.isRemote);
		} else if (item == EEItems.BOLLOOM_FRUIT.get() && !this.isAggressive() && !this.hasCaughtFruit() && this.onGround) {
			IParticleData particle = ParticleTypes.HEART;
			EntityItemStackHelper.consumeItemFromStack(player, itemstack);
			this.setCaughtFruit(true);
			this.setHungry(false);

			if (!this.isTamed()) {
				if (this.getFruitsNeededTillTamed() >= 1) {
					this.setFruitsNeeded(this.getFruitsNeededTillTamed() - 1);
					this.setLastFedId(player.getUniqueID());
					particle = ParticleTypes.SMOKE;

					if (!this.isWorldRemote()) {
						NetworkUtil.setPlayingAnimationMessage(this, GROWL);
					}
				} else {
					if (player == this.getLastFedPlayer()) {
						this.setFruitsNeeded(0);
						this.setTamedBy(player);
						this.croakDelay = 40;
					}
				}
			}

			if (this.isWorldRemote()) {
				for (int i = 0; i < 7; ++i) {
					double d0 = this.rand.nextGaussian() * 0.02D;
					double d1 = this.rand.nextGaussian() * 0.02D;
					double d2 = this.rand.nextGaussian() * 0.02D;
					this.world.addParticle(particle, this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getPosY() + 0.5D + (double) (this.rand.nextFloat() * this.getHeight()), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), d0, d1, d2);
				}
			}
			return ActionResultType.func_233537_a_(this.world.isRemote);
		} else if (item instanceof DyeItem && this.isTamed()) {
			DyeColor dyecolor = ((DyeItem) item).getDyeColor();
			if (dyecolor != this.getBraceletsColor()) {
				this.setBraceletsColor(dyecolor);
				if (!player.abilities.isCreativeMode) {
					itemstack.shrink(1);
				}
				return ActionResultType.func_233537_a_(this.world.isRemote);
			}
		} else {
			ActionResultType result = itemstack.interactWithEntity(player, this, hand);
			if (result == ActionResultType.CONSUME || result == ActionResultType.SUCCESS) {
				return ActionResultType.PASS;
			}

			if (this.isTamed() && !this.isBeingRidden() && !this.isPregnant()) {
				if (!this.world.isRemote) {
					player.rotationYaw = this.rotationYaw;
					player.rotationPitch = this.rotationPitch;
					player.startRiding(this);
				}
				return ActionResultType.PASS;
			}
		}
		return super.func_230254_b_(player, hand);
	}

	@Override
	public void updatePassenger(Entity passenger) {
		if (this.isPassenger(passenger)) {
			if (passenger instanceof BoofloBabyEntity) {
				int passengerIndex = this.getPassengers().indexOf(passenger);

				double xOffset = passengerIndex == 0 ? 0.25F : -0.25F;
				double zOffset = passengerIndex == 0 ? 0.0F : passengerIndex == 1 ? -0.25F : 0.25F;
				Vector3d ridingOffset = (new Vector3d(xOffset, 0.0D, zOffset)).rotateYaw(-this.getLockedYaw() * ((float) Math.PI / 180F) - ((float) Math.PI / 2F));

				passenger.setPosition(this.getPosX() + ridingOffset.x, this.getPosY() + 0.9F, this.getPosZ() + ridingOffset.z);
			} else if (passenger instanceof PuffBugEntity) {
				PuffBugEntity puffbug = (PuffBugEntity) passenger;
				passenger.rotationYaw = puffbug.renderYawOffset = puffbug.rotationYawHead = (this.rotationYaw - 75.0F);
				if (this.isEndimationPlaying(EAT) && this.getAnimationTick() > 15) {
					Vector3d ridingPos = (new Vector3d(1.0D, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * ((float) Math.PI / 180F) - ((float) Math.PI / 2F));
					float yOffset = puffbug.isChild() ? 0.1F : 0.3F;

					passenger.setPosition(this.getPosX() + ridingPos.getX(), this.getPosY() - yOffset - (0.15F * this.FRUIT_HOVER.getAnimationProgressServer()), this.getPosZ() + ridingPos.getZ());
				} else {
					passenger.setPosition(this.getPosX(), this.getPosY() + 0.25F, this.getPosZ());
				}
			} else {
				super.updatePassenger(passenger);
				if (passenger instanceof MobEntity) {
					this.renderYawOffset = ((MobEntity) passenger).renderYawOffset;
				}
			}
		}
	}

	@Override
	public double getMountedYOffset() {
		double original = super.getMountedYOffset();
		return this.isBoofed() ? original + 0.15F : original;
	}

	@Override
	public boolean canBeSteered() {
		return this.getControllingPassenger() instanceof PlayerEntity;
	}

	@Override
	public boolean isOnLadder() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return !this.isBeingRidden();
	}

	@Override
	protected boolean canFitPassenger(Entity passenger) {
		int limit = this.isPregnant() ? 3 : 1;
		return this.getPassengers().size() < limit;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		Entity entitySource = source.getTrueSource();
		if (entitySource instanceof LivingEntity && !this.isBeingRidden()) {
			if (entitySource instanceof PlayerEntity) {
				if (!entitySource.isSpectator() && !((PlayerEntity) entitySource).isCreative()) {
					this.setBoofloAttackTargetId(entitySource.getEntityId());
				}
			} else {
				this.setBoofloAttackTargetId(entitySource.getEntityId());
			}
		}
		float newCalculatedDamage = source == DamageSource.IN_WALL ? 0.5F : amount;
		return super.attackEntityFrom(source, source.getTrueSource() instanceof PuffBugEntity ? 2.5F : newCalculatedDamage);
	}

	@Override
	protected void damageEntity(DamageSource damageSrc, float damageAmount) {
		Entity entitySource = damageSrc.getTrueSource();
		if (entitySource instanceof LivingEntity && !this.isBeingRidden()) {
			if (entitySource instanceof PlayerEntity) {
				if (!entitySource.isSpectator() && !((PlayerEntity) entitySource).isCreative()) {
					this.setBoofloAttackTargetId(entitySource.getEntityId());
				}
			} else {
				this.setBoofloAttackTargetId(entitySource.getEntityId());
			}
		}
		super.damageEntity(damageSrc, damageAmount);
	}

	@Override
	public int getHorizontalFaceSpeed() {
		return 1;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	public EntitySize getSize(Pose poseIn) {
		return this.isBoofed() ? BOOFED_SIZE : super.getSize(poseIn);
	}

	@Override
	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
	}

	protected boolean isResistantToBoof(Entity entity) {
		return entity instanceof BoofloEntity || entity instanceof BoofloAdolescentEntity || entity instanceof BoofloBabyEntity;
	}

	@Override
	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		if (id == 18) {
			for (int i = 0; i < 7; ++i) {
				double d0 = this.rand.nextGaussian() * 0.02D;
				double d1 = this.rand.nextGaussian() * 0.02D;
				double d2 = this.rand.nextGaussian() * 0.02D;
				this.world.addParticle(ParticleTypes.HEART, this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getPosY() + 0.5D + (double) (this.rand.nextFloat() * this.getHeight()), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), d0, d1, d2);
			}
		} else {
			super.handleStatusUpdate(id);
		}
	}

	/*
	 * Overridden to do nothing; gets remade in this class
	 * @see EntityBooflo#livingTick
	 */
	@Override
	public void playAmbientSound() {
	}

	public SoundEvent getHopSound(boolean landing) {
		return landing ? EESounds.BOOFLO_HOP_LAND.get() : EESounds.BOOFLO_HOP.get();
	}

	public SoundEvent getGrowlSound() {
		return EESounds.BOOFLO_GROWL.get();
	}

	public SoundEvent getSlamSound() {
		return EESounds.BOOFLO_SLAM.get();
	}

	public SoundEvent getInflateSound() {
		return EESounds.BOOFLO_INFLATE.get();
	}

	protected SoundEvent getDeflateSound() {
		return EESounds.BOOFLO_DEFLATE.get();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return EESounds.BOOFLO_CROAK.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return EESounds.BOOFLO_DEATH.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return EESounds.BOOFLO_HURT.get();
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack(EEItems.BOOFLO_SPAWN_EGG.get());
	}

	@Override
	public boolean hasGrowthProgress() {
		return false;
	}

	@Override
	public void resetGrowthProgress() {
	}

	@Override
	public boolean canAge(boolean isGrowing) {
		return !isGrowing;
	}

	@Override
	public LivingEntity attemptAging(boolean isGrowing) {
		return isGrowing ? this : this.growDown();
	}

	public static class GroundMoveHelperController extends MovementController {
		private final BoofloEntity booflo;
		private float yRot;
		public boolean isAggressive;

		public GroundMoveHelperController(BoofloEntity booflo) {
			super(booflo);
			this.booflo = booflo;
			this.yRot = (float) (180.0F * booflo.rotationYaw / Math.PI);
		}

		public void setDirection(float yRot, boolean aggressive) {
			this.yRot = yRot;
			this.isAggressive = aggressive;
		}

		public void setSpeed(double speed) {
			this.speed = speed;
			this.action = MovementController.Action.MOVE_TO;
		}

		public void tick() {
			if (!this.booflo.hasCaughtPuffBug()) {
				this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, this.yRot, 90.0F);
				this.mob.rotationYawHead = this.mob.rotationYaw;
				this.mob.renderYawOffset = this.mob.rotationYaw;
			}

			if (this.action != MovementController.Action.MOVE_TO) {
				this.mob.setMoveForward(0.0F);
			} else {
				this.action = MovementController.Action.WAIT;
				if (this.mob.isOnGround()) {
					this.mob.setAIMoveSpeed((float) (this.speed * this.mob.getAttribute(Attributes.MOVEMENT_SPEED).getValue()));
					if (this.booflo.hopDelay == 0 && this.booflo.isEndimationPlaying(HOP) && this.booflo.getAnimationTick() == 10) {
						this.booflo.getJumpController().setJumping();

						this.booflo.hopDelay = this.booflo.getDefaultGroundHopDelay();
					} else {
						this.booflo.moveStrafing = 0.0F;
						this.booflo.moveForward = 0.0F;
						this.mob.setAIMoveSpeed(0.0F);
					}
				} else {
					this.mob.setAIMoveSpeed(0.0F);
				}
			}
		}
	}

	public static class FlyingMoveController extends MovementController {
		private final BoofloEntity booflo;

		public FlyingMoveController(BoofloEntity booflo) {
			super(booflo);
			this.booflo = booflo;
		}

		public void tick() {
			if (this.action == MovementController.Action.MOVE_TO && !this.booflo.getNavigator().noPath()) {
				if (this.booflo.hasAggressiveAttackTarget()) {
					Vector3d vec3d = this.booflo.getMoveControllerPathDistance(this.posX, this.posY, this.posZ);

					this.booflo.rotationYaw = this.limitAngle(this.booflo.rotationYaw, this.booflo.getTargetAngleForPathDistance(vec3d), 10.0F);
					this.booflo.renderYawOffset = this.booflo.rotationYaw;
					this.booflo.rotationYawHead = this.booflo.rotationYaw;

					float f1 = (float) (2 * this.booflo.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					float f2 = MathHelper.lerp(0.125F, this.booflo.getAIMoveSpeed(), f1);

					this.booflo.setAIMoveSpeed(f2);
				} else {
					Vector3d vec3d = new Vector3d(this.posX - this.booflo.getPosX(), this.posY - this.booflo.getPosY(), this.posZ - this.booflo.getPosZ());
					double d0 = vec3d.length();
					double d1 = vec3d.y / d0;
					float f = (float) (MathHelper.atan2(vec3d.z, vec3d.x) * (double) (180F / (float) Math.PI)) - 90F;

					this.booflo.rotationYaw = this.limitAngle(this.booflo.rotationYaw, f, 10.0F);
					this.booflo.renderYawOffset = this.booflo.rotationYaw;
					this.booflo.rotationYawHead = this.booflo.rotationYaw;

					float f1 = (float) (this.speed * this.booflo.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					float f2 = MathHelper.lerp(0.125F, this.booflo.getAIMoveSpeed(), f1);

					this.booflo.setAIMoveSpeed(f2);

					double d3 = Math.cos((double) (this.booflo.rotationYaw * ((float) Math.PI / 180F)));
					double d4 = Math.sin((double) (this.booflo.rotationYaw * ((float) Math.PI / 180F)));
					double d5 = Math.sin((double) (this.booflo.ticksExisted + this.booflo.getEntityId()) * 0.75D) * 0.05D;

					if (!this.booflo.isInWater()) {
						float f3 = -((float) (MathHelper.atan2(vec3d.y, (double) MathHelper.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z)) * (double) (180F / (float) Math.PI)));
						f3 = MathHelper.clamp(MathHelper.wrapDegrees(f3), -85.0F, 85.0F);
						this.booflo.rotationPitch = this.limitAngle(this.booflo.rotationPitch, f3, 5.0F);
					}

					this.booflo.setMotion(this.booflo.getMotion().add(0, d5 * (d4 + d3) * 0.25D + (double) f2 * d1 * 0.02D, 0));
				}
				this.booflo.setMovingInAir(true);
			} else {
				this.booflo.setAIMoveSpeed(0F);
				this.booflo.setMovingInAir(false);
			}
		}
	}

	static class FlyingLookController extends LookController {
		private final int angleLimit;

		public FlyingLookController(BoofloEntity booflo, int angleLimit) {
			super(booflo);
			this.angleLimit = angleLimit;
		}

		public void tick() {
			if (this.isLooking) {
				this.isLooking = false;
				this.mob.rotationYawHead = this.clampedRotate(this.mob.rotationYawHead, this.getTargetYaw() + 20.0F, this.deltaLookYaw);
				this.mob.rotationPitch = this.clampedRotate(this.mob.rotationPitch, this.getTargetPitch() + 10.0F, this.deltaLookPitch);
			} else {
				if (this.mob.getNavigator().noPath()) {
					this.mob.rotationPitch = this.clampedRotate(this.mob.rotationPitch, 0.0F, 5.0F);
				}
				this.mob.rotationYawHead = this.clampedRotate(this.mob.rotationYawHead, this.mob.renderYawOffset, this.deltaLookYaw);
			}

			float wrappedDegrees = MathHelper.wrapDegrees(this.mob.rotationYawHead - this.mob.renderYawOffset);
			if (wrappedDegrees < (float) (-this.angleLimit)) {
				this.mob.renderYawOffset -= 4.0F;
			} else if (wrappedDegrees > (float) this.angleLimit) {
				this.mob.renderYawOffset += 4.0F;
			}

			if (((BoofloEntity) this.mob).isEndimationPlaying(CHARGE)) {
				this.mob.rotationPitch = this.clampedRotate(this.mob.rotationPitch, 0.0F, 10.0F);
			}
		}
	}
}
