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
public class PositiveDouble extends Constraint {
    Double number; 

    public PositiveDouble( String erreurMessage, String value ) {
        super(erreurMessage);
      
    }
    

    @Override
    public boolean isValid(String value) {
        boolean result = false; 
        try{
            this.number = Double.parseDouble(value);
            result = this.number > 0.0;
            
        }
        catch( NumberFormatException e)
        {
            e.printStackTrace();
        }
        return result;
            
    }
    
}
