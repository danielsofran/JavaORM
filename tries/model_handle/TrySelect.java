package tries.model_handle;

import models.demo.*;
import orm.ConnectionManager;
import orm.classparser.MethodCaller;
import orm.exceptions.OrmException;
import orm.sql.SelectExecutor;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TrySelect {
    static void test_ctor() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, OrmException, SQLException {
        MData mData = new MData(1, "str");
        String returned = MethodCaller.callGetter(new MData(1, "str"), "Data");
        Object id2 = 2;
        MethodCaller.callSetter(mData, "Id", id2);
        ConnectionManager conn = new ConnectionManager();
        SelectExecutor se = new SelectExecutor(conn);
        mData = se.findByPK(MData.class, 1);
        Persoana persoana = se.findByPK(Persoana.class, 1);
        Angajat angajat = se.findByPK(Angajat.class, 1);
    }

    static void test_timestamp() throws OrmException, SQLException {
        Ore ora = new Ore();
        ora.setId(1);
        ora.setOra(LocalDateTime.now());
        ora.setTip(MyEnum.A);

        LocalDateTime localDateTime = MethodCaller.callGetter(ora, "Ora");
        MethodCaller.callSetter(ora, "Ora", localDateTime);

        MyEnum tip = MethodCaller.callGetter(ora, "Tip");
        MethodCaller.callSetter(ora, "Tip", MyEnum.B);

        ConnectionManager conn = new ConnectionManager();
        SelectExecutor se = new SelectExecutor(conn);

        ora = se.findByPK(Ore.class, 1);
    }


    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, OrmException, SQLException {
        //test_ctor();
        test_timestamp();
        //System.out.println(MyEnum.A.toString());
    }
}
