package org.simiancage.DeathTpPlus.helpers;

/**
 * PluginName: ${plugin}
 * Class: TombSaveSystemDTP
 * User: DonRedhorse
 * Date: 14.11.11
 * Time: 20:39
 */

import org.simiancage.DeathTpPlus.logs.TombLogDTP;
import org.simiancage.DeathTpPlus.objects.TombDTP;

import java.io.*;
import java.util.HashMap;



public class TombSaveSystemDTP {
    String path;

    public TombSaveSystemDTP(String path) {
        this.path = path;
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdir();
    }

    /**
     * Save all the tombs to the file tombs.dat
     *
     * @param toBeSaved
     */
    public void save(HashMap<String, TombDTP> toBeSaved) {
        File saveFile = new File(this.path + File.separator + "tombs.dat");
        if (!saveFile.exists())
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        HashMap<String, TombLogDTP> toWrite = new HashMap<String, TombLogDTP>();
        for (String name : toBeSaved.keySet())
            toWrite.put(name, toBeSaved.get(name).save());

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
    public HashMap<String, TombDTP> load() {
        HashMap<String, TombDTP> result = new HashMap<String, TombDTP>();
        HashMap<String, TombLogDTP> saved=null;
        File saveFile = new File(this.path + File.separator + "tombs.dat");
        if (!saveFile.exists())
            return new HashMap<String, TombDTP>();

        FileInputStream fis = null;
        ObjectInputStream in = null;

        try {
            fis = new FileInputStream(saveFile);
            in = new ObjectInputStream(fis);
            saved = (HashMap<String, TombLogDTP>) in.readObject();
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        if(saved == null)
            return new HashMap<String, TombDTP>();
        for(String name : saved.keySet())
            result.put(name, saved.get(name).load());

        return result;

    }

}


