/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.enumeration;

/**
 *
 * @author tayeb
 */
public enum Mois {
    Janvier,
    Fevrier,
    Mars,
    Avril,
    Mai,
    Juin,
    Juilet,
    Aout,
    Septembre,
    Octobre, 
    Novembre,
    Decembre;

   public static Mois of(int index){
       Mois mois =Mois.Janvier;
       switch(index){
           case 2:
               mois = Mois.Fevrier; 
           break;
           case 3:
               mois = Mois.Mars ; 
           break;
           case 4:
               mois = Mois.Avril ; 
           break;
           case 5:
               mois = Mois.Mai;  
           break;
           case 6:
               mois = Mois.Juin;  
           break;
           case 7:
               mois = Mois.Juilet ; 
           break;
           case 8:
               mois = Mois.Aout;  
           break;
           case 9:
               mois = Mois.Septembre ; 
           break;
           case 10:
               mois = Mois.Octobre;  
           break;
           case 11:
               mois = Mois.Novembre ;  
           break;
           case 12:
             mois = Mois.Decembre ; 
            break;
           
           default:
                
           break;
       
       }
       return mois;
   } 
    
}
