package com.github.exopandora.shouldersurfing.compat.wynntils;

import com.github.exopandora.shouldersurfing.api.client.event.ComputeTemporaryFirstPersonStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeTemporaryFirstPersonStateEventHandler;
import com.github.exopandora.shouldersurfing.config.Config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Optional bridge to the cutscene state exposed by Wynntils 5. */
public enum WynntilsCutsceneEventHandler implements ComputeTemporaryFirstPersonStateEventHandler {
	INSTANCE;

	private boolean initialized;
	private Object model;
	private Method isCutsceneActive;

	@Override
	public void handle(ComputeTemporaryFirstPersonStateEvent event) {
		if (!event.getResult()
			&& Config.CLIENT.getIntegrationsConfig().isWynntilsCutsceneFirstPersonEnabled()
			&& this.isCutsceneActive()) {
			event.setResult(true);
		}
	}

	private boolean isCutsceneActive() {
		if (!this.initialized) {
			this.initialized = true;
			try {
				Class<?> models = Class.forName("com.wynntils.core.components.Models");
				Field field = models.getField("Cutscene");
				this.model = field.get(null);
				if (this.model == null) {
					this.initialized = false;
					return false;
				}
				this.isCutsceneActive = this.model.getClass().getMethod("isCutsceneActive");
			} catch (ReflectiveOperationException | LinkageError exception) {
				this.model = null;
				return false;
			}
		}
		if (this.model == null) return false;
		try {
			return Boolean.TRUE.equals(this.isCutsceneActive.invoke(this.model));
		} catch (ReflectiveOperationException | RuntimeException exception) {
			return false;
		}
	}
}
