/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.entities;

import facturation.enumeration.Mois;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;



/**
 *
 * @author tayeb
 */
@Entity
@DiscriminatorValue("PRO")

public class FactureProfessionnel extends Facture implements Serializable  {

   
    
    public FactureProfessionnel() {
       
    }
    
    public FactureProfessionnel(Professionnel c,String date ,Ligne l ,String immat ) {
       super(c,date,l,immat );
        
    }
    
     @Override
    public Path getPathFacture()
   {
        String mois_string = "";
        String annee = this.extraireAnnee();
        int  extraireMois = Integer.parseInt(this.extraireMois())-1;
        
        for (Mois mois : Mois.values()) 
        {
          if(extraireMois == mois.ordinal())
              mois_string = mois.toString();
        
        }
    
        String nomFichier  = "facture-pro"+this.getId_facture()+"-"+this.extraireJour()+"-"+this.extraireMois()+"-"+annee+".pdf";
        Professionnel p = (Professionnel) this.getClient();
        String nomSociete =  p.getNomSociete();
        
       
        
     return Paths.get("Professionnels",nomSociete ,annee,mois_string ,nomFichier);
   }
   
   
}
