package musicretrievaldemo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maor
 * Date: 07/07/13
 * Time: 22:38
 * To change this template use File | Settings | File Templates.
 */
public abstract class Test {
    private static Test instance = null;
    private final List<String> list = new ArrayList<String>();

    private Test() { }

    public static Test getInstance() {
        if (instance == null) {
            instance = new Test();
        }

        return instance;
    }

    public void getDataBaseConnection(){}

    public void addToAccount() {};

    protected void blabla() {}
}
