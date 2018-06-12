package br.unicamp.MECA_Demo;

///This is how to use it
//import SoarBridge.NativeUtils;
// 
//public class HelloJNI {	 
//  static {	 
//    try {	 
//      NativeUtils.loadLibraryFromJar("/resources/libHelloJNI.so");	 
//    } catch (IOException e) {	 
//      e.printStackTrace(); // This is probably not the best way to handle exception :-)	 
//    }	 
//  }	 
// 
//  public native void hello();	 
//}


 
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
 
/**
 * Simple library class for working with JNI (Java Native Interface)
 * 
 * @see http://frommyplayground.com/how-to-load-native-jni-library-from-jar
 *
 * @author Adam Heirnich <adam@adamh.cz>, http://www.adamh.cz
 */
public class NativeUtils {
 
    /**
     * Private constructor - this class will never be instanced
     */
    private NativeUtils() {
    }
 
    public static void setLibraryPath(String path) {
        Field fieldSysPath=null;
        System.setProperty( "java.library.path", path );
        try {
        fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
        } catch (Exception e) {
            System.out.println("Exceção: "+e);
        }
        fieldSysPath.setAccessible( true );
        try {
        fieldSysPath.set( null, null );
        } catch (Exception e) {
            System.out.println("Exceção: "+e);
        }
    }
    
    public static void loadFileFromJar(String path) throws IOException {
 
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("The path to be absolute (start with '/').");
        }
 
        // Obtain filename from path
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;
 
        // Split filename to prexif and suffix (extension)
        String prefix = "";
        String suffix = null;
        if (filename != null) {
            parts = filename.split("\\.", 2);
            prefix = parts[0];
            suffix = (parts.length > 1) ? "."+parts[parts.length - 1] : null; // Thanks, davs! :-)
        }
 
        // Check if the filename is okay
        if (filename == null || prefix.length() < 3) {
            throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
        }
 
        System.out.println("Loading file from JAR: "+prefix+suffix);
        // Prepare temporary file
        //File temp = File.createTempFile(prefix, suffix);
        File temp = new File(prefix+suffix);
        temp.deleteOnExit();
 
//        if (!temp.exists()) {
//            throw new FileNotFoundException("File " + temp.getAbsolutePath() + " does not exist.");
//        }
 
        // Prepare buffer for data copying
        byte[] buffer = new byte[1024];
        int readBytes;
 
        // Open and check input stream
        InputStream is = NativeUtils.class.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        }
 
        // Open output stream and copy data between source file in JAR and the temporary file
        OutputStream os = new FileOutputStream(temp);
        try {
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } finally {
            // If read/write fails, close streams safely before throwing an exception
            os.close();
            is.close();
        }
    }
    
    /**
     * Loads library from current JAR archive
     * 
     * The file from JAR is copied into system temporary directory and then loaded. The temporary file is deleted after exiting.
     * Method uses String as filename because the pathname is "abstract", not system-dependent.
     * 
     * @param filename The filename inside JAR as absolute path (beginning with '/'), e.g. /package/File.ext
     * @throws IOException If temporary file creation or read/write operation fails
     * @throws IllegalArgumentException If source file (param path) does not exist
     * @throws IllegalArgumentException If the path is not absolute or if the filename is shorter than three characters (restriction of {@see File#createTempFile(java.lang.String, java.lang.String)}).
     */
    public static void loadLibraryFromJar(String path) throws IOException {
 
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("The path to be absolute (start with '/').");
        }
 
        // Obtain filename from path
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;
 
        // Split filename to prexif and suffix (extension)
        String prefix = "";
        String suffix = null;
        if (filename != null) {
            parts = filename.split("\\.", 2);
            prefix = parts[0];
            suffix = (parts.length > 1) ? "."+parts[parts.length - 1] : null; // Thanks, davs! :-)
        }
 
        // Check if the filename is okay
        if (filename == null || prefix.length() < 3) {
            throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
        }
 
        System.out.println("Loading Library: "+prefix+suffix);
        // Prepare temporary file
        //File temp = File.createTempFile(prefix, suffix);
        File temp = new File(prefix+suffix);
        temp.deleteOnExit();
 
//        if (!temp.exists()) {
//            throw new FileNotFoundException("File " + temp.getAbsolutePath() + " does not exist.");
//        }
 
        // Prepare buffer for data copying
        byte[] buffer = new byte[1024];
        int readBytes;
 
        // Open and check input stream
        InputStream is = NativeUtils.class.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        }
 
        // Open output stream and copy data between source file in JAR and the temporary file
        OutputStream os = new FileOutputStream(temp);
        try {
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } finally {
            // If read/write fails, close streams safely before throwing an exception
            os.close();
            is.close();
        }
 
        // Finally, load the library
        System.load(temp.getAbsolutePath());
        
//        // Starts contributed code from Lofi to deal with files on the temporary folder in Windows
//        final String libraryPrefix = prefix;
//        final String lockSuffix = ".lock";
// 
//        // create lock file
//        final File lock = new File( temp.getAbsolutePath() + lockSuffix);
//        lock.createNewFile();
//        lock.deleteOnExit();
// 
//        // file filter for library file (without .lock files)
//        FileFilter tmpDirFilter =
//          new FileFilter()
//          {
//            public boolean accept(File pathname)
//            {
//              return pathname.getName().startsWith( libraryPrefix) && pathname.getName().endsWith( lockSuffix);
//            }
//          };
// 
//        // get all library files from temp folder  
//        String tmpDirName = System.getProperty("java.io.tmpdir");
//        File tmpDir = new File(tmpDirName);
//        File[] tmpFiles = tmpDir.listFiles(tmpDirFilter);
// 
//        // delete all files which don't have n accompanying lock file
//        for (int i = 0; i < tmpFiles.length; i++)
//        {
//          // Create a file to represent the lock and test.
//          File lockFile = new File( tmpFiles[i].getAbsolutePath() + lockSuffix);
//          if (!lockFile.exists())
//          {
//            System.out.println( "deleting: " + tmpFiles[i].getAbsolutePath());
//            tmpFiles[i].delete();
//          }
//        }
    }
}