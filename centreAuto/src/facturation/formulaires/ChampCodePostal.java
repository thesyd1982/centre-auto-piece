/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.formulaires;

import facturation.constraints.MaxLengthValidator;
import facturation.constraints.NotEmpty;
import facturation.constraints.TypeDouble;
import facturation.constraints.Constraint;

/**
 *
 * @author tayeb
 */
public class ChampCodePostal extends Champ{
    
   public ChampCodePostal(String value) {
        super("Integer","Code Postal", value);
        
        Constraint ne = new NotEmpty("le champ " + name + " est obligatoire");
        this.add(ne);
        
        Constraint tv = new TypeDouble( "le champ " + name + " doit etre un nombre à virgule",this.type );
        this.add(tv);
        
        Constraint lv = new MaxLengthValidator( "le champ " + name + "un nombre avec 10 chiffres au maximum",10 );
        this.add(lv);
    }
    
}
