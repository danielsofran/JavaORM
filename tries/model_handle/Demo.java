package tries.model_handle;

import models.demo.*;
import orm.ConnectionManager;
import orm.ORM;
import orm.exceptions.OrmException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Demo {
    static ORM orm = new ORM(new ConnectionManager("TestORM", "postgres", "parola", "localhost:5432"));

    static void singleEntity() throws OrmException, SQLException {
        List<MData> res = orm.select(MData.class);
        assert res.isEmpty();

        // CRUD operations
        MData mData = new MData(1, "str");
        mData = orm.insert(mData);
        assert mData.getId() == 1;

        MData mData2 = new MData();
        mData2.setData("str2");
        mData2 = orm.insert(mData2);
        assert mData2.getId() == 2;

        List<MData> mDataList = orm.select(MData.class);
        assert mDataList.size() == 2;
        assert mDataList.get(0).getId() == 1;
        assert mDataList.get(1).getId() == 2;
        assert mDataList.get(0).getData().equals("str");
        assert mDataList.get(1).getData().equals("str2");

        mData2.setData("str3");
        orm.update(mData2, mData2.getId());
        mData2 = orm.select(MData.class, mData2.getId());
        assert mData2.getData().equals("str3");

        orm.delete(MData.class, mData2.getId());
        mDataList = orm.select(MData.class);
        assert mDataList.size() == 1;
        assert mDataList.get(0).getId() == 1;
        assert mDataList.get(0).getData().equals("str");
    }

    /**
     * Test if all types are supported
     *
     * @implNote TData: short, char, double, float and float primary key
     * @implNote Ore: date, times, long, boolean, enums
     * @throws OrmException
     * @throws SQLException
     */
    static void typesSupport() throws OrmException, SQLException {
        // Test if all types are supported
        List<TData> res = orm.select(TData.class);
        assert res.isEmpty();
        List<Ore> res2 = orm.select(Ore.class);
        assert res2.isEmpty();

        TData tData = new TData();
        tData.setC('c');
        tData.setCh('C');
        tData.setDbl(1e-50);
        tData.setS((short) 2);
        tData.setId(1.0f);
        tData = orm.insert(tData);
        tData = orm.select(TData.class, tData.getId());
        assert tData.getCh() == 'C';
        assert tData.getC() == 'c';
        assert tData.getDbl() == 1e-50;
        assert tData.getS() == 2;
        assert tData.getId() == 1.0f;

        Ore ora = new Ore();
        LocalDateTime oraDateTime = LocalDateTime.now();
        ora.setOra(oraDateTime);
        ora.setTip(MyEnum.B);
        ora.setFlag(true);
        ora.setDate(oraDateTime.toLocalDate());
        ora.setTime(oraDateTime.toLocalTime());
        ora.setALong(3L);
        ora = orm.insert(ora);
        ora = orm.select(Ore.class, ora.getId());
        assert ora.getOra().withNano(0).equals(oraDateTime.withNano(0));
        assert ora.getTip() == MyEnum.B;
        assert ora.getFlag() == true;
        assert ora.getDate().equals(oraDateTime.toLocalDate());
        assert ora.getTime().withNano(0).equals(oraDateTime.toLocalTime().withNano(0));
        assert ora.getALong() == 3L;
        assert ora.getId() == 1;
    }

    static void multipleNonRelatedEntities() throws OrmException, SQLException {
        List<MData> res = orm.select(MData.class);
        assert res.isEmpty();
        List<NonAIData> res2 = orm.select(NonAIData.class);
        assert res2.isEmpty();
    }

    // Persoana2 has a foreign key MData specified through the @FK annotation
    static void simpleFK() throws OrmException, SQLException {
        List<MData> res = orm.select(MData.class);
        assert res.isEmpty();
        List<Persoana2> res2 = orm.select(Persoana2.class);
        assert res2.isEmpty();
    }

    // Persoana has a foreign key MData specified through agregation of the MData class
    static void aggregationFK() throws OrmException, SQLException {
        List<MData> res = orm.select(MData.class);
        assert res.isEmpty();
        List<Persoana> res2 = orm.select(Persoana.class);
        assert res2.isEmpty();
    }

    // Angajat extends a Persoana and has a foreign key to Persoana through aggregation
    static void inheritanceAndAggregation() throws OrmException, SQLException {
        List<MData> res = orm.select(MData.class);
        assert res.isEmpty();
        List<Persoana> res2 = orm.select(Persoana.class);
        assert res2.isEmpty();
        List<Angajat> res3 = orm.select(Angajat.class);
        assert res3.isEmpty();

        // CRUD operations
        MData dataSef = new MData(1, "sef");
        Persoana sef = new Persoana();
        sef.setData(dataSef);
        sef.setNume("Sef");

        MData dataAngajat = new MData(2, "persoana angajat");
        Angajat angajat = new Angajat();
        angajat.setNume("Angajat");
        angajat.setData(dataAngajat);
        angajat.setSef(sef);
        angajat.setSalariu(1000.5);

        // Insert the data
        dataSef = orm.insert(dataSef);
        sef = orm.insert(sef);
        dataAngajat = orm.insert(dataAngajat);
        angajat = orm.insert(angajat);
        assert sef.getId() == 1;
        assert angajat.getId() == 1;
        assert dataSef.getId() == 1;
        assert dataAngajat.getId() == 2;
        assert angajat.getSef().getId() == sef.getId();
        assert angajat.getData().getId() == dataAngajat.getId();
        assert sef.getData().getId() == dataSef.getId();
        assert angajat.getSalariu() - 1000.5 < 0.0001;
        assert angajat.getNume().equals("Angajat");
        assert sef.getNume().equals("Sef");
        assert dataSef.getData().equals("sef");
        assert dataAngajat.getData().equals("persoana angajat");
        assert angajat.getSef().getData().getId() == dataSef.getId();

        // deleting angajat will not delete sef
        orm.delete(Angajat.class, angajat.getId());
        try{
            orm.select(Angajat.class, angajat.getId());
            throw new Exception("Angajat should not be found");
        } catch (OrmException expected){} catch (Exception e) {
            throw new RuntimeException(e);
        }
        sef = orm.select(Persoana.class, sef.getId());
        assert sef.getId() == 1;
    }

    static void testAll() throws OrmException, SQLException {
        executeTest(Demo::singleEntity, MData.class);
        executeTest(Demo::typesSupport, TData.class, Ore.class);
        executeTest(Demo::multipleNonRelatedEntities, MData.class, NonAIData.class);
        executeTest(Demo::simpleFK, MData.class, Persoana2.class);
        executeTest(Demo::aggregationFK, MData.class, Persoana.class);
        executeTest(Demo::inheritanceAndAggregation, MData.class, Persoana.class, Angajat.class);
    }

    public static void main(String[] args) throws OrmException, SQLException {
        testAll();
    }

    private static void executeTest(ThrowingRunnable test, Class<?>... classes) throws OrmException, SQLException {
        orm.createTables(classes);
        try {
            test.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        orm.dropTables(classes);
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
