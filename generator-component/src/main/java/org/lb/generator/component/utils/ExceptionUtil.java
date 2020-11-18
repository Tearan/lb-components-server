/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.utils;

/**
 * @author Terran
 * @since  1.0
 */
public class ExceptionUtil {
    private static final int DEFAULT_LINE = 10;

    public static String getExceptionCause(Throwable e) {
        StringBuilder sb;
        for (sb = new StringBuilder(); e != null; e = e.getCause()) {
            sb.append(e.toString()).append("\n");
        }
        return sb.toString();
    }

    public static String getAllExceptionStackTrace(Throwable e) {
        if (e == null) {
            return "";
        } else {
            StringBuilder stackTrace = new StringBuilder(e.toString());
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            StackTraceElement[] var3 = stackTraceElements;
            int var4 = stackTraceElements.length;
            for (int var5 = 0; var5 < var4; ++var5) {
                StackTraceElement stackTraceElement = var3[var5];
                stackTrace.append("\r\n").append("\tat ").append(stackTraceElement);
            }

            return stackTrace.toString();
        }
    }

    public static String getExceptionStackTrace(Throwable e, int lineNum) {
        if (e == null) {
            return "";
        } else {
            StringBuilder stackTrace = new StringBuilder(e.toString());
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            int size = lineNum > stackTraceElements.length ? stackTraceElements.length : lineNum;
            for (int i = 0; i < size; ++i) {
                stackTrace.append("\r\n").append("\tat ").append(stackTraceElements[i]);
            }
            return stackTrace.toString();
        }
    }

    public static String getBriefStackTrace(Throwable e) {
        return getExceptionStackTrace(e, DEFAULT_LINE);
    }
}
