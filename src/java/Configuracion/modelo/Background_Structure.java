/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Configuracion.modelo;

import java.util.List;

/**
 *
 * @author ricparmo
 */
public class Background_Structure {
    
    private List<Float> background_Color;
    private String Name_Part_Product;
    private int Code_Part_Product;

    public int getCode_Part_Product() {
        return Code_Part_Product;
    }

    public void setCode_Part_Product(int Code_Part_Product) {
        this.Code_Part_Product = Code_Part_Product;
    }

    public String getName_Part_Product() {
        return Name_Part_Product;
    }

    public void setName_Part_Product(String Name_Part_Product) {
        this.Name_Part_Product = Name_Part_Product;
    }

    public  List<Float> getBackground_Color() {
        return background_Color;
    }

    public void setBackground_Color(List<Float> background_Color) {
        this.background_Color = background_Color;
    }
    
    
    public Background_Structure(){
        
    }

    
    
}
