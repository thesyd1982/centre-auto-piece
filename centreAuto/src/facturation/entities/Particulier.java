/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.entities;


import java.io.Serializable;
import java.util.Objects;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author tayeb
 */
@Entity
@DiscriminatorValue("PAR")
public class Particulier extends Client implements Serializable {

    private static final long serialVersionUID = 1L;
    
    
    protected String nom;
    protected String prenom;

    public Particulier() {
    }

    public Particulier(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    public Particulier(String nom, String prenom, Adresse adresse) {
        super(adresse);
        this.nom = nom;
        this.prenom = prenom;
    }
    public Particulier(Long id , String nom, String prenom, Adresse adresse) {
        super(id,adresse);
        this.nom = nom;
        this.prenom = prenom;
    }
    
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.nom);
        hash = 41 * hash + Objects.hashCode(this.prenom);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Particulier other = (Particulier) obj;
        if (!Objects.equals(this.nom, other.nom)) {
            return false;
        }
        if (!Objects.equals(this.prenom, other.prenom)) {
            return false;
        }
        return true;
    }
    
    
    
    @Override
    public String toString() {
        return "Particulie{" + "nom=" + nom + ", prenom=" + prenom + '}';
    }
    
    
    
    

  
    
    
}
