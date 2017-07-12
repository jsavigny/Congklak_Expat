/**
 * Class representation of a hole
 */
public class Hole {
    private int shells;
    private boolean isNgacang;
    private boolean isStorage;

    /**
     * Constructor
     * @param isStorage is this hole a storage hole?
     * @param shells how many shells to initialize in this hole
     */
    public Hole(boolean isStorage, int shells){
        this.isStorage = isStorage;
        this.shells = shells;
        this.isNgacang = false;
    }

    public void addShells(int shells){
        this.shells += shells;
    }

    public int getShells() {
        return shells;
    }

    public void setShells(int shells) {
        this.shells = shells;
    }

    public boolean isNgacang() {
        return isNgacang;
    }

    public void setNgacang(boolean ngacang) {
        isNgacang = ngacang;
    }

    public boolean isStorage() {
        return isStorage;
    }

    public void setStorage(boolean storage) {
        isStorage = storage;
    }

    /**
     * For printing purpose
     * @return < %02dk >
     */
    public String toString(){
        String s;
        String kacang = " ";
        if (this.isNgacang){
            kacang = "n";
        }
        String shellsNum = String.format("%02d", this.shells);
        s = "< "+shellsNum+kacang+">";
        return s;
    }
}

