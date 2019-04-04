/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.guillaumevdn.gcore.libs.org.apache.commons.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileUtils
{
	public static FileInputStream openInputStream(final File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (file.canRead() == false) {
				throw new IOException("File '" + file + "' cannot be read");
			}
		} else {
			throw new FileNotFoundException("File '" + file + "' does not exist");
		}
		return new FileInputStream(file);
	}

	public static LineIterator lineIterator(final File file, final String encoding) throws IOException
	{
		InputStream in = null;
		try {
			in = openInputStream(file);
			return IOUtils.lineIterator(in, encoding);
		} catch (final IOException | RuntimeException ex) {
			try {
				if (in != null) {
					in.close();
				}
			}
			catch (final IOException e) {
				ex.addSuppressed(e);
			}
			throw ex;
		}
	}

	/**
	 * Copies a whole directory to a new location preserving the file dates.
	 * <p>
	 * This method copies the specified directory and all its child
	 * directories and files to the specified destination.
	 * The destination is the new location and name of the directory.
	 * <p>
	 * The destination directory is created if it does not exist.
	 * If the destination directory did exist, then this method merges
	 * the source with the destination, with the source taking precedence.
	 * <p>
	 * <strong>Note:</strong> This method tries to preserve the files' last
	 * modified date/times using {@link File#setLastModified(long)}, however
	 * it is not guaranteed that those operations will succeed.
	 * If the modification operation fails, no indication is provided.
	 *
	 * @param srcDir  an existing directory to copy, must not be {@code null}
	 * @param destDir the new directory, must not be {@code null}
	 *
	 * @throws NullPointerException if source or destination is {@code null}
	 * @throws IOException          if source or destination is invalid
	 * @throws IOException          if an IO error occurs during copying
	 * @since 1.1
	 */
	public static void copyDirectory(final File srcDir, final File destDir) throws IOException {
		copyDirectory(srcDir, destDir, true);
	}

	/**
	 * Copies a whole directory to a new location.
	 * <p>
	 * This method copies the contents of the specified source directory
	 * to within the specified destination directory.
	 * <p>
	 * The destination directory is created if it does not exist.
	 * If the destination directory did exist, then this method merges
	 * the source with the destination, with the source taking precedence.
	 * <p>
	 * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
	 * {@code true} tries to preserve the files' last modified
	 * date/times using {@link File#setLastModified(long)}, however it is
	 * not guaranteed that those operations will succeed.
	 * If the modification operation fails, no indication is provided.
	 *
	 * @param srcDir           an existing directory to copy, must not be {@code null}
	 * @param destDir          the new directory, must not be {@code null}
	 * @param preserveFileDate true if the file date of the copy
	 *                         should be the same as the original
	 *
	 * @throws NullPointerException if source or destination is {@code null}
	 * @throws IOException          if source or destination is invalid
	 * @throws IOException          if an IO error occurs during copying
	 * @since 1.1
	 */
	public static void copyDirectory(final File srcDir, final File destDir,
			final boolean preserveFileDate) throws IOException {
		copyDirectory(srcDir, destDir, null, preserveFileDate);
	}

	/**
	 * Copies a filtered directory to a new location preserving the file dates.
	 * <p>
	 * This method copies the contents of the specified source directory
	 * to within the specified destination directory.
	 * <p>
	 * The destination directory is created if it does not exist.
	 * If the destination directory did exist, then this method merges
	 * the source with the destination, with the source taking precedence.
	 * <p>
	 * <strong>Note:</strong> This method tries to preserve the files' last
	 * modified date/times using {@link File#setLastModified(long)}, however
	 * it is not guaranteed that those operations will succeed.
	 * If the modification operation fails, no indication is provided.
	 * </p>
	 * <h3>Example: Copy directories only</h3>
	 * <pre>
	 *  // only copy the directory structure
	 *  FileUtils.copyDirectory(srcDir, destDir, DirectoryFileFilter.DIRECTORY);
	 *  </pre>
	 *
	 * <h3>Example: Copy directories and txt files</h3>
	 * <pre>
	 *  // Create a filter for ".txt" files
	 *  IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".txt");
	 *  IOFileFilter txtFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, txtSuffixFilter);
	 *
	 *  // Create a filter for either directories or ".txt" files
	 *  FileFilter filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, txtFiles);
	 *
	 *  // Copy using the filter
	 *  FileUtils.copyDirectory(srcDir, destDir, filter);
	 *  </pre>
	 *
	 * @param srcDir  an existing directory to copy, must not be {@code null}
	 * @param destDir the new directory, must not be {@code null}
	 * @param filter  the filter to apply, null means copy all directories and files
	 *                should be the same as the original
	 *
	 * @throws NullPointerException if source or destination is {@code null}
	 * @throws IOException          if source or destination is invalid
	 * @throws IOException          if an IO error occurs during copying
	 * @since 1.4
	 */
	public static void copyDirectory(final File srcDir, final File destDir,
			final FileFilter filter) throws IOException {
		copyDirectory(srcDir, destDir, filter, true);
	}

	/**
	 * Copies a filtered directory to a new location.
	 * <p>
	 * This method copies the contents of the specified source directory
	 * to within the specified destination directory.
	 * <p>
	 * The destination directory is created if it does not exist.
	 * If the destination directory did exist, then this method merges
	 * the source with the destination, with the source taking precedence.
	 * <p>
	 * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
	 * {@code true} tries to preserve the files' last modified
	 * date/times using {@link File#setLastModified(long)}, however it is
	 * not guaranteed that those operations will succeed.
	 * If the modification operation fails, no indication is provided.
	 * </p>
	 * <h3>Example: Copy directories only</h3>
	 * <pre>
	 *  // only copy the directory structure
	 *  FileUtils.copyDirectory(srcDir, destDir, DirectoryFileFilter.DIRECTORY, false);
	 *  </pre>
	 *
	 * <h3>Example: Copy directories and txt files</h3>
	 * <pre>
	 *  // Create a filter for ".txt" files
	 *  IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".txt");
	 *  IOFileFilter txtFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, txtSuffixFilter);
	 *
	 *  // Create a filter for either directories or ".txt" files
	 *  FileFilter filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, txtFiles);
	 *
	 *  // Copy using the filter
	 *  FileUtils.copyDirectory(srcDir, destDir, filter, false);
	 *  </pre>
	 *
	 * @param srcDir           an existing directory to copy, must not be {@code null}
	 * @param destDir          the new directory, must not be {@code null}
	 * @param filter           the filter to apply, null means copy all directories and files
	 * @param preserveFileDate true if the file date of the copy
	 *                         should be the same as the original
	 *
	 * @throws NullPointerException if source or destination is {@code null}
	 * @throws IOException          if source or destination is invalid
	 * @throws IOException          if an IO error occurs during copying
	 * @since 1.4
	 */
	public static void copyDirectory(final File srcDir, final File destDir,
			final FileFilter filter, final boolean preserveFileDate) throws IOException {
		checkFileRequirements(srcDir, destDir);
		if (!srcDir.isDirectory()) {
			throw new IOException("Source '" + srcDir + "' exists but is not a directory");
		}
		if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
			throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
		}

		// Cater for destination being directory within the source directory (see IO-141)
		List<String> exclusionList = null;
		if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
			final File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
			if (srcFiles != null && srcFiles.length > 0) {
				exclusionList = new ArrayList<>(srcFiles.length);
				for (final File srcFile : srcFiles) {
					final File copiedFile = new File(destDir, srcFile.getName());
					exclusionList.add(copiedFile.getCanonicalPath());
				}
			}
		}
		doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
	}

	/**
	 * checks requirements for file copy
	 * @param src the source file
	 * @param dest the destination
	 * @throws FileNotFoundException if the destination does not exist
	 */
	private static void checkFileRequirements(final File src, final File dest) throws FileNotFoundException {
		if (src == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (dest == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (!src.exists()) {
			throw new FileNotFoundException("Source '" + src + "' does not exist");
		}
	}

	/**
	 * Internal copy directory method.
	 *
	 * @param srcDir           the validated source directory, must not be {@code null}
	 * @param destDir          the validated destination directory, must not be {@code null}
	 * @param filter           the filter to apply, null means copy all directories and files
	 * @param preserveFileDate whether to preserve the file date
	 * @param exclusionList    List of files and directories to exclude from the copy, may be null
	 * @throws IOException if an error occurs
	 * @since 1.1
	 */
	private static void doCopyDirectory(final File srcDir, final File destDir, final FileFilter filter,
			final boolean preserveFileDate, final List<String> exclusionList)
					throws IOException {
		// recurse
		final File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
		if (srcFiles == null) {  // null if abstract pathname does not denote a directory, or if an I/O error occurs
			throw new IOException("Failed to list contents of " + srcDir);
		}
		if (destDir.exists()) {
			if (destDir.isDirectory() == false) {
				throw new IOException("Destination '" + destDir + "' exists but is not a directory");
			}
		} else {
			if (!destDir.mkdirs() && !destDir.isDirectory()) {
				throw new IOException("Destination '" + destDir + "' directory cannot be created");
			}
		}
		if (destDir.canWrite() == false) {
			throw new IOException("Destination '" + destDir + "' cannot be written to");
		}
		for (final File srcFile : srcFiles) {
			final File dstFile = new File(destDir, srcFile.getName());
			if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
				if (srcFile.isDirectory()) {
					doCopyDirectory(srcFile, dstFile, filter, preserveFileDate, exclusionList);
				} else {
					doCopyFile(srcFile, dstFile, preserveFileDate);
				}
			}
		}

		// Do this last, as the above has probably affected directory metadata
		if (preserveFileDate) {
			destDir.setLastModified(srcDir.lastModified());
		}
	}

	/**
	 * Internal copy file method.
	 * This caches the original file length, and throws an IOException
	 * if the output file length is different from the current input file length.
	 * So it may fail if the file changes size.
	 * It may also fail with "IllegalArgumentException: Negative size" if the input file is truncated part way
	 * through copying the data and the new file size is less than the current position.
	 *
	 * @param srcFile          the validated source file, must not be {@code null}
	 * @param destFile         the validated destination file, must not be {@code null}
	 * @param preserveFileDate whether to preserve the file date
	 * @throws IOException              if an error occurs
	 * @throws IOException              if the output file length is not the same as the input file length after the
	 * copy completes
	 * @throws IllegalArgumentException "Negative size" if the file is truncated so that the size is less than the
	 * position
	 */
	private static void doCopyFile(final File srcFile, final File destFile, final boolean preserveFileDate)
			throws IOException {
		if (destFile.exists() && destFile.isDirectory()) {
			throw new IOException("Destination '" + destFile + "' exists but is a directory");
		}

		try (FileInputStream fis = new FileInputStream(srcFile);
				FileChannel input = fis.getChannel();
				FileOutputStream fos = new FileOutputStream(destFile);
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

		final long srcLen = srcFile.length();
		final long dstLen = destFile.length();
		if (srcLen != dstLen) {
			throw new IOException("Failed to copy full contents from '" +
					srcFile + "' to '" + destFile + "' Expected length: " + srcLen + " Actual: " + dstLen);
		}
		if (preserveFileDate) {
			destFile.setLastModified(srcFile.lastModified());
		}
	}

	/**
	 * The number of bytes in a kilobyte.
	 */
	public static final long ONE_KB = 1024;

	/**
	 * The number of bytes in a megabyte.
	 */
	public static final long ONE_MB = ONE_KB * ONE_KB;

	/**
	 * The file copy buffer size (30 MB)
	 */
	private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;

	/**
	 * Copies a file to a new location preserving the file date.
	 * <p>
	 * This method copies the contents of the specified source file to the
	 * specified destination file. The directory holding the destination file is
	 * created if it does not exist. If the destination file exists, then this
	 * method will overwrite it.
	 * <p>
	 * <strong>Note:</strong> This method tries to preserve the file's last
	 * modified date/times using {@link File#setLastModified(long)}, however
	 * it is not guaranteed that the operation will succeed.
	 * If the modification operation fails, no indication is provided.
	 *
	 * @param srcFile  an existing file to copy, must not be {@code null}
	 * @param destFile the new file, must not be {@code null}
	 *
	 * @throws NullPointerException if source or destination is {@code null}
	 * @throws IOException          if source or destination is invalid
	 * @throws IOException          if an IO error occurs during copying
	 * @throws IOException          if the output file length is not the same as the input file length after the copy
	 * completes
	 * @see #copyFileToDirectory(File, File)
	 * @see #copyFile(File, File, boolean)
	 */
	public static void copyFile(final File srcFile, final File destFile) throws IOException {
		copyFile(srcFile, destFile, true);
	}

	/**
	 * Copies a file to a new location.
	 * <p>
	 * This method copies the contents of the specified source file
	 * to the specified destination file.
	 * The directory holding the destination file is created if it does not exist.
	 * If the destination file exists, then this method will overwrite it.
	 * <p>
	 * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
	 * {@code true} tries to preserve the file's last modified
	 * date/times using {@link File#setLastModified(long)}, however it is
	 * not guaranteed that the operation will succeed.
	 * If the modification operation fails, no indication is provided.
	 *
	 * @param srcFile          an existing file to copy, must not be {@code null}
	 * @param destFile         the new file, must not be {@code null}
	 * @param preserveFileDate true if the file date of the copy
	 *                         should be the same as the original
	 *
	 * @throws NullPointerException if source or destination is {@code null}
	 * @throws IOException          if source or destination is invalid
	 * @throws IOException          if an IO error occurs during copying
	 * @throws IOException          if the output file length is not the same as the input file length after the copy
	 * completes
	 * @see #copyFileToDirectory(File, File, boolean)
	 * @see #doCopyFile(File, File, boolean)
	 */
	public static void copyFile(final File srcFile, final File destFile,
			final boolean preserveFileDate) throws IOException {
		checkFileRequirements(srcFile, destFile);
		if (srcFile.isDirectory()) {
			throw new IOException("Source '" + srcFile + "' exists but is a directory");
		}
		if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
			throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
		}
		final File parentFile = destFile.getParentFile();
		if (parentFile != null) {
			if (!parentFile.mkdirs() && !parentFile.isDirectory()) {
				throw new IOException("Destination '" + parentFile + "' directory cannot be created");
			}
		}
		if (destFile.exists() && destFile.canWrite() == false) {
			throw new IOException("Destination '" + destFile + "' exists but is read-only");
		}
		doCopyFile(srcFile, destFile, preserveFileDate);
	}

}