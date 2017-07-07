package com.unipd.fabio.agorun;

import java.util.ArrayList;

/**
 * Created by riccardo on 16/05/17.
 */

/* Interfaccia necessaria per le classi che vogliono connettersi al DB
 * Il metodo implementato viene usato per trasmettere alla classe l'output del DB
 */

public interface DBConnection {
    void onTaskCompleted(ArrayList<String> result);
}
