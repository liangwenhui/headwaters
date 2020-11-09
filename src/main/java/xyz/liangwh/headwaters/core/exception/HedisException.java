package xyz.liangwh.headwaters.core.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;

public class HedisException extends Exception {
    private static final long serialVersionUID = -1618184616248018216L;

    private Throwable _cause;

    private HedisError _error;

    public HedisException(HedisError error) {
        super();
        _error = error;
    }

    public HedisException(HedisError error, String message) {
        super(message);
        _error = error;
    }

    public HedisException(HedisError error, Throwable cause) {
        super(cause.getMessage());
        _cause = cause;
        _error = error;
    }

    public HedisException(String errorCode, Throwable cause) {
        super(cause.getMessage());
        _cause = cause;
        _error = new HedisError(errorCode);
    }

    public HedisException(String errorCode, String message) {
        super(message);
        _error = new HedisError(errorCode);
    }

    public HedisException(HedisError error, String message, Throwable cause) {
        super(message);
        if (cause instanceof RemoteException) {
            _cause = ((RemoteException) cause).detail;
        }
        else {
            _cause = cause;
        }
        _error = error;
    }

    public HedisException(String errorCode, String message, Throwable cause) {
        super(message);
        if (cause instanceof RemoteException) {
            _cause = ((RemoteException) cause).detail;
        }
        else {
            _cause = cause;
        }
        _error = new HedisError(errorCode);
    }

    @Override
    public Throwable getCause() {
        return _cause;
    }

    public HedisError getError() {
        return _error;
    }

    /**
     * 重载得到信息的方法
     *
     * @return <code>String</code>,错误信息
     */
    @Override
    public String getMessage() {
        StringBuffer sb = new StringBuffer(getError() == null ? "" : getError().toString());
        sb.append(" ");
        String message = super.getMessage();
        if (message != null) {
            sb.append(message);
        }
        else if (getCause() != null) {
            sb.append(getCause().getMessage());
        }
        return sb.toString();
        // return super.getMessage();
    }

    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    public final void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * 获取当前堆栈信息
     *
     * @return String
     */
    public final String getStackTraceAsString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 打印堆栈
     *
     * @param stream
     */
    @Override
    public final void printStackTrace(PrintStream stream) {
        // stream.println("Caused by: " + this.toString());
        Throwable t = getCause();
        if (t == null) {
            super.printStackTrace(stream);
        }
        while (t != null) {
            // stream.println("Caused by: " + t);
            if (t instanceof HedisException) {
                t = ((HedisException) t).getCause();
            }
            else if (t instanceof RemoteException) {
                t = ((RemoteException) t).detail;
            }
            else {
                t.printStackTrace(stream);
                break;
            }
        }
    }

    /**
     * 打印堆栈
     *
     * @param writer
     */
    @Override
    public final void printStackTrace(PrintWriter writer) {
        Throwable t = getCause();
        if (t == null) {
            super.printStackTrace(writer);
        }
        while (t != null) {
            if (t instanceof HedisException) {
                t = ((HedisException) t).getCause();
            }
            else if (t instanceof RemoteException) {
                t = ((RemoteException) t).detail;
            }
            else {
                t.printStackTrace(writer);
                break;
            }
        }
    }
}
