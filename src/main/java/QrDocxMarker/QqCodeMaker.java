package QrDocxMarker;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * @author Andrey V. Markelov
 */
public class QqCodeMaker
{
    /**
     * Codeset.
     */
    private static final String CODESET = "ISO-8859-1";

    /**
     * QR-code data.
     */
    private String codeData;

    /**
     * Output file.
     */
    private String outFile;

    /**
     * Constructor.
     */
    public QqCodeMaker(
        String codeData,
        String outFile)
    {
        this.codeData = codeData;
        this.outFile = outFile;
    }

    public void createQrCode()
    throws QrException
    {
        Charset charset = Charset.forName(CODESET);
        CharsetEncoder encoder = charset.newEncoder();
        byte[] b = null;
        try
        {
            // Convert a string to ISO-8859-1 bytes in a ByteBuffer
            ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(codeData));
            b = bbuf.array();
        }
        catch (CharacterCodingException e)
        {
            System.out.println(e.getMessage());
        }

        String data = null;
        try
        {
            data = new String(b, CODESET);
        }
        catch (UnsupportedEncodingException e)
        {
            //--> impossible
        }

        // get a byte matrix for the data
        BitMatrix matrix = null;
        int h = 100;
        int w = 100;
        com.google.zxing.Writer writer = new QRCodeWriter();
        try
        {
            matrix = writer.encode(data, com.google.zxing.BarcodeFormat.QR_CODE, w, h);
        }
        catch (com.google.zxing.WriterException e)
        {
            throw new QrException(2, e);
        }

        File file = new File(outFile);
        try
        {
            MatrixToImageWriter.writeToFile(matrix, "PNG", file);
        }
        catch (IOException e)
        {
            throw new QrException(1, e);
        }
    }
}
