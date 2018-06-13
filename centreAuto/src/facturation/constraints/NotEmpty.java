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
public class NotEmpty extends Constraint{

    public NotEmpty(String erreurMessage) {
        super(erreurMessage);
    }

    @Override
    public boolean isValid(String value) {
        
        
        return (value != null && !value.isEmpty()) ;
    }
       
}
