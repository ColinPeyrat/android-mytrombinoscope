package tp4.info.iut.acy.fr.mytrombi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * Created by peyratc on 01/02/2016.
 */
public class MyShotsAdapter {

    // variables de définition de la base gérée
    private static final String DATABASE_NAME = "maBase.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase shotsDB; // reference vers une base de données
    private ShotsDBhelper dbHelper; // référence vers le Helper de gestion de la base

    public MyShotsAdapter(Context context) { // constructeur
        dbHelper = new ShotsDBhelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void open() throws SQLiteException {
        try{
            shotsDB=dbHelper.getWritableDatabase();
            // LogCat message
            Log.i("MyShotsAdapter", "Base ouverte en ecriture " + shotsDB);
        }catch (SQLiteException e){
            shotsDB=dbHelper.getReadableDatabase();
            Log.i("MyShotsAdapter", "Base ouverte en lecture " + shotsDB);
        }
    }
    public void close(){
        Log.i("MyShotsAdapter", "close: demande de fermeture de la base");
        dbHelper.close();
    }

    // insertion
    public long insertShot(String chemin, String typeShot, String commentaire){
        Log.i("insertShot", "appelé");
        ContentValues newValue  = new ContentValues();
        newValue.put(dbHelper.KEY_PATH, chemin);
        newValue.put(dbHelper.KEY_TYPE, typeShot);
        newValue.put(dbHelper.KEY_COMMENT, commentaire);
        return shotsDB.insert(ShotsDBhelper.NOM_TABLE, null, newValue);
    }
    // modification
    public boolean updateShot(int ligneID, String chemin, String typeShot, String commentaire){
        Log.i("updateShot", "appelé");
        ContentValues newValue = new ContentValues();
        newValue.put(dbHelper.KEY_PATH, chemin);
        newValue.put(dbHelper.KEY_TYPE, typeShot);
        newValue.put(dbHelper.KEY_COMMENT, commentaire);
        return shotsDB.update(ShotsDBhelper.NOM_TABLE, newValue,
                ShotsDBhelper.KEY_ID + " = " + ligneID, null) > 0;
    }

    // suppression
    public boolean removeShot(long ligneID){
        Log.i("removeLine", "appelé");
        return shotsDB.delete(ShotsDBhelper.NOM_TABLE, ShotsDBhelper.KEY_ID + " = " + ligneID, null)>0;
    }

    // select * (renvoie tous les éléments de la table)
    public Cursor getAllData(){
        return shotsDB.query(dbHelper.NOM_TABLE, new String[]{ ShotsDBhelper.KEY_ID,
                ShotsDBhelper.KEY_PATH, ShotsDBhelper.KEY_TYPE,ShotsDBhelper.KEY_COMMENT}, null, null, null, null, dbHelper.KEY_ID+" DESC");
    }

    // renvoie un seul éléments de la table identifié par son ID
    public Cursor getSingleShot(long ligneID){
        Cursor reponse = shotsDB.query(ShotsDBhelper .NOM_TABLE, new String[]{
                        ShotsDBhelper.KEY_ID, ShotsDBhelper.KEY_PATH, ShotsDBhelper.KEY_TYPE,
                        ShotsDBhelper.KEY_COMMENT}, ShotsDBhelper.KEY_ID + " = " + ligneID, null, null,
                null, null);
        return reponse;
    }

    // renvoie tous les éléments de la table qui ont le type_media voulu.
    public Cursor getAllShotsOfAtype(String type_media){
        Cursor reponse = shotsDB.query(ShotsDBhelper .NOM_TABLE, new String[]{
                        ShotsDBhelper.KEY_ID, ShotsDBhelper.KEY_PATH, ShotsDBhelper.KEY_TYPE,
                        ShotsDBhelper.KEY_COMMENT}, ShotsDBhelper.KEY_TYPE + " = " + type_media, null, null,
                null, null);
        return reponse;
    }


}
