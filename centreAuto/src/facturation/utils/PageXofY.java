/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.utils;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import java.io.IOException;

/**
 *
 * @author tayeb
 */
 class PageXofY implements IEventHandler {
    protected PdfFormXObject placeholder;
    protected float side = 20;
    protected float x = 300;
    protected float y = 12;
    protected float space = 4.5f;
    protected float descent = 3;
    
    protected String mentions;
    protected PdfFont font ;
    public PageXofY(PdfDocument pdf ,PdfFont f) throws IOException {
        placeholder =
            new PdfFormXObject(new Rectangle(0, 0, side, side));
         font =f;

    
    }
    @Override
    public void handleEvent(Event event) 
    {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdf = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        
        int pageNumber = pdf.getPageNumber(page);
        
        Rectangle pageSize = page.getPageSize();
        
        PdfCanvas pdfCanvas = new PdfCanvas(
            page.getLastContentStream(), page.getResources(), pdf);
        Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize).setFont(font);
        Paragraph p = new Paragraph()
            .add("Page ").add(String.valueOf(pageNumber)).add(" sur");
        canvas.showTextAligned(p, x, y, TextAlignment.RIGHT);
        pdfCanvas.addXObject(placeholder, x + space, y - descent);
        
        pdfCanvas.release();
        
    }
    public void writeTotal(PdfDocument pdf) {
        Canvas canvas = new Canvas(placeholder, pdf).setFont(font);
        canvas.showTextAligned(String.valueOf(pdf.getNumberOfPages()),
            0, descent, TextAlignment.LEFT);
        
    }

    
}
