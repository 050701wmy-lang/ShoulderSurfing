package com.github.exopandora.shouldersurfing.fabric.mixin;

import com.github.exopandora.shouldersurfing.fabric.CameraFeatures;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
abstract class MouseHandlerZoomMixin {
	@Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
	private void onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
		if (CameraFeatures.onScroll(vertical)) {
			ci.cancel();
		}
	}
}
