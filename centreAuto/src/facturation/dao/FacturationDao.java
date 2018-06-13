/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.dao;


import facturation.entities.Adresse;
import facturation.entities.Client;
import facturation.entities.Facture;
import facturation.entities.FactureParticulier;
import facturation.entities.FactureProfessionnel;
import facturation.entities.Ligne;
import facturation.entities.Particulier;
import facturation.entities.Piece;
import facturation.entities.Professionnel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author tayeb
 */
public class FacturationDao implements IFacturationDao{
 @PersistenceContext
 protected EntityManager em ;
     
    public FacturationDao(EntityManager em)
    {
       this.em =em ;
        
    }
    @Override
    public Client addClient(Client client) {
            em.getTransaction().begin();
            em.persist(client);
            em.getTransaction().commit();
        return client;
    }
    
    @Override
    public Client consulterClient(Long id_client) {
           Client c = em.find(Client.class, id_client);
           return c;
    }

    @Override
    public void removeClient(Long id) {
         Client c = this.consulterClient(id);
                em.getTransaction().begin();
                if(c!=null)
                {
                    em.remove(c);
                    em.getTransaction().commit(); 
                }
                else
                {
                   em.getTransaction().rollback();  
                }
                
    }
   
    

   
    @Override
    public Facture addFacture(Facture facture) 
    {
            em.getTransaction().begin();
            em.persist(facture);
            em.getTransaction().commit();
       return facture;    
    
    }
     @Override
    public FactureProfessionnel addFactureProfessionnel (FactureProfessionnel facture) 
    {
            em.getTransaction().begin();
            em.persist(facture);
            em.getTransaction().commit();
       return facture;    
    
    }
    @Override
    public FactureParticulier addFactureParticulier(FactureParticulier facture) 
    {
          em.getTransaction().begin();
          em.persist(facture);
          em.getTransaction().commit();
       return facture;    
    
    }
    
    @Override
    public void removeFacture(Long id_facture) {
        
 
       Facture f = consulterFacture(id_facture);
       em.getTransaction().begin();
       for (Ligne ligne : f.getLignes()) 
       {
           em.remove(ligne);
       }
       
       em.remove(f);
       em.getTransaction().commit();     
    }

    @Override
    public Facture consulterFacture(Long id_facture) {
        Facture f = em.find(Facture.class, id_facture);
           if(f==null)
           {
                throw new RuntimeException("Facture introuvable");
            }
        return f;
    }

    @Override
    public Ligne addLigne(Ligne ligne,Long id_facture) {
            Facture f = consulterFacture(id_facture);
            f.getLignes().add(ligne);
            ligne.setFacture(f);
            em.getTransaction().begin(); 
            em.persist(ligne);
            em.getTransaction().commit();
       return ligne;   
    }

    @Override
    public void removeLigne(Long id_ligne) {
     Ligne ligne = this.consulterLigne(id_ligne);
     if(ligne!=null)
     {
         em.remove(ligne);
     
     }
    }

    @Override
    public Ligne consulterLigne(Long id_ligne) {
        Ligne l  = em.find(Ligne.class, id_ligne);
        return l;
    }

    @Override
    public List<Facture> consulterFactureByClient(Long id_client) {
      Client c = consulterClient(id_client);
      Query query = em.createQuery( "SELECT f from Facture f INNER JOIN Client c where c.id_client=:id_client",Facture.class);
      query.setParameter("id_client", id_client);
      return query.getResultList();
    }

    @Override
    public List<Ligne> consulterLigneByPiece(Long id_piece) {
     
     Piece p = consulterPiece(id_piece);
     Query query = em.createQuery( "SELECT l from Ligne l where l.piece = :p",Ligne.class); 
     query.setParameter("p", p);
     return query.getResultList(); 
     
    }
    
    @Override
    public Long lastFactureId()
    {
        Query query = em.createQuery( "select max(f.id_facture) from Facture f",Facture.class);
        Long id_facture = (Long) query.getSingleResult();
        return id_facture;
    }

