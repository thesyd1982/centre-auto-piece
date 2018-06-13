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
public class MaxLengthValidator extends Constraint {
    protected  int lenght;

    public MaxLengthValidator( String erreurMessage, int lenght) {
        super(erreurMessage);
        this.lenght = lenght;
    }
    
    @Override
    public boolean isValid(String value) {
        return value.length() <=this.lenght; 
    }
    
}
