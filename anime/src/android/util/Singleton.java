package android.util;

/**
 * @author Qinghua Zhang @create 2017/3/16.
 */
public abstract class Singleton {

    private Object mInstance;

    protected abstract Object create();

    public Object get() {
        synchronized (this) {
            if (this.mInstance == null) {
                this.mInstance = this.create();
            }

            return this.mInstance;
        }
    }
}
