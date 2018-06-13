/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.utils;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import facturation.entities.Client;
import facturation.entities.Facture;
import facturation.entities.Ligne;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;



/**
 *
 * @author tayeb
 */
public class PdfUtils 
{
   
    protected Document document;
    protected PdfDocument pdf;
    
    protected PdfFont font;
    protected PdfFont font_bold;
    
    protected float fontSizeL;
    protected float fontSizeM;
    protected float fontSizeS;
    
    protected Facture facture ;
    protected Client client ;
    
   
    
    protected String logoPath ="/facturation/images/logo-CPA.png";

    
    String nomFichier;
    
     float logoWidth = 210;
     float logoHeight = 108;
     float offSet = 24 ;
     float pageWidth = 0;
     float pageHeight = 0;
     float x =0;
     float y = 0;
    Table [] tables; 
     Image logo  ;
    public String getNomFichier() {
        return nomFichier;
    }
    
   
    @SuppressWarnings("empty-statement")
    public PdfUtils(String nomFichier,Facture facture) throws IOException 
    {  
       
      
        Path logoP = Paths.get(logoPath);
        
        this.facture = facture;
        this.client = this.facture.getClient();
        
       
        
        this.nomFichier = nomFichier; 
        
        File fichier = new File(this.nomFichier);
         
            PdfWriter writer = new PdfWriter(nomFichier);
            this.pdf = new PdfDocument(writer);
            PageSize ps = PageSize.A4;
            //initialisation des coordonées
            pageWidth = ps.getWidth();
            pageHeight = ps.getHeight();
            
            x =logoWidth-offSet;
            y = (pageHeight)-offSet;
            
            fontSizeS = 10;
            fontSizeM = 9;
            fontSizeL= 10;
            
           
            //initialisation du Logo
            logo = new Image(ImageDataFactory.create( getClass().getResource(logoPath)));
            
            logo.scaleToFit(210,108);
            
            font_bold = PdfFontFactory.createFont(FontConstants.COURIER_BOLD);
            font= PdfFontFactory.createFont(FontConstants.COURIER);

            this.document = new Document(pdf,ps);
            
            this.document.setProperty(Property.FONT, font);
            this.document.setProperty(Property.FONT_SIZE,fontSizeS);
            
            HeaderContentEventHandler  eventHeader = new HeaderContentEventHandler(document,this.facture,font_bold ,logo,offSet);
            pdf.addEventHandler(PdfDocumentEvent.END_PAGE, eventHeader);
            
            PageXofY event = new PageXofY(pdf,font_bold);
            pdf.addEventHandler(PdfDocumentEvent.END_PAGE, event);
            
            this.generateContent();
           
        event.writeTotal(pdf);
        
        document.flush();        
        document.close();
        
        
      
        
       
        
        
        
    
    }

    public String getLogoPath() {
        return logoPath;
    }
      
    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
    
