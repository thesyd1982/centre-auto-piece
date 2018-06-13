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
public class ChampNomSociete extends Champ{
    
    public ChampNomSociete(String value) {
        super("java.lang.String","nom de la société", value);
        MaxLengthValidator ml = new MaxLengthValidator( "Le "+name+" ne doit pas depasser les 250 caractères" ,250);
        Constraint ne = new NotEmpty("le " + name + " est obligatoire");
        this.add(ne);
        this.add(ml);
    }

    
}