    @Override
    public Long lastNumeroFacture(String annee) {
        Query query = em.createQuery( "select max(f.numero_facture) from Facture f where f.date_facturation LIKE '%/"+annee+"'",Facture.class);
       
        Long last_num_facture = (Long) query.getSingleResult();
        return last_num_facture;
    }

   

   
   

    @Override
    public Facture consulterByNumeroFacture(Long numero_facture,Date date_facturation) 
    {
        Facture f =null; 
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        String mois = dateFormatter.format(date_facturation);
        
        
        Query query = em.createQuery( "select f from Facture f where (f.date_facturation LIKE(:mois)) and (f.numero_facture =:numero_facture)" ,Facture.class); 
        query.setParameter("mois",mois);
        query.setParameter("numero_facture",numero_facture);
        
        try 
        {
            f = (Facture) query.getSingleResult();
           
        } 
        catch(javax.persistence.NoResultException e)
        {
            System.err.println("pas de facture ce mois la avec ce numero");
        } 
       
       
        return f ;
    }
    
    @Override
    public boolean isNumFactureUniqueInMonth(Long num_fac, Date date_fac )
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/yyyy");
        String mois = "%/"+dateFormatter.format(date_fac);
        
        
        List<Facture> factures_mois = null;
        Query query = em.createQuery( "select f from Facture f where (f.date_facturation LIKE(:mois)) and (f.numero_facture =:num_fac)" ,Facture.class); 
        query.setParameter("mois",mois);
        query.setParameter("num_fac",num_fac);
        factures_mois  = query.getResultList();
        
       
       
