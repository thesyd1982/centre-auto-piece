/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.formulaires;

import facturation.constraints.NotEmpty;
import facturation.constraints.PositiveDouble;
import facturation.constraints.TypeDouble;
import facturation.constraints.Constraint;

/**
 *
 * @author tayeb
 */
public class ChampPrix extends Champ{
    
     public ChampPrix(String value ,String name) {
        super("Double",name, value);
        
        Constraint ne = new NotEmpty("le champ " + name + " est obligatoire");
        this.add(ne);
        
        Constraint tv = new TypeDouble( "le champ " + name + " doit etre un nombre à virgule",this.value);
        this.add(tv);
        
        Constraint pnv = new PositiveDouble( "le champ " + name + " doit etre un nombre à virgule positif" ,this.value );
        this.add(tv);
    }
}
