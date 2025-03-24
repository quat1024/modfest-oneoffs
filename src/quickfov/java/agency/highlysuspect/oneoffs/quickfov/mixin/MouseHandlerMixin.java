package agency.highlysuspect.oneoffs.quickfov.mixin;

import agency.highlysuspect.oneoffs.quickfov.Quickfov;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
	@Shadow private double accumulatedDX;
	@Shadow private double accumulatedDY;

	@Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
	private void quickfov$whenTurning(
		@SuppressWarnings("unused") double dt,
		CallbackInfo ci
	) {
		if(Quickfov.INSTANCE.key.isDown()) {
			Quickfov.INSTANCE.doIt(accumulatedDX, accumulatedDY);
			ci.cancel();
		} else {
			Quickfov.INSTANCE.dontDoIt();
		}
	}
}
