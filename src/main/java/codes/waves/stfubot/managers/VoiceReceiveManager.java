package codes.waves.stfubot.managers;

import codes.waves.stfubot.handlers.VoiceReceiveHandler;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.Vector;


/**
 * Handles cleaning up the VoiceReceive classes, generating
 * new ones, and serving new ones.
 */
// Manages the VoiceReceive classes.
public class VoiceReceiveManager {

    // Holds all of the VoiceReceivers.
    static Vector<VoiceReceiveHandler> vrs = new Vector<>();

    /**
     * @return true if a VoiceReceiver was found with the inputs, or false if it was not.
     * (can probably delete)
     */
    public static boolean exists(Guild g, VoiceChannel vc) {
        // Iterate over all of the members of the vrs list.
        for (VoiceReceiveHandler vr: vrs) {
            // If we find a receiver with the desired elements, we return true.
            if (vr.getGuild() == g && vr.getVoiceChannel() == vc)
                return true;
        }
        // We return false if we did not successfully find a VoiceReceiver with the desired elements.
        return false;
    }

    /**
     * @return A new VoiceReceiver if there isn't already one
     * with the desired traits, but an already existing one if
     * there was.
     */
    public static VoiceReceiveHandler get(Guild g, VoiceChannel vc) {
        // Iterates over all of the VoiceReceivers and makes sure there isn't already a handler. But only if the vrs list contains some elements.
        if (!vrs.isEmpty())
            for (VoiceReceiveHandler vr: vrs) {
                // If the VoiceReceiver has the values we are trying to find, we return it.
                if (vr.getGuild() == g && vr.getVoiceChannel() == vc)
                    return vr;
            }

        // If we did not return a VoiceReceiver, we make a new one, add it to our list, and return it.
        VoiceReceiveHandler vrr = new VoiceReceiveHandler(vc, g);
        vrs.add(vrr);
        return vrr;
    }

    /**
     * @return True if the destroying of an VoiceReceiver was
     * found and destroyed, but false if it was not.
     */
    public static boolean destroy(Guild g, VoiceChannel vc) {
        // Iterate over all of the VoiceReceivers.
        for (VoiceReceiveHandler vr: vrs) {
            /*
             If the VoiceReceiver has the values we are trying to find, we nullify it, and return true.
             This is generally handled by JDA but we may need to in some cases.
             TODO: Add cleanup for VoiceReceivers that are unused or un-needed.
            */
            if (vr.getGuild() == g && vr.getVoiceChannel() == vc) {
                vrs.set(vrs.indexOf(vr), null);
                return true;
            }
        }

        // If we did not return true, then we failed to find and destroy the VoiceReceiver, so we return false;
        return false;
    }



}
