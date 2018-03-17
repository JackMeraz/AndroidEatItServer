package com.dev.jackmeraz.androideatitserver.Common;

import com.dev.jackmeraz.androideatitserver.Model.User;

/**
 * Created by jacobo.meraz on 20/01/2018.
 */

public class Common {

    public static User currentUser;

    public static final String UPDATE = "Actualizar";
    public static final String DELETE = "Borrar";

    public static final int PICK_IMAGE_REQUEST = 71;

    public static String convertirCodigoToStatus (String codigo)
    {
        if (codigo.equals("0"))
            return "En Espera de ser Procesado";
        else if (codigo.equals("1"))
            return "Preparando Orden para ser Enviada";
        else
            return "Orden en proceso de entrega";
    }

}
