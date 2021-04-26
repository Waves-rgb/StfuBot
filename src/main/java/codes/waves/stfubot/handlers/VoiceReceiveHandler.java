package codes.waves.stfubot.handlers;

import codes.waves.stfubot.managers.MemberVoiceManager;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Per-Guild Audio manager. Responsible for receiving audio and serving it
 * to the UserAudioHandler of the speaking user.
 */
public class VoiceReceiveHandler implements AudioReceiveHandler {

    private static Logger log = LoggerFactory.getLogger(VoiceReceiveHandler.class);

    private VoiceChannel vc;
    private Guild g;

    /**
     * @param vc VoiceChannel for the handler.
     * @param g The Guild the handler is connected to.
     */
    public VoiceReceiveHandler(VoiceChannel vc, Guild g) {
        this.vc = vc;
        this.g = g;
    }

    /**
     * @param samples An array of audio samples.
     * @return The peak of the given samples.
     */
    public static double calculatePeak(short [] samples) {
        double peakSample = 0.0;     // peak sample.

        for (short sample : samples) {
            if (Math.abs(sample) > peakSample)
                peakSample = Math.abs(sample);
        }

        return 20 * Math.log10(peakSample / 32767);
    }

    // Encode a byte[] to a short[] array.
    public static short[] encodeToSample(byte[] srcBuffer, int numBytes) {
        byte[] tempBuffer = new byte[2];
        int nSamples = numBytes / 2;
        short[] samples = new short[nSamples];  // 16-bit signed value

        for (int i = 0; i < nSamples; i++) {
            tempBuffer[0] = srcBuffer[2 * i];
            tempBuffer[1] = srcBuffer[2 * i + 1];
            samples[i] = bytesToShort(tempBuffer);
        }

        return samples;
    }

    // convert byte[] to a short.
    public static short bytesToShort(byte [] buffer) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(buffer[0]);
        bb.put(buffer[1]);
        return bb.getShort(0);
    }

    public Guild getGuild() {
        return g;
    }
    public VoiceChannel getVoiceChannel() {
        return vc;
    }
    @Override
    public boolean canReceiveCombined() {
        return false;
    }
    @Override
    public boolean canReceiveUser() {
        return true;
    }

    /**
     * Handles incoming user audio. Calculates the peak of the given sample,
     * sends the peak to the speaking user's MemberVoiceHandler.
     */
    @Override
    public void handleUserAudio(@NotNull UserAudio userAudio) {
        // We ignore bots.
        if (userAudio.getUser().isBot())
            return;

        // Encode the audiodata into a sample, then calculate the peak.
        double peak = calculatePeak(encodeToSample(userAudio.getAudioData(1), userAudio.getAudioData(1).length));
        // Find the MemberVoiceHandler for the user, then serve the peak to the kick check.
         MemberVoiceManager.get(g.getMember(userAudio.getUser()), g, vc).runKickCheck(peak);
    }
}
