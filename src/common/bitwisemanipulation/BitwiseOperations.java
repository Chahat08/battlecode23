package common.bitwisemanipulation;

public interface BitwiseOperations {

    /**
     * Sets the Kth least significant bit of an integer (first bit has 0 index)
     * Example: x = 9234, Binary Representation: 0000 0000 0000 0000 0010 0100 0001 0010
     * If k = 24, 25th bit is set: 0000 0010 0000 0000 0001 0011 1111 0100, which is the decimal number 33559540
     * @param x     the integer
     * @param k     the 0 indexed bit position which is to be set
     * @return      integer x with kth bit set
     */
    int setKthBitOfInteger(int x, int k);

    /**
     * Clears the Kth least significant bit of an integer (first bit has 0 index)
     * Example: x = 33559540, Binary Representation: 0000 0010 0000 0000 0001 0011 1111 0100
     * If k = 24, 25th bit is cleared: 0000 0000 0000 0000 0001 0011 1111 0100, which is the decimal number 9234
     * @param x     the integer
     * @param k     the 0 indexed bit position which is to be cleared
     * @return      integer x with kth bit cleared
     */
    int clearKthBitOfInteger(int x, int k);

    /**
     * set kth bit in a bitarray
     * @param array the integer array representing the bitarray
     * @param k the bit to set
     */
    void setKthBitOfArray(int[] array, int k);

    /**
     * clear kth bit of a bitarray
     * @param array the integer array representing the bitarray
     * @param k the bit to set
     */
    void clearKthBitOfArray(int[] array, int k);

    /**
     * Returns the integer passed as a bit string of 32 characters.
     * @param x the integer
     * @return 32 character bit string representing the integer
     */
    String getIntegerAs32BitString(int x);

    /**
     * Returns the integer represented by the bitstring
     * @param bitstring a string containing 1s and 0s representing an integer
     * @return the integer represented by the passed bitstring
     */
    int getBitStringAsInteger(String bitstring);

    /**
     * Set N bits starting from 0-indexed Kth bit of integer x
     * Eg: x = 0: 0000 0000 0000 0000 0000 0000 0000 0000
     * N (number of bits to set) = 6
     * k (index to set from) = 16
     * Gives, x = 129024: 0000 0000 0000 0001 1111 1000 0000 0000
     * @param x the original integer
     * @param n number of bits to set in it
     * @param k 0 based starting location to set bits
     * @return passed integer x, with n bits set starting from index k
     */
    int setNBitsInIntegerStartingFromPositionK(int x, int n, int k);

    /**
     * Clear N bits starting from 0-indexed Kth bit of integer x
     * Eg: x = 129024: 0000 0000 0000 0001 1111 1000 0000 0000
     * N (number of bits to clear) = 6
     * k (index to clear from) = 16
     * Gives, x = 0: 0000 0000 0000 0000 0000 0000 0000 0000
     * @param x the original integer
     * @param n number of bits to clear in it
     * @param k 0 based starting location to clear bits
     * @return passed integer x, with n bits cleared starting from index k
     */
    int clearNBitsInIntegerStartingFromPositionK(int x, int n, int k);

    /**
     * Given an integer x, and a bit string bitString, set the bits starting from k in x according to the bitString
     * Example: x = 1: 0000 0000 0000 0000 0000 0000 0000 0001
     * bitString = "1000101110"
     * k = 19
     * Gives, x with bitString set starting from position 19 (20th index): 0000 0000 0000 1000 1011 1000 0000 0001,
     * which is the integer 571393
     * @param x the integer whose bits are to be set
     * @param bitString the bit string according to which to set the bits
     * @param k position to set the bitString inside x
     * @return integer with its bits set according to bitString starting at position k
     */
    int setBitStringInIntegerAtPositionK(int x, String bitString, int k);
}
