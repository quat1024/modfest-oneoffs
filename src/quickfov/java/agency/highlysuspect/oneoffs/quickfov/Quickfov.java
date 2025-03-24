package agency.highlysuspect.oneoffs.quickfov;

import agency.highlysuspect.oneoffs.common.AutoloadProperties;
import agency.highlysuspect.oneoffs.common.BaseClient;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.lwjgl.glfw.GLFW;

public class Quickfov extends BaseClient {
	public Quickfov() {
		super("oneoffs-quickfov");
		INSTANCE = this;
	}

	public static Quickfov INSTANCE;

	public final KeyMapping key = new KeyMapping("key.quickfov.key", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT, "key.categories.quickfov");
	public QuickfovConfig config = new QuickfovConfig();

	@Override
	public void onInitializeClient() {
		log.info("Hello from Quickfov");
		KeyBindingHelper.registerKeyBinding(key);

		AutoloadProperties<QuickfovConfig> autoload = new AutoloadProperties<>(
			log,
			configPath("quickfov.properties"),
			QuickfovConfig::new,
			QuickfovConfig::toProperties,
			QuickfovConfig::fromProperties,
			QuickfovConfig.comment()
		);
		autoload.load(newConfig -> config = newConfig);
		autoload.watch(newConfig ->
			Minecraft.getInstance().execute(() -> config = newConfig));
	}

	double accumRoundoff;

	public void doIt(double dx, double dy) {
		Options opts = Minecraft.getInstance().options;

		double d = config.horiz ? dx : dy;

		//minecraft does this same type of computation for the sensitivity
		//fern calls the local variable "g"
		double sens = opts.sensitivity().get();
		sens *= 0.6;
		sens += 0.2;
		sens = sens * sens * sens;
		sens *= 8;

		//apply your sensitivity to d
		double dSens = d * sens * config.sensitivity;

		//how much to change the fov
		int deltaFov = (int) Math.round(dSens);

		//accumulate roundoff since the fov can only be set to integers
		//this seems backwards but idk it works
		accumRoundoff += (deltaFov - dSens);
		if(accumRoundoff >= 1) {
			accumRoundoff--;
			deltaFov--;
		} else if(accumRoundoff <= -1) {
			accumRoundoff++;
			deltaFov++;
		}

		//log.info("d {} deltaFov {} roundoff {}", d, deltaFov, accumRoundoff);
		int oldFov = opts.fov().get();
		int newFov = oldFov + deltaFov;

		//some mods (zoom mods?) might change the min and max fov...?
		//so only clamp if the old value was already inside the range
		if(oldFov >= config.min) newFov = Math.max(config.min, newFov);
		if(oldFov <= config.max) newFov = Math.min(config.max, newFov);

		opts.fov().set(newFov);
	}

	public void dontDoIt() {
		accumRoundoff = 0;
	}
}
