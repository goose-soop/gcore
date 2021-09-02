package com.guillaumevdn.gcore.lib.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTCompound;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterItemStack;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterNBTCompound;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterPotionEffect;
import com.guillaumevdn.gcore.lib.serialization.gson.DuplicateFieldsExclusionStrategy;
import com.guillaumevdn.gcore.lib.serialization.gson.GsonAdapterClassFactory;
import com.guillaumevdn.gcore.lib.serialization.gson.GsonAdapterText;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.libs.com.google.gson.GsonBuilder;

/**
 * @author GuillaumeVDN
 */
public final class FileUtils {

	public static GsonBuilder createGsonBuilder() {
		GsonBuilder builder = new GsonBuilder()
				.addDeserializationExclusionStrategy(new DuplicateFieldsExclusionStrategy())
				.addSerializationExclusionStrategy(new DuplicateFieldsExclusionStrategy())
				.enableComplexMapKeySerialization()
				.disableInnerClassSerialization()
				.serializeSpecialFloatingPointValues()
				.registerTypeAdapterFactory(new GsonAdapterClassFactory())
				.registerTypeAdapter(Text.class, new GsonAdapterText())
				.registerTypeAdapter(ItemStack.class, AdapterItemStack.INSTANCE.getGsonAdapter())
				.registerTypeAdapter(NBTCompound.class, AdapterNBTCompound.INSTANCE.getGsonAdapter())
				.registerTypeAdapter(PotionEffect.class, AdapterPotionEffect.INSTANCE.getGsonAdapter())
				;
		for (Serializer serializer : Serializer.values()) {
			builder.registerTypeAdapter(serializer.getTypeClass(), serializer.getGsonAdapter());
		}
		return builder;
	}

	public static boolean saveDefaultResource(GPlugin plugin, String defaultResource, File target, boolean createIfNoDefault) throws IOException {
		target.getParentFile().mkdirs();
		if (!target.exists() && defaultResource != null) {
			try (InputStream in = plugin.getResource(defaultResource)) {
				if (in == null) {
					throw new IllegalArgumentException("there's no default resource '" + defaultResource + "'");
				}
				try (OutputStream out = new FileOutputStream(target)) {
					byte[] buf = new byte['?'];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
				}
				return true;
			} catch (Throwable exception) {
				throw new IOException("couldn't set default resource to file " + target.getName(), exception);
			}
		}
		if (!target.exists() && createIfNoDefault) {
			try {
				target.createNewFile();
				return true;
			} catch (IOException exception) {
				throw new IOException("couldn't create file " + target.getName(), exception);
			}
		}
		return false;
	}

	public static boolean delete(File file) {
		if (!file.exists()) return true;
		if (file.isDirectory()) {
			for (File sub : file.listFiles()) {
				delete(sub);
			}
		}
		try {
			Files.delete(file.toPath());
			return true;
		} catch (Throwable exception) {
			if (exception instanceof NoSuchFileException) {
				// ??? happens sometimes, alright
				return true;
			}
			GCore.inst().getMainLogger().error("Couldn't delete file " + file + ", attempting to delete on exit", exception);
			file.deleteOnExit();
			return false;
		}
	}

	public static boolean ensureExistence(File file) {
		return file.exists() || reset(file);
	}

	public static boolean reset(File file) {
		if (!delete(file)) return false;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			Files.createFile(file.toPath());
			return true;
		} catch (Throwable exception) {
			if (exception instanceof FileAlreadyExistsException) {
				return true;
			}
			GCore.inst().getMainLogger().error("Couldn't create file " + file, exception);
			return false;
		}
	}

	private static final Set<String> DONT_IGNORE = new HashSet<>();

	public static void copy(File src, File target) throws IOException {
		copy(src, target, DONT_IGNORE);
	}

	public static void copy(File src, File target, Set<String> ignorePathsContaining) throws IOException {
		if (!src.exists()) throw new IOException("source file don't exist");
		if (target.exists()) throw new IOException("target file already exists");
		doCopy(src, target, ignorePathsContaining);
	}

	private static final long FILE_COPY_BUFFER_SIZE = 1024 * 1024 * 30;
	private static void doCopy(File src, File dest, Set<String> ignorePathsContaining) throws IOException {
		String path = src.getPath().toLowerCase();
		if (ignorePathsContaining.stream().anyMatch(ignore -> path.contains(ignore.toLowerCase()))) {
			return;
		}
		if (src.isDirectory()) {
			dest.mkdir();
			for (File sub : src.listFiles()) {
				doCopy(sub, new File(dest + "/" + sub.getName()), ignorePathsContaining);
			}
		} else {
			reset(dest);
			try (FileInputStream fis = new FileInputStream(src);
					FileChannel input = fis.getChannel();
					FileOutputStream fos = new FileOutputStream(dest);
					FileChannel output = fos.getChannel()) {
				final long size = input.size();
				long pos = 0;
				long count = 0;
				while (pos < size) {
					final long remain = size - pos;
					count = remain > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : remain;
					final long bytesCopied = output.transferFrom(input, pos, count);
					if (bytesCopied == 0) { // IO-385 - can happen if file is truncated after caching the size
						break; // ensure we don't loop forever
					}
					pos += bytesCopied;
				}
			}

			final long srcLen = src.length();
			final long dstLen = dest.length();
			if (srcLen != dstLen) {
				throw new IOException("Failed to copy full contents from '" + src + "' to '" + dest + "' Expected length: " + srcLen + " Actual: " + dstLen);
			}
		}
	}

	public static void zip(File file, File target) {
		final Path sourceDir = Paths.get(file.getPath());
		String zipFileName = target.getPath();
		try {
			if (!file.exists()) {
				target.createNewFile();
			}
			ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
			Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
					try {
						Path targetFile = sourceDir.relativize(file);
						outputStream.putNextEntry(new ZipEntry(targetFile.toString()));
						byte[] bytes = Files.readAllBytes(file);
						outputStream.write(bytes, 0, bytes.length);
						outputStream.closeEntry();
					} catch (IOException exception) {
						exception.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
			});
			outputStream.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public static List<String> readLines(File file) throws Throwable {
		return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
		/*List<String> lines = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			for (String line; (line = reader.readLine()) != null;) {
				lines.add(line);
			}
		}
		return lines;*/
	}

	/** @return the file name (without extension if not a directory) */
	public static String getSimpleName(File file) {
		String name = file.getName();
		if (file.isFile()) {
			int index = name.lastIndexOf('.');
			if (index != -1) {
				name = name.substring(0, index);
			}
		}
		return name;
	}

	// ----- https://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
	public static int countLines(File file) {
		try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
			byte[] c = new byte[1024];
			int readChars = is.read(c);
			// bail out if nothing to read
			if (readChars == -1) {
				return 0;
			}
			// make it easy for the optimizer to tune this loop
			int count = 0;
			while (readChars == 1024) {
				for (int i = 0; i < 1024;) {
					if (c[i++] == '\n') {
						++count;
					}
				}
				readChars = is.read(c);
			}
			// count remaining characters
			while (readChars != -1) {
				for (int i=0; i<readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
				readChars = is.read(c);
			}
			// done
			return count == 0 ? 1 : count;
		} catch (Throwable exception) {
			exception.printStackTrace();
			return -1;
		}
	}

}
