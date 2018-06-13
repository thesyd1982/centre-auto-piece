/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.service;

import facturation.dao.FacturationDao;
import facturation.dao.IFacturationDao;
import facturation.entities.Client;
import facturation.entities.Facture;
import facturation.entities.FactureParticulier;
import facturation.entities.FactureProfessionnel;
import facturation.entities.Ligne;
import facturation.entities.Particulier;
import facturation.entities.Piece;
import facturation.entities.Professionnel;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author tayeb
 */
public class FacturationService implements IFacturationService{
    protected  IFacturationDao dao;
    @Override
    public Client addClient(Client client) {
        return this.dao.addClient(client);
    }

    @Override
    public void removeClient(Long id_client) {
                this.dao.removeClient(id_client);    
    }

    @Override
    public Client consulterClient(Long id_client) {
       return this.dao.consulterClient(id_client);
    }

    @Override
    public Piece addPiece(Piece piece) {
         return this.dao.addPiece(piece) ;
      
         
    }

    @Override
    public void removePiece(Long id_piece) {
        Piece piece = this.consulterPiece(id_piece);
        Integer quantite = piece.getQuantite();
        
        if(consulterLigneByPiece(id_piece).isEmpty())
        {
            
            this.dao.removePiece(id_piece);
        }
        else
        {
            this.deduireStock(piece, quantite);
        }
    }

    @Override
    public Piece consulterPiece(Long id_piece) {
        return this.dao.consulterPiece(id_piece);
    }
    
    @Override
    public List<Piece> findAllPiece(){
    return this.dao.findAllPiece();
    } 
    @Override
    public Facture addFacture(Facture facture) {
        
        return this.dao.addFacture(facture);
               
    }
    
    @Override
    public FactureParticulier addFactureParticulier(FactureParticulier facture) {
       
           
        return this.dao.addFactureParticulier(facture);
    
    }
    
    @Override
    public FactureProfessionnel addFactureProfessionnel(FactureProfessionnel facture) {
        
        return  this.dao.addFactureProfessionnel(facture);
    
    }
    @Override
    public void removeFacture(Long id_facture) {
        dao.removeFacture(id_facture);
    }

    @Override
    public Facture consulterFacture(Long id_facture) {
    return this.dao.consulterFacture(id_facture);
    }

    @Override
    public Ligne addLigne(Ligne ligne,Long id_facture) {
        
        return this.dao.addLigne(ligne, id_facture);
    }

    @Override
    public void removeLigne(Long id_ligne) {
        this.dao.removeLigne(id_ligne);
    }

    @Override
    public Ligne consulterLigne(Long id_ligne) {
        return dao.consulterLigne(id_ligne);
    }

    public void setDao(FacturationDao dao) {
        this.dao = dao;
    }

    @Override
    public void addLigneToFacture(Long id_facture, Long id_ligne) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deduireStock(Piece p, int quantite) {
        
         int quantiteStock = p.getQuantite();
        
            int q = quantiteStock-quantite;
            p.setQuantite(q);
            this.updatePiece(p);
            
       
       
    }
    @Override
    public void ajouterQuantiePiece(Piece p, double quantite) 
    {
        
        double quantiteEnStock = p.getQuantite();
        double q = quantiteEnStock + quantite;
         p.setQuantite((int) q);
        this.addPiece(p);
   
    }
    
    
    
    
    // suprime les facture d'un client 
    @Override
    public void supprimerClientFactures(Long id_client){
        List<Facture> factures =  dao.consulterFactureByClient(id_client);
        if(!factures.isEmpty())
        {
            for (Facture facture : factures) {
                dao.removeFacture(facture.getId_facture());
                
            }
        }
    
    }

    @Override
    public List<Facture> consulterFactureByClient(Long id_client) {
        return dao.consulterFactureByClient(id_client);
    
    }

    @Override
    public void supprimerPieceFactures(Long id_piece) {
        /* List<Ligne> lignes =  dao.consulterLigneByPiece(id_piece); 
         if(!lignes.isEmpty())
        {
            for (Facture facture : factures) {
                dao.removeFacture(facture.getId_facture());
                
            }
        }*/
    }

   

    @Override
    public Long lastFactureId() {
        return dao.lastFactureId(); 
    }

    @Override
    public Long lastNumeroFacture(String annee) 
    {
        return dao.lastNumeroFacture(annee);
    }

    

    @Override
    public Facture consulterByNumeroFacture(Long numero_facture ,Date date_facturation) {
        return dao.consulterByNumeroFacture(numero_facture,date_facturation);
    }
    
