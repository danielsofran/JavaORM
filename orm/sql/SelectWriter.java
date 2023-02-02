package orm.sql;

import orm.classparser.PropertyParser;

public class SelectWriter {
    private final PropertyParser<?> parser;
    public SelectWriter(Class<?> the_class){
        parser = new PropertyParser<>(the_class);
    }


}
