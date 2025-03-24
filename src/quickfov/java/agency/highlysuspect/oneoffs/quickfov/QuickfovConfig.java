package agency.highlysuspect.oneoffs.quickfov;

import java.util.Properties;

public class QuickfovConfig {
	public double sensitivity = 0.5;

	public int min = 30;
	public int max = 110;

	public void toProperties(Properties props) {
		props.setProperty("sensitivity", Double.toString(sensitivity));
		props.setProperty("min", Integer.toString(min));
		props.setProperty("max", Integer.toString(max));
	}

	public static QuickfovConfig fromProperties(Properties props) {
		QuickfovConfig cfg = new QuickfovConfig();
		cfg.sensitivity = Double.parseDouble(props.getProperty("sensitivity", "0.5"));
		cfg.min = Integer.parseInt(props.getProperty("min", "30"));
		cfg.max = Integer.parseInt(props.getProperty("max", "110"));

		return cfg;
	}
}
