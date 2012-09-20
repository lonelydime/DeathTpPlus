package org.simiancage.DeathTpPlus.commons;

import java.util.List;

import com.ensifera.animosity.craftirc.CraftIRC;
import com.ensifera.animosity.craftirc.EndPoint;
import com.ensifera.animosity.craftirc.RelayedMessage;

public class CraftIRCEndPoint implements EndPoint
{

    private CraftIRC craftIRCPlugin;
    private boolean registered;

    public CraftIRCEndPoint(CraftIRC plugin)
    {
        craftIRCPlugin = plugin;
        registered = craftIRCPlugin.registerEndPoint(ConfigManager.getInstance().getIrcDeathTpTag(), this);
    }

    @Override
    public boolean adminMessageIn(RelayedMessage rm)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Type getType()
    {
        return EndPoint.Type.MINECRAFT;
    }

    @Override
    public List<String> listDisplayUsers()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> listUsers()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void messageIn(RelayedMessage rm)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean userMessageIn(String arg0, RelayedMessage rm)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isRegistered()
    {
    	return registered;
    }

    public void sendMessage(String message)
    {
    	if (registered) {
	        final RelayedMessage rm = craftIRCPlugin.newMsg(this, null, "generic");
	        rm.setField("message", message);
	        rm.post();
    	}
    }
}
