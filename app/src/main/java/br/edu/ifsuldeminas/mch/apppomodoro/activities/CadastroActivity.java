package br.edu.ifsuldeminas.mch.apppomodoro.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import br.edu.ifsuldeminas.mch.apppomodoro.R;
import br.edu.ifsuldeminas.mch.apppomodoro.utils.LocalAuthManager;

public class CadastroActivity extends AppCompatActivity {
    private static final String TAG = "CadastroActivity";
    
    private EditText editTextNome, editTextEmail, editTextSenha, editTextConfirmarSenha;
    private Button buttonCadastrar, buttonGoogleSignUp;
    private TextView textViewJaTemConta;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private LocalAuthManager localAuthManager;
    private boolean useLocalAuth = false;
    
    // ActivityResultLauncher para Google Sign In
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        
        // Inicializar autenticação local
        localAuthManager = new LocalAuthManager(this);
        
        try {
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Erro ao inicializar Firebase Auth: " + e.getMessage());
            Toast.makeText(this, "Usando modo offline", Toast.LENGTH_SHORT).show();
            useLocalAuth = true;
        }
        
        // Configurar Google Sign In
        setupGoogleSignIn();
        
        initViews();
        setupListeners();
    }
    
    private void setupGoogleSignIn() {
        try {
            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        } catch (Exception e) {
            Log.w(TAG, "Erro ao configurar Google Sign In: " + e.getMessage());
            mGoogleSignInClient = null;
        }
        
        // Initialize ActivityResultLauncher
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                            firebaseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e) {
                            Log.w(TAG, "Google sign in failed", e);
                            Toast.makeText(this, "Erro no cadastro com Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
    
    private void initViews() {
        editTextNome = findViewById(R.id.editTextNome);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextSenha = findViewById(R.id.editTextSenha);
        editTextConfirmarSenha = findViewById(R.id.editTextConfirmarSenha);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);
        buttonGoogleSignUp = findViewById(R.id.buttonGoogleSignUp);
        textViewJaTemConta = findViewById(R.id.textViewJaTemConta);
    }
    
    private void setupListeners() {
        buttonCadastrar.setOnClickListener(v -> realizarCadastro());
        
        if (buttonGoogleSignUp != null) {
            buttonGoogleSignUp.setOnClickListener(v -> signUpWithGoogle());
            // Ocultar botão Google se não estiver configurado
            if (mGoogleSignInClient == null) {
                buttonGoogleSignUp.setVisibility(android.view.View.GONE);
            }
        }
        
        textViewJaTemConta.setOnClickListener(v -> finish());
    }
    
    private void realizarCadastro() {
        String nome = editTextNome.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String senha = editTextSenha.getText().toString();
        String confirmarSenha = editTextConfirmarSenha.getText().toString();
        
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!senha.equals(confirmarSenha)) {
            Toast.makeText(this, "Senhas não coincidem", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (senha.length() < 6) {
            Toast.makeText(this, "Senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }
        
        buttonCadastrar.setEnabled(false);
        buttonCadastrar.setText("Cadastrando...");
        
        // Tentar Firebase primeiro, usar Local Auth como fallback
        if (mAuth != null && !useLocalAuth) {
            mAuth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Atualizar o perfil com o nome do usuário
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nome)
                                    .build();
                            
                            mAuth.getCurrentUser().updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        buttonCadastrar.setEnabled(true);
                                        buttonCadastrar.setText("Cadastrar");
                                        
                                        if (profileTask.isSuccessful()) {
                                            Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, BoasVindasActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Firebase falhou - tentar Local Auth
                            String error = task.getException() != null ? 
                                    task.getException().getMessage() : "Erro desconhecido";
                            Log.e(TAG, "Erro no cadastro: " + error);
                            
                            if (error.contains("API key not valid") || error.contains("internal error")) {
                                useLocalAuth = true;
                                Toast.makeText(this, "Usando cadastro local...", Toast.LENGTH_SHORT).show();
                                realizarCadastroLocal(nome, email, senha);
                            } else {
                                buttonCadastrar.setEnabled(true);
                                buttonCadastrar.setText("Cadastrar");
                                Toast.makeText(this, "Erro ao criar conta: " + error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            // Usar Local Auth diretamente
            realizarCadastroLocal(nome, email, senha);
        }
    }
    
    private void realizarCadastroLocal(String nome, String email, String senha) {
        if (localAuthManager.signUp(nome, email, senha)) {
            buttonCadastrar.setEnabled(true);
            buttonCadastrar.setText("Cadastrar");
            Toast.makeText(this, "Conta criada localmente com sucesso!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, BoasVindasActivity.class));
            finish();
        } else {
            buttonCadastrar.setEnabled(true);
            buttonCadastrar.setText("Cadastrar");
            Toast.makeText(this, "Erro ao criar conta local", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void signUpWithGoogle() {
        if (mGoogleSignInClient == null) {
            Toast.makeText(this, "Cadastro com Google não está disponível. Configure o Firebase primeiro.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }
    
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Cadastro com Google realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, BoasVindasActivity.class));
                        finish();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Falha no cadastro com Google.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
