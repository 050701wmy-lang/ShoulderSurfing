package com.github.exopandora.shouldersurfing.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

/** Settings for the features added by Better Shoulder Surfing. */
public class AddedFeaturesConfig {
	private final BooleanValue distanceScrollEnabled;
	private final DoubleValue distanceScrollStep;
	private final DoubleValue maximumDistance;
	private final BooleanValue smoothPerspectiveEnabled;
	private final IntValue perspectiveTransitionMilliseconds;
	private final BooleanValue ridingThirdPersonEnabled;
	private final BooleanValue elytraThirdPersonEnabled;
	private final BooleanValue deathFirstPersonEnabled;
	private final BooleanValue wynncraftCutsceneFirstPersonEnabled;

	protected AddedFeaturesConfig(ModConfigSpec.Builder builder) {
		builder.push("added_features");
		this.distanceScrollEnabled = builder.comment("Enable modifier-key scrolling to adjust third-person distance.")
			.translation(MOD_ID + ".configuration.added_features.distance_scroll_enabled")
			.define("distance_scroll_enabled", true);
		this.distanceScrollStep = builder.comment("Blocks per scroll step when adjusting third-person distance.")
			.translation(MOD_ID + ".configuration.added_features.distance_scroll_step")
			.defineInRange("distance_scroll_step", 1.0D, 0.05D, 20.0D);
		this.maximumDistance = builder.comment("Maximum distance in blocks for modifier-key scrolling.")
			.translation(MOD_ID + ".configuration.added_features.maximum_distance")
			.defineInRange("maximum_distance", 100.0D, 1.0D, 100.0D);
		this.smoothPerspectiveEnabled = builder.comment("Smooth camera position when switching perspective.")
			.translation(MOD_ID + ".configuration.added_features.smooth_perspective_enabled")
			.define("smooth_perspective_enabled", true);
		this.perspectiveTransitionMilliseconds = builder.comment("Duration of perspective transitions in milliseconds.")
			.translation(MOD_ID + ".configuration.added_features.perspective_transition_milliseconds")
			.defineInRange("perspective_transition_milliseconds", 250, 0, 2000);
		this.ridingThirdPersonEnabled = builder.comment("Temporarily switch to shoulder view while riding, then restore the previous view.")
			.translation(MOD_ID + ".configuration.added_features.riding_third_person_enabled")
			.define("riding_third_person_enabled", true);
		this.elytraThirdPersonEnabled = builder.comment("Temporarily switch to shoulder view while flying with elytra.")
			.translation(MOD_ID + ".configuration.added_features.elytra_third_person_enabled")
			.define("elytra_third_person_enabled", true);
		this.deathFirstPersonEnabled = builder.comment("Temporarily switch to first person on death.")
			.translation(MOD_ID + ".configuration.added_features.death_first_person_enabled")
			.define("death_first_person_enabled", true);
		this.wynncraftCutsceneFirstPersonEnabled = builder.comment("Temporarily switch to first person when Wynncraft puts the player in spectator mode.")
			.translation(MOD_ID + ".configuration.added_features.wynncraft_cutscene_first_person_enabled")
			.define("wynncraft_cutscene_first_person_enabled", true);
		builder.pop();
	}

	public boolean isDistanceScrollEnabled() { return this.distanceScrollEnabled.get(); }
	public double getDistanceScrollStep() { return this.distanceScrollStep.get(); }
	public double getMaximumDistance() { return this.maximumDistance.get(); }
	public boolean isSmoothPerspectiveEnabled() { return this.smoothPerspectiveEnabled.get(); }
	public int getPerspectiveTransitionMilliseconds() { return this.perspectiveTransitionMilliseconds.get(); }
	public boolean isRidingThirdPersonEnabled() { return this.ridingThirdPersonEnabled.get(); }
	public boolean isElytraThirdPersonEnabled() { return this.elytraThirdPersonEnabled.get(); }
	public boolean isDeathFirstPersonEnabled() { return this.deathFirstPersonEnabled.get(); }
	public boolean isWynncraftCutsceneFirstPersonEnabled() { return this.wynncraftCutsceneFirstPersonEnabled.get(); }
}
