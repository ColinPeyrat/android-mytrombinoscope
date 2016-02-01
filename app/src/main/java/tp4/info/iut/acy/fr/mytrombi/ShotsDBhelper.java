package tp4.info.iut.acy.fr.mytrombi;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by peyratc on 01/02/2016.
 */
public class ShotsDBhelper extends SQLiteOpenHelper {

    // membres public permettant de dfinir les champs de la base
    public static final String NOM_TABLE = "MesMedias";
    public static final String KEY_ID = "_id";
    public static final String KEY_PATH = "chemin_media";
    public static final String KEY_TYPE = "type_media";
    public static final String KEY_COMMENT = "commentaire";

    // String permettant la creation de la table
    private static final String DATABASE_CREATE = "create table " + NOM_TABLE +
            " (" + KEY_ID + " integer primary key autoincrement, " +
            KEY_PATH + " text not null, " +
            KEY_TYPE + " text not null, " +
            KEY_COMMENT + " text );";

    public ShotsDBhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.i("ShotsDBhelper", "Helper construit");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // creation de la table
        db.execSQL(DATABASE_CREATE);

        Log.i("ShotsDBhelper", "Database created with instruction : " +
                DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // destruction de la base et recreation
        db.execSQL("DROP TABLE IF EXISTS " + NOM_TABLE);
        Log.i("ShotsDBhelper", "Base mise a jour !!!");
        onCreate(db);
    }

}
