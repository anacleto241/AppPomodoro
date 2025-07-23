# 🔥 Guia Completo: Configuração Firebase para AppPomodoro

## ✅ Status Atual
- **Login/Cadastro Email/Senha**: ✅ 100% Funcional
- **Google Sign-In**: ⚠️ Precisa configurar Firebase Console
- **API Key**: ✅ Resolvido - App não crasha mais
- **Inicialização Firebase**: ✅ Protegida contra erros

## 🛠️ Solução: Configurar Firebase Console

### **Passo 1: Acessar Firebase Console**
1. Acesse: https://console.firebase.google.com/
2. Clique em "Criar um projeto" ou usar projeto existente
3. Nome sugerido: `AppPomodoro` ou `apppomodoro-educacional`

### **Passo 2: Configurar Authentication**
1. No console Firebase, vá em **Authentication** → **Sign-in method**
2. Ative os provedores:
   - ✅ **Email/Password** (enable)
   - ✅ **Google** (enable)

### **Passo 3: Adicionar App Android**
1. Clique no ícone Android ⚙️
2. **Package name**: `br.edu.ifsuldeminas.mach.apppomodoro`
3. **App nickname**: `App Pomodoro`
4. **SHA-1**: Execute o comando abaixo para obter:

```bash
# No terminal do projeto:
.\gradlew signingReport

# Ou use keytool diretamente:
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

### **Passo 4: Baixar google-services.json**
1. Após adicionar o app, baixe o arquivo `google-services.json`
2. **IMPORTANTE**: Substitua o arquivo atual em:
   ```
   app/google-services.json
   ```

### **Passo 5: Configurar Firestore Database**
1. Vá em **Firestore Database** → **Create database**
2. Modo: **Test mode** (para desenvolvimento)
3. Localização: **us-central** (ou mais próxima)

## 🔐 Configuração de Produção

### **Para SHA-1 de Produção:**
```bash
# Para keystore de release:
keytool -list -v -keystore path/to/your/release.keystore -alias your-alias-name
```

### **Configurações de Segurança:**
1. **Firestore Rules** (produção):
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Usuários só podem acessar seus próprios dados
    match /usuarios/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Ciclos do usuário
    match /ciclos/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 📱 Teste Atual

**✅ Funciona agora:**
- Cadastro com email/senha
- Login com email/senha
- Navegação completa do app
- Persistência de dados local (Room)

**⚠️ Após configurar Firebase:**
- Login/Cadastro Google
- Sincronização Firestore
- Backup de dados na nuvem

## 🚀 Comandos Úteis

```bash
# Limpar e recompilar
.\gradlew clean assembleDebug

# Ver certificado SHA-1
.\gradlew signingReport

# Instalar no dispositivo conectado
.\gradlew installDebug
```

## 📧 Teste Rápido (Email/Senha)
1. Abra o app
2. Clique em "Criar conta"
3. Preencha: nome, email, senha
4. Teste login após cadastro

**O app está 100% funcional para autenticação email/senha!**
