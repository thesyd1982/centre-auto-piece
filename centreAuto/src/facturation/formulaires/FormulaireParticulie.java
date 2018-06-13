/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.formulaires;

import facturation.entities.Particulier;
import java.util.Locale;

/**
 *
 * @author tayeb
 */
public class FormulaireParticulie extends Formulaire{
    protected ChampNom champNom;
    protected ChampPrenom champPrenom;
    protected ChampVille champVille;
    protected ChampLieu champLieu;
    protected ChampCodePostal  champCodePostal; 
                      
    
    protected Particulier particulie;
    
    public FormulaireParticulie() 
    {
           super();
          particulie = new Particulier();
                 
    }
     
    public FormulaireParticulie(String nom,String prenom,String lieu,String ville,String codePostal) 
    {
            champNom = new ChampNom(nom);
            champPrenom = new ChampPrenom(prenom);
            this.champs.add(champNom);
            this.champs.add(champPrenom);
            
            
            if(lieu==null)lieu="";
            if(!lieu.isEmpty())
            {
                champLieu = new ChampLieu(lieu);
                this.champs.add(champLieu);
            }
            
            
            if(ville==null)ville="";
            if(!ville.isEmpty())
            {
             champVille = new ChampVille(ville);
             this.champs.add(champVille);
            }    
            
            if(codePostal==null)codePostal="";
            
           
            if(!codePostal.isEmpty())    
            {
                champCodePostal= new ChampCodePostal(codePostal);
                this.champs.add(champCodePostal);
            }
            
            particulie = new Particulier();
                 
    }

    public String getNom() {
        return champNom.getValue();
    }
    
    public void setNom(String siret) {
        this.champNom.setValue(siret);
    }
    
    public String getPrenom() {
        return champPrenom.getValue();
    }
    
    public void setNomSociete(String nom) {
        this.champPrenom.setValue(nom);
    }

     public String getVille() {
        if(champVille==null)
            return "";
        else
            return champVille.getValue();
    }

    public void setVille(String ville) {
        this.champVille.setValue(ville); 
    }

    public String getLieu() { 
        if(champLieu==null)
            return "";
        else
        return champLieu.getValue();
    }

    public void setLieu(String lieu) {
        this.champLieu.setValue(lieu);
    }

    public String getCodePostal() {
         if(champCodePostal==null)
            return "";
        else
            return champCodePostal.getValue();
    }

    public void setCodePostalSociete(String codePostal) {
        this.champCodePostal.setValue(codePostal);
    }

    
    
    public String  hydrate()
    {
        String message= "";
        if(this.validate().isEmpty())
        {   
            this.particulie.setNom(this.getNom().toUpperCase(Locale.FRANCE));
            this.particulie.setPrenom(this.getPrenom().toUpperCase(Locale.FRANCE));
            this.particulie.setLieu(this.getLieu().toUpperCase(Locale.FRANCE));
            this.particulie.setVille(this.getVille().toUpperCase(Locale.FRANCE));
            this.particulie.setCodePostal(this.getCodePostal());
          
        }
        else
        {
           message= this.validate();
        }
        return message;
    }

    public Particulier getParticulie() {
        return particulie;
    }

    public void setParticulie(Particulier particulie) {
        this.particulie = particulie;
    }

   
}
