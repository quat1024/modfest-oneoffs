package agency.highlysuspect.oneoffs.hideservermessages;

import java.util.Properties;

public class HideServerMessagesConfig {
	public boolean show = true;

	public void toProperties(Properties props) {
		props.setProperty("show", Boolean.toString(show));
	}

	public static HideServerMessagesConfig fromProperties(Properties props) {
		HideServerMessagesConfig cfg = new HideServerMessagesConfig();
		cfg.show = Boolean.parseBoolean(props.getProperty("show", "true"));

		return cfg;
	}

	public static String[] comment() {
		return new String[] {
			"oneoffs-hideservermessages config file",
			"",
		};
	}
}
