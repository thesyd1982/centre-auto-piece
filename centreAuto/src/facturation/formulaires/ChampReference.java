/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.formulaires;

import facturation.constraints.MaxLengthValidator;
import facturation.constraints.NotEmpty;

/**
 *
 * @author tayeb
 */
public class ChampReference extends Champ{
    
    public ChampReference(String value) {
       
        super("java.lang.String","référence", value);
        MaxLengthValidator ml = new MaxLengthValidator( "Le champ "+name+" doit est trop long" , 50);
        NotEmpty ne = new NotEmpty("Le champ "+name+" ne peut être vide");
        this.add(ne);
        this.add(ml);
       
    }
    
}
