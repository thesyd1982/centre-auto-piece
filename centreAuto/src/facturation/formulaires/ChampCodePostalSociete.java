/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.formulaires;

import facturation.constraints.MaxLengthValidator;
import facturation.constraints.NotEmpty;
import facturation.constraints.Constraint;

/**
 *
 * @author tayeb
 */
public class ChampCodePostalSociete extends Champ{
    
   public ChampCodePostalSociete(String value) {
        super("Integer","Code Postal", value);
        
        Constraint ne = new NotEmpty("le champ " + name + " est obligatoire");
        this.add(ne);
        
        Constraint lv = new MaxLengthValidator( "le champ " + name + " doit etre un nombre à dix chiffres",10 );
        this.add(lv);
    }
    
}
