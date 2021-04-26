package codes.waves.stfubot.generators;

import java.io.IOException;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Class for generating a message from the list in resources/dms.txt
 */
public class RandomDmMessage {
    // Open an InputStream to resources/dms.txt
    static InputStream dms = RandomDmMessage.class.getClassLoader().getResourceAsStream("dms.txt");

    static String sDms;
    static String[] saDms;

    /**
     * @param min The minimum that the random number should be.
     * @param max The maximum that the random number should be.
     * @return Random number between min and max, including min and max.
     */
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    /**
     * Initializes the sDms and saDms variables, and readies the class for use.
     */
    public static void init() {
        // read the resources/dms.txt file into sDms, then split it with \n and store the final result in saDms.
        try {
            sDms = IOUtils.toString(dms, StandardCharsets.UTF_8);
            saDms = sDms.split("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates and returns a random string to be sent to the user's dms upon disconnection.
     * @return Random string from saDms.
     */
    public static String getRandomMessage() {
        // Return a random message from saDms.
        return saDms[getRandomNumber(0, saDms.length)];
    }
}
