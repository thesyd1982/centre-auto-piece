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
public abstract class Constraint {
    
    protected String erreurMessage;
    public abstract boolean isValid(String value);

    public Constraint(String erreurMessage) {
        this.erreurMessage = erreurMessage;
    }
   
    public String getErreurMessage() {
        return erreurMessage;
    }

    public void setErreurMessage(String erreurMessage) {
        this.erreurMessage = erreurMessage;
    }
   
}
