package QrDocxMarker;

/**
 * Created with IntelliJ IDEA.
 * User: andrey
 * Date: 05.10.12
 * Time: 17:25
 * To change this template use File | Settings | File Templates.
 */
public class QrException
    extends Exception
{
    private final int code;

    /**
     * Constructor.
     */
    public QrException(int code)
    {
        super();
        this.code = code;
    }

    public QrException(int code, Throwable t)
    {
        super(t);
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }
}
