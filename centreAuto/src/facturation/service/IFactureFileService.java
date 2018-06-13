/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturation.service;

import facturation.entities.Facture;
import java.nio.file.Path;

/**
 *
 * @author tayeb
 */
public interface IFactureFileService {
    
    public Path createFileFacture(Facture facture);
    public String removeFileFacture(Facture facture);
    
    
}
