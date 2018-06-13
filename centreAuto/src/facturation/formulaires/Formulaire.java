/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.formulaires;

import facturation.hydrators.Hydratation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tayeb
 */
public abstract class Formulaire {
    
    protected List<Champ> champs ;
    protected Hydratation strategie;
    
    public Formulaire(){
     champs =new ArrayList<>() ;
    }

    public Formulaire(List<Champ> champs) {
        this.champs = champs;
    }
    
    public Formulaire build(Champ champ){
        this.champs.add(champ);
        return this;
    }
    
    public String validate(){
    
        String valide ="";
        for(Champ c : champs)
        {
            while(!c.validate().isEmpty())
            {
                return c.validate();
            }
               
        }
        return valide;
     
    }

    public List<Champ> getChamps() {
        return champs;
    }

    public void setChamps(List<Champ> champs) {
        this.champs = champs;
    }

    public Hydratation getStrategie() {
        return strategie;
    }

    public void setStrategie(Hydratation strategie) {
        this.strategie = strategie;
    }

    public String hydrate(){
      
      String message ="";
      if(this.validate().isEmpty()) 
      {
          this.strategie.hydrate();
      }
      else
      {
           message = this.validate();
      }
      return message;
     }
    
    
}
