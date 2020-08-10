package defeatedcrow.hac.config;

import java.io.File;

import defeatedcrow.hac.core.climate.ArmorResistantRegister;
import defeatedcrow.hac.core.climate.ClimateRegister;
import defeatedcrow.hac.core.climate.HeatBlockRegister;
import defeatedcrow.hac.core.climate.MobResistantRegister;
import defeatedcrow.hac.core.fluid.FluidDictionaryDC;
import net.minecraftforge.common.config.Configuration;

public class ClimateConfig {

	private ClimateConfig() {}

	public static final ClimateConfig INSTANCE = new ClimateConfig();

	public static File configDir;

	public void load(File file) {

		configDir = new File(file, "defeatedcrow/climate/");
		if (configDir == null) {
			configDir.mkdirs();
		}
		File dir = new File(file, "defeatedcrow/climate/core.cfg");
		CoreConfigDC.INSTANCE.load(new Configuration(dir));
		CoreConfigDC.leadBlockNames();

		// resistant data
		MobResistantRegister.setDir(configDir);
		ArmorResistantRegister.setDir(configDir);
		HeatBlockRegister.setDir(configDir);
		ClimateRegister.setDir(configDir);
		FluidDictionaryDC.setDir(configDir);
	}

}
