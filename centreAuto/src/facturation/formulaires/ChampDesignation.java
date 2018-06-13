/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.formulaires;

import facturation.constraints.NotEmpty;
import facturation.constraints.Constraint;

/**
 *
 * @author tayeb
 */
public class ChampDesignation extends Champ{
   
     public ChampDesignation (String value) {
        
         super("String","designation", value);
        
        Constraint ne = new NotEmpty("le champ " + name + " est obligatoire");
        this.add(ne);
    }
}
