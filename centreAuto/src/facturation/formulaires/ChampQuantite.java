/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.formulaires;

import facturation.constraints.NotEmpty;
import facturation.constraints.TypeInteger;
import facturation.constraints.Constraint;
import facturation.constraints.PostiveInteger;

/**
 *
 * @author tayeb
 */
public class ChampQuantite extends Champ {
    
    public ChampQuantite(String value) {
        super("java.lang.Integer","quantite", value);
        
        Constraint ne = new NotEmpty("le champ " + name + " est obligatoire");
        this.add(ne);
        
        Constraint tv = new TypeInteger( "le champ " + name + " doit etre un nombre entiter" );
        this.add(tv);
        
        Constraint pI = new PostiveInteger( "le champ " + name + " doit etre un nombre entiter positif." );
        this.add(pI);
    }
    
     
}
