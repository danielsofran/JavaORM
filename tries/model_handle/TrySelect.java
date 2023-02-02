package tries.model_handle;

import models.demo.Angajat;
import models.demo.MData;
import orm.classparser.MethodCaller;
import orm.classparser.PropertyParser;
import orm.exceptions.MethodNotFoundException;
import orm.exceptions.OrmException;
import orm.sql.SelectWriter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrySelect {
    static void test_ctor() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, OrmException {
        MethodCaller methodCaller = new MethodCaller();
        MData mData = new MData(1, "str");
        Object returned = methodCaller.callGetter(new MData(1, "str"), "Data");
        Object id2 = 2;
        methodCaller.callSetter(mData, "Id", id2);
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, OrmException {
        test_ctor();
    }
}
