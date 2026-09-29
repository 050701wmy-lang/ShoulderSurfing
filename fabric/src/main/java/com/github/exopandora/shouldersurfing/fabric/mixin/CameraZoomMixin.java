package com.github.exopandora.shouldersurfing.fabric.mixin;

import com.github.exopandora.shouldersurfing.fabric.CameraFeatures;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
abstract class CameraZoomMixin {
	@Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true)
	private void calculateFov(float partialTick, CallbackInfoReturnable<Float> cir) {
		cir.setReturnValue(CameraFeatures.applyZoom(cir.getReturnValue()));
	}
}
