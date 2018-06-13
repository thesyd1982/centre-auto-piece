package facturation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sun.rowset.internal.Row;
import facturation.dao.FacturationDao;
import facturation.entities.Adresse;

import facturation.entities.Facture;
import facturation.entities.Ligne;
import facturation.entities.Particulier;
import facturation.entities.Piece;
import facturation.entities.Professionnel;
import facturation.service.FacturationService;
import facturation.service.FactureFileService;
import facturation.formulaires.FormulairePiece;
import facturation.formulaires.FormulaireParticulie;
import facturation.formulaires.FormulaireProfessionnel;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Point;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;



/**
 *
 * @author tayeb
 */
public class FactureUI extends javax.swing.JFrame implements Observateur{
   
    
    
    protected FacturationService fs;
    protected ArrayList<Ligne> lignes=new ArrayList<>();
     
    protected Facture selectedFacture;
    protected FactureFileService ffs;
   
    protected TableRowSorter<TableModel> sorterPiece;
    protected TableRowSorter<TableModel> sorterParticulie;
    protected TableRowSorter<TableModel> sorterProfessionnel;
    protected TableRowSorter<TableModel> sorterFactureParticulier;
    protected TableRowSorter<TableModel> sorterFactureProfessionnel;
    protected LigneTableModel modelLigne;
    protected Double valeur = 0.0 ;
    
    private static Cache<String, ArrayList<Piece>> cachePiece ;
    private static Cache<String, ArrayList<Piece>> cachePieceRecherche ;
    private static Cache<String,TableRowSorter<TableModel>>cacheSorterPieces;
    private static Piece pieceSelectionee ;
    private static String recherchePrecedante = "";
    
    /**
     * Creates new form Facture
     */
    
    public FactureUI() {
        
        
  
        
        initComponents();
        
        
        
        cachePiece = CacheBuilder.newBuilder()
       .maximumSize(100) // Taille Max
       .expireAfterWrite(24, TimeUnit.HOURS) // TTL
       .build();
        
        chargementCachePieces();
        
        List b = new ArrayList(cachePiece.getIfPresent("cachePieces"));
        this.pieceList.addAll(b);
        bindingPiecesTable();
        
       cachePieceRecherche = CacheBuilder.newBuilder()
            .maximumSize(100) // Taille Max
            .expireAfterWrite(24, TimeUnit.HOURS) // TTL
            .build();
       
       chargementCachePiecesRecherche(this.recherchePrecedante);
       
        //<editor-fold defaultstate="collapsed" desc="comment">
         cacheSorterPieces = CacheBuilder.newBuilder()
            .maximumSize(100) // Taille Max
            .expireAfterWrite(24, TimeUnit.HOURS) // TTL
            .build();
         
        Date d = new Date();
        System.out.println(d);
        
        chargementCacheSoterPieces();
        
        d = new Date();
        System.out.println(d);
         
         
        
        this.sorterPiece = new TableRowSorter<>(this.piecesJTable.getModel());
        sorterPiece = cacheSorterPieces.getIfPresent("cacheSorterPieces");
        this.piecesJTable.setRowSorter(sorterPiece);
        
        
        //</editor-fold>
        
        
        
        
        
        
        this.sorterParticulie = new TableRowSorter<>(this.particulieJTable.getModel());
        this.particulieJTable.setRowSorter(sorterParticulie);
        
        this.sorterProfessionnel = new TableRowSorter<>(this.professionnelJTable.getModel());
        this.professionnelJTable.setRowSorter(sorterProfessionnel);
        
        this.sorterFactureProfessionnel = new TableRowSorter<>(this.facturesProfessionnelsJTable.getModel());
        this.facturesProfessionnelsJTable.setRowSorter(sorterFactureProfessionnel);
        
        this.sorterFactureParticulier = new TableRowSorter<>(this.facturesParticulieJTable.getModel());
        this.facturesParticulieJTable.setRowSorter(sorterFactureParticulier);
        
        this.setLocationRelativeTo(null);
        fs = new FacturationService();
        FacturationDao dao = new FacturationDao(this.entityManager);
        fs.setDao(dao);
        
        ffs =new FactureFileService();
        this.getValeurStockRestant();
        updateInfos();
        
        
       
        
    }

    private void chargementCacheSoterPieces() {
        TableRowSorter s = new TableRowSorter(piecesJTable.getModel());
        
        s =cacheSorterPieces.getIfPresent("cacheSorterPieces");
        if (s == null) {
            //Calcul de la valeur et mise en cachePiece
            
            s=createPieceSorter();
            
            cacheSorterPieces.put("cacheSorterPieces", s);
        }
    }

    private TableRowSorter createPieceSorter() {
        TableRowSorter s = new TableRowSorter(piecesJTable.getModel());
        RowFilter rowFilter = null;
        
        String ref = "";
        
        
        rechercheParRef(ref);
        
        rowFilter = RowFilter.regexFilter(
                Pattern.compile(ref,
                        Pattern.CASE_INSENSITIVE).toString(),0);
       
        s.setRowFilter(rowFilter);
        return s;
    }

    private void chargementCachePieces() {
        ArrayList<Piece> cp = new ArrayList<>();
        
        cp = new ArrayList<>();
        cp =cachePiece.getIfPresent("cachePieces");
        if (cp == null) {
            //Calcul de la valeur et mise en cachePiece
            
            pieceQuery = entityManager.createQuery("SELECT p FROM Piece p");
            cp = new ArrayList<>(pieceQuery.getResultList());
            
            cachePiece.put("cachePieces", cp);
        }
    }

    
    private void chargementCachePiecesRecherche(String recherche) {
        ArrayList<Piece> cpr = new ArrayList<>();
        
        cpr = new ArrayList<>();
        cpr =cachePieceRecherche.getIfPresent("cachePieceRecherche");
        if (cpr == null && recherche!=this.recherchePrecedante) {
            //Calcul de la valeur et mise en cachePiece
         if(recherche == ""){
            pieceQuery = entityManager.createQuery("SELECT p FROM Piece p");
            cpr = new ArrayList<>(pieceQuery.getResultList());
         }
         else{
                Query pieceQuery1 = entityManager.createQuery("SELECT p FROM Piece p where p.reference LIKE \'%"+recherche+"%\'");
                Query pieceQuery2 = entityManager.createQuery("SELECT p FROM Piece p where p.designation LIKE \'%"+recherche+"%\'");
                Query pieceQuery3 = entityManager.createQuery("SELECT p FROM Piece p where p.marque LIKE \'%"+recherche+"%\'");      
        
             cpr = new ArrayList<>(pieceQuery1.getResultList());

                cpr.addAll(pieceQuery2.getResultList());
                cpr.addAll(pieceQuery3.getResultList()); 
         }
        
        cachePieceRecherche.put("cachePiecesRecherche", cpr);
        this.recherchePrecedante = recherche;
        }
    }
    
    
    
    
    private void showPieces() {
        
         bindingPiecesTable();
        
        
    }
    
    private void removePiece(Piece piece) {
        this.fs.removePiece(piece.getId_piece());
          
    }
    
    
    private void addPiece(Piece piece) {
         
      
        EntityTransaction transaction = entityManager.getTransaction();
         transaction.begin();
           
            pieceList.add(piece);
            entityManager.persist(piece);
            
         transaction.commit();
       
        
    }
    
    
    
    private void bindingPiecesTable() {
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, pieceList, piecesJTable);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${id_piece}"));
        columnBinding.setColumnName("N° Piece");
        columnBinding.setColumnClass(Long.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${reference}"));
        columnBinding.setColumnName("Reference");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${designation}"));
        columnBinding.setColumnName("Designation");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${marque}"));
        columnBinding.setColumnName("Marque");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${prixAchat}"));
        columnBinding.setColumnName("Prix Achat");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${prixVente}"));
        columnBinding.setColumnName("Prix Vente");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${quantite}"));
        columnBinding.setColumnName("Quantite");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);
        
