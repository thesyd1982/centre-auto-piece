/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.entities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 *
 * @author tayeb
 */
@Entity
@Table(name = "PIECE")
public class Piece implements Serializable {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    @Transient
    protected Double cout;
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID_PIECE")
    protected Long id_piece;
    
    @Basic(optional = false)
    @Column(name = "REFERENCE",length=50,nullable=false)
    protected String reference;
    
    @Basic(optional = true)
    @Column(name = "DESIGNATION",nullable=false ,columnDefinition="varchar(255) default ''")
    protected String designation;
    
    @Basic(optional = true)
    @Column(name = "MARQUE",length=200)
    protected String marque;
    @Column(name = "PRIX_ACHAT",columnDefinition="Decimal(10,2) default '0.00'" )
   
    protected Double prixAchat;
    @Column(name = "PRIX_VENTE",columnDefinition="Decimal(10,2) default '0.00'" )
    
    protected Double prixVente;
    
    @Column(name = "QUANTITE",columnDefinition="int(6) default '0'" )
    protected Integer quantite;
    
    public Piece() {
        this.reference = "";
        this.marque = "";
        this.designation = "";
        this.prixAchat = 0.0;
        this.prixVente = 0.0;
        this.quantite = 0;
    }
    
    public Piece(Long id_piece ,String reference, 
                 String marque,
                 String designation,
                 Double prixAchat, 
                 Double prixVente, 
                 Integer quantite) {
        this.id_piece=id_piece;
        this.reference = reference;
        this.marque = marque;
        this.designation = designation;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.quantite = quantite;
       
      
    }

   
    
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        String oldReference = this.reference;
        this.reference = reference;
        changeSupport.firePropertyChange("reference", oldReference, reference);
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        String oldDesignation = this.designation;
        this.designation = designation;
        changeSupport.firePropertyChange("designation", oldDesignation, designation);
    }
    
    
    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        String oldMarque = this.marque;
        this.marque = marque;
        changeSupport.firePropertyChange("marque", oldMarque, marque);
    }

    public Double getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(Double prixAchat) {
        Double oldPrixAchat = this.prixAchat;
        this.prixAchat = prixAchat;
        changeSupport.firePropertyChange("prixAchat", oldPrixAchat, prixAchat);
    }

    public Double getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(Double prixVente) {
        Double oldPrixVente = this.prixVente;
        this.prixVente = prixVente;
        changeSupport.firePropertyChange("prixVente", oldPrixVente, prixVente);
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        Integer oldQuantite = this.quantite;
        this.quantite = quantite;
      
        changeSupport.firePropertyChange("quantite", oldQuantite, quantite);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public Long getId_piece() {
        return id_piece;
    }

    public void setId_piece(Long id_piece) {
        Long oldId_piece = this.id_piece;
        this.id_piece = id_piece;
        changeSupport.firePropertyChange("id_piece", oldId_piece, id_piece);
    }

    @Override
    public String toString() {
        return "Piece{ id_piece=" + id_piece + ", reference=" + reference + ", designation=" + designation + ", marque=" + marque + ", prixAchat=" + prixAchat + ", prixVente=" + prixVente + ", quantite=" + quantite + '}';
    }

    public Double getCout() {
        return this.prixAchat*this.quantite;
    }

    
    
    

}