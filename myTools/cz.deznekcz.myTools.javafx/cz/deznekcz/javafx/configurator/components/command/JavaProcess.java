package cz.deznekcz.javafx.configurator.components.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cz.deznekcz.util.Utils;

public class JavaProcess extends Process implements Runnable {

	public final PrintStream out;
	public final PrintStream err;
	public final InputStream in;

	private final InputStream outRedirect;
	private final InputStream errRedirect;
	private final OutputStream inRedirect;

	private Thread thread;
	private Method method;
	private String cmd;
	private String args;
	private String dir;

	private int exitValue;
	private File rootDirectory;
	private boolean complete;
	private Map<String, String> enviroment;


	public JavaProcess(CommandInstance commandInstance) throws FileNotFoundException {
		out = new PrintStream(commandInstance.getStandartOutputFile());
		err = new PrintStream(commandInstance.getStandartErrorFile());
		in  = new FileInputStream(commandInstance.getStandartInputFile());

		outRedirect = new FileInputStream(commandInstance.getStandartOutputFile());
		errRedirect = new FileInputStream(commandInstance.getStandartOutputFile());
		inRedirect  = new PrintStream(commandInstance.getStandartOutputFile());

		method = commandInstance.getMethod();
		cmd    = commandInstance.getCmd();
		args   = commandInstance.getArgs();
		dir    = commandInstance.getDir();

		enviroment = new HashMap<>();
		System.getenv().forEach(enviroment::put);
		commandInstance.getEnviroment().forEach(enviroment::put);

		if (dir == null || dir.length() == 0) {
			setRootDirectory(new File("."));
		} else {
			setRootDirectory(new File(dir));
			if (!getRootDirectory().exists())
				throw new FileNotFoundException(dir);
		}

		thread = new Thread(this, "JavaFunction:"+cmd+" "+args);
		thread.start();
	}

	@Override
	public int waitFor() throws InterruptedException {
		thread.join();
		return exitValue;
	}

	@Override
	public OutputStream getOutputStream() {
		return inRedirect;
	}

	@Override
	public InputStream getInputStream() {
		return outRedirect;
	}

	@Override
	public InputStream getErrorStream() {
		return errRedirect;
	}

	@Override
	public synchronized int exitValue() {
		if (!complete)
			throw new IllegalThreadStateException();
		else
			return exitValue;
	}

	@Override
	public void destroy() {
		exitValue = 3;
		thread.interrupt();

		synchronized (this) {
			complete = true;
		}
	}

	public File getRootDirectory() {
		return rootDirectory;
	}

	public void setRootDirectory(File rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	public Map<String, String> getEnviroment() {
		return enviroment;
	}

	@Override
	public void run() {
		try {
			exitValue = (int) method.invoke(
					null, // static
					this, // JavaProcess instance
					Utils.subArray(
							1, // removes first element (empty string)
							CommandInstance.splitArguments("", args), // arguments from combined string
							String[]::new // array constructor
					)
			);
			synchronized (this) {
				complete = true;
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			if (e.getCause() instanceof InterruptedException) {
				// do nothing
			} else {
				e.printStackTrace(err);
				synchronized (this) {
					exitValue = 2;
					complete = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace(err);
			synchronized (this) {
				exitValue = 1;
				complete = true;
			}
		}
	}

}
