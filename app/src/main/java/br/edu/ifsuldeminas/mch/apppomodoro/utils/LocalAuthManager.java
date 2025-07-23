package br.edu.ifsuldeminas.mch.apppomodoro.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Sistema de autenticação local temporário
 * Usado quando Firebase não está configurado corretamente
 */
public class LocalAuthManager {
    private static final String TAG = "LocalAuthManager";
    private static final String PREFS_NAME = "local_auth_prefs";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";
    private static final String KEY_CURRENT_USER_NAME = "current_user_name";
    
    private SharedPreferences prefs;
    private Context context;
    
    public LocalAuthManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Criar conta local
     */
    public boolean createAccount(String name, String email, String password) {
        try {
            // Verificar se email já existe
            if (prefs.contains("user_" + email)) {
                Log.w(TAG, "Email já cadastrado: " + email);
                return false;
            }
            
            // Gerar hash da senha
            String passwordHash = hashPassword(password);
            String userId = UUID.randomUUID().toString();
            
            // Salvar dados do usuário
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_" + email, passwordHash);
            editor.putString("name_" + email, name);
            editor.putString("id_" + email, userId);
            editor.apply();
            
            Log.d(TAG, "Conta local criada para: " + email);
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Erro ao criar conta local: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Alias para createAccount (compatibilidade)
     */
    public boolean signUp(String name, String email, String password) {
        return createAccount(name, email, password);
    }
    
    /**
     * Login local
     */
    public boolean signIn(String email, String password) {
        try {
            String storedHash = prefs.getString("user_" + email, null);
            if (storedHash == null) {
                Log.w(TAG, "Usuário não encontrado: " + email);
                return false;
            }
            
            String passwordHash = hashPassword(password);
            if (storedHash.equals(passwordHash)) {
                // Login bem-sucedido - salvar sessão
                String userId = prefs.getString("id_" + email, UUID.randomUUID().toString());
                String name = prefs.getString("name_" + email, "Usuário");
                
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(KEY_CURRENT_USER_ID, userId);
                editor.putString(KEY_CURRENT_USER_EMAIL, email);
                editor.putString(KEY_CURRENT_USER_NAME, name);
                editor.apply();
                
                Log.d(TAG, "Login local bem-sucedido: " + email);
                return true;
            } else {
                Log.w(TAG, "Senha incorreta para: " + email);
                return false;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Erro no login local: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verificar se usuário está logado
     */
    public boolean isSignedIn() {
        return prefs.getString(KEY_CURRENT_USER_ID, null) != null;
    }
    
    /**
     * Obter ID do usuário atual
     */
    public String getCurrentUserId() {
        return prefs.getString(KEY_CURRENT_USER_ID, null);
    }
    
    /**
     * Obter email do usuário atual
     */
    public String getCurrentUserEmail() {
        return prefs.getString(KEY_CURRENT_USER_EMAIL, null);
    }
    
    /**
     * Obter nome do usuário atual
     */
    public String getCurrentUserName() {
        return prefs.getString(KEY_CURRENT_USER_NAME, "Usuário");
    }
    
    /**
     * Logout
     */
    public void signOut() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_CURRENT_USER_ID);
        editor.remove(KEY_CURRENT_USER_EMAIL);
        editor.remove(KEY_CURRENT_USER_NAME);
        editor.apply();
        
        Log.d(TAG, "Logout local realizado");
    }
    
    /**
     * Hash da senha (simples para demonstração)
     */
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
}
