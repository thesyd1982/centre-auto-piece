/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.formulaires;

import facturation.entities.Professionnel;
import java.util.Locale;

/**
 *
 * @author tayeb
 */
public class FormulaireProfessionnel extends Formulaire{
   
    protected ChampNomSociete champNomSociete;
    protected ChampVilleSociete champVilleSociete;
    protected ChampLieuSociete champLieuSociete;
    protected ChampCodePostalSociete  champCodePostalSociete; 
                      
    
    protected Professionnel professionnel;
    
    public FormulaireProfessionnel() 
    {
           super();
           professionnel = new Professionnel();
                 
    }
     
    public FormulaireProfessionnel(String nomSociete,String lieuSociete,String villeSociete,String codePostalSociete) 
    {
            champNomSociete = new ChampNomSociete(nomSociete);
            champVilleSociete = new ChampVilleSociete(villeSociete);
            champLieuSociete = new ChampLieuSociete(lieuSociete);
            champCodePostalSociete = new ChampCodePostalSociete(codePostalSociete);
            
            this.champs.add(champNomSociete);
            this.champs.add(champVilleSociete);
            this.champs.add(champLieuSociete);
            this.champs.add(champCodePostalSociete);
            
            professionnel = new Professionnel();
                 
    }

    
    
    public String getNomSociete() {
        return champNomSociete.getValue();
    }
    
    public void setNomSociete(String nomSociete) {
        this.champNomSociete.setValue(nomSociete);
    }

     public String getVilleSociete() {
        return champVilleSociete.getValue();
    }

    public void setVilleSociete(String villeSociete) {
        this.champVilleSociete.setValue(villeSociete); 
    }

    public String getLieuSociete() {
        return champLieuSociete.getValue();
    }

    public void setLieuSociete(String lieuSociete) {
        this.champLieuSociete.setValue(lieuSociete);
    }

    public String getCodePostalSociete() {
        return champCodePostalSociete.getValue();
    }

    public void setCodePostalSociete(String codePostalSociete) {
        this.champCodePostalSociete.setValue(codePostalSociete);
    }

    
    
    public String  hydrate()
    {
        String message= "";
        if(this.validate().isEmpty())
        {    
           
            this.professionnel.setNomSociete(this.getNomSociete().toUpperCase(Locale.FRANCE));
            this.professionnel.setLieu(this.getLieuSociete().toUpperCase(Locale.FRANCE));
            this.professionnel.setVille(this.getVilleSociete().toUpperCase(Locale.FRANCE));
            this.professionnel.setCodePostal(this.getCodePostalSociete().toUpperCase(Locale.FRANCE));
          
        }
        else
        {
           message= this.validate();
        }
        return message;
    }

    public Professionnel getProfessionnel() {
        return professionnel;
    }

    public void setProfessionnel(Professionnel professionnel) {
        this.professionnel = professionnel;
    }

   
}
