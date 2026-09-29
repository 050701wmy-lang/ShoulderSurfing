package com.github.exopandora.shouldersurfing.fabric.mixin;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.fabric.CameraTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShoulderSurfing.class)
abstract class ShoulderSurfingTransitionMixin {
	@Inject(method = "changePerspective(Lcom/github/exopandora/shouldersurfing/api/client/Perspective;Z)V", at = @At("HEAD"))
	private void onPerspectiveChange(Perspective perspective, boolean lookAtCrosshairTarget, CallbackInfo ci) {
		CameraTransition.onPerspectiveChange(perspective);
	}
}
