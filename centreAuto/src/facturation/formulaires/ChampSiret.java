/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.formulaires;


import facturation.constraints.NotEmpty;
import facturation.constraints.Constraint;
import facturation.constraints.Length;

/**
 *
 * @author tayeb
 */
public class ChampSiret extends Champ{
    
   public ChampSiret(String value) {
        super("Integer","Numéro de Siret", value);
        
       /* Constraint ne = new NotEmpty("le champ " + name + " est obligatoire");
        this.add(ne);
        
        Constraint lv = new Length( "le champ " + name + " doit etre un nombre à 14 chiffres",14 );
        this.add(lv);*/
    }
    
}
