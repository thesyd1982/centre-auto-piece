/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.formulaires;

import facturation.constraints.Constraint;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tayeb
 */
public abstract class Champ {
    
    protected String type;
    protected String name;
    protected String value;
    
    protected List<Constraint> constraints;

    public Champ(String type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.constraints = new ArrayList<>() ;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public void add(Constraint v){
        this.constraints.add(v);
    }
    public String validate()
    {
        for(Constraint v : constraints)
        {
            if(!v.isValid(value))
                return v.getErreurMessage();
        }
        return "";
    }
    
}
