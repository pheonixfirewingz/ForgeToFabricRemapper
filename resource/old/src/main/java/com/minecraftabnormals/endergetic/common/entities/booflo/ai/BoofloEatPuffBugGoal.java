package com.minecraftabnormals.endergetic.common.entities.booflo.ai;

import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatedGoal;
import com.minecraftabnormals.endergetic.common.entities.booflo.BoofloEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class BoofloEatPuffBugGoal extends EndimatedGoal<BoofloEntity> {
	private float originalYaw;
	private int soundDelay;

	public BoofloEatPuffBugGoal(BoofloEntity booflo) {
		super(booflo, BoofloEntity.EAT);
	}

	@Override
	public boolean shouldExecute() {
		if (this.entity.isPlayerNear(1.0F)) {
			if (!this.entity.isTamed()) {
				return false;
			}
		}
		return this.entity.isNoEndimationPlaying() && this.entity.hasCaughtPuffBug() && !this.entity.isBoofed() && this.entity.isOnGround() && !this.entity.isInLove();
	}

	@Override
	public boolean shouldContinueExecuting() {
		boolean flag = true;
		if (!this.entity.hasCaughtPuffBug()) {
			if (this.entity.getAnimationTick() < 140) {
				flag = false;
			}
		}

		if (this.entity.isPlayerNear(0.6F)) {
			if (!this.entity.isTamed()) {
				this.entity.hopDelay = 0;
				for (PlayerEntity players : this.entity.getNearbyPlayers(0.6F)) {
					if (!this.entity.hasAggressiveAttackTarget()) {
						this.entity.setBoofloAttackTargetId(players.getEntityId());
					}
				}
				return false;
			}
		}
		return this.isEndimationPlaying() && flag && !this.entity.isBoofed() && this.entity.isOnGround();
	}

	@Override
	public void startExecuting() {
		this.playEndimation();
		this.originalYaw = this.entity.rotationYaw;
		this.entity.setLockedYaw(this.originalYaw);
	}

	@Override
	public void resetTask() {
		this.originalYaw = 0;
		this.playEndimation(BoofloEntity.BLANK_ANIMATION);
	}

	@Override
	public void tick() {
		if (this.soundDelay > 0) this.soundDelay--;

		this.entity.rotationYaw = this.originalYaw;
		this.entity.prevRotationYaw = this.originalYaw;

		if (this.entity.isPlayerNear(1.0F) && this.soundDelay == 0) {
			if (!this.entity.isTamed()) {
				this.entity.playSound(this.entity.getGrowlSound(), 0.75F, (float) MathHelper.clamp(this.entity.getRNG().nextFloat() * 1.0, 0.95F, 1.0F));
				this.soundDelay = 50;
			}
		}
	}
}