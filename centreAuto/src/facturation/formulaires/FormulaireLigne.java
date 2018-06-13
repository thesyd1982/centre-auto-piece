/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.formulaires;

import facturation.entities.Ligne;
import facturation.entities.Piece;



/**
 *
 * @author tayeb
 */
public class FormulaireLigne extends Formulaire{
    
    protected ChampPrix champPrix;
    protected ChampQuantite champQuantite;
   
    protected FormulairePiece fp;
    
    
    protected Ligne ligne ;
     
    
    public FormulaireLigne() 
    {
           super();
           ligne = new Ligne();
                 
    }
     
    public FormulaireLigne(String prix, String quantite,Piece p ) 
    {       
            ligne = new Ligne();
            fp = new FormulairePiece();
            fp.setPiece(p);
            champPrix = new ChampPrix(prix ,"Prix HT");
            champQuantite = new ChampQuantite(quantite);
            
            this.champs.add(champPrix);
            this.champs.add(champQuantite );             
    }

    public String getPrix() {
        return champPrix.getValue();
    }

    public void setPrix(String prix) {
        this.champPrix.setValue(prix);
    }

    public String getQuantite() {
        return champQuantite.getValue();
    }

    public void setQuantite(String quantite) {
        this.champQuantite.setValue(quantite);
    }

    

   
    
    
    public String  hydrate()
    {
        String message= "";
        if(this.validate().isEmpty())
        {    
            this.ligne.setPrixUHT(Double.parseDouble(this.getPrix()));
            this.ligne.setQuantite(Integer.parseInt(this.getQuantite()));
           
            
          
        }
        else
        {
           message= this.validate();
        }
        return message;
    }

    public Ligne getLigne() {
        return ligne;
    }

    public void setLigne(Ligne ligne) {
        this.ligne = ligne;
    }
}
