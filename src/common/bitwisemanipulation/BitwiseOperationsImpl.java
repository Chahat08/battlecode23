package common.bitwisemanipulation;

public class BitwiseOperationsImpl implements BitwiseOperations{

    @Override
    public int setKthBitOfInteger(int x, int k) {
        int flag = 1;
        flag = flag << k;
        return x | flag;
    }

    @Override
    public int clearKthBitOfInteger(int x, int k) {
        int flag = 1;
        flag = flag << k;
        return x & (~flag);
    }

    @Override
    public int readKthBitOfInteger(int x, int k) {
        int flag = 1;
        x >>= k;
        return x&flag;
    }

    @Override
    public String readIthToJthBitOfIntegerAsBitString(int x, int i, int j) {
        return getIntegerAs32BitString(x).substring(i, j);
    }

    @Override
    public void setKthBitOfArray(int[] array, int k) {
        int idx = k/32;
        int position = k%32;
        array[idx] = setKthBitOfInteger(array[idx], position);
    }

    @Override
    public void clearKthBitOfArray(int[] array, int k) {
        int idx = k/32;
        int position = k%32;
        array[idx] = clearKthBitOfInteger(array[idx], position);
    }

    @Override
    public int readKthBitOfArray(int[] array, int k) {
        int idx = k/32;
        int position = k%32;
        return readKthBitOfInteger(array[idx], position);
    }

    @Override
    public String getIntegerAs32BitString(int x) {
        StringBuilder bitString = new StringBuilder("00000000000000000000000000000000");
        for(int i=0; i<32; ++i){
            int idx= 32-i-1;
            if((x&1)==1) bitString.setCharAt(idx, '1');
            else bitString.setCharAt(idx, '0');
            x>>=1;
        }
        return bitString.toString();
    }

    @Override
    public String getIntegerAsStrippedBitString(int x) {
        String bitStr = getIntegerAs32BitString(x);
        int rem=0, i=0;
        while(bitStr.charAt(i++)=='0'){
            rem++;
            if(i==bitStr.length()) break;
        } if(rem==bitStr.length()) return "0";

        return bitStr.substring(rem);
    }

    @Override
    public String getIntegerAs12BitString(int x) {
        StringBuilder bitString = new StringBuilder("000000000000");
        for(int i=0; i<12; ++i){
            int idx= 12-i-1;
            if((x&1)==1) bitString.setCharAt(idx, '1');
            else bitString.setCharAt(idx, '0');
            x>>=1;
        }
        return bitString.toString();
    }

    @Override
    public String getIntegerAs16BitString(int x) {
        StringBuilder bitString = new StringBuilder("0000000000000000");
        for(int i=0; i<16; ++i){
            int idx= 16-i-1;
            if((x&1)==1) bitString.setCharAt(idx, '1');
            else bitString.setCharAt(idx, '0');
            x>>=1;
        }
        return bitString.toString();
    }

    @Override
    public String getIntegerAs3BitString(int x) {
        StringBuilder bitString = new StringBuilder("000");
        for(int i=0; i<3; ++i){
            int idx= 3-i-1;
            if((x&1)==1) bitString.setCharAt(idx, '1');
            else bitString.setCharAt(idx, '0');
            x>>=1;
        }
        return bitString.toString();
    }

    @Override
    public int getBitStringAsInteger(String bitStr) {
        // removing leading 0s, if any
        int rem=0, i=0;
        while(bitStr.charAt(i++)=='0'){
            rem++;
            if(i==bitStr.length()) break;
        } if(rem==bitStr.length()) return 0;

        String bitString = bitStr.substring(rem);
        int x=0;
        for(i=0; i<bitString.length(); ++i){
            char ch = bitString.charAt(i);
            if(ch=='1') x+=Math.pow(2, bitString.length()-i-1);
        }
        return x;
    }

    @Override
    public String[] getIntegerAs8FourBitStrings(int x) {
        String[] bitStrings = new String[8];
        String bitString = getIntegerAs32BitString(x);

        for(int i=0, j=0; j<8; i+=4, ++j){
            bitStrings[j]=bitString.substring(i, i+4);
        }

        return bitStrings;
    }

    @Override
    public int setNBitsInIntegerStartingFromPositionK(int x, int n, int k) {
        StringBuilder xAsBitString = new StringBuilder(getIntegerAs32BitString(x));
        for(int i=32-k-1, j=0; j<n; ++j, ++i){
            xAsBitString.setCharAt(i, '1');
        }
        return getBitStringAsInteger(xAsBitString.toString());
    }

    @Override
    public int clearNBitsInIntegerStartingFromPositionK(int x, int n, int k) {

        StringBuilder xAsBitString = new StringBuilder(getIntegerAs32BitString(x));
        for(int i=32-k-1, j=0; j<n; ++j, ++i){
            xAsBitString.setCharAt(i, '0');
        }
        return getBitStringAsInteger(xAsBitString.toString());
    }

    @Override
    public int setBitStringInIntegerAtPositionK(int x, String bitString, int k) {
        StringBuilder xAsBitString = new StringBuilder(getIntegerAs32BitString(x));

        return getBitStringAsInteger(
                xAsBitString.replace(k, bitString.length()+k, bitString)
                        .toString()
        );
    }

    @Override
    public int setBitStringInBitStringAtPositionK(String originalBitString, String bitStringToEmbed, int k) {
        StringBuilder origString = new StringBuilder(originalBitString);
        return getBitStringAsInteger(
                origString.replace(k, bitStringToEmbed.length()+k, bitStringToEmbed)
                        .toString()
        );
    }
}
