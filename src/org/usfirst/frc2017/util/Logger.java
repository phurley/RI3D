package org.usfirst.frc2017.util;

import java.io.File;

public class Logger {
    private LogWriter writer;

    public static final int TRACE = 0;
    public static final int DEBUG = 10;
    public static final int INFO = 20;
    public static final int WARN = 30;
    public static final int ERROR = 40;

    private static int level = DEBUG;
    private static Logger logger;

    private static Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    private Logger() {
        File file = logFileName();
        writer = new LogWriter(file.getAbsolutePath(), 500);            
    }

    public static LogWriter getWriter() {
        return getLogger().writer;
    }
    
    private File logFileName() {
        File base = null;

        // find the mount point
        char mount = 'u';
        while (base == null && mount <= 'z') {
            File f = new File("/" + mount);
            if (f.isDirectory()) {
                base = f;
            }
            ++mount;
        }

        if (base == null) {
            base = new File("/home/lvuser");
        }

        base = new File(base, "log");
        base.mkdirs();

        int counter = 0;
        File result = new File(base, String.format("robot-%05d.log", counter));
        while (result.exists()) {
            result = new File(base, String.format("robot-%05d.log", ++counter));
        }

        return result;
    }

    private void logString(String s) {
        writer.logString(s);
    }
    
    private void logString(String format, Object... args) {
        logString(String.format(format, args));
    }

    public static void log(String s) {
        getLogger().logString(s);
    }
    
    public static void setLevel(int l) {
        level = l;
    }

    public static void trace(String s) {
        if (level <= TRACE)
            getLogger().logString(s);
    }

    public static void trace(String format, Object... args) {
        if (level <= TRACE)
            getLogger().logString(format, args);
    }

    public static void debug(String s) {
        if (level <= DEBUG)
            getLogger().logString(s);
    }

    public static void debug(String format, Object... args) {
        if (level <= DEBUG)
            getLogger().logString(format, args);
    }

    public static void info(String s) {
        if (level <= INFO)
            getLogger().logString(s);
    }

    public static void info(String format, Object... args) {
        if (level <= INFO)
            getLogger().logString(format, args);
    }

    public static void warn(String s) {
        if (level <= WARN) {
            System.err.println(s);
            getLogger().logString(s);
        }
    }

    public static void warn(String s, Object... args) {
        if (level <= WARN) {
            String msg = String.format(s, args);
            System.err.println(msg);
            getLogger().logString(msg);
        }
    }

    public static void error(String s) {
        System.err.println(s);
        if (level <= ERROR) getLogger().logString(s);
    }

    public static void error(String s, Object... args) {
        String msg = String.format(s, args);
        System.err.println(msg);
        if (level <= ERROR) getLogger().logString(msg);
    }

    public static void flush() {
        getLogger().writer.flush();
    }
}