        return factures_mois.size() == 0;
        
    }
    
    
    
   

    @Override
    public Facture clonnerFacture(Long id_facture) 
    {
        Facture f = consulterFacture(id_facture);
        Facture clonne = new Facture();
        clonne.setClient(f.getClient());
        clonne.setDate_facturation(f.getDate_facturation());
        clonne.setLignes(f.getLignes());
        clonne.setDate_livraison(f.getDate_livraison());
        clonne.setMode_payement(f.getMode_payement() );
        
        return clonne;
    }

    @Override
    public Facture findFactureByNumFacture(Long num_facture) 
    {
       Facture f =null;
       Query query = em.createQuery( "select f from Facture f where f.numero_facture = :num_facture" ,Facture.class);
       query.setParameter("num_facture",num_facture);
       
       try {
                f= (Facture) query.getSingleResult();
            }
       catch (javax.persistence.NoResultException e)
            {
            
            }
          return f;
    }

 
    @Override
    public Piece addPiece(Piece p) {
            em.getTransaction().begin(); 
            em.persist(p);
            em.getTransaction().commit();
       return p;    
    }

    @Override
    public void removePiece(Long id_piece) {
            Piece p = consulterPiece( id_piece);
                em.getTransaction().begin();
                em.remove(p);
                em.getTransaction().commit();    
    }

    @Override
    public Piece consulterPiece(Long id_piece) {
            em.getTransaction().begin();
            Piece p = em.find(Piece.class, id_piece);
             if(p==null)
            {
                throw new RuntimeException("Produit introuvable");
            }
             em.getTransaction().commit();
        return p ;
    }
    @Override
    public List<Piece> findAllPiece() {
            Query query = em.createQuery( "SELECT p FROM Piece p");
            return (List<Piece>)query.getResultList();
    
    }
    
 @Override
    public List<Piece> findByReference(String reference){
        Query query = em.createQuery( "SELECT p FROM Piece p WHERE  p.reference LIKE :reference ");
        query.setParameter("reference",reference);
        return (List<Piece>)query.getResultList();
    }

    @Override
    public Long updatePiece(Piece p) {
        Long result = 0L;
        Piece old = this.consulterPiece(p.getId_piece());
        em.getTransaction().begin();
       
        if(old != null)
        {   
            result = p.getId_piece();
            
            em.merge(p);
            em.getTransaction().commit();
             
        }
        else
        { 
          em.getTransaction().rollback();  
        }
        
       return result ;
    }

    @Override
    public Professionnel addProfessionnel(Professionnel p) {
            em.getTransaction().begin(); 
            em.persist(p);
            em.getTransaction().commit();
       return p;    
    }
    
    @Override
    public Long updateProfessionnel(Professionnel p) {
        Long result = 0L;
        Professionnel old = this.consulterProfessionnel(p.getId());
        em.getTransaction().begin();
       
        if(old != null)
        {   
            result = p.getId();
            
            em.merge(p);
            em.getTransaction().commit();
             
        }
        else
        { 
          em.getTransaction().rollback();  
        }
        
       return result ;
    }

 @Override
    public  Professionnel consulterProfessionnel(Long id) {
         Professionnel p = em.find(Professionnel.class, id);
           if( p ==null)
           {
                throw new RuntimeException("Client introuvable");
            }
        return p;
    }

    @Override
    public Particulier addParticulie(Particulier p) {
         em.getTransaction().begin(); 
            em.persist(p);
            em.getTransaction().commit();
       return p;    
    }

    @Override
    public Particulier consulterParticulie(Long id) {
         Particulier p = em.find(Particulier.class, id);
           if( p ==null)
           {
                throw new RuntimeException("Client introuvable");
            }
        return p;
    }

    @Override
    public Long updateParticulie(Particulier p) {
        Long result = 0L;
        Particulier old = this.consulterParticulie(p.getId());
        Adresse newAdresse = p.getAdresse();
        Adresse oldAdresse  = old.getAdresse();
        
        em.getTransaction().begin();
       
        if(old != null)
        {   
            result = p.getId();
            em.persist(newAdresse);
            //em.remove(oldAdresse);
            em.merge(p);
            em.getTransaction().commit();
             
        }
        else
        { 
          em.getTransaction().rollback();  
        }
        
       return result ;
    }

    @Override
    public List<Professionnel> consulterProfessionnelByNomSociete(String nomSociete) {
        nomSociete = "%"+nomSociete+"%";
        Query query = em.createQuery( "SELECT p FROM Professionnel p WHERE  p.nomSociete LIKE :nomSociete");
        query.setParameter("nomSociete",nomSociete);
       
        return (List<Professionnel>)query.getResultList();
    }
    @Override
    public List<Professionnel> consulterProfessionnelBySiret(String siret) {
        siret = "%"+siret+"%";
        Query query = em.createQuery( "SELECT p FROM Professionnel p WHERE  p.siret LIKE :siret");
        query.setParameter("siret",siret);
        return (List<Professionnel>)query.getResultList();
    }

    @Override
    public FactureProfessionnel consulterFactureProfessionnel(Long id_facture) {
           FactureProfessionnel f = em.find(FactureProfessionnel.class, id_facture);
           if(f==null)
           {
                throw new RuntimeException("Facture Pros introuvable");
            }
        return f;
    }

    @Override
    public FactureParticulier consulterFactureParticulier(Long id_facture) {
       FactureParticulier f = em.find(FactureParticulier.class, id_facture);
           if(f==null)
           {
                throw new RuntimeException("Facture du particulier introuvable");
            }
        return f;
    }

    @Override
    public List<Facture> findAllFacture() {
        Query query = em.createQuery( "select f from Facture f" ,Facture.class);
        return query.getResultList();
    }

    @Override
    public List<FactureProfessionnel> findAllFactureProfessionnel() {
        Query query = em.createQuery( "select f from FactureProfessionnel f" ,FactureProfessionnel.class);
      return query.getResultList();
    }

    @Override
    public List<FactureParticulier> findAllFactureParticulier() {
        Query query = em.createQuery( "select f from FactureProfessionnel f" ,FactureProfessionnel.class);
      return query.getResultList();
    }
   
    
}
