package QrDocxMarker;

import java.io.File;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        if (args.length != 4)
        {
            if (args.length == 1 && args[0].equals("test"))
            {
                System.exit(0);
            }
            else
            {
                System.exit(Consts.INCORRECT_ARGS);
            }
        }

        String qrData = args[0];
        String qrFile = args[1];
        String docxFile = args[2];
        String docxFileOut = args[3];

        try
        {
            QqCodeMaker qrC = new QqCodeMaker(qrData, qrFile);
            qrC.createQrCode();
            DocxChanger docxC = new DocxChanger(docxFile, qrFile, docxFileOut);
            docxC.addHeaderImage();
            new File(qrFile).delete();
        }
        catch (Exception e)
        {
            System.exit(-1);
        }

        System.exit(Consts.OK);
    }
}
