package com.github.exopandora.shouldersurfing.fabric;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/** Interpolates between already collision-adjusted camera positions. */
public final class CameraTransition {
	private static final long DURATION_NANOS = 250_000_000L;
	private static Vec3 startOffset;
	private static long startTime;

	private CameraTransition() {
	}

	public static void onPerspectiveChange(Perspective next) {
		var minecraft = Minecraft.getInstance();
		if (minecraft.player == null || minecraft.getCameraEntity() != minecraft.player
			|| minecraft.gameRenderer.mainCamera().entity() != minecraft.player
			|| next == Perspective.current()) {
			return;
		}
		startOffset = minecraft.gameRenderer.mainCamera().position()
			.subtract(minecraft.player.getEyePosition(1.0F));
		startTime = System.nanoTime();
	}

	public static Vec3 position(Camera camera, float partialTick) {
		if (startOffset == null) {
			return camera.position();
		}
		var minecraft = Minecraft.getInstance();
		if (minecraft.player == null || minecraft.getCameraEntity() != minecraft.player) {
			startOffset = null;
			return camera.position();
		}
		double progress = (double) (System.nanoTime() - startTime) / DURATION_NANOS;
		if (progress >= 1.0D) {
			startOffset = null;
			return camera.position();
		}
		progress = Math.clamp(progress, 0.0D, 1.0D);
		progress = progress * progress * (3.0D - 2.0D * progress);
		var eye = minecraft.player.getEyePosition(partialTick);
		var targetOffset = camera.position().subtract(eye);
		return eye.add(startOffset.lerp(targetOffset, progress));
	}

	public static void reset() {
		startOffset = null;
	}
}
