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
public class TypeDouble extends Constraint{
    
    protected Double number;

    public TypeDouble(String erreurMessage ,String value ) {
        super(erreurMessage);
        
    }
    
    @Override
    public boolean isValid(String value) {
        boolean result =false;
        try {
            
              this.number = Double.parseDouble(value);
              result =true;
            } 
        catch(NumberFormatException e) 
            {
               erreurMessage="ce nombre doits etre un nombre à virgule";
            }
       
        return result;
       
    }
    
}
