package net.praqma.util.debug;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

public class Logger {

	private static final int levelMaxlength = 8;
	

	private static final String filesep = System.getProperty( "file.separator" );
	private static final String linesep = System.getProperty( "line.separator" );

	private static Logger instance = null;
	
	private boolean logAll = true;
	private boolean enabled = true;
	private boolean toStdOut = false;
	private boolean append = true;
	
	private Date current;
	
	private LogLevel minLevel = LogLevel.DEBUG;
	
	private Set<String> included = new LinkedHashSet<String>();
	
	private static String filename = "logger_";
	
	private File loggerPath;
	private File loggerFile;

	private SimpleDateFormat logformat  = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
	private SimpleDateFormat fileformat = new SimpleDateFormat( "yyyyMMdd" );


	private FileWriter fw;
	private PrintWriter out;
	
	public enum LogLevel {
		DEBUG,
		INFO,
		WARNING,
		ERROR,
		FATAL
	}
	
	private Logger() {
		
	}
	
	public static Logger getLogger() {
		if( instance == null ) {
			instance = new Logger();
		}
		
		return instance;
	}
	
	private boolean initialize() {
		/* CWD */
		if( loggerPath == null ) {
			loggerPath = new File( System.getProperty("user.dir") );
		}
		
		String format = instance.fileformat.format( new Date() );
		loggerFile = new File( loggerPath, filename + format + ".log" );
		
		/* Existence + creation */
		if( !loggerPath.exists() ) {
			boolean created = false;
			try {
				created = loggerFile.mkdirs();
			} catch (Exception e) {
				created = false;
			}

			if( !created ) {
				return false;
			}
		}
		
		if( fw != null ) {
			try {
				fw.close();
				out.close();
			} catch (IOException e) {
				System.err.println( "Could not close file writer and/or buffered writer." );
			}
		}

		try {
			fw = new FileWriter( loggerFile, append );

		} catch (IOException e) {
			System.err.println( "Could not create logger. Quitting!" );
			System.exit( 1 );
		}

		out = new PrintWriter( fw );
		
		return true;
	}

	public <T> void subscribe( Class<T> tclass ) {
		if( !included.contains( tclass.getCanonicalName() ) ) {
			included.add( tclass.getCanonicalName() );
			logAll = false;
		}
	}
	
	public void subscribeAll() {
		logAll = true;
	}
	
	public void toStdOut( boolean tf ) {
		toStdOut = tf;
	}
	
	public static void setFilename( String filename1 ) {
		filename = filename1;
	}
	
	public void setMinLogLevel( LogLevel min ) {
		this.minLevel = min;
	}
	
	
	public String objectToString( Object t ) {
		if( t instanceof Throwable ) {
			Writer sw = new StringWriter();
			PrintWriter pw = new PrintWriter( sw );
			( (Throwable) t ).printStackTrace( pw );

			return sw.toString();
		} else {
			return String.valueOf( t );
		}
	}
	
	public void log( Object message ) {
		log( message, LogLevel.INFO, 3 );
	}
	
	public void debug( Object message ) {
		log( message, LogLevel.DEBUG, 3 );
	}
	
	public void info( Object message ) {
		log( message, LogLevel.INFO, 3 );
	}
	
	public void warning( Object message ) {
		log( message, LogLevel.WARNING, 3 );
	}
	
	public void error( Object message ) {
		log( message, LogLevel.ERROR, 3 );
	}
	
	public void fatal( Object message ) {
		log( message, LogLevel.FATAL, 3 );
	}
	
	public void log( Object message, LogLevel level ) {
		log( message, level, 3 );
	}
	
	private void log( Object message, LogLevel level, int depth ) {
		if(!enabled || minLevel.ordinal() > level.ordinal()) {
			return;
		}
		
		Date now = new Date();
		
		String logMsg = "";
		
		if( current == null ) {
			instance.initialize();
		}
		
		if( level != null ) {

			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			String name = stack[depth].getClassName();
			
			String stackMsg = stack[depth].getClassName() + "::" + stack[depth].getMethodName() + "," + stack[depth].getLineNumber();
			String msg = logformat.format( now ) + " [" + level + "] " + new String( new char[Logger.levelMaxlength - level.toString().length()] ).replace( "\0", " " ) + stackMsg;
			logMsg = msg + ": " + objectToString( message ) + linesep;
		} else {
			logMsg = objectToString( message ) + linesep;
		}

		/* To sdt out? */
		if( toStdOut ) {
			System.out.println( "[" + level + "] " + new String( new char[Logger.levelMaxlength - level.toString().length()] ).replace( "\0", " " ) + message );
		} 
		{
			/* Writing */
			out.write( logMsg );
			out.flush();
		}

	}
}