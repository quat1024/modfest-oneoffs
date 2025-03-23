package agency.highlysuspect.oneoffs.spectatorheads.mixin;

import agency.highlysuspect.oneoffs.spectatorheads.SpectatorHeads;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void spectatorheads$onRender(
		AbstractClientPlayer acp,
		@SuppressWarnings("unused") float _unused1,
		@SuppressWarnings("unused") float _unused2,
		@SuppressWarnings("unused") PoseStack _unused3,
		@SuppressWarnings("unused") MultiBufferSource _unused4,
		@SuppressWarnings("unused") int _unused5,
		CallbackInfo ci
	) {
		if(acp.isSpectator() && !SpectatorHeads.CONFIG.showSpectatorHeads)
			ci.cancel();
	}
	
//	@Inject(method = "setModelProperties", at = @At(
//		value = "INVOKE",
//		target = "Lnet/minecraft/client/model/PlayerModel;setAllVisible(Z)V",
//		shift = At.Shift.AFTER,
//		ordinal = 0 //inside the "isSpectator" block
//	), cancellable = true)
//	private void spectatorheads$reallySetModelProperties(AbstractClientPlayer acp, CallbackInfo ci) {
//		if(!SpectatorHeads.CONFIG.showSpectatorHeads) ci.cancel();
//	}
}
