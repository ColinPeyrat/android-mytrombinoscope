package tp4.info.iut.acy.fr.mytrombi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.File;


public class MyTrombi extends Activity implements OnClickListener {

    MyShotsAdapter sauvegardeShotsDB;
    Cursor shotsDBcursor;

    // definition des extensions pour video et photo
    private static final String EXTENSION_PHOTO = ".jpg";
    private static final String EXTENSION_VIDEO = ".mp4";

    // definition des flags pour les intents
    private static final Integer PRENDRE_PHOTO_FLAG =  1;
    private static final Integer PRENDRE_VIDEO_FLAG =  2;
    private String filepath_last = "photo0"+EXTENSION_PHOTO;
    private String filepathvideo_last = "video0.mp4"+EXTENSION_VIDEO;

    private static String stringforAsk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trombi);

        Button btnPhoto = (Button)findViewById(R.id.btnPhoto);
        btnPhoto.setOnClickListener(this);

        Button btnVideo = (Button)findViewById(R.id.btnVideo);
        btnVideo.setOnClickListener(this);

        Button btnAdd = (Button)findViewById(R.id.btnAddDB);
        btnAdd.setOnClickListener(this);

        sauvegardeShotsDB = new MyShotsAdapter(getApplicationContext());

        ListView ListViewDB = (ListView)findViewById(R.id.ListViewDB);

        ListViewDB.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(final AdapterView<?> adapter,
                                           View arg1, int position, final long id) {
                final AlertDialog.Builder b = new AlertDialog.Builder(
                        MyTrombi.this);
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setMessage("Supprimer ceci de votre trombinoscope ?");
                b.setPositiveButton("Oui",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                Toast.makeText(getApplicationContext(), "Supprimé", Toast.LENGTH_LONG).show();
                                sauvegardeShotsDB.removeLine(id);

                                //rafraichis la liste view
                                populate();
                            }
                        });
                b.setNegativeButton("Non",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                        b.show();
                        return true;
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // ouvre la connexion a la base de donnée
        sauvegardeShotsDB.open();
        // refraichis la listeView
        populate();

    }

    @Override
    protected void onPause() {
        super.onPause();
        // ferme la connexion a la base de donee
        // On ne doit pas le mettre, sinon quand l'appareil photo est appelé, la connexion a la base de donnée est fermé (PROBLEME AVEC TELEPHONE PYSIQUE)
        // sauvegardeShotsDB.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPhoto:
                Log.d("nouvelle photo", "cliqué");
                takeAPicture();
                break;
            case R.id.btnVideo:
                Log.d("nouvelle video", "cliqué");
                takeAMovie();
                break;
            case R.id.btnAddDB:
                Log.d("fournir la bd", "cliqué");
                sauvegardeShotsDB.insertShot("test", "test", "test");
                populate();
                break;
        }

    }

    public void takeAPicture(){

        // recupere l'ancien nom du fichier sans l extension .jpg
        String extensionRemoved = filepath_last.split("\\.")[0];

        Log.d("attention le bug",extensionRemoved);

        // recupere seulement le chiffre après "photo"
        Integer pictureNumber = Integer.parseInt(extensionRemoved.substring(5, extensionRemoved.length()));

        // l incremente
        Integer newPictureNumber = pictureNumber + 1;
        // et reconcatene le tout avec la bonne extension .jpg
        String filepath = "photo"+ String.valueOf(newPictureNumber) + EXTENSION_PHOTO;
        Log.d("filepath",String.valueOf(filepath));

        // affiche un Toast pour signaler la demande de prise photo.
        Toast.makeText(getApplicationContext(), "Demande de prise de photo",
                Toast.LENGTH_SHORT). show ();

        // on crée un Intent d'appel au lancement de la fonctionnalite photo de l'appareil
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // ajouter des information à l'Intent: ou enregistrer la photo
        File lieuSauvegarde = new File(Environment.getExternalStorageDirectory(), filepath);
        Uri fichierDeSortie = Uri.fromFile(lieuSauvegarde);

        // mise à jour du nom de fichier de sauvegarde
        filepath_last = fichierDeSortie.toString();

        // ENCORE OBLIGE DE COMMENTER CETTE LIGNE POUR QUE LE DATA.GETDATA() MARCHE
        // on ajoute a l intent des informations sur le fichier d enregistrement de l'image
        //photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fichierDeSortie);

        /* lancement de l intent... avec attente de reponse.... lorsque la reponse
        est disponible
        * la methode qui suit: 'onActivityResult est appelee automatiquement
        */
        startActivityForResult(photoIntent, PRENDRE_PHOTO_FLAG);

        // met a jour la variable
        filepath_last = filepath;
    }
    public void takeAMovie(){

        // recupere l'ancien nom du fichier sans l extension .jpg
        String extensionRemovedMovie = filepathvideo_last.split("\\.")[0];

        // recupere seulement le chiffre après "video"
        Integer movieNumber = Integer.parseInt(extensionRemovedMovie.substring(5, extensionRemovedMovie.length()));

        // l incremente
        Integer newMovieNumber = movieNumber + 1;
        // et reconcatene le tout avec la bonne extension .mp4
        String filepathMovie = "video"+ String.valueOf(newMovieNumber) + EXTENSION_VIDEO;
        Log.d("filepath movie",String.valueOf(filepathMovie));

        // affiche un Toast pour signaler la demande de prise photo.
        Toast.makeText(getApplicationContext(), "Demande de prise de vidéo",
                Toast.LENGTH_SHORT). show ();

        // on crée un Intent d'appel au lancement de la fonctionnalite photo de l'appareil
        Intent photoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        // ajouter des information à l'Intent: ou enregistrer la photo
        File lieuSauvegarde = new File(Environment.getExternalStorageDirectory(), filepathMovie);
        Uri fichierDeSortie = Uri.fromFile(lieuSauvegarde);

        // mise à jour du nom de fichier de sauvegarde
        filepathvideo_last = fichierDeSortie.toString();

        // ENCORE OBLIGE DE COMMENTER CETTE LIGNE POUR QUE LE DATA.GETDATA() MARCHE
        // on ajoute a l intent des informations sur le fichier d enregistrement de l'image
        // photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fichierDeSortie);

        /* lancement de l intent... avec attente de reponse.... lorsque la reponse
        est disponible
        * la methode qui suit: 'onActivityResult est appelee automatiquement
        */
        startActivityForResult(photoIntent, PRENDRE_VIDEO_FLAG);

        // met a jour la variable
        filepathvideo_last = filepathMovie;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        // affiche un Toast si le parametre requestCode correspond à PRENDRE_PHOTO_FLAG
        if(requestCode == PRENDRE_PHOTO_FLAG && resultCode == RESULT_OK){
            Toast.makeText(getApplicationContext(), "Photo bien sauvegardé",
                    Toast.LENGTH_LONG).show();

            // recupere l'uri de la photo et le convertis en String
            String pathPicture = data.getData().toString();

            // recupere la ligne saisie par l'utilisateur et insère dans la base de donnée
            askAndInsert("Un commentaire ?", "photo", pathPicture);

            // ANCIENNE METHODE AVANT LE "POUR ALLER PLUS LOIN"
            // ajoute l'enregistrement de la photo à la base de donnée
            //sauvegardeShotsDB.insertShot(pathPicture, "photo", commentPicture);
        }
        if(requestCode == PRENDRE_VIDEO_FLAG && resultCode == RESULT_OK){
            Toast.makeText(getApplicationContext(), "Vidéo bien sauvegardé",
                    Toast.LENGTH_LONG).show ();

            // recupere l'uri de la video et le convertis en String
            String pathMovie = data.getData().toString();

            // recupere la ligne saisie par l'utilisateur et insère dans la base de donnée
            askAndInsert("Un commentaire ?", "video", pathMovie);


            // ANCIENNE METHODE AVANT LE "POUR ALLER PLUS LOIN"
            // ajoute l'enregistrement de la vidéo à la base de donnée
            // sauvegardeShotsDB.insertShot(pathMovie,"vidéo","esdsq");
        }
    }

    // sauvegarde de variables de l'état courant avant mise en pause
    @Override
    protected void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
        if (filepath_last!=null)
            outState.putString("FILENAME_PHOTO", filepath_last);
        if (filepathvideo_last!=null)
            outState.putString("FILENAME_VIDEO", filepathvideo_last);

        //sauvegardeShotsDB.close();
    }

    // restoration de l'état de l'activity avant fermeture
    @Override
    public void onRestoreInstanceState(Bundle inState){
        Log.i("onRestoreInstanceState", "appelé");
        super.onRestoreInstanceState(inState);
        // vérification préliminaire: le bundle existe? a-t-il le champ voulu?
        if (inState != null)
            if (inState.containsKey("FILENAME_PHOTO"))
                // mise à jour du chemin d'enregistrement du fichier
                filepath_last=inState.getString("FILENAME_PHOTO");
        filepathvideo_last=inState.getString("FILENAME_VIDEO");

        //sauvegardeShotsDB.open();
    }

    // alimentation de la liste par le contenu de la base de données
    private void populate(){
        ListAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.affichage_ligne_base, sauvegardeShotsDB.getAllData(),
                new String[] {ShotsDBhelper.KEY_PATH, ShotsDBhelper.KEY_COMMENT},
                new int[] {R.id.nom_fichier, R.id.commentaire});
        // Bind to our new adapter.
        ((ListView)findViewById(R.id.ListViewDB)).setAdapter(adapter);
    }

    // AlertDialog pour demander à l'utilisateur le nom de fichier d'enregistrement.
    private void askAndInsert(String note, final String fileType, final String pathFile){

        // valeur par défaut attribuee a filename (gestion du bouton cancel)
        stringforAsk = null;

        // creation du alert dialog avec un EditText, 2 boutons: OK et Cancel
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setText(note);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                stringforAsk = input.getText().toString().trim();
                sauvegardeShotsDB.insertShot(pathFile,fileType,stringforAsk);

                // rafraichis la listView après ajout
                populate();

                Toast.makeText(getApplicationContext(), "Votre fichier a bien été ajouté au trombinoscope",
                        Toast.LENGTH_LONG).show();

            }
        });
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "Insertion dans le trombinoscope annulé",
                                Toast.LENGTH_LONG).show();
                    }
                });
        alert.show();
    }

}
