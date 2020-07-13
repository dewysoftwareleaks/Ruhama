package bleach.a32k.settings;

class NetworkError extends Error
{
    private static final long serialVersionUID = 69696969420L;

    public NetworkError()
    {
        super("Error Connecting To Hwid Server");
        this.setStackTrace(new StackTraceElement[0]);
    }

    public String toString()
    {
        return "Error Connecting To Hwid Server";
    }

    public synchronized Throwable fillInStackTrace()
    {
        return this;
    }
}