        jTableBinding.bind();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        entityManager = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory("centre_auto_piece_PUN").createEntityManager();
        pieceQuery = java.beans.Beans.isDesignTime() ? null : entityManager.createQuery("SELECT p FROM Piece p");
        pieceList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : pieceQuery.getResultList();
        filtrePieceBG = new javax.swing.ButtonGroup();
        particulierQuery = java.beans.Beans.isDesignTime() ? null : entityManager.createQuery("SELECT p FROM Particulier  p");
        particulierList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(particulierQuery.getResultList());
        professionnelQuery = java.beans.Beans.isDesignTime() ? null : entityManager.createQuery("SELECT p FROM Professionnel  p");
        professionnelList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(professionnelQuery.getResultList());
        filtreProfessionnelBG = new javax.swing.ButtonGroup();
        professionnelFactureList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(professionnelQuery.getResultList());
        factureProfessionnelQuery = java.beans.Beans.isDesignTime() ? null : entityManager.createQuery("SELECT f FROM FactureProfessionnel f");
        factureProfessionnelList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(factureProfessionnelQuery.getResultList());
        factureParticulierQuery = java.beans.Beans.isDesignTime() ? null : entityManager.createQuery("SELECT f FROM FactureParticulier f");
        factureParticulierList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(factureParticulierQuery.getResultList());
        fitreParticulierBG = new javax.swing.ButtonGroup();
        filtreFactureParticulierBG = new javax.swing.ButtonGroup();
        filtreFactureProfessionnelBG = new javax.swing.ButtonGroup();
        list1 = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : factureParticulierQuery.getResultList();
        piecesCardJP = new javax.swing.JPanel();
        conteneurStockJP = new javax.swing.JPanel();
        stockJP = new javax.swing.JPanel();
        ajoutModificationJP = new javax.swing.JPanel();
        referencePieceJT = new javax.swing.JTextField();
        designationPieceJT = new javax.swing.JTextField();
        prixAchatPieceJT = new javax.swing.JTextField();
        refernceJL = new javax.swing.JLabel();
        designationJL = new javax.swing.JLabel();
        marqueJL = new javax.swing.JLabel();
        quantiteJL = new javax.swing.JLabel();
        prixAchatJL = new javax.swing.JLabel();
        marquePieceJT = new javax.swing.JTextField();
        quantiteJT = new javax.swing.JTextField();
        prixVenteJL = new javax.swing.JLabel();
        prixVentePieceJT = new javax.swing.JTextField();
        ajouterPieceJB = new javax.swing.JButton();
        modifierPieceJB = new javax.swing.JButton();
        supprimerPieceJB = new javax.swing.JButton();
        recherchePieceJP = new javax.swing.JPanel();
        recherchePieceJTF = new javax.swing.JTextField();
        piecesJSP = new javax.swing.JScrollPane();
        piecesJTable = new javax.swing.JTable();
        valeurStockJL = new javax.swing.JLabel();
        clientsJP = new javax.swing.JPanel();
        clientsJTP = new javax.swing.JTabbedPane();
        particuliesTabJP = new javax.swing.JPanel();
        conteneurParticulieJP = new javax.swing.JPanel();
        particuliesJSP = new javax.swing.JScrollPane();
        particulieJTable = new javax.swing.JTable();
        particuliesJP = new javax.swing.JPanel();
        nomJL = new javax.swing.JLabel();
        nomJTF = new javax.swing.JTextField();
        prenomJL = new javax.swing.JLabel();
        prenomJTF = new javax.swing.JTextField();
        villeJL = new javax.swing.JLabel();
        villeJTF = new javax.swing.JTextField();
        lieuJL = new javax.swing.JLabel();
        lieuJSP = new javax.swing.JScrollPane();
        lieuJTA = new javax.swing.JTextArea();
        codePostalJL = new javax.swing.JLabel();
        codePostalJTF = new javax.swing.JTextField();
        supprimerParticulieJB = new javax.swing.JButton();
        ajouterParticulieJB = new javax.swing.JButton();
        modifierParticulieJB = new javax.swing.JButton();
        rechercheParticulierJP = new javax.swing.JPanel();
        nomJRB = new javax.swing.JRadioButton();
        prenomJRB = new javax.swing.JRadioButton();
        rechercheParticulierJTF = new javax.swing.JTextField();
        professionnelsTabJP = new javax.swing.JPanel();
        conteneurProfessionnelJP = new javax.swing.JPanel();
        professionnelsJSP = new javax.swing.JScrollPane();
        professionnelJTable = new javax.swing.JTable();
        professionnelsJP = new javax.swing.JPanel();
        nomSocieteJL = new javax.swing.JLabel();
        nomSocieteJTF = new javax.swing.JTextField();
        villeSocieteJL = new javax.swing.JLabel();
        villeSocieteJTF = new javax.swing.JTextField();
        lieuSocieteJL = new javax.swing.JLabel();
        lieuSocieteJSP = new javax.swing.JScrollPane();
        lieuSocieteJTA = new javax.swing.JTextArea();
        codePostalSocieteJL = new javax.swing.JLabel();
        codePostalSocieteJTF = new javax.swing.JTextField();
        supprimerProfessionnelJB = new javax.swing.JButton();
        ajouterProfessionnelJB = new javax.swing.JButton();
        modifierProfessionnelJB = new javax.swing.JButton();
        rechercheProfessionnelJP = new javax.swing.JPanel();
        nomSocieteJRB = new javax.swing.JRadioButton();
        rechercheProfessionnelJTF = new javax.swing.JTextField();
        facturesCardJP = new javax.swing.JPanel();
        facturesJTabbedPane = new javax.swing.JTabbedPane();
        factureParticulierJP = new javax.swing.JPanel();
        ajouterParticulieJP = new javax.swing.JPanel();
        facturerParticulierJButton = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        facturesParticulieJSP = new javax.swing.JScrollPane();
        facturesParticulieJTable = new javax.swing.JTable();
        rechercheFactureParticulierJP = new javax.swing.JPanel();
        numeroFactureParticulierJRB = new javax.swing.JRadioButton();
        nomFactureParticulierJRB = new javax.swing.JRadioButton();
        rechercheFactureParticulierJTF = new javax.swing.JTextField();
        dateFacturationParticulierJRB = new javax.swing.JRadioButton();
        dateLivraisonParticulierJRB = new javax.swing.JRadioButton();
        caParticulierJL = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        factureProfessionnelJP = new javax.swing.JPanel();
        ajouterFactureJP = new javax.swing.JPanel();
        ajouterFactureProfessionnelJB = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        rechercheFactureProfessionnelJP = new javax.swing.JPanel();
        rechercheFactureProfessionnelJTF = new javax.swing.JTextField();
        caProfessionnelJL = new javax.swing.JLabel();
        caProfessionnelTitreJL = new javax.swing.JLabel();
        facturesProfessionelsJSP = new javax.swing.JScrollPane();
        facturesProfessionnelsJTable = new javax.swing.JTable();
        infosJP = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        infosClientTitreJL = new javax.swing.JLabel();
        infosCoutTitreJL = new javax.swing.JLabel();
        infosCATitreJL = new javax.swing.JLabel();
        infosTaxeTitreJL = new javax.swing.JLabel();
        infosBeneficesTitreJL = new javax.swing.JLabel();
        infosProfessionnelsTitreJL = new javax.swing.JLabel();
        infosParticuliersTitreJL = new javax.swing.JLabel();
        infosTotalTitreJL = new javax.swing.JLabel();
        infosCoutProfessionnelJL = new javax.swing.JLabel();
        infosCoutParticulierJL = new javax.swing.JLabel();
        infosCoutTotalJL = new javax.swing.JLabel();
        infosCaProfessionnelsJL = new javax.swing.JLabel();
        infosCaParticuliersJL = new javax.swing.JLabel();
        infosCaTotalJL = new javax.swing.JLabel();
        infosTaxeProfessionnelsJL = new javax.swing.JLabel();
        infosTaxeParticuliersJL = new javax.swing.JLabel();
        infosTaxeTotalJL = new javax.swing.JLabel();
        infosBeneficesProfessionnelJL = new javax.swing.JLabel();
        infosBeneficesParticulierJL = new javax.swing.JLabel();
        infosBeneficesTotalJL = new javax.swing.JLabel();
        appJMenuBar = new javax.swing.JMenuBar();
        accueilJM = new javax.swing.JMenu();
        piecesJMI = new javax.swing.JMenuItem();
        clientsJMI = new javax.swing.JMenuItem();
        factureJMI = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Facturation");
        setName("mainJF"); // NOI18N
        getContentPane().setLayout(new java.awt.CardLayout());

        conteneurStockJP.setLayout(new javax.swing.BoxLayout(conteneurStockJP, javax.swing.BoxLayout.LINE_AXIS));
        piecesCardJP.add(conteneurStockJP);

