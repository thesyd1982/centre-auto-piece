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
@DiscriminatorValue("PRO")
public class Professionnel extends Client implements Serializable {

    
    
   
    protected String nomSociete;
    private String nom;
    
    public Professionnel() {
    }
    public Professionnel( String nomSociete) {
      this.nomSociete = nomSociete;
    }
   

    public Professionnel(Long id, String nom, String nomSociete, Adresse adresse) {
        super(id,adresse);
       this.nomSociete = nomSociete;
        this.nomSociete = nom;
    }
    
     public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
 

    public String getNomSociete() {
        return nomSociete;
    }

    public void setNomSociete(String nomSociete) {
        this.nomSociete = nomSociete;
    }

   

    @Override
    public String toString() {
        return "Professionnel{" + ", nomSociete=" + nomSociete + " " +this.adresse.toString()+ '}';
    }

    @Override
    public Adresse getAdresse() {
        return adresse;
    }

    @Override
    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.nomSociete);
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
        final Professionnel other = (Professionnel) obj;
        if (!Objects.equals(this.nomSociete, other.nomSociete)) {
            return false;
        }
        return true;
    }

   

   
    
}
