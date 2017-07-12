/**
 * Class representation of a hole
 */
public class Hole {
    private int seeds;
    private boolean isNgacang;
    private boolean isStorage;

    /**
     * Constructor
     * @param isStorage is this hole a storage hole?
     * @param seeds how many seed to initialize in this hole
     */
    public Hole(boolean isStorage, int seeds){
        this.isStorage = isStorage;
        this.seeds = seeds;
        this.isNgacang = false;
    }

    public void addSeeds(int seeds){
        this.seeds += seeds;
    }

    public int getSeeds() {
        return seeds;
    }

    public void setSeeds(int seeds) {
        this.seeds = seeds;
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
        String seedsNum = String.format("%02d", this.seeds);
        s = "< "+seedsNum+kacang+">";
        return s;
    }
}

