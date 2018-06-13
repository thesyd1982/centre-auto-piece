/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.hydrators;



/**
 *
 * @author tayeb
 */
public abstract class Hydrator {
    
    protected Hydratation strategie;
    
    public Hydrator(){
            
    }

    public Hydratation getStrategie() {
        return strategie;
    }

    public void setStrategie(Hydratation strategie) {
        this.strategie = strategie;
    }
    
        
    
    
    
    
    
   
}
