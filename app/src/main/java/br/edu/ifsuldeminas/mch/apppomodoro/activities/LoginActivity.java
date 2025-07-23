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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import br.edu.ifsuldeminas.mch.apppomodoro.R;
import br.edu.ifsuldeminas.mch.apppomodoro.utils.LocalAuthManager;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    private EditText editTextEmail, editTextSenha;
    private Button buttonLogin, buttonGoogleLogin;
    private TextView textViewCriarConta;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private LocalAuthManager localAuthManager;
    private boolean useLocalAuth = false;
    
    // ActivityResultLauncher para Google Sign In
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar autenticação local
        localAuthManager = new LocalAuthManager(this);
        
        // Verificar se usuário já está logado localmente
        if (localAuthManager.isSignedIn()) {
            irParaBoasVindas();
            return;
        }

        // Garantir que Firebase está inicializado
        ensureFirebaseInitialized();
        
        // Configurar Google Sign In
        setupGoogleSignIn();
        
        initViews();
        setupListeners();
    }
    
    private void ensureFirebaseInitialized() {
        try {
            // Verificar se já está inicializado
            if (FirebaseApp.getApps(this).isEmpty()) {
                Log.w(TAG, "Firebase não inicializado, tentando inicializar...");
                FirebaseApp.initializeApp(this);
            }
            
            // Tentar obter instância do Auth
            mAuth = FirebaseAuth.getInstance();
            
            // Verificar se usuário já está logado
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Log.d(TAG, "Usuário já logado, redirecionando...");
                irParaBoasVindas();
                return;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Erro ao inicializar Firebase Auth: " + e.getMessage());
            Toast.makeText(this, "Modo offline - Apenas login/cadastro email/senha disponível", Toast.LENGTH_LONG).show();
            mAuth = null;
        }
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
                            Toast.makeText(this, "Erro no login com Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
    
    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextSenha = findViewById(R.id.editTextSenha);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoogleLogin = findViewById(R.id.buttonGoogleLogin);
        textViewCriarConta = findViewById(R.id.textViewCriarConta);
    }
    
    private void setupListeners() {
        buttonLogin.setOnClickListener(v -> realizarLogin());
        
        if (buttonGoogleLogin != null) {
            buttonGoogleLogin.setOnClickListener(v -> signInWithGoogle());
            // Ocultar botão Google se não estiver configurado
            if (mGoogleSignInClient == null) {
                buttonGoogleLogin.setVisibility(android.view.View.GONE);
                TextView textViewOu = findViewById(R.id.textViewOu);
                if (textViewOu != null) {
                    textViewOu.setVisibility(android.view.View.GONE);
                }
            }
        }
        
        textViewCriarConta.setOnClickListener(v -> {
            startActivity(new Intent(this, CadastroActivity.class));
        });
    }
    
    private void realizarLogin() {
        String email = editTextEmail.getText().toString().trim();
        String senha = editTextSenha.getText().toString();
        
        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }
        
        buttonLogin.setEnabled(false);
        buttonLogin.setText("Entrando...");
        
        // Tentar Firebase primeiro, usar Local Auth como fallback
        if (mAuth != null && !useLocalAuth) {
            mAuth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            buttonLogin.setEnabled(true);
                            buttonLogin.setText("Entrar");
                            Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                            irParaBoasVindas();
                        } else {
                            // Firebase falhou - tentar Local Auth
                            String error = task.getException() != null ? 
                                    task.getException().getMessage() : "Erro desconhecido";
                            Log.e(TAG, "Erro no login: " + error);
                            
                            if (error.contains("API key not valid") || error.contains("internal error")) {
                                useLocalAuth = true;
                                Toast.makeText(this, "Usando autenticação local...", Toast.LENGTH_SHORT).show();
                                realizarLoginLocal(email, senha);
                            } else {
                                buttonLogin.setEnabled(true);
                                buttonLogin.setText("Entrar");
                                Log.e(TAG, "Falha no login: " + error);
                                Toast.makeText(this, "Erro: " + error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            // Usar Local Auth diretamente
            realizarLoginLocal(email, senha);
        }
    }
    
    private void realizarLoginLocal(String email, String senha) {
        if (localAuthManager.signIn(email, senha)) {
            buttonLogin.setEnabled(true);
            buttonLogin.setText("Entrar");
            Toast.makeText(this, "Login local realizado com sucesso!", Toast.LENGTH_SHORT).show();
            irParaBoasVindas();
        } else {
            buttonLogin.setEnabled(true);
            buttonLogin.setText("Entrar");
            Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void signInWithGoogle() {
        if (mGoogleSignInClient == null) {
            Toast.makeText(this, "Login com Google não está disponível. Configure o Firebase primeiro.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(this, "Login com Google realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        irParaBoasVindas();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Falha na autenticação.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void irParaBoasVindas() {
        startActivity(new Intent(this, BoasVindasActivity.class));
        finish();
    }
}