    @Override
    public Facture creerFacture(
             Long numero_facture, 
             Client c , 
             Date facturation , 
             Date livraison ,
             String modePayement,
            ArrayList<Ligne> lignes)
        {
            Facture f = new Facture();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("d/M/y");
            f.setMode_payement(modePayement);
        
            String date_facturation=dateFormatter.format(facturation);
            String date_Livraison=dateFormatter.format(livraison);
        
            f.setClient(c);
            f.setDate_facturation(date_facturation);
            f.setDate_livraison(date_Livraison);
            f.setMode_payement(modePayement);
          //  f.setNumero_facture(numero_facture);
                
        this.addFacture(f);

        for (Ligne ligne : lignes) 
        {
            this.addLigne(ligne,f.getId_facture());
            this.deduireStock(ligne.getPiece(),ligne.getQuantite());
        }
        return f;
    } 

    @Override
    public boolean isNumFactureUnique(Long numero_facture, Date date_facturation) {
       return dao.isNumFactureUniqueInMonth(numero_facture, date_facturation);
    }

    @Override
    public List<Piece> findByReference(String reference) {
     return dao.findByReference(reference);
    }

    @Override
    public Long updatePiece(Piece p) {
        return dao.updatePiece(p);
    }

    @Override
    public Professionnel addProfessionnel(Professionnel professionnel) {
        return this.dao.addProfessionnel(professionnel);
    }
   
    @Override
    public Long updateProfessionnel(Professionnel p) {
        return dao.updateProfessionnel(p);
    }

    @Override
    public Professionnel consulterProfessionnel(Long id) {
        return dao.consulterProfessionnel(id);
    }

    @Override
    public Particulier addParticulie(Particulier particulie) {
        return dao.addParticulie(particulie);
    }

    @Override
    public Particulier consulterParticulie(Long id) {
        return dao.consulterParticulie(id);
    }

    @Override
    public Long updateParticulie(Particulier p) {
        return dao.updateParticulie(p);
    }

    @Override
    public void removeParticulie(Long id) {
         dao.removeClient(id);
    }

    @Override
    public void removeProfessionnel(Long id){
        dao.removeClient(id);
    }

    @Override
    public List<Professionnel> consulterProfessionnelByNomSociete(String nomSociete) {
        return dao.consulterProfessionnelByNomSociete(nomSociete);
    }

    @Override
    public List<Professionnel> consulterProfessionnelByNom(String nom) {
        return dao.consulterProfessionnelBySiret(nom);
    }

    @Override
    public List<Ligne> consulterLigneByPiece(Long id_piece) {
       return dao.consulterLigneByPiece(id_piece);
    }

    @Override
    public FactureParticulier consulterFactureParticulierByNumFacture(Long num_facture) {
         Long id_facture = this.convertNumFactureToIdFacture(num_facture);  
     return this.consulterFactureParticulier(id_facture);   

    }

    @Override
    public FactureProfessionnel consulterFactureProfessionnelByNumFacture(Long num_facture) {
        Long id_facture = this.convertNumFactureToIdFacture(num_facture);
        return this.consulterFactureProfessionnel(id_facture);
    }

    @Override
    public Facture consulterByNumFacture(Long num_facture) {
       Long id_facture = this.convertNumFactureToIdFacture(num_facture);
       return this.consulterFacture(id_facture);
    }

    @Override
    public FactureParticulier consulterFactureParticulier(Long id_facture) {
        return dao.consulterFactureParticulier(id_facture); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long convertNumFactureToIdFacture(Long num_facture){
         String id = num_facture.toString();
        int length = id.length();
        int indexFirstChar = length-8;
        
        id = id.substring(0,indexFirstChar);
        
        Long id_facture= Long.parseLong(id);
        
        
        return id_facture;
    }

    @Override
    public FactureProfessionnel consulterFactureProfessionnel(Long id_facture) {
        return dao.consulterFactureProfessionnel(id_facture);
    }

    @Override
    public Double valeurStockRestant() {
     List<Piece> pieces = this.findAllPiece();
     Double valeur = 0.0 ; 
     for(Piece piece : pieces)
     {
         valeur = valeur +piece.getCout();
         
     }
     
     return valeur;
    }

    @Override
    public Double consulterCaProfessionnel() {
        
        List<FactureProfessionnel> factures = this.dao.findAllFactureProfessionnel();
        Double ca = 0.0;
        for(Facture f : factures)
            ca = ca +f.getTotalHT();
        return ca;
    }

   
  

    
    
    
    
}
