/*
 *  MicroEmulator
 *  Copyright (C) 2001-2007 MicroEmulator Team.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  @version $Id$
 */
package org.microemu.log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.microemu.app.ui.Message;

/**
 * 
 * This class is used as abstraction layer for log messages 
 * Minimum Log4j implemenation with multiple overloaded functions
 * 
 * @author vlads
 *
 */
public class Logger {
	
    private static final String FQCN = Logger.class.getName();

    private static final Set fqcnSet = new HashSet();
    
    private static boolean java13 = false;
    
    private static List loggerAppenders = new Vector();
    
    static {
        fqcnSet.add(FQCN);
        // This class can be moved to different sub project
        //fqcnSet.add("org.microemu.app.ui.Message");
        fqcnSet.add(Message.class.getName());
        
        addAppender(new StdOutAppender());
    }

	public static boolean isDebugEnabled() {
		return true;
	}

	public static boolean isErrorEnabled() {
		return true;
	}
	
    private static StackTraceElement getLocation() {
    	if (java13) {
    		return null;
    	}
    	try {
			StackTraceElement[] ste = new Throwable().getStackTrace();
			for (int i = 0; i < ste.length - 1; i++) {
			    if (fqcnSet.contains(ste[i].getClassName()) && !fqcnSet.contains(ste[i + 1].getClassName())) {
			        return ste[i + 1];
			    }
			}
		} catch (Throwable e) {
			java13 = true;
		}
        return null;
    }
    
    private static void write(int level, String message, Throwable throwable) {
    	callAppenders(new LoggingEvent(level, message, getLocation(), throwable));
    }
    
    private static void write(int level, String message, Throwable throwable, Object data) {
    	callAppenders(new LoggingEvent(level, message, getLocation(), throwable, data));
    }
    
	public static void debug(String message) {
		if (isDebugEnabled()) {
			write(LoggingEvent.DEBUG, message, null);
		}
	}
	
	public static void debug(String message, Throwable t) {
		if (isDebugEnabled()) {
			write(LoggingEvent.DEBUG, message, t);
		}
	}
	
	public static void debug(Throwable t) {
		if (isDebugEnabled()) {
			write(LoggingEvent.DEBUG, "error", t);
		}
	}
	
	public static void debug(String message, String v) {
		if (isDebugEnabled()) {
			write(LoggingEvent.DEBUG, message, null, v);
		}
	}
	
	public static void debug(String message, String v1, String v2) {
		if (isDebugEnabled()) {
			write(LoggingEvent.DEBUG, message, null, new LoggerDataWrapper(v1, v2));
		}
	}
	
	public static void debug(String message, long v) {
		if (isDebugEnabled()) {
			write(LoggingEvent.DEBUG, message, null, new LoggerDataWrapper(v));
		}
	}

	public static void debug(String message, long v1, long v2) {
		if (isDebugEnabled()) {
			write(LoggingEvent.DEBUG, message, null, new LoggerDataWrapper(v1, v2));
		}
	}
	
	public static void debug(String message, boolean v) {
		if (isDebugEnabled()) {
			write(LoggingEvent.DEBUG, message, null, new LoggerDataWrapper(v));
		}
	}
	
	public static void info(String message) {
		if (isErrorEnabled()) {
			write(LoggingEvent.INFO, message, null);
		}
	}
	
	public static void warn(String message) {
		if (isErrorEnabled()) {
			write(LoggingEvent.WARN, message, null);
		}	
	}
	
	public static void error(String message) {
		if (isErrorEnabled()) {
			write(LoggingEvent.ERROR, "error " + message, null);
		}
	}
	
	public static void error(String message, long v) {
		if (isErrorEnabled()) {
			write(LoggingEvent.ERROR, "error " + message, null, new LoggerDataWrapper(v));
		}
	}
	
	public static void error(String message, String v) {
		if (isErrorEnabled()) {
			write(LoggingEvent.ERROR, "error " + message, null, v);
		}
	}
	
	public static void error(String message, String v, Throwable t) {
		if (isErrorEnabled()) {
			write(LoggingEvent.ERROR, "error " + message, t, v);
		}
	}

	public static void error(Throwable t) {
		if (isErrorEnabled()) {
			write(LoggingEvent.ERROR, "error " + t.toString(), t);
		}
	}
	
	public static void error(String message, Throwable t) {
		if (isErrorEnabled()) {
			write(LoggingEvent.ERROR, "error " + message + " " + t.toString(), t);
		}
	}
	
	private static void callAppenders(LoggingEvent event) {
		for (Iterator iter = loggerAppenders.iterator(); iter.hasNext();) {
			LoggerAppender a = (LoggerAppender) iter.next();
			a.append(event);
		};
	}
	
	public static void addAppender(LoggerAppender newAppender) {
		loggerAppenders.add(newAppender);
	}
}