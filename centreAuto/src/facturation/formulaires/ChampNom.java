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
public class ChampNom extends Champ{
    public ChampNom(String value) {
        super("java.lang.String","nom du client", value);
        MaxLengthValidator ml = new MaxLengthValidator( "Le "+name+" ne doit pas depasser les 250 caractères" ,250);
        Constraint ne = new NotEmpty("le " + name + " est obligatoire");
        this.add(ne);
        this.add(ml);
    }
}
