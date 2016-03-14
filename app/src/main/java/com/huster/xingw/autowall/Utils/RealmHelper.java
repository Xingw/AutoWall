package com.huster.xingw.autowall.Utils;

import android.content.Context;

import com.huster.xingw.autowall.Model.Wall;

import java.io.File;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.internal.Table;

/**
 * Created by Xingw on 2016/3/14.
 */
public class RealmHelper {
    public static Realm getInstance(Context ctx) {
        Realm realm = null;
        try {
            realm = Realm.getInstance(ctx);
        } catch (RealmMigrationNeededException e) {
            RealmMigration migration = new RealmMigration() {
                @Override
                public long execute(Realm realm, long version) {
                    Table table = realm.getTable(Wall.class);
                    // Needed for all Strings
                    table.convertColumnToNullable(table.getColumnIndex("_id"));
                    return 1;
                }
            };
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(ctx)
                    .schemaVersion(1)
                    .migration(migration)
                    .build();
            realm = Realm.getInstance(realmConfig);
        }

        return realm;
    }
}
