package hakito.carclient;

public class Normalizer {

    private int min;
    private int max;

    public Normalizer(int min, int max) {
        this.max = max;
        this.min = min;
    }

    public double normalize(double val) {
        if (val < 0) val = 0;
        if (val > 1) val = 1;
        return (int) (min + val * (max - min));
    }
}
