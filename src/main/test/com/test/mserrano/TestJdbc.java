package com.test.mserrano;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.ConnectionProviderFromUrl;
import com.github.davidmoten.rx.jdbc.Database;
import junit.framework.TestCase;
import rx.Observable;

import java.util.List;

public class TestJdbc extends TestCase {
    /*private static final String DB_CONNECTION = "jdbc:oracle:thin:@localhost:1523/ORCLPDB1.localdomain";
    private static final String DB_USER       = "USER1";
    private static final String DB_PASSWORD   = "USER1";*/

    private static final String DB_CONNECTION = "jdbc:oracle:thin:@localhost:1522/ORCLPDB1.localdomain";
    private static final String DB_USER       = "ACSELEGU_ALFA_FUN";
    private static final String DB_PASSWORD   = "ACSELEGU_ALFA_FUN";

    public static ConnectionProvider connectionProvider
            = new ConnectionProviderFromUrl(
            DB_CONNECTION, DB_USER, DB_PASSWORD);

    Database db = Database.from(connectionProvider);



    public void testA(){

        System.out.println("onSubscribe: ");

        Observable<Integer> create,
                insert1,
                insert2,
                insert3,
                update,
                delete = null;

        //create = db.update(
        //        "CREATE TABLE EMPLOYEE("
        //                + "id int primary key, name varchar(255))")
        //        .count();
        /*insert1 = db.update(
                "INSERT INTO EMPLOYEE(id, name) VALUES(1, 'John')")
                //.dependsOn(create)
                .count();
        update = db.update(
                "UPDATE EMPLOYEE SET name = 'Alan' WHERE id = 1")
                //.dependsOn(create)
                .count();
        insert2 = db.update(
                "INSERT INTO EMPLOYEE(id, name) VALUES(2, 'Sarah')")
                //.dependsOn(create)
                .count();
        insert3 = db.update(
                "INSERT INTO EMPLOYEE(id, name) VALUES(3, 'Mike')")
                //.dependsOn(create)
                .count();
        delete = db.update(
                "DELETE FROM EMPLOYEE WHERE id = 2")
                //.dependsOn(create)
                .count();*/

        List<String> names = db.select(
                "select name from EMPLOYEE where id < ?")
                .parameter(10)
                /*.dependsOn(create)
                .dependsOn(insert1)
                .dependsOn(insert2)
                .dependsOn(insert3)
                .dependsOn(update)
                .dependsOn(delete)*/
                .getAs(String.class)
                .toList()
                .toBlocking()
                .single();
                names.forEach(s -> System.out.println(s));
    }

}
