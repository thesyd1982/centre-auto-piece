/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.entities;

import facturation.enumeration.Mois;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author tayeb
 */
@Entity
@DiscriminatorValue("PAR")
public class FactureParticulier extends Facture{
    
    public FactureParticulier() {
       
    }
    
    public FactureParticulier(Particulier c,String date ,Ligne l,String immat ) {
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
        Particulier p = (Particulier) this.getClient();
        String nomClient=  p.getNom()+"_"+p.getPrenom();
        String nom  = "facture-par"+this.getId_facture()+"-"+this.extraireJour()+"-"+this.extraireMois()+"-"+annee+".pdf";
        
   return Paths.get("Particuliers",nomClient,annee,mois_string ,nom);
   }

    
}
