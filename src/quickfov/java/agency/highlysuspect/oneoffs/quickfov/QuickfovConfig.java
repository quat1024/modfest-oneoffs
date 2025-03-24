package agency.highlysuspect.oneoffs.quickfov;

import java.util.Properties;

public class QuickfovConfig {
	public boolean horiz = true;
	public double sensitivity = -0.2;

	public int min = 30;
	public int max = 110;

	public void toProperties(Properties props) {
		props.setProperty("horiz", Boolean.toString(horiz));
		props.setProperty("sensitivity", Double.toString(sensitivity));
		props.setProperty("min", Integer.toString(min));
		props.setProperty("max", Integer.toString(max));
	}

	public static QuickfovConfig fromProperties(Properties props) {
		QuickfovConfig cfg = new QuickfovConfig();
		cfg.horiz = Boolean.parseBoolean(props.getProperty("horiz", "true"));
		cfg.sensitivity = Double.parseDouble(props.getProperty("sensitivity", "-0.2"));
		cfg.min = Integer.parseInt(props.getProperty("min", "30"));
		cfg.max = Integer.parseInt(props.getProperty("max", "110"));

		return cfg;
	}

	public static String[] comment() {
		return new String[] {
			"oneoffs-quickfov config file",
			"",
			"sensitivity: Multiplier applied to the mouse movement. Make it negative to",
			"   go the other way.",
			"",
			"horiz: If 'true', moving the mouse left and right changes the FoV. If 'false',",
			"   moving the mouse up and down changes the FoV.",
			"",
			"min, max: The range of FoVs that quickfov can set. Note that vanilla clamps",
			"   the FoV between 30 and 110 and won't permit anything outside that range.",
			"",
		};
	}
}
