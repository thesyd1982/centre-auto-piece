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
public class PostiveInteger extends Constraint {

    private Integer number = 0 ;
    

    public PostiveInteger(String erreurMessage) {
        super(erreurMessage);

    }

    @Override
    public boolean isValid(String value) {
         boolean result = false; 
        try{
             this.number = Integer.parseInt(value);
             result = this.number > 0;
            
        }
        catch( NumberFormatException e)
        {
            e.printStackTrace();
        }
        return result;
    }
   
    
}
