/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.dao;


import facturation.entities.Client;
import facturation.entities.Facture;
import facturation.entities.FactureParticulier;
import facturation.entities.FactureProfessionnel;
import facturation.entities.Ligne;
import facturation.entities.Particulier;
import facturation.entities.Piece;
import facturation.entities.Professionnel;

import java.util.Date;
import java.util.List;



/**
 *
 * @author tayeb
 */
public interface IFacturationDao {
    
    public Client addClient(Client client);
    public Client consulterClient(Long id); 
    public void removeClient(Long id);
   
    

    public Facture addFacture(Facture facture);
    public  void removeFacture(Long id_facture);
    public Facture consulterFacture(Long id_facture);
    public Facture findFactureByNumFacture(Long num_facture);
    public Facture clonnerFacture(Long id_facture); 
    
    public FactureProfessionnel addFactureProfessionnel(FactureProfessionnel facture);
    public FactureParticulier addFactureParticulier(FactureParticulier facture);
    public Facture consulterByNumeroFacture(Long numero_facture,Date date_facturation);
    
  /*  public Double consulterCaProfessionnelFacture();
    public Double consulterCaParticulierFacture();
    public Double consulterCaFacture();*/
    
    public Ligne addLigne(Ligne ligne,Long id_facture);
    public  void removeLigne(Long id_ligne);
    public Ligne consulterLigne(Long id_ligne); 

    public List<Facture> consulterFactureByClient(Long id_client);

    public List<Ligne> consulterLigneByPiece(Long id_piece);
    Long lastFactureId();
    
    Long lastNumeroFacture(String annee);
    
 
    public boolean isNumFactureUniqueInMonth(Long num_fac, Date date_fac );
    
    
    public Piece addPiece(Piece p);
    public void removePiece(Long id_piece);
    public Piece consulterPiece(Long id_piece) ;
   
    public List<Piece> findAllPiece();

    public List<Piece> findByReference(String reference);
    public Long updatePiece(Piece p);

    public Professionnel addProfessionnel(Professionnel professionnel);

    public Long updateProfessionnel(Professionnel p);
    public Professionnel consulterProfessionnel(Long id);

    public Particulier addParticulie(Particulier particulie);

    public Particulier consulterParticulie(Long id);

    public Long updateParticulie(Particulier p);

    public List<Professionnel> consulterProfessionnelByNomSociete(String nomSociete);

    public List<Professionnel> consulterProfessionnelBySiret(String siret);
    public FactureProfessionnel consulterFactureProfessionnel(Long id_facture);
    public FactureParticulier consulterFactureParticulier(Long id_facture);
    
    public List<Facture> findAllFacture();
    public List<FactureProfessionnel> findAllFactureProfessionnel();
    public List<FactureParticulier> findAllFactureParticulier();
    
}
