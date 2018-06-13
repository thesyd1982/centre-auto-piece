/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 *
 * @author tayeb
 */
@Entity
public class Adresse implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   
    protected Long id_adresse;
    
    
    
    protected String lieu;
    protected String ville;
    protected String cp;

    public Adresse() {
    }

    
    
    
    public Adresse(String lieu, String ville, String cp) {
        this.lieu = lieu;
        this.ville = ville;
        this.cp = cp;
    }

    
    
    public Long getId_adresse() {
        return id_adresse;
    }

    public void setId_adresse(Long id_adresse) {
        this.id_adresse = id_adresse;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }
    
   

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }
    
    
   

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id_adresse != null ? id_adresse.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Adresse)) {
            return false;
        }
        Adresse other = (Adresse) object;
        if ((this.id_adresse == null && other.id_adresse != null) || (this.id_adresse != null && !this.id_adresse.equals(other.id_adresse))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Adresse{" + "lieu=" + lieu + ", ville=" + ville + ", cp=" + cp + '}';
    }

  
    
    
   
    
}
