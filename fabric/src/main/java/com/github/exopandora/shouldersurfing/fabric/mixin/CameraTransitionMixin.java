package com.github.exopandora.shouldersurfing.fabric.mixin;

import com.github.exopandora.shouldersurfing.fabric.CameraTransition;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
abstract class CameraTransitionMixin {
	@Shadow
	protected abstract void setPosition(double x, double y, double z);

	@Inject(method = "alignWithEntity", at = @At("TAIL"))
	private void smoothPosition(float partialTick, CallbackInfo ci) {
		var position = CameraTransition.position((Camera) (Object) this, partialTick);
		this.setPosition(position.x, position.y, position.z);
	}
}
