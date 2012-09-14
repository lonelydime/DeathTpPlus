package org.simiancage.DeathTpPlus.tomb.persistence;

/**
 * PluginName: ${plugin}
 * Class: TombSaveSystem
 * User: DonRedhorse
 * Date: 14.11.11
 * Time: 20:39
 */

import org.simiancage.DeathTpPlus.tomb.models.Tomb;

import java.io.*;
import java.util.HashMap;


public class TombSaveSystem {
    private String path;

    public TombSaveSystem(String path) {
        this.path = path;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    /**
     * Save all the tombs to the file tombs.dat
     *
     * @param toBeSaved
     */
    public void save(HashMap<String, Tomb> toBeSaved) {
        File saveFile = new File(this.path + File.separator + "tombs.dat");
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        HashMap<String, TombLog> toWrite = new HashMap<String, TombLog>();
        for (String name : toBeSaved.keySet()) {
            toWrite.put(name, toBeSaved.get(name).save());
        }

        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(toWrite);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    public HashMap<String, Tomb> load() {
        HashMap<String, Tomb> result = new HashMap<String, Tomb>();
        HashMap<String, TombLog> saved = null;
        File saveFile = new File(this.path + File.separator + "tombs.dat");
        if (!saveFile.exists()) {
            return new HashMap<String, Tomb>();
        }

        FileInputStream fis = null;
        ObjectInputStream in = null;

        try {
            fis = new FileInputStream(saveFile);
            in = new ObjectInputStream(fis);
            saved = (HashMap<String, TombLog>) in.readObject();
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        if (saved == null) {
            return new HashMap<String, Tomb>();
        }
        for (String name : saved.keySet()) {
            result.put(name, saved.get(name).load());
        }

        return result;

    }

}


