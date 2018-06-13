/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.service;

import facturation.entities.Client;
import facturation.entities.Facture;
import facturation.entities.FactureParticulier;
import facturation.entities.FactureProfessionnel;
import facturation.entities.Ligne;
import facturation.entities.Particulier;
import facturation.entities.Piece;
import facturation.entities.Professionnel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author tayeb
 */
public interface IFacturationService {
    public Client addClient(Client client);
    public void removeClient(Long id_client);
    
    public Client consulterClient(Long id_client);
    public Professionnel addProfessionnel(Professionnel professionnel); 
    public Professionnel consulterProfessionnel(Long id);
    public Long updateProfessionnel(Professionnel p);
    
    public List<Professionnel> consulterProfessionnelByNomSociete(String nomSociete);
    public List<Professionnel> consulterProfessionnelByNom(String nom);
    
    public Particulier addParticulie(Particulier p); 
    public Particulier consulterParticulie(Long id);
    public Long updateParticulie(Particulier p);
    
    public void removeParticulie(Long id);
    public void removeProfessionnel(Long id);
    
    public Piece addPiece(Piece piece );
    public void removePiece(Long id_piece);
    // return 0 si la piece n'est pas trouver sinon son identifiant ;
    public Long updatePiece(Piece p);
    
    public Piece consulterPiece(Long id_piece);
    public List<Piece> findByReference(String reference);
    
    public List<Piece> findAllPiece(); 
    
    public Facture creerFacture(Long numero_facture, 
                                Client c , 
                                Date facturation , 
                                Date livraison ,
                                String modePayement,
                                ArrayList<Ligne> lignes);
    
    
    
    public Facture addFacture(Facture facture);
    public  void removeFacture(Long id_facture);
    
    public Facture consulterFacture(Long id_facture);
    
    public Ligne addLigne(Ligne ligne,Long id_facture);
    public  void removeLigne(Long id_ligne);
    public Ligne consulterLigne(Long id_ligne);
    
    
    public void addLigneToFacture(Long id_facture,Long id_ligne);
    
    public List<Facture> consulterFactureByClient(Long id_client);
    public List<Ligne> consulterLigneByPiece(Long id_piece);
    
    public void supprimerPieceFactures(Long id_piece);
    public void supprimerClientFactures(Long id_client);
    public Long lastFactureId();
    public Long lastNumeroFacture(String annee);
    public Facture consulterByNumeroFacture(Long numero_facture,Date date_facturation);
    public boolean isNumFactureUnique(Long numero_facture,Date date_facturation);
    
  
    
    public void deduireStock(Piece p, int quantite);
    public void ajouterQuantiePiece(Piece p, double quantite);
    
    public FactureParticulier addFactureParticulier(FactureParticulier facture) ;
    public FactureProfessionnel addFactureProfessionnel(FactureProfessionnel facture);
    
    public FactureParticulier consulterFactureParticulierByNumFacture(Long num_facture);
    public FactureProfessionnel consulterFactureProfessionnelByNumFacture(Long num_facture);
    public Facture consulterByNumFacture(Long num_facture);
    
    public FactureParticulier consulterFactureParticulier(Long id_facture);
    public FactureProfessionnel consulterFactureProfessionnel(Long id_facture);
    
    public Long convertNumFactureToIdFacture(Long num_facture);
    public Double valeurStockRestant();
    
    public Double consulterCaProfessionnel();
}
