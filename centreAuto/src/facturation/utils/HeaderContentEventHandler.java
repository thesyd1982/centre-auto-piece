package facturation.utils;

import com.itextpdf.kernel.events.IEventHandler;


import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import facturation.entities.Facture;
import facturation.entities.FactureParticulier;
import facturation.entities.Particulier;
import facturation.entities.Professionnel;

class HeaderContentEventHandler implements IEventHandler
   {
    
    protected  int pageNumber = 1;  
    protected Facture facture; 
    
    protected PdfFont font_bold;
    
    protected float offSet;
    protected float  pageHeight;
    protected Document document;
    protected float pageWidth;
    protected Image logo;
    
    protected float elementPositionX;
    protected float elementPositionY;
    protected float logoWidth;
    protected float logoHeight;
    
    public HeaderContentEventHandler(Document document,Facture facture,PdfFont font_bold ,Image logo,float offSet) 
        {
            this.offSet = offSet; 
            this.font_bold = font_bold;
            this.logo = logo;
            this.document = document;
            this.facture = facture ;
            pageNumber = 1; 
            init();
            
        }
    
    public void init()
    {
        this.logoWidth  = 210;
        this.logoHeight = 108;
        
        
       
        this.pageWidth=document.getPdfDocument().getDefaultPageSize().getWidth();
        this.pageHeight=document.getPdfDocument().getDefaultPageSize().getHeight();
        
        this.elementPositionX = this.logoWidth-offSet;
        this.elementPositionY = this.pageHeight-offSet;
        
        
    } 

        @Override
        public void handleEvent(Event event) 
        {
            
            
            PdfDocumentEvent docEvent = (PdfDocumentEvent)event;
        
            PdfDocument pdfDocment = docEvent.getDocument();
            PdfPage pageCourante = pdfDocment.getPage(pageNumber);
            
            int numberOfPages = pdfDocment.getNumberOfPages();
            
            PdfCanvas canvas = new PdfCanvas(pageCourante);
            String client = "";
            String ima = "\n\nImmatriculation:\n"+this.facture.getImmatriculation();
            
           
            
            String lieu =  this.facture.getClient().getAdresse().getLieu();
            
            
            lieu = (lieu==null) ? "" : lieu ;
            String cp = this.facture.getClient().getAdresse().getCp();
            cp = (cp==null) ? "" : cp ;
            
            
            
            String ville = this.facture.getClient().getAdresse().getVille();
            ville = (ville==null) ? "" : ville ;
            
            String adresse ="";
            
            String dateLivraison = "Date de livraison: "+this.facture.getDate_livraison();
            if(facture instanceof FactureParticulier )
            {   String nom = ((Particulier)this.facture.getClient()).getNom();
                String prenom = ((Particulier)this.facture.getClient()).getPrenom();
                client = nom+" "+prenom+"\n";
                dateLivraison="";
                
                if(!lieu.isEmpty()&& !ville.isEmpty()&& !cp.isEmpty())
                {
                   adresse= lieu+"\n"+cp+" "+ville+" FRANCE";
                }
            }
            else
            {   
                String nomSociete = ((Professionnel)this.facture.getClient()).getNomSociete();
               
                client="SOCIÉTÉ: "+nomSociete+"\n";
                adresse= lieu+"\n"+cp+" "+ville+" FRANCE";
                
            }
            
            
            
            float contantWidth = pageWidth-document.getRightMargin()-document.getLeftMargin();
         
            
            String numFacture;
            numFacture = "Facture N° "+facture.getNumero_facture();
            String dateFacture = this.facture.getDate_facturation();
            
            String numerotation = numFacture+"\nÉtablie le "+dateFacture;
            String societe = "SAS Cirta Pièces Auto\n"+
                             "15 AVENUE MARIE REYNOARD\n"+
                             "38100 GRENOBLE FRANCE";
                    
            String coordonnees ="SIRET 838 802 239 00015 \n" + "Tél 1: - 04 38 70 40 61\n"+"Tél 2: - 09 63 59 40 61 \nE-mail cirtapiecesautos@gmail.com"; 
            String  mentions =
                "Paiement à réception de facture, avec un delais de 15 jours "+
                ",aucun escompte consenti pour règlement anticipé. "+
                "Tout incident de paiement est passible d'intérêt de retard ,"+
                "le montant des pénalités résulte de l'application aux sommes restant dues d'un taux d'intérêt légal en vigueur au moment de l'incident.\n\n"+
                "Indemnité forfaitaire pour frais de recouvrement due au créancier en cas de retard de paiement: 40€"; 
            
            String modeReglement = "Mode de règlement: "+this.facture.getMode_payement();
            
           
            Paragraph zoneFactureDate = new Paragraph(numerotation)
                    .setFont(font_bold);
            
            Paragraph zoneClient =new Paragraph(client+adresse+ima);
            System.out.println(ima);
            
            
            zoneClient.setFont(font_bold);
            
            Paragraph zoneLogo = new Paragraph().setBorderBottom(new SolidBorder(1)).add(logo);
            zoneLogo.setWidth(logoWidth);
           
            Paragraph zoneSociete = new Paragraph(societe)
                    .setFont(font_bold).add("\n\n"+coordonnees);
            Paragraph zoneReglement = new Paragraph(dateLivraison+"\n" + modeReglement );
            Paragraph zoneMentions = new Paragraph(mentions).setFontSize(8).setTextAlignment(TextAlignment.LEFT).setWidth(contantWidth);
            
            zoneMentions.setKeepTogether(true);
            
                   
          document.showTextAligned(
                    zoneLogo,
                   
                    1.5f*offSet,
                    pageHeight-(offSet),
                    pageNumber,
                    TextAlignment.LEFT,
                    VerticalAlignment.TOP,
                    0
            );
            
            
            document.showTextAligned(zoneFactureDate,
                    pageWidth-logoWidth,
                    elementPositionY-offSet,
                    pageNumber,
                    TextAlignment.LEFT,
                    VerticalAlignment.TOP,
                    0
            );
             
            
            document.showTextAligned(zoneClient,
                    pageWidth-logoWidth,
                    elementPositionY-2.0f*offSet,
                    pageNumber,
                    TextAlignment.LEFT,
                    VerticalAlignment.TOP,
                    0
            );
            
            document.showTextAligned(
                    zoneReglement,
                    pageWidth-logoWidth,
                    elementPositionY-logoHeight-offSet/2,
                    pageNumber,
                    TextAlignment.LEFT,
                    VerticalAlignment.TOP,
                    0
            );
            
            
            document.showTextAligned(zoneSociete,
                    1.5f*offSet,
                    elementPositionY-logoHeight-offSet/2,
                    pageNumber,
                    TextAlignment.LEFT,
                    VerticalAlignment.TOP,
                    0
            );
            document.showTextAligned(
                    zoneMentions,
                    1.5f*offSet,
                    2*offSet,
                    pageNumber,
                    TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM,
                    0
            );
            pageNumber++;
            
    } 

    
   
   
}


