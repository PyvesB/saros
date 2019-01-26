package de.fu_berlin.inf.dpp.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Adler32;
import org.apache.commons.io.IOUtils;

/**
 * Utility class offering static methods to perform file and folder manipulation. If not stated
 * otherwise the operations performed by this class should not be treated as atomic operations
 * regarding the modification that they perform on the underlying file system!
 */
public class FileSystem {

  private static final int BUFFER_SIZE = 32 * 1024;

  private FileSystem() {
    // NOP
  }

  /**
   * Calculate Adler32 checksum for given file.
   *
   * @return checksum of file
   * @throws IOException if an I/O error occurred
   */
  public static long checksum(IFile file) throws IOException {

    InputStream in;

    try {
      in = file.getContents();
    } catch (IOException e) {
      throw new IOException("failed to calculate checksum", e);
    }

    byte[] buffer = new byte[BUFFER_SIZE];

    Adler32 adler = new Adler32();

    int read;

    try {
      while ((read = in.read(buffer)) != -1) adler.update(buffer, 0, read);
    } finally {
      IOUtils.closeQuietly(in);
    }

    return adler.getValue();
  }

  /**
   * Creates the folder for the given file, including any necessary but nonexistent parent folders.
   * Note that if this operation fails it may have succeeded in creating some of the necessary
   * parent folders.
   *
   * @param file
   * @throws IOException if an I/O error occurred
   */
  public static void createFolder(final IFile file) throws IOException {
    createFolders(file);
  }

  /**
   * Creates the given folder, including any necessary but nonexistent parent folders. Note that if
   * this operation fails it may have succeeded in creating some of the necessary parent folder.
   *
   * @param folder the folder to create
   * @throws IOException if an I/O error occurred
   */
  public static void createFolder(final IFolder_V2 folder) throws IOException {
    createFolders(folder);
  }

  private static void createFolders(final IResource resource) throws IOException {

    if (!(resource.getType() == IResource.FILE || resource.getType() == IResource.FOLDER)) return;

    final List<IFolder_V2> parents = new ArrayList<IFolder_V2>();

    if (resource.getType() == IResource.FOLDER) parents.add((IFolder_V2) resource);

    IContainer parent = resource.getParent();

    while (parent != null && parent.getType() == IResource.FOLDER) {

      if (parent.exists()) break;

      parents.add((IFolder_V2) parent);
      parent = parent.getParent();
    }

    Collections.reverse(parents);

    for (final IFolder_V2 folder : parents) folder.create(false, true);
  }
}
