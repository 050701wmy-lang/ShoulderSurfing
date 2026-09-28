package com.github.exopandora.shouldersurfing.api.config;

import java.util.List;

public interface IIntegrationsConfig {
	boolean isWynntilsCutsceneFirstPersonEnabled();

	List<? extends String> getCuriosAdaptiveCrosshairItems();
	
	List<? extends String> getCuriosAdaptiveCrosshairDefaultItemComponents();
	
	List<? extends String> getCuriosAdaptiveCrosshairItemComponents();
}
