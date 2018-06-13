/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.constraints;

/**
 *
 * @author tayeb
 */
public class TypeInteger extends Constraint{

   protected Integer number;

    public TypeInteger(String erreurMessage  ) {
        super(erreurMessage);
        
    }
    
    @Override
    public boolean isValid(String value) {
        boolean result =false;
        try {
            
              this.number = Integer.parseInt(value);
              result =true;
            } 
        catch(NumberFormatException e) 
            {
                erreurMessage = "ce nombre doits etre un nombre entier";
            }
       
        return result;
       
    }
}
