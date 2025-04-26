package agency.highlysuspect.oneoffs.hideservermessages.mixin;

import agency.highlysuspect.oneoffs.hideservermessages.HideServerMessages;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
	@Inject(method = "addMessageToDisplayQueue", at = @At("HEAD"), cancellable = true)
	public void asdf(GuiMessage msg, CallbackInfo ci) {
		if(HideServerMessages.INSTANCE.config.show) return;

		//try to allow discord bridge messages
		if(msg.content().getString().trim().contains("<")) return;

		if(GuiMessageTag.system().equals(msg.tag()) || GuiMessageTag.systemSinglePlayer().equals(msg.tag()))
			ci.cancel();
	}
}
