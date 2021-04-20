package ch.epfl.javass.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Une classe facilitant la (dé)sérialisation des valeurs échangées entre le
 * client et le serveur,
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */

public final class StringSerializer {

    private final static int RADIX = 16;

    /**
     * Constructeur privé de StringSerializer
     */
    private StringSerializer() {
    }

    /**
     * Sérialise l'entier de type int donné en argument sous la forme de sa
     * représentation textuelle en base 16 en une de chaine de caractères
     * 
     * @param value
     *            l'entier int à sérialiser
     * @return la chaine correspondante à l'entier sérialisé
     */
    public static String serializeInt(int value) {
        return Integer.toUnsignedString(value, RADIX);
    }

    /**
     * Désérialise une chaine de caractères en un entier de type int sous la
     * forme de sa représentation textuelle en base 16
     * 
     * @param serializedValue
     *            la chaine à désérialiser
     * @return l'entier int correspondant à la désérialisation de la chaine
     */
    public static int deserializeInt(String serializedValue) {
        return Integer.parseUnsignedInt(serializedValue, RADIX);
    }

    /**
     * Sérialise l'entier de type long donné en argument sous la forme de sa
     * représentation textuelle en base 16 en une de chaine de caractères
     * 
     * @param value
     *            l'entier long à sérialiser
     * @return la chaine correspondante à l'entier sérialisé
     */
    public static String serializeLong(long value) {
        return Long.toUnsignedString(value, RADIX);
    }

    /**
     * Désérialise une chaine de caractères sous forme d'un entier long sous la
     * forme de sa représentation textuelle en base 16
     * 
     * @param serializedValue
     *            la chaine à désérialiser
     * @return l'entier long correspondant à la désérialisation de la chaine
     */
    public static long deserializeLong(String serializedValue) {
        return Long.parseUnsignedLong(serializedValue, RADIX);
    }

    /**
     * Sérialise une chaine de caractères sous forme d'une autre chaine
     * sérialisée par encodage en base64
     * 
     * @param toSerialize
     *            la chaine à sérialiser
     * @return la chaine sérialisée
     */
    public static String serializeString(String toSerialize) {
        byte[] UTF_Bytes = toSerialize.getBytes(StandardCharsets.UTF_8);
        Base64.Encoder encoder = Base64.getEncoder();
        String serializedString = encoder.encodeToString(UTF_Bytes);
        return serializedString;
    }

    /**
     * Désérialise la chaine sérialisée par encodage en base64
     * 
     * @param toDeserialize
     *            la chaine à désérialiser
     * @return la chaine désérialisée
     */
    public static String deserializeString(String toDeserialize) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] UTF_Bytes = decoder.decode(toDeserialize);
        String deserializedString = new String(UTF_Bytes,
                StandardCharsets.UTF_8);
        return deserializedString;
    }

    /**
     * Renvoie une chaine composée des chaines passées en argument, séparées par
     * le delimiter
     * 
     * @param delimiter
     *            caractère de séparation
     * @param toCombine
     *            les chaines à combiner
     * @return la chaine composée des chaines passées en argument
     */
    public static String combine(CharSequence delimiter, String... toCombine) {
        return String.join(delimiter, toCombine);
    }

    /**
     * Construit un tableau dont les éléments sont les bouts de chaines obtenues
     * en divisant la chaine de base à l'aide du delimiter
     * 
     * @param delimiter
     *            caractère de séparation
     * @param toSplit
     *            la chaine à diviser
     * @return le tableau constitué des bouts de chaines formés à partir de la
     *         chaine de base
     */
    public static String[] split(String delimiter, String toSplit) {
        return toSplit.split(delimiter);
    }
}
