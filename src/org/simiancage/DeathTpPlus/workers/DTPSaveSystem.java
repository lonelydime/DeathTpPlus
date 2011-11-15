package org.simiancage.DeathTpPlus.workers;

/**
 * PluginName: ${plugin}
 * Class: DTPSaveSystem
 * User: DonRedhorse
 * Date: 14.11.11
 * Time: 20:39
 */

import org.simiancage.DeathTpPlus.DTPTomb;
import org.simiancage.DeathTpPlus.DTPTombSave;

import java.io.*;
import java.util.HashMap;



public class DTPSaveSystem {
    String path;

    public DTPSaveSystem(String path) {
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
    public void save(HashMap<String, DTPTomb> toBeSaved) {
        File saveFile = new File(this.path + File.separator + "tombs.dat");
        if (!saveFile.exists())
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        HashMap<String, DTPTombSave> toWrite = new HashMap<String, DTPTombSave>();
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
    public HashMap<String, DTPTomb> load() {
        HashMap<String, DTPTomb> result = new HashMap<String, DTPTomb>();
        HashMap<String, DTPTombSave> saved=null;
        File saveFile = new File(this.path + File.separator + "tombs.dat");
        if (!saveFile.exists())
            return new HashMap<String, DTPTomb>();

        FileInputStream fis = null;
        ObjectInputStream in = null;

        try {
            fis = new FileInputStream(saveFile);
            in = new ObjectInputStream(fis);
            saved = (HashMap<String, DTPTombSave>) in.readObject();
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        if(saved == null)
            return new HashMap<String, DTPTomb>();
        for(String name : saved.keySet())
            result.put(name, saved.get(name).load());

        return result;

    }

}


