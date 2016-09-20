package cz.deznekcz.tool.loader;

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
	
	private static final String INSTALER_NAME = Messages.getString("Installer.0"); //$NON-NLS-1$

	/**
	 * Install this packed application
	 * compiling is not documented
	 * @param args destination folder
	 */
	public static void main(String[] args) {
		try {
			if (args.length == 0) {
				extract(new File("")); //$NON-NLS-1$
			} else {
				extract(new File(args[0]));
			}
		} catch (IOException e) {
			error(Messages.getString("Installer.2")+e.getMessage()); //$NON-NLS-1$
		}
		
	}

	private static void extract(File folder) throws IOException {
		if (folder.exists() && !folder.isDirectory()) {
			error(Messages.getString("Installer.3")); //$NON-NLS-1$
		} else {
			if (!folder.exists()) folder.mkdirs();
			
			double maximum = jarFilesSize(folder);
			double currentInstaled = 0;
			
			JarFile jar = new JarFile(INSTALER_NAME);
			Enumeration<JarEntry> enumEntries = jar.entries();
			
			String label = Messages.getString("Installer.4"); //$NON-NLS-1$
			
			Loader.start(Messages.getString("Installer.5"), "", null); //$NON-NLS-1$ //$NON-NLS-2$
			
			while (enumEntries.hasMoreElements()) {
			    JarEntry file = enumEntries.nextElement();
			    String fileName = folder.getAbsolutePath() + File.separator + file.getName();
			    if (fileName.contains(Messages.getString("Installer.7"))) continue; //$NON-NLS-1$
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
		Loader.newLoading(Messages.getString("Installer.8"), ""); //$NON-NLS-1$ //$NON-NLS-2$
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
		JOptionPane.showMessageDialog(null, string, Messages.getString("Installer.10"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
		System.exit(1);
	}
}