    protected void generateContent() throws IOException{
        //creation du tableau de commandes
        int nbr_lignes= this.facture.getLignes().size();
        int nbr_ligne_par_page = 35 ;
        
        int nb_tab = (nbr_lignes/nbr_ligne_par_page) +1;
        int nb_col = 6 ;
        
        String[][][] headers =new String[1][nb_tab][6];
        String[] headersTitles = {"N° Pièce","Désignation","Prix UHT", "Quantité","TVA%","Montant HT" };   
        
        for(int j =0 ;j < nb_tab;j++)
        {
            headers[0][j] = headersTitles;
            
        
        }
       
                                 
        float contantWidth = pageWidth-document.getRightMargin()-document.getLeftMargin();
        
        String data[][][]=new String[nbr_lignes][nb_tab][nb_col];
        
         this.tables = new Table[nb_tab];
        
        for (int j = 0; j < nb_tab; j++) 
        {
            tables[j] = new Table(new float[]{1,7, 2, 2,1,4 });
            tables[j].setWidth(contantWidth);
            tables[j].setSkipLastFooter(true);
            creerLigneTableau(tables, headers , 0,j ,font_bold, 1,nbr_ligne_par_page);
        }
        
        Ligne[] lignes = new Ligne[nbr_lignes];
        Object[] tabObject = this.facture.getLignes().toArray();
        
        for (int i=0 ;i<nbr_lignes;i++)
        {
           lignes[i]=(Ligne)tabObject[i];
        }
        
     
       
        
        for (int i = 0; i < nbr_lignes; i++) 
            {
               
                data[i][(i/nbr_ligne_par_page)] = extraireData(lignes[i]);
                creerLigneTableau(tables, data,i,i/nbr_ligne_par_page,font, 2,nbr_ligne_par_page);
             }
        //fin creation des tableaux de commandes
        //aout des  tableaux au document 
        for (int t = 0; t < nb_tab; t++) 
        {
            document.add(tables[t]);
        }
        
      
        
        
        
        
        String dataTotal = String.format("%,.2f€",facture.getTotalHT()); 
        String dataTotalTva =  String.format("%,.2f€",facture.getTotalTVA());
        String datNet = String.format("%,.2f€",facture.getTotalTTC());
        
        String[] totauxHaders = new String[]{ "TOTAL","TOTAL TVA" ,"Net à Payer"};
        String[] totauxData = new String[]{dataTotal,dataTotalTva,datNet};
        
        String[]detailTvaHeaders =new String[]{"Base HT","TVA%","Montant TVA" };
        double[][] lignesTva = facture.getLignesDetailTva();
       
        String[]detailTvaData = detailTvaData = new String[]{ String.format( "%,.2f€", lignesTva[0][0]),
                String.format( "%,.2f", lignesTva[0][1])+"%",
                String.format( "%,.2f€", lignesTva[0][2]) 
            };
            
            
        
         
       String[] reglementHeaders = new String[]{"Date de livraison","Mode de reglement"};
       String[] reglementData = new String[]{ facture.getDate_livraison(),facture.getMode_payement()}; 
        
        
        float[] reptotal = { 1,2 };
        Table totaTable = new Table(reptotal);
        
        totaTable.setWidth(contantWidth/3); 
        totaTable.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        creerTableauVertical(totaTable,totauxHaders ,totauxData ,font_bold ); 
        totaTable.setMargin(0);
       
        document.add(totaTable);
        
        Table detailTvaTable = new Table(new float[]{2,1,2});
        creerTableauDetail(detailTvaTable,detailTvaHeaders,detailTvaData);
        detailTvaTable.setKeepTogether(true);
        detailTvaTable.setMarginTop(-1f*offSet+2);
        document.add( detailTvaTable );
        
        Table reglementTable = new Table(new float[]{2,1}); 
        creerTableauVertical(reglementTable, reglementHeaders, reglementData, font); 
        
        reglementTable.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        reglementTable.setMargin(0);
       
     
       
        
    }
    public void creerTableauVertical(Table table ,String[] headers , String[] data ,PdfFont font)
    {
        table.setBorder(new SolidBorder(1));
        table.setBorderTop(Border.NO_BORDER);
        table.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        for (int i=0;i<headers.length ; i++) 
        {
            Cell headerCell = new Cell().add(new Paragraph(headers[i]).setFont(font)).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(1));
            Cell dataCell = new Cell().add(new Paragraph(data[i]).setFont(font)).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(1));
            table.addCell(headerCell);
            table.addCell(dataCell);
        }
    
    }
    
   public void creerTableauDetail(Table table ,String[] headers , String[] data )
    {
       
       table.setVerticalAlignment(VerticalAlignment.MIDDLE);
       table.setBorder(new SolidBorder(Color.BLACK,1));
       
       int nb_cols_header = data.length;
       
       for (int i = 0; i < nb_cols_header; i++) 
            {
               Cell headerCell =  new Cell().add( new Paragraph(headers[i]).setFont(font_bold) );
               table.addCell(headerCell.setBorder(new SolidBorder(Color.BLACK,1)) );     
            }
        for (int i = 0; i < nb_cols_header; i++) 
            {
              Cell dataCell= new Cell().add( new Paragraph(data[i]));
              table.addCell( dataCell );
            }
    }
   
   /*cet objet doit implementer une strategie de dession pour desinner les differentes factures pro particulier avec diffrente nombre de ligne */ 
    public void creerLigneTableau(Table tables[], String[][][] lignes,int indice_ligne,int indice_tab, PdfFont font, int tablePart,int nbr_ligne_par_page) 
    {
        
        tables[indice_tab].setHorizontalAlignment(HorizontalAlignment.CENTER);
        tables[indice_tab].setVerticalAlignment(VerticalAlignment.MIDDLE);
        tables[indice_tab].setTextAlignment(TextAlignment.LEFT);
        tables[indice_tab].setBorder(Border.NO_BORDER);
        
        int numberOfColumns = 6;
        
        switch(tablePart) 
        {
            case 1:
                 for (int i = 0; i < numberOfColumns ;i++) 
                    {
                        
                        tables[indice_tab].addHeaderCell
                            (
                                new Cell().add( new Paragraph(lignes[indice_ligne][indice_tab][i])
                                        .setFont(font)) );
                           
                            
                        tables[indice_tab].addCell(new Cell().setHeight(((nbr_ligne_par_page/2)+1)*offSet));    
                    }
                    
                    tables[indice_tab].getHeader().setMarginTop(2*(logoHeight)-offSet);
                    
            break;
                 
            case 2: 
                
                for (int i = 0; i < numberOfColumns ;i++)
                    {
                        String contenu = lignes[indice_ligne][indice_tab][i];
                        Table table =  tables[indice_tab];
                        Cell cellule = table.getCell(0, i);
                        
                       // .setPadding(0).setVerticalAlignment(VerticalAlignment.MIDDLE).setHeight(offSet*2)
                        cellule.add(new Paragraph(contenu).setHeight(offSet).setPadding(3).setVerticalAlignment(VerticalAlignment.MIDDLE)).setPadding(0);
                    } 
            break;
                 
            default:
                for (int i = 0; i < numberOfColumns ;i++)
                    {
                        tables[indice_tab].addFooterCell(
                                new Cell().add(new Paragraph(""))
                        );
                    }
             break;
        }
        
    } 

    
 public String[] extraireData(Ligne l)
   {
            
           String  nProduit = ""+l.getPiece().getId_piece();
           
           String reduction =""; 
           if(l.getRabais()!=0.0)
           {
               reduction ="\nRabais:  "+String.format( "%,.2f", l.getRabais())+"%";
           }
           if(l.getRemise()!=0.0)
           {
               reduction +="\nRemise:  "+String.format( "%,.2f", l.getRemise())+"%";
           }
           if(l.getRistourne()!=0.0)
           {
               reduction +="\nRistourne:  "+String.format( "%,.2f", l.getRistourne())+"%";
           }
           
           String designation = ""+l.getPiece().getDesignation()+reduction;
           String prix = String.format( "%,.2f€", l.getPrixUHT());
           String quantite = ""+l.getQuantite();

           String tva = String.format( "%,.2f", l.getTVA()*100);
           String montantHT = String.format( "%,.2f€", l.getMontantHT());
          
           
           return new String[]{
                nProduit,designation, prix,quantite,tva,montantHT
            };
          
    }
   
   

}