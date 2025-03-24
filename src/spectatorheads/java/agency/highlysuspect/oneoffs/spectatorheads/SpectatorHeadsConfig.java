package agency.highlysuspect.oneoffs.spectatorheads;

import java.util.Properties;

public class SpectatorHeadsConfig {
	public boolean showSpectatorHeads = false;
	
	public void toProperties(Properties props) {
		props.setProperty("showSpectatorHeads", Boolean.toString(showSpectatorHeads));
	}
	
	public static SpectatorHeadsConfig fromProperties(Properties props) {
		SpectatorHeadsConfig cfg = new SpectatorHeadsConfig();
		cfg.showSpectatorHeads = Boolean.parseBoolean(props.getProperty("showSpectatorHeads", "false"));
		return cfg;
	}

	public static String[] comment() {
		return new String[] {
			"oneoffs-spectatorheads config file",
			"",
			"showSpectatorHeads: If 'false', other spectators will not render in spectator mode.",
			"",
		};
	}
}
