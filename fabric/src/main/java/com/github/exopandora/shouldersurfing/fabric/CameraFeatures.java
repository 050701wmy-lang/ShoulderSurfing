package com.github.exopandora.shouldersurfing.fabric;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.client.InputHandler;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.config.Config;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

/** Fabric-only controls layered on the Shoulder Surfing camera. */
public final class CameraFeatures {
	public static final KeyMapping THIRD_PERSON_DISTANCE = new KeyMapping(
		"key.shouldersurfing.third_person_distance", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_CONTROL, InputHandler.GENERAL
	);

	private static Object activeLevel;
	private static Perspective requestedPerspective;
	private static Perspective appliedPerspective;
	private static Reason activeReason = Reason.NONE;
	private static boolean manualOverride;

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
		if (!THIRD_PERSON_DISTANCE.isDown() || minecraft.gui.screen() != null || minecraft.player == null
			|| Perspective.current() == Perspective.FIRST_PERSON || amount == 0.0D) {
			return false;
		}
		Config.CLIENT.getCameraConfig().adjustThirdPersonDistance(amount);
		return true;
	}

	private static void reset() {
		CameraTransition.reset();
		activeLevel = null;
		requestedPerspective = null;
		appliedPerspective = null;
		activeReason = Reason.NONE;
		manualOverride = false;
	}

	private enum Reason {
		NONE, DEATH, CUTSCENE, RIDING, ELYTRA
	}
}
