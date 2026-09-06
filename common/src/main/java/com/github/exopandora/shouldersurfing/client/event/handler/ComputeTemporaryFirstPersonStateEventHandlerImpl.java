package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeTemporaryFirstPersonStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.TickEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeTemporaryFirstPersonStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.TickEventHandler;
import com.github.exopandora.shouldersurfing.config.Config;

public class ComputeTemporaryFirstPersonStateEventHandlerImpl {
	public enum WhenAiming implements ComputeTemporaryFirstPersonStateEventHandler {
		INSTANCE;
		
		@Override
		public void handle(ComputeTemporaryFirstPersonStateEvent event) {
			if (!event.getResult()) {
				var result = switch (Config.CLIENT.getCrosshairConfig().getCrosshairType()) {
					case STATIC_WITH_1PP, DYNAMIC_WITH_1PP -> IShoulderSurfing.getInstance().isAiming();
					default -> false;
				};
				event.setResult(result);
			}
		}
	}
	
	public enum ConstrainedSpace implements ComputeTemporaryFirstPersonStateEventHandler, TickEventHandler {
		INSTANCE;
		
		private boolean isSpaceConstrained;
		private int extendedTime;
		
		@Override
		public void handle(ComputeTemporaryFirstPersonStateEvent event) {
			if (!event.getResult()) {
				event.setResult(this.isSpaceConstrained || this.extendedTime > 0);
			}
		}
		
		@Override
		public void handle(TickEvent event) {
			var perspectiveConfig = Config.CLIENT.getPerspectiveConfig();
			if (perspectiveConfig.isTemporaryFirstPersonInConstrainedSpacesEnabled()) {
				var camera = IShoulderSurfing.getInstance().getCamera();
				this.isSpaceConstrained = isSpaceConstrained(camera);
				if (this.isSpaceConstrained) {
					this.extendedTime = perspectiveConfig.getAdditionalTemporaryFirstPersonTimeInConstrainedSpaces();
				} else if (this.extendedTime > 0) {
					this.extendedTime--;
				}
			}
		}
		
		private static boolean isSpaceConstrained(IShoulderSurfingCamera camera) {
			var perspectiveConfig = Config.CLIENT.getPerspectiveConfig();
			var cameraConfig = Config.CLIENT.getCameraConfig();
			if (Math.abs(camera.getRenderOffset().x) < Math.min(perspectiveConfig.getTemporaryFirstPersonOffsetXThreshold(), Math.abs(cameraConfig.getOffsetX()))) {
				return true;
			} else if (Math.abs(camera.getRenderOffset().y) < Math.min(perspectiveConfig.getTemporaryFirstPersonOffsetYThreshold(), Math.abs(cameraConfig.getOffsetY()))) {
				return true;
			}
			return Math.abs(camera.getRenderOffset().z) < Math.min(perspectiveConfig.getTemporaryFirstPersonOffsetZThreshold(), Math.abs(cameraConfig.getOffsetZ()));
		}
	}
}
