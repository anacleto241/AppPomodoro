package br.edu.ifsuldeminas.mach.apppomodoro.data.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import br.edu.ifsuldeminas.mach.apppomodoro.data.entities.CicloEntity;
import br.edu.ifsuldeminas.mach.apppomodoro.data.entities.ConfigEntity;
import br.edu.ifsuldeminas.mach.apppomodoro.data.dao.CicloDao;
import br.edu.ifsuldeminas.mach.apppomodoro.data.dao.ConfigDao;

@Database(
    entities = {CicloEntity.class, ConfigEntity.class},
    version = 1,
    exportSchema = false
)
public abstract class PomodoroDatabase extends RoomDatabase {
    
    private static volatile PomodoroDatabase INSTANCE;
    
    public abstract CicloDao cicloDao();
    public abstract ConfigDao configDao();
    
    public static PomodoroDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PomodoroDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        PomodoroDatabase.class,
                        "pomodoro_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
