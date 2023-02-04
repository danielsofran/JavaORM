package tries.model_handle;

import models.demo.*;
import orm.ConnectionManager;
import orm.ORM;
import orm.classparser.MethodCaller;
import orm.classparser.PropertyParser;
import orm.exceptions.OrmException;
import orm.sql.DMLWriter;
import orm.sql.SelectExecutor;
import orm.sql.utils.SequenceType;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TrySelect {
    static void test_ctor() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, OrmException, SQLException, NoSuchFieldException {
        MData mData = new MData(1, "str");
        String returned = MethodCaller.callGetter(new MData(1, "str"), "Data");
        Object id2 = 2;
        MethodCaller.callSetter(mData, "Id", id2);
        ConnectionManager conn = new ConnectionManager();
        SelectExecutor se = new SelectExecutor(conn);
        mData = se.findByPK(MData.class, 1);
        Persoana persoana = se.findByPK(Persoana.class, 1);
        Angajat angajat = se.findByPK(Angajat.class, 1);

//        String sql = InsertWriter.getInsertSQL(persoana);
//        System.out.println(sql);

//
//        ORM orm = new ORM(new ConnectionManager());
//        angajat = orm.insertValue(angajat);
        Ore ora = se.findByPK(Ore.class, 1);

//        PropertyParser<?> ps = new PropertyParser<>(Angajat.class);
//        String rez = DMLWriter.createSequence(angajat, ps.getFields(), SequenceType.SET);
//        System.out.println(rez);
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

    static void test_insert() throws OrmException, SQLException {
        Ore ora = new Ore();
        ora.setId(1);
        ora.setOra(LocalDateTime.now());
        ora.setTip(MyEnum.B);

        ora.setFlag(true);
        ora.setDate(LocalDateTime.now().toLocalDate());
        ora.setTime(LocalDateTime.now().toLocalTime());

        ora.setALong(3L);

        String sql = DMLWriter.getInsertSQL(ora);
        System.out.println(sql);

        ORM orm = new ORM(new ConnectionManager());
        ora = orm.insert(ora);
    }

    static void test_select() throws SQLException, OrmException {
        ORM orm = new ORM(new ConnectionManager());
        List<Angajat> rez = orm.select(Angajat.class);
        MData data = orm.select(MData.class, 1);
    }

    static void test_createSeq() throws OrmException {
        Ore ora = new Ore();
        ora.setId(1);
        ora.setOra(LocalDateTime.now());
        ora.setTip(MyEnum.B);

        ora.setFlag(true);
        ora.setDate(LocalDateTime.now().toLocalDate());
        ora.setTime(LocalDateTime.now().toLocalTime());

        ora.setALong(3L);

//        PropertyParser<?> psOre = new PropertyParser<>(Ore.class);
//        String rez = DMLWriter.createSequence(ora, psOre.getFields(), SequenceType.CONDITION);
//        System.out.println(rez);
    }


    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, OrmException, SQLException, NoSuchFieldException {
        test_ctor();
        //test_timestamp();
        //test_insert();
        //test_select();
        //test_createSeq();
        //System.out.println(MyEnum.A.toString());
    }
}
