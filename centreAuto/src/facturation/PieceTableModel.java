/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation;


import facturation.entities.Piece;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author tayeb
 */
public class PieceTableModel  extends AbstractTableModel{
     
    private final EntityManager manager ;
    
    private int startPosition ;

    private int counter=0;
    
    protected List<Piece> pieces = new ArrayList<>();
    
    protected  String[] entetes = {"N°","Référence", "Marque","Designation","Prix achat","Prix vente","Quantité"};

    public String[] getEntetes() {
        return entetes;
    }

    public void setEntetes(String[] entetes) {
        this.entetes = entetes;
    }

    public PieceTableModel() {
        manager =null;
     this.pieces = new ArrayList<>();
             
    }
    
    public PieceTableModel(List<Piece> pieces) {
            System.out.println(pieces.size());
            manager =null;
            this.pieces = pieces;
    }
    
    
     public PieceTableModel(EntityManager manager , int startPosition) {
            this.manager = manager;
            this.startPosition = startPosition;
            this.pieces = getItems(startPosition, startPosition+100);
  }
    
    
    
    @Override
    public int getRowCount() {
        return pieces.size();
        

    }

    @Override
    public int getColumnCount() {
        return entetes.length;
    }
    
    @Override
    public String getColumnName(int columnIndex){
        return entetes[columnIndex];
    }
    
//    @Override
//    public Object getValueAt(int rowIndex, int columnIndex) {
//        switch(columnIndex){
//            case 0:
//                return pieces.get(rowIndex).getId_piece();
//            case 1:
//                return pieces.get(rowIndex).getReference();
//        
//            case 2:
//                 return pieces.get(rowIndex).getMarque();
//            case 3:
//                 return pieces.get(rowIndex).getDesignation();
//            case 4:
//                 return pieces.get(rowIndex).getPrixAchat();
//            case 5:
//                 return pieces.get(rowIndex).getPrixVente();
//            case 6:
//                 return pieces.get(rowIndex).getQuantite();
//            
//            default:
//                 throw new IllegalArgumentException();
//        }
//    }
    
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
                return String.class;
            case 4:
                return Double.class;
            case 5:
                return Double.class;
            case 6:
                return Integer.class;
            default:
                throw new IllegalArgumentException();
        }
    }
    
    public void addPiece(Piece piece){
        pieces.add(piece);
        fireTableRowsInserted(pieces.size() -1, pieces.size() -1);
    }   
    
    public void removePiece(int rowIndex){
        pieces.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; //Toutes les cellules éditables
        }
    
 
  @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if(aValue != null)
            {
                Piece p = pieces.get(rowIndex);
                switch(columnIndex){
                
                case 0:
                    p.setId_piece((Long)aValue);
                break;
                
                case 1:
                    p.setReference((String)aValue);
                break;
                
                case 2:
                    p.setMarque((String)aValue);
                break;
                
                case 3:
                    p.setDesignation((String)aValue);
                break;
                
                case 4:
                     p.setPrixAchat((Double)aValue);
                break;
                
                case 5:
                    p.setPrixVente((Double)aValue);
                break;
                
                case 6:
                    p.setQuantite((Integer)aValue);
                break;
                
                
            }
            this.fireTableRowsUpdated(rowIndex,rowIndex);
            }
        }

    public List<Piece> getPieces() {
        return pieces;
    }

    public void setPieces(List<Piece> pieces) {
        this.pieces = pieces;
    }   
    
    
    
    @Override
  public Object getValueAt(int rowIndex, int columnIndex) {

    if((rowIndex >= startPosition) && (rowIndex<(startPosition+100))){

    } else
    {
      this.pieces = getItems(rowIndex, rowIndex+100);
      this.startPosition=rowIndex;
    }
    Piece p = pieces.get(rowIndex-startPosition);

    Object toReturn ;
    switch (columnIndex) {
            case 0:
                toReturn = p.getId_piece();
                break;
            case 1:
                toReturn = p.getReference();
                break;
            case 2:
                 toReturn = p.getMarque();
                 break;
            case 3:
                 toReturn = p.getDesignation();
                 break;
            case 4:
                 toReturn = p.getPrixAchat();
                 break;
            case 5:
                 toReturn = p.getPrixVente();
                 break;
            case 6:
                 toReturn = p.getQuantite();
                 break;
            default:
                 toReturn = p.getId_piece();
    }
    return toReturn;
  }

  private List<Piece> getItems(int from, int to) {
    System.out.println("number of requests to the database "+counter++);
    Query pieceQuery = manager.createQuery("SELECT p FROM Piece p").setMaxResults(to-from).setFirstResult(from);

    //add the cache
    List<Piece> resultList = pieceQuery.getResultList();
    
  
        
    
    return resultList;
  }
    
}
