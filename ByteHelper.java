public class ByteHelper {
/**
 * * Converts a 4 byte array of unsigned bytes to an long
 * * @param b an array of 4 unsigned bytes
 * * @return a long representing the unsigned int
 * */
    public static final long unsignedIntToLong(byte[] b)
    {
        long l = 0;
        l |= b[0] & 0xFF;
        l <<= 8;
        l |= b[1] & 0xFF;
        l <<= 8;
        l |= b[2] & 0xFF;
        l <<= 8;
        l |= b[3] & 0xFF;
        return l;
    }
    /**
     * * Converts a two byte array to an integer
     * * @param b a byte array of length 2
     * * @return an int representing the unsigned short
     * */
    public static final int unsignedShortToInt(byte[] b)
    {
        int i = 0;
        i |= b[0] & 0xFF;
        i <<= 8;
        i |= b[1] & 0xFF;
        return i;
    }
}
