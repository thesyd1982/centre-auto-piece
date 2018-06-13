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
public class ChampLieuSociete extends Champ{
    
    public ChampLieuSociete(String value) {
        super("java.lang.String","Lieu", value);
        MaxLengthValidator ml = new MaxLengthValidator( "Le champ "+name+" ne doit pas depasser les 250 caractères" ,250);
        Constraint ne = new NotEmpty("le champ " + name + " est obligatoire");
        this.add(ne);
        this.add(ml);
    }
    
}
