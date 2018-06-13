/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.formulaires;

import facturation.constraints.MaxLengthValidator;

/**
 *
 * @author tayeb
 */
public class ChampMarque extends Champ{
     
    public ChampMarque(String value) {
        super("java.lang.String","Marque", value);
        MaxLengthValidator ml = new MaxLengthValidator( "Le champ "+name+" doit est trop long" , 100);
        
        this.add(ml);
    }

}
