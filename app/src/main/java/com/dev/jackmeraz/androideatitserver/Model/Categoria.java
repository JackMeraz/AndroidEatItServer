package com.dev.jackmeraz.androideatitserver.Model;

/**
 * Created by jacobo.meraz on 27/01/2018.
 */

public class Categoria {

    private String Name;
    private String Image;

    public Categoria() {
    }

    public Categoria(String name, String image) {
        Name = name;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
