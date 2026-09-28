package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.phase.SimpleFeatureRenderPhase;
import net.minecraft.client.renderer.feature.phase.TranslucentFeatureRenderPhase;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SubmitNodeCollection.class)
class SubmitNodeCollectionMixin {
	@Shadow @Final public SimpleFeatureRenderPhase afterTerrain;

	@ModifyVariable(
		at = @At("HEAD"),
		method = "submitModel",
		index = 7,
		argsOnly = true
	)
	private int submitModel(int tintedColor) {
		return ShoulderSurfing.getInstance().getCameraEntityRenderer().applyCameraEntityAlphaContextAware(tintedColor);
	}

	@Redirect(method = "submitModel", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/feature/phase/TranslucentFeatureRenderPhase;submit(Lnet/minecraft/client/renderer/feature/submit/TranslucentSubmit;)V"))
	private void submitFadedCameraEntityAfterTerrain(TranslucentFeatureRenderPhase phase, TranslucentSubmit submit) {
		if (ShoulderSurfing.getInstance().getCameraEntityRenderer().isRenderingFadedCameraEntity()) {
			this.afterTerrain.submit(submit);
		} else {
			phase.submit(submit);
		}
	}
}
