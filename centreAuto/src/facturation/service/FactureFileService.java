/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.service;


import facturation.entities.Facture;
import facturation.utils.PdfUtils;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tayeb
 */
public class FactureFileService implements IFactureFileService{
    
    
    
    protected Facture facture;
    protected String racine = "\\\\CPA2\\Factures";
    protected String fichier;
    protected Path racinePath = Paths.get(racine);

    public Path getRacinePath() {
        return racinePath;
    }

    public void setRacinePath(Path racinePath) {
        this.racinePath = racinePath;
    }
    
    public FactureFileService() 
    {}

    public String getRacine() {
        return racine;
    }

    public void setRacine(String racine) {
        this.racine = racine;
    }
    
    
    public FactureFileService( String racine) 
    {
       
    }
   
    @Override
    public Path createFileFacture(Facture f ) 
    {
       
       
      
      Path filePath = racinePath.resolve(f.getPathFacture());
      Path directoriesPath =filePath.getParent() ;
      try { 
            
            Files.createDirectories(directoriesPath);
            PdfUtils pdf = new PdfUtils(filePath.toString(),f);
                 
           } catch (IOException ex) {
            Logger.getLogger(FactureFileService.class.getName()).log(Level.SEVERE, null, ex);
        }
       return filePath;
      
    }
    
  
   
  
    
  
    @Override
    public String removeFileFacture(Facture facture)
    {
       
        Path filePath = racinePath.resolve(facture.getPathFacture());
        
        Path dossierMois = filePath.getParent();
        Path dossierAnnee= dossierMois.getParent();
        Path dossierRacine =  dossierAnnee.getParent();
        
        try {
                Files.deleteIfExists(filePath);
                spprimerDossierVide(dossierMois);
                spprimerDossierVide(dossierAnnee);
                spprimerDossierVide(dossierRacine);
            
        } catch (IOException ex) {
            Logger.getLogger(FactureFileService.class.getName()).log(Level.SEVERE, null, ex);
        }
     
  
   
  
        
        return filePath.toString();
    }
    
    protected void spprimerDossierVide(Path dossier)
    {
       int i = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dossier)) 
        {
                Iterator<Path> it = stream.iterator();

                while( it.hasNext()) 
                {
                    it.next();
                    i++;
                }
                if(i==0)
                {
                    Files.deleteIfExists(dossier);
                }


        } 
        catch (IOException | DirectoryIteratorException e) 
        {
          e.printStackTrace();
        }
    }
    
    public void openFactureFile(Facture facture) throws IOException{
        String path = facture.getPathFacture().toString();
        String absolute_path = this.racinePath+"/"+path; 
           
        File file = new File(absolute_path);
        if(file.exists())    
        {
            Desktop.getDesktop().open(file);
        }
        else
        {
            this.createFileFacture(facture);
            Desktop.getDesktop().open(new File(this.racinePath+"/"+path));
        }
    
    }
    
}
