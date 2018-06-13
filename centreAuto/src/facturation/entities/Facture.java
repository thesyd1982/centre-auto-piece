/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.entities;

import facturation.enumeration.Mois;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 *
 * @author tayeb
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name ="FACTURE_TYPE",discriminatorType= DiscriminatorType.STRING,length=3 )



public class Facture implements Serializable {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    protected Long id_facture;
    
    /*protected Long numero_facture;*/
    
    
    @OneToOne
    @JoinColumn(name ="ID_CLIENT")
    protected Client client;
    
    @OneToMany(mappedBy="facture",cascade = {CascadeType.ALL},fetch=FetchType.EAGER)
    protected List<Ligne> lignes = lignes = new ArrayList<>(); 
    
    protected String date_facturation;
    protected String mode_payement;
    protected String date_livraison;
    protected String immatriculation;

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public void setChangeSupport(PropertyChangeSupport changeSupport) {
        this.changeSupport = changeSupport;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }
    
    public Facture() {
       
    }
    
    public Facture(Client c,String date ,Ligne l , String immatriculation ) {
        this.client = c;
        this.date_facturation = date;
        //this.numero_facture = this.genererNumeroFacture(this.id_facture,date);
        lignes.add(l);
        this.immatriculation = immatriculation;
        
    }
    

    public Long getId_facture() {
        return id_facture;
    }

    public void setId_facture(Long id_facture) {
        Long oldId_facture = this.id_facture;
        this.id_facture = id_facture;
        changeSupport.firePropertyChange("id_facture", oldId_facture, id_facture);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Ligne> getLignes() {
        return lignes;
    }

    public void setLignes(List<Ligne> lignes) {
        this.lignes = lignes;
    }

    public String getDate_facturation() {
        return date_facturation;
    }

    public void setDate_facturation(String date_facturation) {
        String oldDate_facturation = this.date_facturation;
        this.date_facturation = date_facturation;
        changeSupport.firePropertyChange("date_facturation", oldDate_facturation, date_facturation);
    }
    
   

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
    

    public String getDate_livraison() {
        return date_livraison;
    }

    public void setDate_livraison(String date_livraison) {
        String oldDate_livraison = this.date_livraison;
        this.date_livraison = date_livraison;
        changeSupport.firePropertyChange("date_livraison", oldDate_livraison, date_livraison);
    }

    public Long getNumero_facture() {
        return genererNumeroFacture(this.id_facture,this.date_facturation);
    }

    

    public String getMode_payement() {
        return mode_payement;
    }

    public void setMode_payement(String mode_payement) {
        String oldMode_payement = this.mode_payement;
        this.mode_payement = mode_payement;
        changeSupport.firePropertyChange("mode_payement", oldMode_payement, mode_payement);
    }
    
    public double getTotalHT()
    {
        Double total =0.0;
         for (Ligne ligne : lignes) 
        {
            
            total+=ligne.getMontantHT();
            
         }
        return total;
    }
    
    public double getTotalTVA()
    {
        Double total =0.0;
        for(Ligne ligne : lignes) 
         { total+=ligne.getMontantTVA(); }
        
        return total;
    }
    
    public double getTotalTTC()
    {
        Double total = 0.0;
        for (Ligne ligne : lignes) 
        {
            total+=ligne.getMontantTTC();
        }
        
        return total;
    }
    
    //base HT par Categorie 
    public double getBaseHTByCategorie(int id_categorie)
    {
        
        double baseHT =0.0;
        for (Ligne ligne : this.lignes) 
        {
            {
                 baseHT = baseHT+ligne.getMontantHT();
            }
            
        }
        
       return baseHT;
        
    }
    //Pour Chaque categorie on contruit la ligne
    public double[][] getLignesDetailTva()
    {   
        double tva = 20;
        double[][] lignesDetail = new double[1][3];
        
      /* for (int i = 1; i <=2; i++) 
        {
          if(i%2==0)tva =20; 
          lignesDetail[i-1][0]=  getBaseHTByCategorie(i); 
          lignesDetail[i-1][1]= tva;
          lignesDetail[i-1][2]= lignesDetail[i-1][0]*tva/100;
        }*/
         lignesDetail[0][0]=  this.getTotalHT(); 
         lignesDetail[0][1]= tva;
         lignesDetail[0][2]= lignesDetail[0][0]*tva/100;
        
        return lignesDetail;
    
    }
    
  

    @Override
    public String toString() {
        return "Facture{\n"+ " id_facture=" + id_facture + ", numero_facture=" + this.getNumero_facture()+ ", date_facturation=" + date_facturation  + ", date_livraison=" + date_livraison + ", mode_payement=" + mode_payement +" , client=" + client + "\n\n lignes=" + lignes + "\n\n}";
    }
    
    
    
     public String extraireNumFacture()
    {
        
        return this.extraireAnnee()+"-"+this.getNumero_facture();
    }
   public String extraireJour(){
        String[] chaines = this.getDate_facturation().split("/");
        return  chaines[0];
        
   }
   public String extraireAnnee(){
        String[] chaines = this.getDate_facturation().split("/");
        return  chaines[2];
        
   }
    
   public String extraireMois(){
        String[] chaines = this.getDate_facturation().split("/");
       
        return chaines[1];
         
   }
  
  public Path getPathFacture()
  {
        String mois_string = "";
        String annee = this.extraireAnnee();
        int  extraireMois = Integer.parseInt(this.extraireMois())-1;
        
        for (Mois mois : Mois.values()) 
        {
          if(extraireMois == mois.ordinal())
              mois_string = mois.toString();
        
        }
    
        String nom  = "facture_"+this.getId_facture()+"-"+this.extraireJour()+"-"+this.extraireMois()+"-"+annee+".pdf";
        
   return Paths.get(annee,mois_string ,nom);
   }  
    
   public void addLigne(Ligne l){
       this.lignes.add(l);
       l.setFacture(this);
   }
   
   public void removeLigne(Ligne l){
       this.lignes.remove(l);
   }
  
   protected Long genererNumeroFacture(Long id_facture ,String date_facturation ){
       if(id_facture == null )
           return Long.parseLong(date_facturation.replace("/",""));
       return  Long.parseLong((id_facture+date_facturation).replace("/",""));
   }


   
}