        stockJP.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Stock", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        ajoutModificationJP.setBackground(new java.awt.Color(255, 255, 255));
        ajoutModificationJP.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Ajout / Modification des pieces", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, piecesJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.reference}"), referencePieceJT, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, piecesJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.designation}"), designationPieceJT, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, piecesJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.prixAchat}"), prixAchatPieceJT, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        refernceJL.setText("Référence");

        designationJL.setText("Désignation");

        marqueJL.setText("Marque");

        quantiteJL.setText("Quantité");

        prixAchatJL.setText("Prix d'achat");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, piecesJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.marque}"), marquePieceJT, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, piecesJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.quantite}"), quantiteJT, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        prixVenteJL.setText("Prix de vente");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, piecesJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.prixVente}"), prixVentePieceJT, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        ajouterPieceJB.setText("Ajouter ");
        ajouterPieceJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouterPieceJBActionPerformed(evt);
            }
        });

        modifierPieceJB.setText("modifier");
        modifierPieceJB.setEnabled(false);
        modifierPieceJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifierPieceJBActionPerformed(evt);
            }
        });

        supprimerPieceJB.setText("supprimer pièce ");
        supprimerPieceJB.setEnabled(false);
        supprimerPieceJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supprimerPieceJBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ajoutModificationJPLayout = new javax.swing.GroupLayout(ajoutModificationJP);
        ajoutModificationJP.setLayout(ajoutModificationJPLayout);
        ajoutModificationJPLayout.setHorizontalGroup(
            ajoutModificationJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ajoutModificationJPLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(ajoutModificationJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ajoutModificationJPLayout.createSequentialGroup()
                        .addGroup(ajoutModificationJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(prixVenteJL)
                            .addComponent(prixAchatJL)
                            .addComponent(quantiteJL)
                            .addComponent(marqueJL)
                            .addComponent(designationJL)
                            .addComponent(refernceJL))
                        .addGap(6, 6, 6)
                        .addGroup(ajoutModificationJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ajoutModificationJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(designationPieceJT, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(referencePieceJT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(marquePieceJT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(quantiteJT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(prixVentePieceJT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prixAchatPieceJT, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12))
                    .addGroup(ajoutModificationJPLayout.createSequentialGroup()
                        .addComponent(ajouterPieceJB)
                        .addGap(18, 18, 18)
                        .addComponent(supprimerPieceJB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(modifierPieceJB, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6))))
        );

        ajoutModificationJPLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {designationPieceJT, marquePieceJT, prixAchatPieceJT, prixVentePieceJT, quantiteJT, referencePieceJT});

        ajoutModificationJPLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {designationJL, marqueJL, prixAchatJL, prixVenteJL, quantiteJL, refernceJL});

        ajoutModificationJPLayout.setVerticalGroup(
            ajoutModificationJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ajoutModificationJPLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(ajoutModificationJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(refernceJL, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(referencePieceJT, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ajoutModificationJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(designationPieceJT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(designationJL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ajoutModificationJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(marqueJL)
                    .addComponent(marquePieceJT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(ajoutModificationJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(quantiteJL)
                    .addComponent(quantiteJT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ajoutModificationJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prixAchatJL)
                    .addComponent(prixAchatPieceJT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ajoutModificationJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prixVenteJL)
                    .addComponent(prixVentePieceJT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(ajoutModificationJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ajouterPieceJB)
                    .addComponent(modifierPieceJB)
                    .addComponent(supprimerPieceJB))
                .addGap(6, 6, 6))
        );

        ajoutModificationJPLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {designationPieceJT, marquePieceJT, prixAchatPieceJT, prixVentePieceJT, quantiteJT, referencePieceJT});

        ajoutModificationJPLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {designationJL, marqueJL, prixAchatJL, prixVenteJL, quantiteJL, refernceJL});

        recherchePieceJP.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Rechercher Piéce", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        recherchePieceJTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                recherchePieceJTFKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout recherchePieceJPLayout = new javax.swing.GroupLayout(recherchePieceJP);
        recherchePieceJP.setLayout(recherchePieceJPLayout);
        recherchePieceJPLayout.setHorizontalGroup(
            recherchePieceJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, recherchePieceJPLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(recherchePieceJTF, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        recherchePieceJPLayout.setVerticalGroup(
            recherchePieceJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recherchePieceJPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(recherchePieceJTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        piecesJSP.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Liste des pièces", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        piecesJTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        piecesJTable.setFont(new java.awt.Font("SansSerif", 0, 15)); // NOI18N
        piecesJTable.setRowHeight(20);
        piecesJTable.setRowSorter(sorterPiece);
        piecesJTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        piecesJTable.setShowVerticalLines(true);

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, pieceList, piecesJTable);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${id_piece}"));
        columnBinding.setColumnName("N° Piece");
        columnBinding.setColumnClass(Long.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${reference}"));
        columnBinding.setColumnName("Reference");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${designation}"));
        columnBinding.setColumnName("Designation");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${marque}"));
        columnBinding.setColumnName("Marque");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${prixAchat}"));
        columnBinding.setColumnName("Prix Achat");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${prixVente}"));
        columnBinding.setColumnName("Prix Vente");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${quantite}"));
        columnBinding.setColumnName("Quantite");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        piecesJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                piecesJTableMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                piecesJTableMousePressed(evt);
            }
        });
        piecesJSP.setViewportView(piecesJTable);

        javax.swing.GroupLayout stockJPLayout = new javax.swing.GroupLayout(stockJP);
        stockJP.setLayout(stockJPLayout);
        stockJPLayout.setHorizontalGroup(
            stockJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stockJPLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(stockJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(stockJPLayout.createSequentialGroup()
                        .addComponent(recherchePieceJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(850, 850, 850))
                    .addGroup(stockJPLayout.createSequentialGroup()
                        .addComponent(ajoutModificationJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(stockJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(stockJPLayout.createSequentialGroup()
                                .addComponent(valeurStockJL)
                                .addContainerGap())
                            .addGroup(stockJPLayout.createSequentialGroup()
                                .addComponent(piecesJSP)
                                .addGap(6, 6, 6))))))
        );
        stockJPLayout.setVerticalGroup(
            stockJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stockJPLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(recherchePieceJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(stockJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ajoutModificationJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(piecesJSP, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(valeurStockJL)
                .addContainerGap())
        );

        recherchePieceJP.getAccessibleContext().setAccessibleName("Rechercher Par");

        piecesCardJP.add(stockJP);

        getContentPane().add(piecesCardJP, "piecesCard");

        clientsJP.setLayout(new java.awt.BorderLayout());

        particuliesTabJP.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        conteneurParticulieJP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        particuliesJSP.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Réréptroir des particuliers", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        particulieJTable.setRowHeight(19);
        particulieJTable.setRowMargin(2);
        particulieJTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, particulierList, particulieJTable);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${id}"));
        columnBinding.setColumnName("N°");
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nom}"));
        columnBinding.setColumnName("Nom");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${prenom}"));
        columnBinding.setColumnName("Prénom");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${adresse.lieu}"));
        columnBinding.setColumnName("Lieu");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${adresse.ville}"));
        columnBinding.setColumnName("Ville");
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${adresse.cp}"));
        columnBinding.setColumnName("CP");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        particulieJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                particulieJTableMousePressed(evt);
            }
        });
        particuliesJSP.setViewportView(particulieJTable);

        particuliesJP.setBackground(new java.awt.Color(255, 255, 255));
        particuliesJP.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Ajout / Modification d'un particulier", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        nomJL.setText("Nom");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, particulieJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.nom}"), nomJTF, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        prenomJL.setText("Prénom");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, particulieJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.prenom}"), prenomJTF, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        villeJL.setText("Ville");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, particulieJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.adresse.ville}"), villeJTF, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        lieuJL.setText("Lieu");

        lieuJTA.setColumns(20);
        lieuJTA.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, particulieJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.adresse.lieu}"), lieuJTA, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        lieuJSP.setViewportView(lieuJTA);

        codePostalJL.setText("Code postal");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, particulieJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.adresse.cp}"), codePostalJTF, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        supprimerParticulieJB.setText("Supprimer");
        supprimerParticulieJB.setEnabled(false);
        supprimerParticulieJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supprimerParticulieJBActionPerformed(evt);
            }
        });

        ajouterParticulieJB.setText("Ajouter");
        ajouterParticulieJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouterParticulieJBActionPerformed(evt);
            }
        });

        modifierParticulieJB.setText("Modifier");
        modifierParticulieJB.setEnabled(false);
        modifierParticulieJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifierParticulieJBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout particuliesJPLayout = new javax.swing.GroupLayout(particuliesJP);
        particuliesJP.setLayout(particuliesJPLayout);
        particuliesJPLayout.setHorizontalGroup(
            particuliesJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, particuliesJPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(particuliesJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prenomJL)
                    .addComponent(nomJL)
                    .addComponent(villeJL)
                    .addComponent(lieuJL)
                    .addComponent(codePostalJL)
                    .addComponent(ajouterParticulieJB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(particuliesJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, particuliesJPLayout.createSequentialGroup()
                        .addComponent(supprimerParticulieJB, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(modifierParticulieJB))
                    .addComponent(nomJTF, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prenomJTF, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(villeJTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(codePostalJTF, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lieuJSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        particuliesJPLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {nomJL, prenomJL});

        particuliesJPLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {codePostalJTF, lieuJSP, nomJTF, prenomJTF, villeJTF});

        particuliesJPLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ajouterParticulieJB, modifierParticulieJB, supprimerParticulieJB});

        particuliesJPLayout.setVerticalGroup(
            particuliesJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(particuliesJPLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(particuliesJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nomJTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nomJL))
                .addGap(18, 18, 18)
                .addGroup(particuliesJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prenomJTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prenomJL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(particuliesJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lieuJL)
                    .addComponent(lieuJSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(particuliesJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(villeJTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(villeJL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(particuliesJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(codePostalJTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(codePostalJL))
                .addGap(16, 16, 16)
                .addGroup(particuliesJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(supprimerParticulieJB)
                    .addComponent(modifierParticulieJB)
                    .addComponent(ajouterParticulieJB))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        rechercheParticulierJP.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Rechercher Par ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        fitreParticulierBG.add(nomJRB);
        nomJRB.setSelected(true);
        nomJRB.setText("Nom");

        fitreParticulierBG.add(prenomJRB);
        prenomJRB.setText("Prénom");

        rechercheParticulierJTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechercheParticulierJTFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout rechercheParticulierJPLayout = new javax.swing.GroupLayout(rechercheParticulierJP);
        rechercheParticulierJP.setLayout(rechercheParticulierJPLayout);
        rechercheParticulierJPLayout.setHorizontalGroup(
            rechercheParticulierJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rechercheParticulierJPLayout.createSequentialGroup()
                .addGroup(rechercheParticulierJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rechercheParticulierJPLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(nomJRB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(prenomJRB))
                    .addGroup(rechercheParticulierJPLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(rechercheParticulierJTF, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        rechercheParticulierJPLayout.setVerticalGroup(
            rechercheParticulierJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rechercheParticulierJPLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(rechercheParticulierJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nomJRB)
                    .addComponent(prenomJRB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rechercheParticulierJTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout conteneurParticulieJPLayout = new javax.swing.GroupLayout(conteneurParticulieJP);
        conteneurParticulieJP.setLayout(conteneurParticulieJPLayout);
        conteneurParticulieJPLayout.setHorizontalGroup(
            conteneurParticulieJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conteneurParticulieJPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(conteneurParticulieJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rechercheParticulierJP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(conteneurParticulieJPLayout.createSequentialGroup()
                        .addComponent(particuliesJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(particuliesJSP, javax.swing.GroupLayout.DEFAULT_SIZE, 863, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        conteneurParticulieJPLayout.setVerticalGroup(
            conteneurParticulieJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, conteneurParticulieJPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rechercheParticulierJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(conteneurParticulieJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(particuliesJP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(particuliesJSP, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout particuliesTabJPLayout = new javax.swing.GroupLayout(particuliesTabJP);
        particuliesTabJP.setLayout(particuliesTabJPLayout);
        particuliesTabJPLayout.setHorizontalGroup(
            particuliesTabJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, particuliesTabJPLayout.createSequentialGroup()
                .addContainerGap(280, Short.MAX_VALUE)
                .addComponent(conteneurParticulieJP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(280, Short.MAX_VALUE))
        );
        particuliesTabJPLayout.setVerticalGroup(
            particuliesTabJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(particuliesTabJPLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(conteneurParticulieJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        clientsJTP.addTab("Particuliers", particuliesTabJP);

        conteneurProfessionnelJP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        professionnelsJSP.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Répértoir des professionnels", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        professionnelJTable.setRowHeight(19);
        professionnelJTable.setRowMargin(2);
        professionnelJTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, professionnelList, professionnelJTable);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${id}"));
        columnBinding.setColumnName("N° ");
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nomSociete}"));
        columnBinding.setColumnName("Société");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${adresse.lieu}"));
        columnBinding.setColumnName("Lieu");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${adresse.ville}"));
        columnBinding.setColumnName("Ville");
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${adresse.cp}"));
        columnBinding.setColumnName("CP");
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        professionnelJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                professionnelJTableMousePressed(evt);
            }
        });
        professionnelsJSP.setViewportView(professionnelJTable);

        professionnelsJP.setBackground(new java.awt.Color(255, 255, 255));
        professionnelsJP.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Ajout / Modification professionnel", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        nomSocieteJL.setText("Société");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, professionnelJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.nomSociete}"), nomSocieteJTF, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        villeSocieteJL.setText("Ville");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, professionnelJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.adresse.ville}"), villeSocieteJTF, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        lieuSocieteJL.setText("Lieu");

        lieuSocieteJTA.setColumns(20);
        lieuSocieteJTA.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, professionnelJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.adresse.lieu}"), lieuSocieteJTA, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        lieuSocieteJSP.setViewportView(lieuSocieteJTA);

        codePostalSocieteJL.setText("Code postal");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, professionnelJTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.adresse.cp}"), codePostalSocieteJTF, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        supprimerProfessionnelJB.setText("Supprimer");
        supprimerProfessionnelJB.setEnabled(false);
        supprimerProfessionnelJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supprimerProfessionnelJBActionPerformed(evt);
            }
        });

        ajouterProfessionnelJB.setText("Ajouter");
        ajouterProfessionnelJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouterProfessionnelJBActionPerformed(evt);
            }
        });

        modifierProfessionnelJB.setText("Modifier");
        modifierProfessionnelJB.setEnabled(false);
        modifierProfessionnelJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifierProfessionnelJBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout professionnelsJPLayout = new javax.swing.GroupLayout(professionnelsJP);
        professionnelsJP.setLayout(professionnelsJPLayout);
        professionnelsJPLayout.setHorizontalGroup(
            professionnelsJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, professionnelsJPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(professionnelsJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nomSocieteJL)
                    .addComponent(villeSocieteJL)
                    .addComponent(lieuSocieteJL)
                    .addComponent(codePostalSocieteJL)
                    .addComponent(ajouterProfessionnelJB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(professionnelsJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, professionnelsJPLayout.createSequentialGroup()
                        .addComponent(supprimerProfessionnelJB, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(modifierProfessionnelJB))
                    .addComponent(nomSocieteJTF, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(villeSocieteJTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(codePostalSocieteJTF, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lieuSocieteJSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        professionnelsJPLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {codePostalSocieteJTF, lieuSocieteJSP, nomSocieteJTF, villeSocieteJTF});

        professionnelsJPLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ajouterProfessionnelJB, modifierProfessionnelJB, supprimerProfessionnelJB});

        professionnelsJPLayout.setVerticalGroup(
            professionnelsJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(professionnelsJPLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(professionnelsJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nomSocieteJTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nomSocieteJL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(professionnelsJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lieuSocieteJL)
                    .addComponent(lieuSocieteJSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(professionnelsJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(villeSocieteJTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(villeSocieteJL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(professionnelsJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(codePostalSocieteJTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(codePostalSocieteJL))
                .addGap(16, 16, 16)
                .addGroup(professionnelsJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(supprimerProfessionnelJB)
                    .addComponent(modifierProfessionnelJB)
                    .addComponent(ajouterProfessionnelJB))
                .addContainerGap(95, Short.MAX_VALUE))
        );

        rechercheProfessionnelJP.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Rechercher Par ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        filtreProfessionnelBG.add(nomSocieteJRB);
        nomSocieteJRB.setSelected(true);
        nomSocieteJRB.setText("Nom de société");

        rechercheProfessionnelJTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechercheProfessionnelJTFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout rechercheProfessionnelJPLayout = new javax.swing.GroupLayout(rechercheProfessionnelJP);
        rechercheProfessionnelJP.setLayout(rechercheProfessionnelJPLayout);
        rechercheProfessionnelJPLayout.setHorizontalGroup(
            rechercheProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rechercheProfessionnelJPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rechercheProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nomSocieteJRB)
                    .addComponent(rechercheProfessionnelJTF, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        rechercheProfessionnelJPLayout.setVerticalGroup(
            rechercheProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rechercheProfessionnelJPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nomSocieteJRB)
                .addGap(7, 7, 7)
                .addComponent(rechercheProfessionnelJTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout conteneurProfessionnelJPLayout = new javax.swing.GroupLayout(conteneurProfessionnelJP);
        conteneurProfessionnelJP.setLayout(conteneurProfessionnelJPLayout);
        conteneurProfessionnelJPLayout.setHorizontalGroup(
            conteneurProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conteneurProfessionnelJPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(conteneurProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(conteneurProfessionnelJPLayout.createSequentialGroup()
                        .addComponent(professionnelsJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(professionnelsJSP, javax.swing.GroupLayout.DEFAULT_SIZE, 868, Short.MAX_VALUE))
                    .addComponent(rechercheProfessionnelJP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        conteneurProfessionnelJPLayout.setVerticalGroup(
            conteneurProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, conteneurProfessionnelJPLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(rechercheProfessionnelJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(conteneurProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(professionnelsJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(professionnelsJSP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        conteneurProfessionnelJPLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {professionnelsJP, professionnelsJSP});

        javax.swing.GroupLayout professionnelsTabJPLayout = new javax.swing.GroupLayout(professionnelsTabJP);
        professionnelsTabJP.setLayout(professionnelsTabJPLayout);
        professionnelsTabJPLayout.setHorizontalGroup(
            professionnelsTabJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(professionnelsTabJPLayout.createSequentialGroup()
                .addContainerGap(280, Short.MAX_VALUE)
                .addComponent(conteneurProfessionnelJP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(279, Short.MAX_VALUE))
        );
        professionnelsTabJPLayout.setVerticalGroup(
            professionnelsTabJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(professionnelsTabJPLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(conteneurProfessionnelJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(154, Short.MAX_VALUE))
        );

        clientsJTP.addTab("Professionnels", professionnelsTabJP);

        clientsJP.add(clientsJTP, java.awt.BorderLayout.CENTER);

        getContentPane().add(clientsJP, "clientsCard");

        facturesCardJP.setLayout(new java.awt.BorderLayout());

        facturesJTabbedPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        facturesJTabbedPane.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        factureParticulierJP.setForeground(new java.awt.Color(152, 254, 5));
        factureParticulierJP.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        ajouterParticulieJP.setMinimumSize(new java.awt.Dimension(192, 110));
        ajouterParticulieJP.setPreferredSize(new java.awt.Dimension(192, 110));
        ajouterParticulieJP.setLayout(new java.awt.GridLayout(3, 0, 12, 10));

        facturerParticulierJButton.setText("Facturer un particulier");
        facturerParticulierJButton.setPreferredSize(new java.awt.Dimension(58, 30));
        facturerParticulierJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                facturerParticulierJButtonActionPerformed(evt);
            }
        });
        ajouterParticulieJP.add(facturerParticulierJButton);

        jButton4.setText("Stocks");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        ajouterParticulieJP.add(jButton4);

        jButton5.setText("Clients");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        ajouterParticulieJP.add(jButton5);

        facturesParticulieJSP.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Factures Particuliers", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        facturesParticulieJTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, factureParticulierList, facturesParticulieJTable);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${numero_facture}"));
        columnBinding.setColumnName("Numero_facture");
        columnBinding.setColumnClass(Long.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${immatriculation}"));
        columnBinding.setColumnName("Immatriculation");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${date_facturation}"));
        columnBinding.setColumnName("Date_facturation");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${date_livraison}"));
        columnBinding.setColumnName("Date_livraison");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${mode_payement}"));
        columnBinding.setColumnName("Mode_payement");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${totalHT}"));
        columnBinding.setColumnName("Total HT");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${totalTVA}"));
        columnBinding.setColumnName("Total TVA");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${totalTTC}"));
        columnBinding.setColumnName("Total TTC");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        facturesParticulieJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                facturesParticulieJTableMouseClicked(evt);
            }
        });
        facturesParticulieJSP.setViewportView(facturesParticulieJTable);
        if (facturesParticulieJTable.getColumnModel().getColumnCount() > 0) {
            facturesParticulieJTable.getColumnModel().getColumn(5).setPreferredWidth(4);
        }

        rechercheFactureParticulierJP.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Rechercher Par ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP));

        numeroFactureParticulierJRB.setBackground(new java.awt.Color(255, 255, 255));
        filtreFactureParticulierBG.add(numeroFactureParticulierJRB);
        numeroFactureParticulierJRB.setText("N° Facture");
        numeroFactureParticulierJRB.setToolTipText("");

        nomFactureParticulierJRB.setBackground(new java.awt.Color(255, 255, 255));
        filtreFactureParticulierBG.add(nomFactureParticulierJRB);
        nomFactureParticulierJRB.setSelected(true);
        nomFactureParticulierJRB.setText("Client");

        rechercheFactureParticulierJTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechercheFactureParticulierJTFActionPerformed(evt);
            }
        });

        dateFacturationParticulierJRB.setBackground(new java.awt.Color(255, 255, 255));
        filtreFactureParticulierBG.add(dateFacturationParticulierJRB);
        dateFacturationParticulierJRB.setText("Date de facturation");

        dateLivraisonParticulierJRB.setBackground(new java.awt.Color(255, 255, 255));
        filtreFactureParticulierBG.add(dateLivraisonParticulierJRB);
        dateLivraisonParticulierJRB.setText("Date de livraison");

        javax.swing.GroupLayout rechercheFactureParticulierJPLayout = new javax.swing.GroupLayout(rechercheFactureParticulierJP);
        rechercheFactureParticulierJP.setLayout(rechercheFactureParticulierJPLayout);
        rechercheFactureParticulierJPLayout.setHorizontalGroup(
            rechercheFactureParticulierJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rechercheFactureParticulierJPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rechercheFactureParticulierJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rechercheFactureParticulierJPLayout.createSequentialGroup()
                        .addComponent(nomFactureParticulierJRB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateFacturationParticulierJRB)
                        .addGap(12, 12, 12)
                        .addComponent(dateLivraisonParticulierJRB, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numeroFactureParticulierJRB))
                    .addComponent(rechercheFactureParticulierJTF, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(925, Short.MAX_VALUE))
        );

        rechercheFactureParticulierJPLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {dateFacturationParticulierJRB, dateLivraisonParticulierJRB, nomFactureParticulierJRB, numeroFactureParticulierJRB});

        rechercheFactureParticulierJPLayout.setVerticalGroup(
            rechercheFactureParticulierJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rechercheFactureParticulierJPLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(rechercheFactureParticulierJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numeroFactureParticulierJRB)
                    .addComponent(nomFactureParticulierJRB)
                    .addComponent(dateFacturationParticulierJRB)
                    .addComponent(dateLivraisonParticulierJRB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rechercheFactureParticulierJTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        caParticulierJL.setFont(new java.awt.Font("Ubuntu", 1, 16)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 16)); // NOI18N
        jLabel1.setText("CA");

        javax.swing.GroupLayout factureParticulierJPLayout = new javax.swing.GroupLayout(factureParticulierJP);
        factureParticulierJP.setLayout(factureParticulierJPLayout);
        factureParticulierJPLayout.setHorizontalGroup(
            factureParticulierJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(factureParticulierJPLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(factureParticulierJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(factureParticulierJPLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(caParticulierJL, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(factureParticulierJPLayout.createSequentialGroup()
                        .addGroup(factureParticulierJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(facturesParticulieJSP)
                            .addGroup(factureParticulierJPLayout.createSequentialGroup()
                                .addComponent(rechercheFactureParticulierJP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ajouterParticulieJP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(84, 84, 84))))
        );
        factureParticulierJPLayout.setVerticalGroup(
            factureParticulierJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(factureParticulierJPLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(factureParticulierJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rechercheFactureParticulierJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ajouterParticulieJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(facturesParticulieJSP, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(factureParticulierJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(caParticulierJL, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37))
        );

        factureParticulierJPLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ajouterParticulieJP, rechercheFactureParticulierJP});

        facturesJTabbedPane.addTab("Facture Particulier", factureParticulierJP);

        factureProfessionnelJP.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        ajouterFactureJP.setLayout(new java.awt.GridLayout(3, 0, 12, 10));

        ajouterFactureProfessionnelJB.setText("Facturer un professionnel");
        ajouterFactureProfessionnelJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouterFactureProfessionnelJBActionPerformed(evt);
            }
        });
        ajouterFactureJP.add(ajouterFactureProfessionnelJB);

        jButton2.setText("Stocks");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        ajouterFactureJP.add(jButton2);

        jButton3.setText("Clients");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        ajouterFactureJP.add(jButton3);

        rechercheFactureProfessionnelJP.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Rechercher Par ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP));

        rechercheFactureProfessionnelJTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rechercheFactureProfessionnelJTFActionPerformed(evt);
            }
        });
        rechercheFactureProfessionnelJTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rechercheFactureProfessionnelJTFKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout rechercheFactureProfessionnelJPLayout = new javax.swing.GroupLayout(rechercheFactureProfessionnelJP);
        rechercheFactureProfessionnelJP.setLayout(rechercheFactureProfessionnelJPLayout);
        rechercheFactureProfessionnelJPLayout.setHorizontalGroup(
            rechercheFactureProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rechercheFactureProfessionnelJPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rechercheFactureProfessionnelJTF, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        rechercheFactureProfessionnelJPLayout.setVerticalGroup(
            rechercheFactureProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rechercheFactureProfessionnelJPLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(rechercheFactureProfessionnelJTF, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        caProfessionnelJL.setFont(new java.awt.Font("Ubuntu", 1, 16)); // NOI18N

        caProfessionnelTitreJL.setFont(new java.awt.Font("Ubuntu", 1, 16)); // NOI18N
        caProfessionnelTitreJL.setText("CA");

        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, factureProfessionnelList, facturesProfessionnelsJTable);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${numero_facture}"));
        columnBinding.setColumnName("Numero_facture");
        columnBinding.setColumnClass(Long.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${immatriculation}"));
        columnBinding.setColumnName("Immatriculation");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${date_facturation}"));
        columnBinding.setColumnName("Date_facturation");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${date_livraison}"));
        columnBinding.setColumnName("Date_livraison");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${mode_payement}"));
        columnBinding.setColumnName("Mode_payement");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${totalHT}"));
        columnBinding.setColumnName("Total HT");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${totalTTC}"));
        columnBinding.setColumnName("Total TTC");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${totalTVA}"));
        columnBinding.setColumnName("Total TVA");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        facturesProfessionnelsJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                facturesProfessionnelsJTableMouseClicked(evt);
            }
        });
        facturesProfessionelsJSP.setViewportView(facturesProfessionnelsJTable);

        javax.swing.GroupLayout factureProfessionnelJPLayout = new javax.swing.GroupLayout(factureProfessionnelJP);
        factureProfessionnelJP.setLayout(factureProfessionnelJPLayout);
        factureProfessionnelJPLayout.setHorizontalGroup(
            factureProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, factureProfessionnelJPLayout.createSequentialGroup()
                .addGroup(factureProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(factureProfessionnelJPLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(caProfessionnelTitreJL)
                        .addGap(40, 40, 40)
                        .addComponent(caProfessionnelJL, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(factureProfessionnelJPLayout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addGroup(factureProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(factureProfessionnelJPLayout.createSequentialGroup()
                                .addComponent(rechercheFactureProfessionnelJP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ajouterFactureJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(factureProfessionnelJPLayout.createSequentialGroup()
                                .addComponent(facturesProfessionelsJSP, javax.swing.GroupLayout.PREFERRED_SIZE, 1021, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(772, 772, 772))
        );
        factureProfessionnelJPLayout.setVerticalGroup(
            factureProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(factureProfessionnelJPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(factureProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rechercheFactureProfessionnelJP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ajouterFactureJP, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(facturesProfessionelsJSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(factureProfessionnelJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(caProfessionnelTitreJL)
                    .addComponent(caProfessionnelJL, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        facturesJTabbedPane.addTab("Facture Professionnel", factureProfessionnelJP);

        infosClientTitreJL.setText("Clients");

        infosCoutTitreJL.setText("Valeur du stock vendu");

        infosCATitreJL.setText("Chiffre D'affaire");

        infosTaxeTitreJL.setText("Taxe sur la valeur ajoutée");

        infosBeneficesTitreJL.setText("Bénéfices");

        infosProfessionnelsTitreJL.setText("Professionnels");

        infosParticuliersTitreJL.setText("Particuliers");

        infosTotalTitreJL.setText("Total");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, caProfessionnelJL, org.jdesktop.beansbinding.ELProperty.create("${text}"), infosCaProfessionnelsJL, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, caParticulierJL, org.jdesktop.beansbinding.ELProperty.create("${text}"), infosCaParticuliersJL, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(infosProfessionnelsTitreJL, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                                .addComponent(infosClientTitreJL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(infosParticuliersTitreJL, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(infosCoutTitreJL))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(infosCoutParticulierJL))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(infosCoutProfessionnelJL))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(infosTotalTitreJL)
                        .addGap(18, 18, 18)
                        .addComponent(infosCoutTotalJL)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infosCaParticuliersJL, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(infosCATitreJL)
                    .addComponent(infosCaProfessionnelsJL, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(infosCaTotalJL))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infosTaxeProfessionnelsJL)
                    .addComponent(infosTaxeTitreJL)
                    .addComponent(infosTaxeParticuliersJL)
                    .addComponent(infosTaxeTotalJL))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infosBeneficesTitreJL)
                    .addComponent(infosBeneficesParticulierJL)
                    .addComponent(infosBeneficesTotalJL)
                    .addComponent(infosBeneficesProfessionnelJL))
                .addGap(205, 205, 205))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {infosBeneficesParticulierJL, infosBeneficesProfessionnelJL, infosBeneficesTitreJL, infosBeneficesTotalJL, infosCATitreJL, infosCaParticuliersJL, infosCaProfessionnelsJL, infosCaTotalJL, infosClientTitreJL, infosCoutParticulierJL, infosCoutProfessionnelJL, infosCoutTitreJL, infosCoutTotalJL, infosParticuliersTitreJL, infosProfessionnelsTitreJL, infosTaxeParticuliersJL, infosTaxeProfessionnelsJL, infosTaxeTitreJL, infosTaxeTotalJL, infosTotalTitreJL});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(infosClientTitreJL)
                    .addComponent(infosCoutTitreJL)
                    .addComponent(infosCATitreJL)
                    .addComponent(infosTaxeTitreJL)
                    .addComponent(infosBeneficesTitreJL))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(infosProfessionnelsTitreJL)
                        .addComponent(infosCoutProfessionnelJL)
                        .addComponent(infosTaxeProfessionnelsJL))
                    .addComponent(infosCaProfessionnelsJL, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(infosBeneficesProfessionnelJL))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(infosTaxeParticuliersJL)
                                .addComponent(infosParticuliersTitreJL)
                                .addComponent(infosCoutParticulierJL))
                            .addComponent(infosCaParticuliersJL, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(infosBeneficesParticulierJL)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infosBeneficesTotalJL)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(infosTotalTitreJL)
                        .addComponent(infosCoutTotalJL))
                    .addComponent(infosCaTotalJL)
                    .addComponent(infosTaxeTotalJL))
                .addContainerGap(45, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {infosBeneficesParticulierJL, infosBeneficesProfessionnelJL, infosBeneficesTitreJL, infosBeneficesTotalJL, infosCATitreJL, infosCaParticuliersJL, infosCaProfessionnelsJL, infosCaTotalJL, infosClientTitreJL, infosCoutParticulierJL, infosCoutProfessionnelJL, infosCoutTitreJL, infosCoutTotalJL, infosParticuliersTitreJL, infosProfessionnelsTitreJL, infosTaxeParticuliersJL, infosTaxeProfessionnelsJL, infosTaxeTitreJL, infosTaxeTotalJL, infosTotalTitreJL});

        javax.swing.GroupLayout infosJPLayout = new javax.swing.GroupLayout(infosJP);
        infosJP.setLayout(infosJPLayout);
        infosJPLayout.setHorizontalGroup(
            infosJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, infosJPLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1037, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(813, Short.MAX_VALUE))
        );
        infosJPLayout.setVerticalGroup(
            infosJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infosJPLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(436, Short.MAX_VALUE))
        );

        facturesJTabbedPane.addTab("Infos", infosJP);

        facturesCardJP.add(facturesJTabbedPane, java.awt.BorderLayout.CENTER);
        facturesJTabbedPane.getAccessibleContext().setAccessibleName("");

        getContentPane().add(facturesCardJP, "facturesCard");

        accueilJM.setText("Accueil");
        accueilJM.setToolTipText("");

        piecesJMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        piecesJMI.setText("Stock");
        piecesJMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                piecesJMIActionPerformed(evt);
            }
        });
        accueilJM.add(piecesJMI);

        clientsJMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK));
        clientsJMI.setText("Clients");
        clientsJMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientsJMIActionPerformed(evt);
            }
        });
        accueilJM.add(clientsJMI);

        factureJMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_MASK));
        factureJMI.setText("Factures");
        factureJMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                factureJMIActionPerformed(evt);
            }
        });
        accueilJM.add(factureJMI);

        appJMenuBar.add(accueilJM);

        setJMenuBar(appJMenuBar);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void piecesJMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_piecesJMIActionPerformed
        changerCard("piecesCard");
    }//GEN-LAST:event_piecesJMIActionPerformed

    private void clientsJMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientsJMIActionPerformed
        changerCard("clientsCard");
    }//GEN-LAST:event_clientsJMIActionPerformed

    private void ajouterPieceJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouterPieceJBActionPerformed
       ajouterPieceJtableRow();
       
       resetFormulairePiece();  
      // this.recherchePieceJTFActionPerformed(evt);
    }//GEN-LAST:event_ajouterPieceJBActionPerformed

    private void modifierPieceJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifierPieceJBActionPerformed
        
        String message ="êtes-vous sûr de vouloir modifier cette pièce?";
        String titre = "Modification de la pièce";
        
        ArrayList test = new ArrayList<Piece>();
            
        test = cachePiece.getIfPresent("cachePieces");
        Piece  lastp  = (Piece) test.get(test.size()-1);
        System.out.println(lastp);
        
        
        int confirmation = JOptionPane.showConfirmDialog(null,message,titre,JOptionPane.OK_CANCEL_OPTION);
        
        if(JOptionPane.OK_OPTION == confirmation )
        {
                FormulairePiece f = initFormulairePiece();
                Piece newPiece = null ;  
                if(f.hydrate().isEmpty())
                {
                    newPiece = f.getPiece();
                    
                    newPiece.setId_piece(pieceSelectionee.getId_piece());
                    
                    this.fs.updatePiece(newPiece);
                    
                    
                    Iterator<Piece> iterator = pieceList.iterator();
                        int index = 0;
                    while(iterator.hasNext()){
                        if(Objects.equals((iterator.next()).getId_piece(), pieceSelectionee.getId_piece())){
                            index++;
                            
                        }
                    }
                    pieceList.set(index , newPiece);
                    
                   
                    updatePieceList();
                    
                    message ="La pièce a été modifiée";
                    JOptionPane.showMessageDialog(null,message,titre,JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    displayError(this.conteneurStockJP,f.validate());
                    message ="Opération Annulée";
                    JOptionPane.showMessageDialog(null,message,titre,JOptionPane.INFORMATION_MESSAGE);
                }
                  
        }
        else
        {
            message ="Opération Annulée";
            JOptionPane.showMessageDialog(null,message,titre,JOptionPane.INFORMATION_MESSAGE);
        }
        showPieces();
        resetFormulairePiece();
        
    }//GEN-LAST:event_modifierPieceJBActionPerformed

    private void supprimerPieceJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supprimerPieceJBActionPerformed
        
        FormulairePiece f = initFormulairePiece();
        String message ="êtes-vous sûr de vouloir supprimer cette pièce du stock ? \n cette opreation est irreversible";
        String titre = "Suppréssion de la pièce";
        int confirmation = JOptionPane.showConfirmDialog(null,message,titre,JOptionPane.WARNING_MESSAGE);
       
        JOptionPane.showMessageDialog(null, "Piece selectionée avant supprsion:"+pieceSelectionee);
        
        if(JOptionPane.OK_OPTION == confirmation )
        {
            
            Piece p = pieceSelectionee;
            
            System.out.println(p);
            
            
            Iterator<Piece> iterator = pieceList.iterator();

            while(iterator.hasNext()){
                if(Objects.equals((iterator.next()).getId_piece(), pieceSelectionee.getId_piece())){
                    iterator.remove();
                }
            }
             
           
            removePiece(p);
            
            updatePieceList();
            
            
            message ="La piece a etée supprimer du stock";
            JOptionPane.showMessageDialog(null,message,titre,JOptionPane.INFORMATION_MESSAGE);
            
        }
        else
        {
            message ="L'operation de suppression annulée";
            JOptionPane.showMessageDialog(null,message,titre,JOptionPane.INFORMATION_MESSAGE);
           
        }
        
        this.resetFormulairePiece();
        showPieces();
    }//GEN-LAST:event_supprimerPieceJBActionPerformed

    private void piecesJTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_piecesJTableMousePressed
           
           
           
    }//GEN-LAST:event_piecesJTableMousePressed

    private void factureJMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_factureJMIActionPerformed
        changerCard("facturesCard");
    }//GEN-LAST:event_factureJMIActionPerformed

    private void ajouterProfessionnelJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouterProfessionnelJBActionPerformed
        ajouterProfessionnelJtableRow();
        resetFormulaireProfessionnel();
    }//GEN-LAST:event_ajouterProfessionnelJBActionPerformed

    private void modifierProfessionnelJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifierProfessionnelJBActionPerformed
        String message ="êtes-vous sûr de vouloir modifier cette fiche client?";
        String titre = "Modification de la fiche";
        
        int confirmation = JOptionPane.showConfirmDialog(this.conteneurProfessionnelJP,message,titre,JOptionPane.OK_CANCEL_OPTION);
        
        if(JOptionPane.OK_OPTION == confirmation )
        {
                FormulaireProfessionnel f = initFormulaireProfessionnel();
                Professionnel newProfessionnel = null ;  
                if(f.hydrate().isEmpty())
                {
                    newProfessionnel = f.getProfessionnel();
                    newProfessionnel.setId(selectedProfessionnel().getId());
                    this.fs.updateProfessionnel(newProfessionnel);
                    updateProfessionnelList();
                    
                    message ="La fiche a été modifiée";
                    JOptionPane.showMessageDialog(this.conteneurProfessionnelJP,message,titre,JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    displayError(this.conteneurProfessionnelJP,f.validate());
                    message ="Opération Annulée";
                    JOptionPane.showMessageDialog(this.conteneurProfessionnelJP,message,titre,JOptionPane.INFORMATION_MESSAGE);
                }
        }
        else
        {
            message ="Opération Annulée";
            JOptionPane.showMessageDialog(this.conteneurProfessionnelJP,message,titre,JOptionPane.INFORMATION_MESSAGE);
        }
        
        resetFormulaireProfessionnel();
    }//GEN-LAST:event_modifierProfessionnelJBActionPerformed

    private void professionnelJTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_professionnelJTableMousePressed
           supprimerProfessionnelJB.setEnabled(true);
           modifierProfessionnelJB.setEnabled(true);
           ajouterProfessionnelJB.setEnabled(false);
           
    }//GEN-LAST:event_professionnelJTableMousePressed

    private void particulieJTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_particulieJTableMousePressed
           supprimerParticulieJB.setEnabled(true);
           modifierParticulieJB.setEnabled(true);
           ajouterParticulieJB.setEnabled(false);
    }//GEN-LAST:event_particulieJTableMousePressed

    private void ajouterParticulieJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouterParticulieJBActionPerformed
         ajouterParticulieJtableRow();
         resetFormulaireParticulie();
          
         
    }//GEN-LAST:event_ajouterParticulieJBActionPerformed

    private void supprimerProfessionnelJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supprimerProfessionnelJBActionPerformed
        FormulaireProfessionnel f = initFormulaireProfessionnel();
        String message ="êtes-vous sûr de vouloir supprimer ce client du répétoir client ? \n cette opreation est irreversible";
        String titre = "Suppréssion du client";
        int confirmation = JOptionPane.showConfirmDialog(this.conteneurProfessionnelJP,message,titre,JOptionPane.WARNING_MESSAGE);
        
        if(JOptionPane.OK_OPTION == confirmation )
        {
            
            this.fs.removeParticulie(selectedProfessionnel().getId());
            this.particulierList.remove(f.getProfessionnel());
            
            updateProfessionnelList();
            
            
            message ="Les information de ce client ont étés supprimer";
            JOptionPane.showMessageDialog(this.conteneurProfessionnelJP,message,titre,JOptionPane.INFORMATION_MESSAGE);
            
        }
        else
        {
            message ="L'operation de suppression annulée";
            JOptionPane.showMessageDialog(this.conteneurProfessionnelJP,message,titre,JOptionPane.INFORMATION_MESSAGE);
           
        }
        this.resetFormulaireProfessionnel();
    }//GEN-LAST:event_supprimerProfessionnelJBActionPerformed

    private void supprimerParticulieJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supprimerParticulieJBActionPerformed
        FormulaireParticulie f = initFormulaireParticulie();
        String message ="êtes-vous sûr de vouloir supprimer ce client du répétoir client ? \n cette opreation est irreversible";
        String titre = "Suppréssion du client";
        int confirmation = JOptionPane.showConfirmDialog(this.conteneurParticulieJP,message,titre,JOptionPane.WARNING_MESSAGE);
        
        if(JOptionPane.OK_OPTION == confirmation )
        {
            
            this.fs.removeParticulie(selectedParticulie().getId());
            this.particulierList.remove(f.getParticulie());
            
            updateParticulieJTable();
            
            
            message ="Les information de ce client ont étés supprimer";
            JOptionPane.showMessageDialog(this.conteneurParticulieJP,message,titre,JOptionPane.INFORMATION_MESSAGE);
            
        }
        else
        {
            message ="L'operation de suppression annulée";
            JOptionPane.showMessageDialog(this.conteneurParticulieJP,message,titre,JOptionPane.INFORMATION_MESSAGE);
           
        }
        this.resetFormulaireParticulie();
    }//GEN-LAST:event_supprimerParticulieJBActionPerformed

    private void modifierParticulieJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifierParticulieJBActionPerformed
        String message ="êtes-vous sûr de vouloir modifier cette fiche client?";
        String titre = "Modification de la fiche";
        
        int confirmation = JOptionPane.showConfirmDialog(this.conteneurParticulieJP,message,titre,JOptionPane.OK_CANCEL_OPTION);
        
        if(JOptionPane.OK_OPTION == confirmation )
        {
                FormulaireParticulie f = initFormulaireParticulie();
                Particulier  newParticulie = null ;  
                if(f.hydrate().isEmpty())
                {
                    newParticulie = f.getParticulie();
                    newParticulie.setId(selectedParticulie().getId());
                    this.fs.updateParticulie(newParticulie);
                    updateParticulieList();
                    
                    message ="La fiche a été modifiée";
                    JOptionPane.showMessageDialog(this.conteneurParticulieJP,message,titre,JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    displayError(this.conteneurParticulieJP,f.validate());
                    message ="Opération Annulée";
                    JOptionPane.showMessageDialog(this.conteneurParticulieJP,message,titre,JOptionPane.INFORMATION_MESSAGE);
                }
        }
        else
        {
            message ="Opération Annulée";
            JOptionPane.showMessageDialog(this.conteneurParticulieJP,message,titre,JOptionPane.INFORMATION_MESSAGE);
        }
        
        resetFormulaireParticulie();
    }//GEN-LAST:event_modifierParticulieJBActionPerformed

    private void ajouterFactureProfessionnelJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouterFactureProfessionnelJBActionPerformed
         
            //FacturerProfessionnel f = new FacturerProfessionnel(fs,ffs);
            
             FacturerProfessionnel f = new FacturerProfessionnel(fs,ffs);
                f.ajouterObservateur(this);
                f.setVisible(true);
      
    }//GEN-LAST:event_ajouterFactureProfessionnelJBActionPerformed
    
    private void facturerParticulierJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_facturerParticulierJButtonActionPerformed
        FacturerParticulier f;
                f = new FacturerParticulier(fs,ffs);
                f.ajouterObservateur(this);
                f.setVisible(true);
    }//GEN-LAST:event_facturerParticulierJButtonActionPerformed

    private void rechercheParticulierJTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rechercheParticulierJTFActionPerformed
                                                        
        int column = 1;
        if(nomJRB.isSelected())column=1;
        if(prenomJRB.isSelected())column=2; 
          
        String recherche=this.rechercheParticulierJTF.getText().toUpperCase(Locale.FRANCE);
        
        String pattern = Pattern.compile(recherche,
                        Pattern.CASE_INSENSITIVE).toString();
       
        
        RowFilter rowFilter = RowFilter.regexFilter( 
                Pattern.compile(recherche,
                        Pattern.CASE_INSENSITIVE).toString(),column);
        sorterParticulie.setRowFilter(rowFilter);  
    
    }//GEN-LAST:event_rechercheParticulierJTFActionPerformed

    private void rechercheProfessionnelJTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rechercheProfessionnelJTFActionPerformed
                                                                
        int column = 2;
       
          
        String recherche=this.rechercheProfessionnelJTF.getText().toUpperCase(Locale.FRANCE);
        
        String pattern = Pattern.compile(recherche,
                        Pattern.CASE_INSENSITIVE).toString();
       
        
        RowFilter rowFilter = RowFilter.regexFilter( 
                Pattern.compile(recherche,
                        Pattern.CASE_INSENSITIVE).toString(),column);
        sorterProfessionnel.setRowFilter(rowFilter);  
    }//GEN-LAST:event_rechercheProfessionnelJTFActionPerformed

    private void facturesParticulieJTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_facturesParticulieJTableMouseClicked
        int selectedRow = facturesParticulieJTable.getSelectedRow();
        selectedRow = facturesParticulieJTable.getRowSorter().convertRowIndexToModel(selectedRow);
        
        Long num_facture = (Long) facturesParticulieJTable.getModel().getValueAt(selectedRow,0);
        Facture f = fs.consulterFactureParticulierByNumFacture(num_facture);
        
       try {
            ffs.openFactureFile(f);
        } catch (IOException ex) {
            Logger.getLogger(FactureUI.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }//GEN-LAST:event_facturesParticulieJTableMouseClicked

    private void rechercheFactureParticulierJTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rechercheFactureParticulierJTFActionPerformed
        
        int column = 0;
        
        if(numeroFactureParticulierJRB.isSelected())column=0; 
        if(nomFactureParticulierJRB.isSelected())column=1;
        if(dateFacturationParticulierJRB.isSelected())column=2;
        if(dateLivraisonParticulierJRB.isSelected())column=3;
        
        String recherche=this.rechercheFactureParticulierJTF.getText().toUpperCase(Locale.FRANCE);
        
        String pattern = Pattern.compile(recherche,
                        Pattern.CASE_INSENSITIVE).toString();
       
        
        RowFilter rowFilter = RowFilter.regexFilter( 
                Pattern.compile(recherche,
                        Pattern.CASE_INSENSITIVE).toString(),column);
        sorterFactureParticulier.setRowFilter(rowFilter);  
    }//GEN-LAST:event_rechercheFactureParticulierJTFActionPerformed

    private void rechercheFactureProfessionnelJTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rechercheFactureProfessionnelJTFActionPerformed
        
         
        
    }//GEN-LAST:event_rechercheFactureProfessionnelJTFActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        changerCard("clientsCard");
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        changerCard("piecesCard");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        changerCard("clientsCard");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        changerCard("piecesCard");
    }//GEN-LAST:event_jButton4ActionPerformed

    private void recherchePieceJTFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_recherchePieceJTFKeyReleased
        
        
        
        
        int Ref = 1, Marque=2, Designation = 3;
        int[] coloumns = {Ref, Marque ,Designation};
    
        RowFilter rowFilter = null;
        String designation = this.recherchePieceJTF.getText().toUpperCase().trim();
        
        
        
        rechercheParRef(designation);
        
        rowFilter = RowFilter.regexFilter(
            Pattern.compile(designation,
                Pattern.CASE_INSENSITIVE).toString(),coloumns);
        
        sorterPiece = cacheSorterPieces.getIfPresent("cacheSorterPieces");
        this.sorterPiece.setRowFilter(rowFilter);
        
        
    }//GEN-LAST:event_recherchePieceJTFKeyReleased

    private void rechercheParRef(String ref) {
        
        
       
        
        if(ref.length()>=3){
            
            Query pieceQuery1 = entityManager.createQuery("SELECT p FROM Piece p where p.reference LIKE \'%"+ref+"%\'");
            Query pieceQuery2 = entityManager.createQuery("SELECT p FROM Piece p where p.designation LIKE \'%"+ref+"%\'");
                
                pieceList.clear();
                pieceList.addAll(pieceQuery1.getResultList());
                pieceList.addAll(pieceQuery2.getResultList());    
        }
        
        else{
            System.out.println("pieceListFromCachePieces de rechrcher");
                pieceListFromCachePieces();
            }
        
    }

    private void pieceListFromCachePieces() {
        
        List b = new ArrayList(cachePiece.getIfPresent("cachePieces"));
        
            pieceList.clear();
            pieceList.addAll(b);
       
    }

    private void pieceListFromCacheRechechePieces() {
        
        List b = new ArrayList(cachePieceRecherche.getIfPresent("cachePieceRecherche"));
        
            pieceList.clear();
            pieceList.addAll(b);
       
    }
    
    
    
    
    
    
    private void rechercheFactureProfessionnelJTFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rechercheFactureProfessionnelJTFKeyReleased
        
        int Ref = 1, Marque=2, Designation = 3;
        int[] coloumns = {Ref, Marque ,Designation};
    
        RowFilter rowFilter = null;
        
        if(!this.rechercheFactureProfessionnelJTF.getText().equals("")){
        String recherche = this.rechercheFactureProfessionnelJTF.getText().toUpperCase(Locale.FRANCE);
        
         rowFilter = RowFilter.regexFilter(
            Pattern.compile(recherche,
                Pattern.CASE_INSENSITIVE).toString(),0,1);
        
        }
        this.sorterFactureProfessionnel.setRowFilter(rowFilter);  
        
        
    }//GEN-LAST:event_rechercheFactureProfessionnelJTFKeyReleased

    private void facturesProfessionnelsJTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_facturesProfessionnelsJTableMouseClicked
       int selectedRow = facturesProfessionnelsJTable.getSelectedRow();
        selectedRow = facturesProfessionnelsJTable.getRowSorter().convertRowIndexToModel(selectedRow);
        
        Long num_facture = (Long) facturesProfessionnelsJTable.getModel().getValueAt(selectedRow,0);
        Facture f = fs.consulterFactureProfessionnelByNumFacture(num_facture);
        
       try {
            ffs.openFactureFile(f);
        } catch (IOException ex) {
            Logger.getLogger(FactureUI.class.getName()).log(Level.SEVERE, null, ex);
        }         // TODO add your handling code here:
    }//GEN-LAST:event_facturesProfessionnelsJTableMouseClicked

    private void piecesJTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_piecesJTableMouseClicked
          
        Point p = evt.getPoint();
        int r;
        r = piecesJTable.rowAtPoint(p);
        
        piecesJTable.setRowSelectionInterval(0, r);
        pieceSelectionee = selectedPiece();
        //JOptionPane.showMessageDialog(null, "Piece selectionée :"+pieceSelectionee);
        
           supprimerPieceJB.setEnabled(true);
           modifierPieceJB.setEnabled(true);
           ajouterPieceJB.setEnabled(false);
    }//GEN-LAST:event_piecesJTableMouseClicked
    
    public void changerCard(String cardName){
        Container contentPane = this.getContentPane();
        CardLayout cl = (CardLayout) this.getContentPane().getLayout();
        cl.show(contentPane, cardName);
    
    }
    
    public Piece selectedPiece(){
        Long id_piece ;
        String reference, designation ,marque ;
        Integer quantite;
        Double prixVente,prixAchat;
  
        
        int row = piecesJTable.getSelectedRow();
        
        
        System.out.println(row);
        TableRowSorter s = cacheSorterPieces.getIfPresent("cacheSorterPieces");
        row = s.convertRowIndexToModel(row);
        
        
        id_piece = (Long)piecesJTable.getModel().getValueAt(row,0);
        reference =  (String)piecesJTable.getModel().getValueAt(row,1);
        designation =  (String)piecesJTable.getModel().getValueAt(row,2);
        marque = (String)piecesJTable.getModel().getValueAt(row,3);
        prixAchat = (Double)piecesJTable.getModel().getValueAt(row,4);
        prixVente = (Double)piecesJTable.getModel().getValueAt(row,5);
        quantite = (Integer)piecesJTable.getModel().getValueAt(row,6); 
        
        return new Piece(id_piece, reference, marque, designation, prixAchat, prixVente, quantite);
        
  
    }
    

    
    public void ajouterPieceJtableRow(){
       
        FormulairePiece f = initFormulairePiece();
        
        
        
        if(f.hydrate().isEmpty())
        {
           
            pieceList.add(f.getPiece());
            
            this.fs.addPiece(f.getPiece()); 
            
            pieceListToCachePieceRecherche();
            
            ArrayList pieces =  new ArrayList(cachePiece.getIfPresent("cachePieces"));
            pieces.add(f.getPiece());
            cachePiece.put("cachePieces",pieces);
            
        }
        else
        {
            displayError(null,f.validate());
        }
        showPieces();
        
        ArrayList test = new ArrayList<Piece>();
            
        test = cachePiece.getIfPresent("cachePieces");
        Piece  lastp  = (Piece) test.get(test.size()-1);
        System.out.println(lastp);
    }

    public void pieceListToCachePieces() {
       
        ArrayList<Piece> b = new ArrayList<>(pieceList);
        cachePiece.put("cachePieces", b);
        
    }
    
     public void pieceListToCachePieceRecherche() {
       
        ArrayList<Piece> b = new ArrayList<>(pieceList);
        cachePiece.put("cachePieceRecherche", b);
        
    }
    
    
    public void modifierPieceJTableRow(Long id_piece ){
        FormulairePiece f = initFormulairePiece();
        Piece newPiece = null ;  
        
        if(f.hydrate().isEmpty())
        {
            newPiece = f.getPiece();
            newPiece.setId_piece(id_piece);
            this.fs.updatePiece(newPiece);
            updatePieceList();
        }
        else
        {
            displayError(this.conteneurStockJP,f.validate());
        }
        
        showPieces();
    }
    
    public FormulairePiece initFormulairePiece(){
        FormulairePiece formulaire = new FormulairePiece(
                this.referencePieceJT.getText(),
                this.designationPieceJT.getText(),
                this.marquePieceJT.getText(),
                this.quantiteJT.getText(),
                this.prixVentePieceJT.getText(), 
                this.prixAchatPieceJT.getText()
                       
                
        );
      return formulaire;
    }
    
    public void resetFormulairePiece(){
                
                this.piecesJTable.setSelectionMode(0);
                this.modifierPieceJB.setEnabled(false);
                this.supprimerPieceJB.setEnabled(false);
                this.ajouterPieceJB.setEnabled(true);
                this.referencePieceJT.setText("");
                this.designationPieceJT.setText("");
                this.marquePieceJT.setText("");
                this.quantiteJT.setText("");
                this.prixVentePieceJT.setText("");
                this.prixAchatPieceJT.setText("");
                
    }
    
    private void displayError(Container c, String validate) {
        JOptionPane.showMessageDialog(c,validate,"Erreur",JOptionPane.ERROR_MESSAGE);
    }
    
    public FormulaireProfessionnel initFormulaireProfessionnel(){
        FormulaireProfessionnel formulaire = new FormulaireProfessionnel(
               
                this.nomSocieteJTF.getText(),
                this.lieuSocieteJTA.getText(),
                this.villeSocieteJTF.getText(),
                this.codePostalSocieteJTF.getText() 
                );
      return formulaire;
    }
    
    public FormulaireParticulie initFormulaireParticulie(){
        FormulaireParticulie formulaire = new FormulaireParticulie(
                this.nomJTF.getText(),
                this.prenomJTF.getText(),
                this.lieuJTA.getText(),
                this.villeJTF.getText(),
                this.codePostalJTF.getText() 
                );
      return formulaire;
    }
    
    public void ajouterProfessionnelJtableRow(){
       
        FormulaireProfessionnel f = initFormulaireProfessionnel();
        
        if(f.hydrate().isEmpty())
        {
            this.professionnelList.add(f.getProfessionnel());
            this.fs.addProfessionnel(f.getProfessionnel());
            updateProfessionnelList();
        }
        else
        {
            displayError(this.conteneurProfessionnelJP , f.validate());
        }
        
    }
    
    public void ajouterParticulieJtableRow(){
       
        FormulaireParticulie f = initFormulaireParticulie();
        
        if(f.hydrate().isEmpty())
        {
            this.particulierList.add(f.getParticulie());
            this.fs.addParticulie(f.getParticulie());
            updateParticulieJTable();
        }
        else
        {
            displayError(this.conteneurParticulieJP,f.validate());
        }
        
    }
    
    public void resetFormulaireProfessionnel(){
                
                this.professionnelJTable.setSelectionMode(0);
                this.modifierProfessionnelJB.setEnabled(false);
                this.supprimerProfessionnelJB.setEnabled(false);
                this.ajouterProfessionnelJB.setEnabled(true);
              
                this.nomSocieteJTF.setText("");
                this.villeSocieteJTF.setText("");
                this.lieuSocieteJTA.setText("");
                this.codePostalSocieteJTF.setText("");
                
    }
    
    public void resetFormulaireParticulie(){
                
                this.particulieJTable.setSelectionMode(0);
                this.modifierParticulieJB.setEnabled(false);
                this.supprimerParticulieJB.setEnabled(false);
                this.ajouterParticulieJB.setEnabled(true);
                this.nomJTF.setText("");
                this.prenomJTF.setText("");
                this.villeJTF.setText("");
                this.lieuJTA.setText("");
                this.codePostalJTF.setText("");
                
    }
    
    public void updateProfessionnelList(){
      professionnelList.clear();
      professionnelList.addAll(professionnelQuery.getResultList());
    }
    
    public void updateParticulieList(){
      particulierList.clear();
      particulierList.addAll(particulierQuery.getResultList());
      updateInfos();
    }
    
    public void updateProfessionnelFactureList(){
      professionnelFactureList.clear();
      professionnelFactureList.addAll(professionnelQuery.getResultList());
      updateInfos();
    }
    
    public void filterByNomSocieteProfessionnelJTable( String nomSociete ){ 
        if(nomSociete==null || nomSociete.isEmpty())
        {
            this.updateProfessionnelFactureList();
        }
        else
        {
            professionnelFactureList.clear();
            professionnelFactureList.addAll(fs.consulterProfessionnelByNomSociete(nomSociete));
    
        } 

    }
    
    public void filterBySiretProfessionnelJTable( String nom ){ 
        if(nom==null || nom.isEmpty())
        {
            this.updateProfessionnelFactureList();
        }
        else
        {
            professionnelFactureList.clear();
            professionnelFactureList.addAll(fs.consulterProfessionnelByNom(nom));
    
        } 

    } 
    
    public void updateParticulieJTable(){
      particulierList.clear();
      particulierList.addAll(particulierQuery.getResultList());
      
    }
    
    public Professionnel selectedProfessionnel(){
        Long id_professionnel ;
        String nom,nomSociete,lieuSociete,villeSociete  ,codePostalSociete ;
        
       
        int row = professionnelJTable.getSelectedRow();
        row = professionnelJTable.getRowSorter().convertRowIndexToModel(row);
        id_professionnel = (Long)professionnelJTable.getModel().getValueAt(row,0);
        nom = (String)professionnelJTable.getModel().getValueAt(row,1);
        nomSociete =  (String)professionnelJTable.getModel().getValueAt(row,2);
        lieuSociete =  (String)professionnelJTable.getModel().getValueAt(row,3);
        villeSociete = (String)professionnelJTable.getModel().getValueAt(row,4);
        codePostalSociete = (String)professionnelJTable.getModel().getValueAt(row,5);
       
        Adresse adresse = new Adresse(lieuSociete,villeSociete,codePostalSociete);
        return new Professionnel(id_professionnel,nom, nomSociete,adresse );
        
  
    }
    
    public Particulier selectedParticulie(){
        Long id_particulie ;
        String nom,prenom,lieu,ville  ,codePostal;
        
       
        int row = this.particulieJTable.getSelectedRow();
        
        row = particulieJTable.getRowSorter().convertRowIndexToModel(row);
        id_particulie = (Long)particulieJTable.getModel().getValueAt(row,0);
        nom = (String)particulieJTable.getModel().getValueAt(row,1);
        prenom =  (String)particulieJTable.getModel().getValueAt(row,2);
        lieu=  (String)particulieJTable.getModel().getValueAt(row,3);
        ville = (String)particulieJTable.getModel().getValueAt(row,4);
        codePostal= (String)particulieJTable.getModel().getValueAt(row,5);
       
        Adresse adresse = new Adresse(lieu,ville,codePostal);
        return new Particulier(id_particulie, nom, prenom,adresse );
        
  
    }
     
    protected void updatePieceList(){
     
        this.getValeurStockRestant();
     
    }    
    
    protected void updateFactureProfessionelList() {
       factureProfessionnelList.clear();
        factureProfessionnelList.addAll(factureProfessionnelQuery.getResultList()); 
        updateCaProfessionnel();
    }           
    
    protected void updateFactureParticulierList() {
        factureParticulierList.clear();
        factureParticulierList.addAll(factureParticulierQuery.getResultList()); 
    }
   
    @Override
    public void update() {
       this.updatePieceList();
       this.updateParticulieList();
       this.updateProfessionnelList();
       this.updateFactureProfessionelList();
       this.updateFactureParticulierList();
       this.getValeurStockRestant();
       this.updateInfos();
    }  
    
    public Date convertStringToDate(String dateString){
        Date date =null;  
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
           
           try {
               date = dateFormat.parse(dateString);
               
           } catch (ParseException ex) {
               Logger.getLogger(FactureUI.class.getName()).log(Level.SEVERE, null, ex);
           }
    
           return date;
    
    }
    
    public String convertDateToString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }
     
    public Double getValeurStockRestant(){
        valeur=0.0;
        TableModel model = piecesJTable.getModel();
        int count = piecesJTable.getRowCount();
        
        for(int row = 0 ; row<count; row++)
        {
      //     valeur = valeur + (Double)model.getValueAt(row, 7);
        }
        
        
        this.valeurStockJL.setText("Valeur du stock restant "+String.format( "%,.2f", valeur)+"€");
        return valeur;
    }
     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu accueilJM;
    private javax.swing.JPanel ajoutModificationJP;
    private javax.swing.JPanel ajouterFactureJP;
    private javax.swing.JButton ajouterFactureProfessionnelJB;
    private javax.swing.JButton ajouterParticulieJB;
    private javax.swing.JPanel ajouterParticulieJP;
    private javax.swing.JButton ajouterPieceJB;
    private javax.swing.JButton ajouterProfessionnelJB;
    private javax.swing.JMenuBar appJMenuBar;
    private javax.swing.JLabel caParticulierJL;
    private javax.swing.JLabel caProfessionnelJL;
    private javax.swing.JLabel caProfessionnelTitreJL;
    private javax.swing.JMenuItem clientsJMI;
    private javax.swing.JPanel clientsJP;
    private javax.swing.JTabbedPane clientsJTP;
    private javax.swing.JLabel codePostalJL;
    private javax.swing.JTextField codePostalJTF;
    private javax.swing.JLabel codePostalSocieteJL;
    private javax.swing.JTextField codePostalSocieteJTF;
    private javax.swing.JPanel conteneurParticulieJP;
    private javax.swing.JPanel conteneurProfessionnelJP;
    private javax.swing.JPanel conteneurStockJP;
    private javax.swing.JRadioButton dateFacturationParticulierJRB;
    private javax.swing.JRadioButton dateLivraisonParticulierJRB;
    private javax.swing.JLabel designationJL;
    private javax.swing.JTextField designationPieceJT;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JMenuItem factureJMI;
    private javax.swing.JPanel factureParticulierJP;
    private java.util.List<facturation.entities.Facture> factureParticulierList;
    private javax.persistence.Query factureParticulierQuery;
    private javax.swing.JPanel factureProfessionnelJP;
    private java.util.List<facturation.entities.Facture> factureProfessionnelList;
    private javax.persistence.Query factureProfessionnelQuery;
    private javax.swing.JButton facturerParticulierJButton;
    private javax.swing.JPanel facturesCardJP;
    private javax.swing.JTabbedPane facturesJTabbedPane;
    private javax.swing.JScrollPane facturesParticulieJSP;
    private javax.swing.JTable facturesParticulieJTable;
    private javax.swing.JScrollPane facturesProfessionelsJSP;
    private javax.swing.JTable facturesProfessionnelsJTable;
    private javax.swing.ButtonGroup filtreFactureParticulierBG;
    private javax.swing.ButtonGroup filtreFactureProfessionnelBG;
    private javax.swing.ButtonGroup filtrePieceBG;
    private javax.swing.ButtonGroup filtreProfessionnelBG;
    private javax.swing.ButtonGroup fitreParticulierBG;
    private javax.swing.JLabel infosBeneficesParticulierJL;
    private javax.swing.JLabel infosBeneficesProfessionnelJL;
    private javax.swing.JLabel infosBeneficesTitreJL;
    private javax.swing.JLabel infosBeneficesTotalJL;
    private javax.swing.JLabel infosCATitreJL;
    private javax.swing.JLabel infosCaParticuliersJL;
    private javax.swing.JLabel infosCaProfessionnelsJL;
    private javax.swing.JLabel infosCaTotalJL;
    private javax.swing.JLabel infosClientTitreJL;
    private javax.swing.JLabel infosCoutParticulierJL;
    private javax.swing.JLabel infosCoutProfessionnelJL;
    private javax.swing.JLabel infosCoutTitreJL;
    private javax.swing.JLabel infosCoutTotalJL;
    private javax.swing.JPanel infosJP;
    private javax.swing.JLabel infosParticuliersTitreJL;
    private javax.swing.JLabel infosProfessionnelsTitreJL;
    private javax.swing.JLabel infosTaxeParticuliersJL;
    private javax.swing.JLabel infosTaxeProfessionnelsJL;
    private javax.swing.JLabel infosTaxeTitreJL;
    private javax.swing.JLabel infosTaxeTotalJL;
    private javax.swing.JLabel infosTotalTitreJL;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lieuJL;
    private javax.swing.JScrollPane lieuJSP;
    private javax.swing.JTextArea lieuJTA;
    private javax.swing.JLabel lieuSocieteJL;
    private javax.swing.JScrollPane lieuSocieteJSP;
    private javax.swing.JTextArea lieuSocieteJTA;
    private java.util.List list1;
    private javax.swing.JLabel marqueJL;
    private javax.swing.JTextField marquePieceJT;
    private javax.swing.JButton modifierParticulieJB;
    private javax.swing.JButton modifierPieceJB;
    private javax.swing.JButton modifierProfessionnelJB;
    private javax.swing.JRadioButton nomFactureParticulierJRB;
    private javax.swing.JLabel nomJL;
    private javax.swing.JRadioButton nomJRB;
    private javax.swing.JTextField nomJTF;
    private javax.swing.JLabel nomSocieteJL;
    private javax.swing.JRadioButton nomSocieteJRB;
    private javax.swing.JTextField nomSocieteJTF;
    private javax.swing.JRadioButton numeroFactureParticulierJRB;
    private javax.swing.JTable particulieJTable;
    private java.util.List particulierList;
    private javax.persistence.Query particulierQuery;
    private javax.swing.JPanel particuliesJP;
    private javax.swing.JScrollPane particuliesJSP;
    private javax.swing.JPanel particuliesTabJP;
    private java.util.List<facturation.entities.Piece> pieceList;
    private javax.persistence.Query pieceQuery;
    private javax.swing.JPanel piecesCardJP;
    private javax.swing.JMenuItem piecesJMI;
    private javax.swing.JScrollPane piecesJSP;
    private javax.swing.JTable piecesJTable;
    private javax.swing.JLabel prenomJL;
    private javax.swing.JRadioButton prenomJRB;
    private javax.swing.JTextField prenomJTF;
    private javax.swing.JLabel prixAchatJL;
    private javax.swing.JTextField prixAchatPieceJT;
    private javax.swing.JLabel prixVenteJL;
    private javax.swing.JTextField prixVentePieceJT;
    private java.util.List professionnelFactureList;
    private javax.swing.JTable professionnelJTable;
    private java.util.List professionnelList;
    private javax.persistence.Query professionnelQuery;
    private javax.swing.JPanel professionnelsJP;
    private javax.swing.JScrollPane professionnelsJSP;
    private javax.swing.JPanel professionnelsTabJP;
    private javax.swing.JLabel quantiteJL;
    private javax.swing.JTextField quantiteJT;
    private javax.swing.JPanel rechercheFactureParticulierJP;
    private javax.swing.JTextField rechercheFactureParticulierJTF;
    private javax.swing.JPanel rechercheFactureProfessionnelJP;
    private javax.swing.JTextField rechercheFactureProfessionnelJTF;
    private javax.swing.JPanel rechercheParticulierJP;
    private javax.swing.JTextField rechercheParticulierJTF;
    private javax.swing.JPanel recherchePieceJP;
    private javax.swing.JTextField recherchePieceJTF;
    private javax.swing.JPanel rechercheProfessionnelJP;
    private javax.swing.JTextField rechercheProfessionnelJTF;
    private javax.swing.JTextField referencePieceJT;
    private javax.swing.JLabel refernceJL;
    private javax.swing.JPanel stockJP;
    private javax.swing.JButton supprimerParticulieJB;
    private javax.swing.JButton supprimerPieceJB;
    private javax.swing.JButton supprimerProfessionnelJB;
    private javax.swing.JLabel valeurStockJL;
    private javax.swing.JLabel villeJL;
    private javax.swing.JTextField villeJTF;
    private javax.swing.JLabel villeSocieteJL;
    private javax.swing.JTextField villeSocieteJTF;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    protected Double getCAProfessionnel() {
        List<Facture> facturesProfessionnel = this.factureProfessionnelList;
        Double ca = 0.0; 
        for(Facture f : facturesProfessionnel   )
            ca = ca + f.getTotalHT();
        
        return ca ; 
    
    }
    
    protected Double getCAParticulier() {
        List<Facture> facturesProfessionnel = this.factureParticulierList;
        Double ca = 0.0; 
        for(Facture f : facturesProfessionnel   )
            ca = ca + f.getTotalHT();
        
        return ca ; 
    
    }
    
    protected Double getTotalTvaParticulier() {
        List<Facture> facturesParticulier = this.factureParticulierList;
        Double tva = 0.0; 
        for(Facture f : facturesParticulier   )
        {  
            tva = tva + f.getTotalTVA();
        }
        
        return tva ; 
    
    }
    
    protected Double getTotalTvaProfessionnel() {
        List<Facture> factures = this.factureProfessionnelList;
        Double tva = 0.0; 
        for(Facture f : factures  )
            tva = tva + f.getTotalTVA();
        return tva ;
    } 
    
    protected void updateTotalTvaParticulier(){
       String tva = String.format( "%,.2f", this.getTotalTvaParticulier())+"€";
       this.infosTaxeParticuliersJL.setText(tva);
    }
    
    protected void updateTotalTvaProfessionnel(){
        String tva = String.format( "%,.2f", this.getTotalTvaProfessionnel())+"€";
        this.infosTaxeProfessionnelsJL.setText(tva);
    }
    
    protected Double getCoutParticulier() {
        List<Facture> factures = this.factureParticulierList;
        List<Ligne> lignes;
        Double cout = 0.0; 
        
        for(Facture f : factures )
        {
            lignes = f.getLignes();
            for(Ligne l : lignes)
            {
                cout = cout +(l.getPiece().getPrixAchat()*l.getQuantite());
                
            }
        }
    
        return cout ; 
    }
    
    protected Double getCoutProfessionnel() {
        List<Facture> factures = this.factureProfessionnelList;
        List<Ligne> lignes;
        Double cout = 0.0; 
        
        for(Facture f : factures )
        {
            lignes = f.getLignes();
            for(Ligne l : lignes)
            {
                cout = cout +(l.getPiece().getPrixAchat()*l.getQuantite());
                
            }
        }
            
        return cout ; 
    
    }
    
    protected Double getBeneficesProfessionnel() {
        
        Double benef = this.getCAProfessionnel()-this.getCoutProfessionnel() ;
        
        return benef;
    }
    
    protected Double getBeneficesParticulier() {
        
        Double benef = this.getCAParticulier()-this.getCoutParticulier() ;
        
        return benef;
    }
    
    protected void updateBeneficesProfessionnel(){
        
        Double benef =  this.getBeneficesProfessionnel();
        String benefString = String.format( "%,.2f", benef)+"€";
        this.infosBeneficesProfessionnelJL.setText(benefString);
        
    }
    
    protected void updateBeneficesParticulier(){
        
        Double benef =  this.getBeneficesParticulier();
        String benefString = String.format( "%,.2f", benef)+"€";
        this.infosBeneficesParticulierJL.setText(benefString);
        
    }
    
    protected void updateCoutProfessionnel(){
       
        Double cout =  this.getCoutProfessionnel();
        String coutTotal = String.format( "%,.2f", cout)+"€";
        this.infosCoutProfessionnelJL.setText(coutTotal);
        
    }
    
    protected void updateCoutParticulier(){
        
        Double cout =  this.getCoutParticulier();
        String tva = String.format( "%,.2f", cout)+"€";
        this.infosCoutParticulierJL.setText(tva);
        
    }
    
    protected void updateTotalTva(){
        
        Double totalTva =  this.getTotalTvaProfessionnel()+this.getTotalTvaParticulier();
        String tva = String.format( "%,.2f", totalTva)+"€";
        this.infosTaxeTotalJL.setText(tva);
    }
    
    protected void updateTotalCa(){
        
        Double totalCa=  this.getCAProfessionnel()+this.getCAParticulier();
        String ca = String.format( "%,.2f", totalCa)+"€";
        this.infosCaTotalJL.setText(ca);
       
    }
    
    protected void updateCoutTotal(){
        
        Double total=  this.getCoutProfessionnel()+this.getCoutParticulier();
        String cout = String.format( "%,.2f", total)+"€";
        this.infosCoutTotalJL.setText(cout);
       
    }
    
    protected void updateBeneficesTotal(){
        
        Double total=  this.getBeneficesProfessionnel()+this.getBeneficesParticulier();
        String cout = String.format( "%,.2f", total)+"€";
        this.infosBeneficesTotalJL.setText(cout);
       
    }
    
    protected void updateInfos(){
        
        updateCaProfessionnel();
        updateCaParticulier();
        updateTotalCa();
        
        updateTotalTvaParticulier();
        updateTotalTvaProfessionnel();
        updateTotalTva();
        
        updateCoutParticulier();
        updateCoutProfessionnel();
        updateCoutTotal();
        
        updateBeneficesProfessionnel();
        updateBeneficesParticulier();
        updateBeneficesTotal();
    }

    protected void updateCaProfessionnel(){
       String ca = String.format( "%,.2f", getCAProfessionnel())+"€";
       this.caProfessionnelJL.setText(ca);
       
   }
    
    protected void updateCaParticulier(){
       String ca = String.format( "%,.2f", getCAParticulier())+"€";
       this.caParticulierJL.setText(ca);
       
   }

    private TableModel getModel() {
        TableModel model = new PieceTableModel(entityManager, 17737);
        return  model;
    }
   
    
    
   

    

   
    
   
    
}   

