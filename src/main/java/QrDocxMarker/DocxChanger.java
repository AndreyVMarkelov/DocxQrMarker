package QrDocxMarker;

import java.io.File;
import java.util.StringTokenizer;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.HeaderReference;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.SectPr;

/**
 * @author Andrey V. Markelov
 */
public class DocxChanger
{
    /**
     * Object factory.
     */
    private ObjectFactory objectFactory;

    /**
     * Path to "docx" fiel.
     */
    private String filePath;

    /**
     * Path to image.
     */
    private String imagePath;

    /**
     * Output file.
     */
    private String outFilePath;

    /**
     * Extra data to new page.
     */
    private String overData;

    /*
     * Constructor.
     */
    public DocxChanger(
        String filePath,
        String imagePath,
        String outFilePath,
        String overData)
    {
        this.filePath = filePath;
        this.imagePath = imagePath;
        this.outFilePath = outFilePath;
        this.objectFactory = new ObjectFactory();
        this.overData = overData;
    }

    public void addHeaderImage()
    throws Exception
    {
        File file = new File(filePath);
        File outFile = new File(outFilePath);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file);
        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

        if (overData != null && overData.length() > 0)
        {
            mainDocumentPart.addParagraphOfText("\n");
            mainDocumentPart.addStyledParagraphOfText("Title", "Согласовали:");

            StringTokenizer st = new StringTokenizer(overData, ",");
            int count = 0;
            while (st.hasMoreTokens())
            {
                String token = st.nextToken();
                switch (count)
                {
                    case 0:
                        mainDocumentPart.addParagraphOfText("Согласование от руководителя ЦФО:" + token);
                        break;
                    case 1:
                        mainDocumentPart.addParagraphOfText("Согласование от СБ:" + token);
                        break;
                    case 2:
                        mainDocumentPart.addParagraphOfText("Согласование от ЮС:" + token);
                        break;
                    case 3:
                        mainDocumentPart.addParagraphOfText("Согласование от Бухгалтерии:" + token);
                        break;
                    case 4:
                        mainDocumentPart.addParagraphOfText("Согласование от ФО:" + token);
                        break;
                }

                count++;
            }
        }

        Relationship relationship = createHeaderPart(wordMLPackage);
        createHeaderReference(wordMLPackage, relationship);
        wordMLPackage.save(outFile);
    }

    public Relationship createHeaderPart(
        WordprocessingMLPackage wordprocessingMLPackage)
    throws Exception
    {
        HeaderPart headerPart = wordprocessingMLPackage.getHeaderFooterPolicy().getDefaultHeader();
        if (headerPart == null)
        {
            headerPart = new HeaderPart();
        }

        // Have to do this so that the next line can add an image
        headerPart.setPackage(wordprocessingMLPackage);
        headerPart.setJaxbElement(getHdr(wordprocessingMLPackage, headerPart));
        return wordprocessingMLPackage.getMainDocumentPart().addTargetPart(headerPart);
    }

    public Hdr getHdr(
        WordprocessingMLPackage wordprocessingMLPackage,
        HeaderPart sourcePart)
    throws Exception
    {
        Hdr hdr = sourcePart.getJaxbElement();
        if (hdr == null)
        {
            hdr = objectFactory.createHdr();
        }
        hdr.getEGBlockLevelElts().add(newImage(wordprocessingMLPackage, sourcePart, getBytes(), "filename", "alttext", 1, 2));
        return hdr;
    }

    public byte[] getBytes() throws Exception {

        File file = new File(imagePath);

        java.io.InputStream is = new java.io.FileInputStream(file);
        long length = file.length();
        // You cannot create an array using a long type.
        // It needs to be an int type.
        if (length > Integer.MAX_VALUE)
        {
            System.err.println("File too large!!");
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
        {
            offset += numRead;
        }
        // Ensure all the bytes have been read in
        if (offset < bytes.length)
        {
            System.err.println("Could not completely read file " + file.getName());
        }
        is.close();

        return bytes;
    }

    public org.docx4j.wml.P newImage(
        WordprocessingMLPackage wordMLPackage, Part sourcePart,
        byte[] bytes,
        String filenameHint,
        String altText,
        int id1,
        int id2)
    throws Exception
    {
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, sourcePart, bytes);
        Inline inline = imagePart.createImageInline(filenameHint, altText, id1, id2);

        // Now add the inline in w:p/w:r/w:drawing
        org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
        org.docx4j.wml.P p = factory.createP();
        org.docx4j.wml.R run = factory.createR();
        p.getParagraphContent().add(run);
        org.docx4j.wml.Drawing drawing = factory.createDrawing();
        run.getRunContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);

        return p;
    }

    public void createHeaderReference(
        WordprocessingMLPackage wordprocessingMLPackage,
        Relationship headerRelationship)
    throws InvalidFormatException
    {
        SectPr sectPr = objectFactory.createSectPr();

        HeaderReference headerReference = objectFactory.createHeaderReference();
        headerReference.setId(headerRelationship.getId());
        headerReference.setType(HdrFtrRef.DEFAULT);
        sectPr.getEGHdrFtrReferences().add(headerReference);

        wordprocessingMLPackage.getMainDocumentPart().addObject(sectPr);
    }
}
