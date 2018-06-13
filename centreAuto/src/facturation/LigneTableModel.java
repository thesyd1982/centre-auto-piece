/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation;


import facturation.entities.Ligne;
import facturation.entities.Piece;
import java.util.List;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;


public class LigneTableModel extends AbstractTableModel{
     
    protected List<Ligne> lignes = new ArrayList<>();
    
    protected  String[] entetes = {"N° Pièce",
        "Référence", 
        "Marque",
        "Prix UHT",
        "Quantité commandée",
        "Remise %",
        };

    public List<Ligne> getLignes() {
        return lignes;
    }

    public void setLignes(List<Ligne> lignes) {
        this.lignes = lignes;
    }

    public String[] getEntetes() {
        return entetes;
    }

    public void setEntetes(String[] entetes) {
        this.entetes = entetes;
    }

    public LigneTableModel() {
    
                     this.lignes = new ArrayList<>();
    }
    
    public LigneTableModel(List<Ligne> lignes) {
           this.lignes = lignes;
    }
    
    
    @Override
    public int getRowCount() {
        return lignes.size();
    }

    @Override
    public int getColumnCount() {
        return entetes.length;
    }
    
    @Override
    public String getColumnName(int columnIndex){
        return entetes[columnIndex];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
       
        switch(columnIndex){
           case 0:
                 return lignes.get(rowIndex).getPiece().getId_piece();
            case 1:
                return lignes.get(rowIndex).getPiece().getReference();
            case 2:
                 return lignes.get(rowIndex).getPiece().getMarque();
            case 3:
                 return lignes.get(rowIndex).getPrixUHT();
            case 4:
                return lignes.get(rowIndex).getQuantite();
        
            case 5:
                 return lignes.get(rowIndex).getRemise()*100;
           
            
            default:
                 throw new IllegalArgumentException();
        }
    }
    
    @Override
    public Class getColumnClass(int columnIndex){
        switch(columnIndex)
        {   
            case 0:
                return Long.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
               return Double.class;
            case 4:
               return Integer.class;
            case 5:
               return Double.class;
          default:
                return Object.class;
        }
    }
    
    public void addLigne(Ligne ligne){
        lignes.add(ligne);
        fireTableRowsInserted(lignes.size() -1, lignes.size() -1);
    }   
    
    public void addLigne(Piece p){
        Ligne ligne =new Ligne();
        ligne.setPiece(p);
        ligne.setPrixUHT(p.getPrixVente());
        ligne.setQuantite(0);
        lignes.add(ligne);
        fireTableRowsInserted(lignes.size() -1, lignes.size() -1);
    }   
    
    
    public void removeLigne(int rowIndex){
        lignes.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
    public void updateLigne(int rowIndex , Piece p , Integer quantite , Double prix ){
        Ligne l = lignes.get(rowIndex);
        l.setPiece(p);
        l.setPrixUHT(prix);
        l.setQuantite(quantite);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        
                 return (columnIndex == 3 || columnIndex==4|| columnIndex==5|| columnIndex==6|| columnIndex==7 );
       
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        
            if(aValue != null)
            {
                Ligne l = lignes.get(rowIndex);
                switch(columnIndex){
                
                case 0:
                    l.getPiece().setId_piece((Long)aValue);
                break;
                
                case 1:
                    l.getPiece().setReference((String)aValue);
                break;
                
                case 2:
                    l.getPiece().setMarque((String)aValue);
                break;
                
                case 3:
                     l.setPrixUHT((Double)aValue);
                break;
                
                case 4:
                    if( l.getPiece().getQuantite() >= (Integer)aValue ) {
                        l.setQuantite((Integer)aValue);
                    }
                break;
                
                case 5:
               
                     l.setRemise(((Double)aValue)/100);
                break;
                
              
            
                
            }
            this.fireTableCellUpdated(rowIndex,rowIndex); 
            
            }
        }

    public List<Ligne> getPieces() {
        return lignes;
    }

    public void setPieces(List<Ligne> lignes) {
        this.lignes = lignes;
    }
    
}


