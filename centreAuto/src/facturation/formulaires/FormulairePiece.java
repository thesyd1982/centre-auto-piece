/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.formulaires;

import facturation.entities.Piece;
import java.util.Locale;


/**
 *
 * @author tayeb
 */
public class FormulairePiece extends Formulaire{
    
    protected ChampReference champReference;
    protected ChampDesignation champDesignation;
    protected ChampMarque champMarque;
    protected ChampQuantite champQuantite;
    protected ChampPrix  champPrixVente; 
    protected ChampPrix  champPrixAchat;                     
    
    protected Piece piece;
    
    public FormulairePiece() 
    {
           super();
           piece = new Piece();
                 
    }
     
    public FormulairePiece(String reference,String designation,String marque,String quantite,String prixVente,String prixAchat) 
    {
            champReference = new ChampReference(reference);
            champDesignation = new ChampDesignation(designation);
            champMarque = new ChampMarque(marque);
            champQuantite = new ChampQuantite(quantite);
            champPrixVente = new ChampPrix(prixVente,"prix de vente");
            champPrixAchat = new ChampPrix(prixAchat,"prix de achat");
            
            this.champs.add(champReference);
            this.champs.add(champDesignation);
            this.champs.add(champMarque);
            this.champs.add(champQuantite);
            this.champs.add(champPrixVente);
            this.champs.add(champPrixAchat);
            piece = new Piece();
                 
    }

    public String getReference() {
        return champReference.getValue();
    }

    public void setReference(String reference) {
        this.champReference.setValue(reference);
    }

    public String getDesignation() {
        return champDesignation.getValue();
    }

    public void setDesignation(String designation) {
        this.champDesignation.setValue(designation);
    }

    public String getMarque() {
        return champMarque.getValue();
    }

    public void setMarque(String marque) {
        this.champMarque.setValue(marque); 
    }

    public String getQuantite() {
        return champQuantite.getValue();
    }

    public void setQuantite(String quantite) {
        this.champQuantite.setValue(quantite);
    }

    public String getPrixVente() {
        return champPrixVente.getValue();
    }

    public void setPrixVente(String prixVente) {
        this.champPrixVente.setValue(prixVente);
    }

    public String getPrixAchat() {
        return champPrixAchat.getValue();
    }

    public void setPrixAchat(String prixAchat) {
        this.champPrixAchat.setValue(prixAchat);
    }
    
    public String  hydrate()
    {
        String message= "";
        if(this.validate().isEmpty())
        {    
            this.piece.setReference(this.getReference().toUpperCase(Locale.FRANCE));
            this.piece.setDesignation(this.getDesignation().toUpperCase(Locale.FRANCE));
            this.piece.setMarque(this.getMarque().toUpperCase(Locale.FRANCE));
            this.piece.setQuantite(Integer.parseInt(this.getQuantite()));
            this.piece.setPrixAchat(Double.parseDouble(this.getPrixAchat()));
            this.piece.setPrixVente(Double.parseDouble(this.getPrixVente()));
        }
        else
        {
           message= this.validate();
        }
        return message;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
    
}
