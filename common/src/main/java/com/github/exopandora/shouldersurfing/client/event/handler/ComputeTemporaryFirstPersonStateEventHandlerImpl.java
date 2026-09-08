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
		private int additionalTime;
		
		@Override
		public void handle(ComputeTemporaryFirstPersonStateEvent event) {
			if (!event.getResult()) {
				event.setResult(this.isSpaceConstrained || this.additionalTime > 0);
			}
		}
		
		@Override
		public void handle(TickEvent event) {
			var perspectiveConfig = Config.CLIENT.getPerspectiveConfig();
			if (perspectiveConfig.isTemporaryFirstPersonInConstrainedSpacesEnabled()) {
				var camera = IShoulderSurfing.getInstance().getCamera();
				this.isSpaceConstrained = isSpaceConstrained(camera);
				if (this.isSpaceConstrained) {
					this.additionalTime = perspectiveConfig.getTemporaryFirstPersonInConstrainedSpacesAdditionalTime();
				} else if (this.additionalTime > 0) {
					this.additionalTime--;
				}
			} else {
				this.isSpaceConstrained = false;
				this.additionalTime = 0;
			}
		}
		
		private static boolean isSpaceConstrained(IShoulderSurfingCamera camera) {
			var perspectiveConfig = Config.CLIENT.getPerspectiveConfig();
			var cameraConfig = Config.CLIENT.getCameraConfig();
			var thresholdX = Math.min(perspectiveConfig.getTemporaryFirstPersonOffsetXThreshold(), Math.abs(cameraConfig.getOffsetX()) - 0.0001);
			var renderOffsetX = Math.abs(camera.getRenderOffset().x);
			if (renderOffsetX < thresholdX) {
				return true;
			}
			var thresholdY = (float) Math.min(perspectiveConfig.getTemporaryFirstPersonOffsetYThreshold(), Math.abs(cameraConfig.getOffsetY()));
			var renderOffsetY = (float) Math.abs(camera.getRenderOffset().y);
			if (thresholdY < renderOffsetY) {
			var thresholdY = Math.min(perspectiveConfig.getTemporaryFirstPersonOffsetYThreshold(), Math.abs(cameraConfig.getOffsetY()) - 0.0001);
			var renderOffsetY = Math.abs(camera.getRenderOffset().y);
			if (renderOffsetY < thresholdY) {
				return true;
			}
			var thresholdZ = Math.min(perspectiveConfig.getTemporaryFirstPersonOffsetZThreshold(), Math.abs(cameraConfig.getOffsetZ()) - 0.0001);
			var renderOffsetZ = Math.abs(camera.getRenderOffset().z);
			return renderOffsetZ < thresholdZ;
		}
	}
}
