package be.pyrrh4.pyrcore.lib.configuration;

import java.io.BufferedReader;
import java.io.FileReader;

import org.bukkit.configuration.file.YamlConfiguration;

class YMLReader
{
	// ------------------------------------------------------------
	// Fields and constructor
	// ------------------------------------------------------------

	private YMLConfiguration config;

	YMLReader(YMLConfiguration config) {
		this.config = config;
	}

	// ------------------------------------------------------------
	// Read
	// ------------------------------------------------------------

	void read()
	{
		// header comment
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(config.file));
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				if (line.contains("#"))
				{
					String lineNoSpaces = line.substring(line.indexOf("#") + 1);

					if (lineNoSpaces.startsWith("#")) {
						config.headerComment += lineNoSpaces + "\n";
					} else {// end of header
						break;
					}
				}
			}
			reader.close();
		} catch (Throwable ignored) {}

		// set yaml
		config.yaml = YamlConfiguration.loadConfiguration(config.file);
	}
}
