package com.minecraftabnormals.endergetic.common.entities.puffbug.ai;

import java.util.EnumSet;

import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatedGoal;
import com.minecraftabnormals.abnormals_core.core.util.NetworkUtil;
import com.minecraftabnormals.endergetic.common.entities.puffbug.PuffBugEntity;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class PuffBugPullOutGoal extends EndimatedGoal<PuffBugEntity> {
	private int pulls;

	public PuffBugPullOutGoal(PuffBugEntity puffbug) {
		super(puffbug, PuffBugEntity.PULL_ANIMATION);
		this.setMutexFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean shouldExecute() {
		return !this.entity.isInflated() && this.entity.isNoEndimationPlaying() && this.entity.stuckInBlock;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !this.entity.isInflated() && this.entity.stuckInBlock;
	}

	@Override
	public void startExecuting() {
		this.pulls = this.entity.getRNG().nextInt(4) + 2;
	}

	@Override
	public void tick() {
		if (this.entity.isNoEndimationPlaying()) {
			if (this.pulls > 0) {
				this.playEndimation();
				this.pulls--;
			}
		} else if (this.isEndimationPlaying() && this.isEndimationAtTick(5) && this.pulls <= 0) {
			this.entity.disableProjectile();
			this.entity.stuckInBlockState = null;

			float[] rotations = this.entity.getRotationController().getRotations(1.0F);

			float motionX = MathHelper.sin(rotations[1] * ((float) Math.PI / 180F)) * MathHelper.cos(rotations[0] * ((float) Math.PI / 180F));
			float motionY = -MathHelper.sin(rotations[0] * ((float) Math.PI / 180F));
			float motionZ = -MathHelper.cos(rotations[1] * ((float) Math.PI / 180F)) * MathHelper.cos(rotations[0] * ((float) Math.PI / 180F));

			Vector3d popOutMotion = new Vector3d(motionX, motionY, motionZ).normalize().scale(0.25F);
			this.entity.setMotion(popOutMotion);
		}
	}

	@Override
	public void resetTask() {
		NetworkUtil.setPlayingAnimationMessage(this.entity, PuffBugEntity.BLANK_ANIMATION);
	}
}