/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.hydrators;


import facturation.entities.Piece;

/**
 *
 * @author tayeb
 */
public class HydratorFormulairePiece implements Hydratation{
    
    String reference ,designation ,marque ;
    Double prixAchat,prixVente;
    Integer quantite;
    
    Piece piece = new Piece() ;

    public HydratorFormulairePiece(String reference, String designation, String marque, Double prixAchat, Double prixVente, Integer quantite) {
        this.reference = reference;
        this.designation = designation;
        this.marque = marque;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.quantite = quantite;
    }
    

    @Override
    public void hydrate() {
            
            this.piece.setReference(reference);
            this.piece.setDesignation(designation);
            this.piece.setMarque(marque);
            this.piece.setQuantite(quantite);
            this.piece.setPrixAchat(prixAchat);
            this.piece.setPrixVente(prixVente);
    }

   
}
