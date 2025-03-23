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
}
