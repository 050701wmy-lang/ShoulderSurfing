package com.github.exopandora.shouldersurfing.fabric;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.client.InputHandler;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

/** Fabric-only controls layered on the Shoulder Surfing camera. */
public final class CameraFeatures {
	public static final KeyMapping ZOOM = new KeyMapping(
		"key.shouldersurfing.zoom", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, InputHandler.GENERAL
	);

	private static Object activeLevel;
	private static Perspective requestedPerspective;
	private static Perspective appliedPerspective;
	private static Reason activeReason = Reason.NONE;
	private static boolean manualOverride;
	private static float zoomScale = 1.0F;
	private static float preferredZoomScale = 0.35F;

	private CameraFeatures() {
	}

	public static void tick(Minecraft minecraft) {
		if (minecraft.level == null) {
			reset();
			return;
		}
		if (minecraft.level != activeLevel) {
			reset();
			activeLevel = minecraft.level;
		}
		if (minecraft.player == null || minecraft.gameMode == null) {
			return;
		}

		float targetZoom = ZOOM.isDown() && minecraft.gui.screen() == null ? preferredZoomScale : 1.0F;
		zoomScale += (targetZoom - zoomScale) * 0.35F;
		if (Math.abs(targetZoom - zoomScale) < 0.001F) {
			zoomScale = targetZoom;
		}

		var current = Perspective.current();
		var reason = reason(minecraft);
		// Shoulder Surfing may already be using its own temporary first-person mode
		// (for example while aiming). Let it restore its perspective first.
		if (ShoulderSurfing.getInstance().isTemporaryFirstPerson()) {
			return;
		}
		if (requestedPerspective == null) {
			requestedPerspective = current;
		}
		if (appliedPerspective != null && current != appliedPerspective) {
			requestedPerspective = current;
			appliedPerspective = null;
			manualOverride = true;
		}
		if (reason != activeReason) {
			manualOverride = false;
			activeReason = reason;
		}
		if (reason == Reason.NONE) {
			if (appliedPerspective != null && current == appliedPerspective && requestedPerspective != current) {
				changePerspective(requestedPerspective);
			}
			appliedPerspective = null;
			requestedPerspective = Perspective.current();
			return;
		}
		if (manualOverride) {
			return;
		}
		var target = reason == Reason.CUTSCENE || reason == Reason.DEATH
			? Perspective.FIRST_PERSON : Perspective.SHOULDER_SURFING;
		if (current != target) {
			changePerspective(target);
			appliedPerspective = target;
		}
	}

	private static Reason reason(Minecraft minecraft) {
		if (minecraft.player.isDeadOrDying()) {
			return Reason.DEATH;
		}
		if (isWynncraft(minecraft) && minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
			return Reason.CUTSCENE;
		}
		if (minecraft.player.isPassenger()) {
			return Reason.RIDING;
		}
		if (minecraft.player.isFallFlying()) {
			return Reason.ELYTRA;
		}
		return Reason.NONE;
	}

	private static boolean isWynncraft(Minecraft minecraft) {
		var server = minecraft.getCurrentServer();
		if (server == null) {
			return false;
		}
		var address = server.ip.toLowerCase(Locale.ROOT).split(":", 2)[0];
		return address.equals("wynncraft.com") || address.endsWith(".wynncraft.com");
	}

	private static void changePerspective(Perspective perspective) {
		ShoulderSurfing.getInstance().changePerspective(perspective);
	}

	public static boolean onScroll(double amount) {
		var minecraft = Minecraft.getInstance();
		if (!ZOOM.isDown() || minecraft.gui.screen() != null || minecraft.player == null || amount == 0.0D) {
			return false;
		}
		preferredZoomScale = (float) Math.clamp(preferredZoomScale * Math.pow(0.85D, amount), 0.1D, 0.9D);
		return true;
	}

	public static float applyZoom(float fov) {
		return fov * zoomScale;
	}

	private static void reset() {
		CameraTransition.reset();
		activeLevel = null;
		requestedPerspective = null;
		appliedPerspective = null;
		activeReason = Reason.NONE;
		manualOverride = false;
		zoomScale = 1.0F;
	}

	private enum Reason {
		NONE, DEATH, CUTSCENE, RIDING, ELYTRA
	}
}
