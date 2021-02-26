package com.minecraftabnormals.endergetic.core.mixin;

import com.minecraftabnormals.endergetic.common.entities.bolloom.BolloomBalloonEntity;
import com.minecraftabnormals.endergetic.core.interfaces.BalloonHolder;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isPassenger()Z"), method = "tickEntities")
	private boolean shouldNotTick(Entity entity) {
		if (entity.isPassenger() || entity instanceof BolloomBalloonEntity && ((BolloomBalloonEntity) entity).isAttachedToEntity()) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;addedToChunk:Z", ordinal = 1, shift = At.Shift.AFTER), method = "updateEntity")
	private void updateBalloons(Entity entity, CallbackInfo info) {
		BalloonHolder balloonHolder = (BalloonHolder) entity;
		ClientChunkProvider chunkProvider = ((ClientWorld) (Object) this).getChunkProvider();
		for (BolloomBalloonEntity balloon : balloonHolder.getBalloons()) {
			if (!balloon.removed && balloon.getAttachedEntity() == entity) {
				if (chunkProvider.isChunkLoaded(balloon)) {
					balloon.forceSetPosition(balloon.getPosX(), balloon.getPosY(), balloon.getPosZ());
					balloon.prevRotationYaw = balloon.rotationYaw;
					balloon.prevRotationPitch = balloon.rotationPitch;
					if (balloon.addedToChunk) {
						balloon.ticksExisted++;
						balloon.updateAttachedPosition();
					}
					this.callCheckChunk(balloon);
				}
			} else {
				balloon.detachFromEntity();
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateRidden()V", shift = At.Shift.AFTER), method = "updateEntityRidden")
	private void updateEntityRiddenBalloons(Entity ridingEntity, Entity passenger, CallbackInfo info) {
		BalloonHolder balloonHolder = (BalloonHolder) passenger;
		ClientChunkProvider chunkProvider = ((ClientWorld) (Object) this).getChunkProvider();
		for (BolloomBalloonEntity balloon : balloonHolder.getBalloons()) {
			if (!balloon.removed && balloon.getAttachedEntity() == passenger) {
				if (chunkProvider.isChunkLoaded(balloon)) {
					balloon.forceSetPosition(balloon.getPosX(), balloon.getPosY(), balloon.getPosZ());
					balloon.prevRotationYaw = balloon.rotationYaw;
					balloon.prevRotationPitch = balloon.rotationPitch;
					if (balloon.addedToChunk) {
						balloon.ticksExisted++;
						balloon.updateAttachedPosition();
					}
					this.callCheckChunk(balloon);
				}
			} else {
				balloon.detachFromEntity();
			}
		}
	}

	@Invoker
	public abstract void callCheckChunk(Entity entity);

}
