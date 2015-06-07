package cz.deznekcz.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;

public class Installer {
	
	private static final String INSTALER_NAME = "instal.jar";

	/**
	 * Install this packed application
	 * compiling is not documented
	 * @param args destination folder
	 */
	public static void main(String[] args) {
		try {
			if (args.length == 0) {
				extract(new File(""));
			} else {
				extract(new File(args[0]));
			}
		} catch (IOException e) {
			error("Writing or reading error:\n"+e.getMessage());
		}
		
	}

	private static void extract(File folder) throws IOException {
		if (folder.exists() && !folder.isDirectory()) {
			error("Name of folder references to a file!");
		} else {
			if (!folder.exists()) folder.mkdirs();
			
			double maximum = jarFilesSize(folder);
			double currentInstaled = 0;
			
			JarFile jar = new JarFile(INSTALER_NAME);
			Enumeration<JarEntry> enumEntries = jar.entries();
			
			String label = "Current file: ";
			
			Loader.start("Instaling", "", null);
			
			while (enumEntries.hasMoreElements()) {
			    JarEntry file = enumEntries.nextElement();
			    String fileName = folder.getAbsolutePath() + File.separator + file.getName();
			    if (fileName.contains("META-INF")) continue;
			    File f = new File(fileName);
			    if (file.isDirectory()) { // if its a directory, create it
			        f.mkdir();
			        continue;
			    }
			
			    InputStream is = jar.getInputStream(file); // get the input stream
			    double filemax = (double) is.available();
			    Loader.newSubLoading(label + f.getName());
			    Loader.subUpdate(0);
			    
			    FileOutputStream fos = new FileOutputStream(f);
			    while (is.available() > 0) {  // write contents of 'is' to 'fos'
			        fos.write(is.read());
			        Loader.update(
			        		(currentInstaled + filemax - is.available()) / maximum);
			        Loader.subUpdate(1-is.available()/filemax);
			    }
			    
			    currentInstaled += filemax;
			    
			    fos.close();
			    is.close();
			}
			jar.close();
		}
		Loader.newLoading("Complete", "");
		Loader.abort();
		System.exit(0);
	}

	private static double jarFilesSize(File folder) throws IOException {
		double size = 0;
		JarFile jar = new JarFile(INSTALER_NAME);
		Enumeration<JarEntry> enumEntries = jar.entries();
		
		while (enumEntries.hasMoreElements()) {
		    JarEntry file = enumEntries.nextElement();
		    if (file.isDirectory()) continue;
		
		    InputStream is = jar.getInputStream(file); // get the input stream
		    size += is.available();
		    is.close();
		}
		jar.close();
		return size;
	}

	private static void error(String string) {
		JOptionPane.showMessageDialog(null, string, "Instalation error", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}
}
