package bleach.a32k.settings;

class InvalidHwidError extends Error
{
    private static final long serialVersionUID = 6969696969L;
    private final String hwid;

    public InvalidHwidError(String hwid)
    {
        super(hwid);
        this.setStackTrace(new StackTraceElement[0]);
        this.hwid = hwid;
    }

    public String toString()
    {
        return "Invaild Hwid: " + this.hwid;
    }

    public synchronized Throwable fillInStackTrace()
    {
        return this;
    }
}
