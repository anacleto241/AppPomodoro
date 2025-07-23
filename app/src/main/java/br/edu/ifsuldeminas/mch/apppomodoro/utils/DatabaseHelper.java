package br.edu.ifsuldeminas.mch.apppomodoro.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifsuldeminas.mch.apppomodoro.activities.HistoricoActivity;
import br.edu.ifsuldeminas.mch.apppomodoro.activities.TelaPrincipalActivity;
import br.edu.ifsuldeminas.mch.apppomodoro.models.Ciclo;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pomodoro.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_CICLOS = "ciclos";
    private static final String COL_ID = "id";
    private static final String COL_DESCRICAO = "descricao";
    private static final String COL_DURACAO = "duracao";
    private static final String COL_DATA = "data";



    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CICLOS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DESCRICAO + " TEXT, "
                + COL_DURACAO + " INTEGER, "
                + COL_DATA + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Método chamado quando a versão do banco de dados é atualizada
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CICLOS);
        onCreate(db);

    }

    public void adicionarCiclo(String descricao, int duracao) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DESCRICAO, descricao);
        values.put(COL_DURACAO, duracao);
        db.insert(TABLE_CICLOS, null, values);
        db.close();
    }

    public List<Ciclo> getAllCiclos() {
        List<Ciclo> ciclos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CICLOS + " ORDER BY " + COL_DATA + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                Ciclo ciclo = new Ciclo(
                        cursor.getString(0),        // id (convert to String)
                        null,                       // disciplinaId - SQLite legacy doesn't have this
                        cursor.getString(1),        // descricao (column 1, not 2)
                        cursor.getInt(2),          // duracao (column 2, not 3)
                        cursor.getLong(3)          // dataHora (column 3, not 4)
                );
                ciclos.add(ciclo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ciclos;
    }

    public int getTotalCiclos() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CICLOS, null);
        cursor.moveToFirst();
        int total = cursor.getInt(0);
        cursor.close();
        return total;
    }

    public void excluirCiclo(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CICLOS, COL_ID + " = ?", new String[]{id});
        db.close();
    }
}
