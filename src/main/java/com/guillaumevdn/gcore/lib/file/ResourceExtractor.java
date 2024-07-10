/**
 *
 * This software is part of the TheEnderBox
 *
 * TheEnderBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * TheEnderBox is distributed in the hope that it will be useful,
 * but WITHOUT UNKNOWN WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TheEnderBox. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.guillaumevdn.gcore.lib.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.jar.JarFile;

import org.bukkit.plugin.java.JavaPlugin;

import com.guillaumevdn.gcore.lib.wrapper.Wrapper;
import com.guillaumevdn.gcore.lib.wrapper.WrapperInteger;

public final class ResourceExtractor {

	private JavaPlugin plugin;
	private File targetFolder;
	private final String resourcePath;
	private List<String> ignoreStartingBy;

	public ResourceExtractor(JavaPlugin plugin, File targetFolder, String resourcePath) {
		this(plugin, targetFolder, resourcePath, null);
	}

	public ResourceExtractor(JavaPlugin plugin, File targetFolder, String resourcePath, List<String> ignoreStartingBy) {
		this.plugin = plugin;
		this.targetFolder = targetFolder;
		this.resourcePath = resourcePath;
		this.ignoreStartingBy = ignoreStartingBy;
	}

	// ----- methods
	public int extract(boolean override, boolean subpaths) throws Throwable {
		WrapperInteger done = WrapperInteger.of(0);
		Wrapper<Throwable> error = Wrapper.of(null);

		// get jar file
		File jarfile = null;
		try {
			Method method = JavaPlugin.class.getDeclaredMethod("getFile");
			method.setAccessible(true);
			jarfile = (File) method.invoke(plugin);
		} catch (Throwable exception) {
			throw new IOException(exception);
		}

		// create folder
		targetFolder.mkdirs();

		// read entries
		JarFile jar = new JarFile(jarfile);
		jar.stream().forEachOrdered(entry -> {
			if (error.get() != null) {
				return;
			}
			try {
				String path = entry.getName();

				// not target, or must ignore
				if (!path.startsWith(resourcePath)) {
					return;
				}
				if (ignoreStartingBy != null && ignoreStartingBy.stream().anyMatch(ignore -> path.startsWith(resourcePath + ignore))) {
					return;
				}

				// directory
				if (entry.isDirectory() && !path.contains(".")) {
					if (subpaths) {
						File file = new File(targetFolder, entry.getName().replaceFirst(resourcePath, ""));
						if (!file.exists()) {
							file.mkdirs();
							done.alter(1);
						}
					}
				} else {
					// use the correct path
					File file;
					if (subpaths) {
						file = new File(targetFolder, path.replaceFirst(resourcePath, ""));
					} else {
						int index = path.indexOf(File.separatorChar);
						if (index == -1)
							index = path.indexOf('/');
						file = new File(targetFolder, path.substring(index, path.length()));
					}

					// delete file if override
					if (file.exists() && override) {
						file.delete();
					}

					// extract file
					if (!file.exists()) {
						if (file.getParentFile() != null && !file.getParentFile().exists()) {
							file.getParentFile().mkdirs();
						}
						file.createNewFile();

						InputStream in = plugin.getClass().getClassLoader().getResourceAsStream(entry.getName());
						FileOutputStream out = new FileOutputStream(file);

						byte[] buffer = new byte[1024];
						for (int n; (n = in.read(buffer)) != -1; out.write(buffer, 0, n))
							;

						in.close();
						out.close();

						/*
						 * // original code, which tends to be slower InputStream is = jar.getInputStream(entry); FileOutputStream fos = new
						 * FileOutputStream(file); while (is.available() > 0) { fos.write(is.read()); } fos.close(); is.close();
						 */

						done.alter(1);
					}

				}
			} catch (Throwable exception) {
				error.set(exception);
			}
		});
		jar.close();

		if (error.get() != null) {
			throw error.get();
		}

		return done.get();
	}

}
