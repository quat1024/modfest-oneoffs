package agency.highlysuspect.oneoffs.hideservermessages;

import java.util.Properties;

public class HideServerMessagesConfig {
	public boolean show = true;
	public boolean permitAngleBracket = true;

	public void toProperties(Properties props) {
		props.setProperty("show", Boolean.toString(show));
		props.setProperty("permitAngleBracket", Boolean.toString(permitAngleBracket));
	}

	public static HideServerMessagesConfig fromProperties(Properties props) {
		HideServerMessagesConfig cfg = new HideServerMessagesConfig();
		cfg.show = Boolean.parseBoolean(props.getProperty("show", "true"));
		cfg.permitAngleBracket = Boolean.parseBoolean(props.getProperty("permitAngleBracket", "true"));

		return cfg;
	}

	public static String[] comment() {
		return new String[] {
			"oneoffs-hideservermessages config file",
			"",
			"show: If 'false', server messages will be hidden. This includes joins/leaves, command feedback, etc.",
			"",
			"permitAngleBracket: If 'true', server messages containing a < character will be allowed through.",
			"   This is a little crude, but typically allows IRC/discord-integration mods to avoid being hidden.",
			"",
		};
	}
}
