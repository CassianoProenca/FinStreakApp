# 🤖 Guia de Configuração de Infraestrutura (Agent-Ready)

Este documento contém instruções estruturadas para que um **agente de IA** configure a infraestrutura do FinStreak na VPS via SSH. 

---

## 🛑 Status atual: Pendente (Aguardando Chaves de Lojas)
A configuração de Deep Linking está em pausa aguardando dois valores que só estarão disponíveis após etapas futuras do projeto:
1. **SHA256 Fingerprint**: Obtida após cadastrar o app no Google Play Console (em Configurar -> Integridade do app).
2. **Apple Team ID**: Obtido após criar/ter acesso a uma conta no Apple Developer Program (developer.apple.com -> Account -> Membership -> Team ID).

**Instrução para Agentes:** Pode deixar a estrutura criada no NPM já com os placeholders `SUA_CHAVE_SHA256_AQUI` e `SEU_TEAM_ID`. Quando ambos os valores estiverem disponíveis, a configuração no NPM será concluída em seguida.

---

## 🛠️ Tarefa 1: Configuração do Nginx Proxy Manager (NPM)
**Objetivo:** Servir arquivos estáticos de validação de Deep Linking para o subdomínio do frontend.

### Instruções para o Agente:
1. Acesse o diretório onde o volume do Nginx Proxy Manager está mapeado na VPS.
2. Crie a estrutura de diretórios: `public/.well-known/`.
3. Crie o arquivo `assetlinks.json` (Android) com o conteúdo abaixo:
   ```json
   [{
     "relation": ["delegate_permission/common.handle_all_urls"],
     "target": {
       "namespace": "android_app",
       "package_name": "com.financial.app", 
       "sha256_cert_fingerprints": ["SUA_CHAVE_SHA256_AQUI"] 
     }
   }]
   ```
4. Crie o arquivo `apple-app-site-association` (iOS - sem extensão) com o conteúdo abaixo:
   ```json
   {
     "applinks": {
       "apps": [],
       "details": [{
           "appID": "SEU_TEAM_ID.com.financial.app",
           "paths": ["/reset-password*"]
       }]
     }
   }
   ```
5. No painel do NPM, acesse as configurações de "Advanced" do Proxy Host `finstreak.app.cassiano.cloud` e insira:
   ```nginx
   location /.well-known/ {
       alias /data/public/.well-known/;
       add_header Content-Type application/json;
   }
   ```

---

## 📧 Tarefa 2: Configuração de E-mail (Resend)
**Objetivo:** Validar o domínio e configurar variáveis de ambiente.

### Instruções para o Agente:
1. Verifique se o domínio `cassiano.cloud` está verificado no painel da Resend.
2. Acesse o servidor via SSH e atualize o arquivo `.env` do container backend (ou os GitHub Secrets) com os seguintes valores:
   - `RESEND_API_KEY`: Chave da API (re_...).
   - `RESEND_FROM_EMAIL`: `no-reply@cassiano.cloud`.
   - `FRONTEND_URL`: `https://finstreak.app.cassiano.cloud`.

---

## 📱 Tarefa 3: Configuração de Deep Linking (Frontend/Expo)
**Objetivo:** Configurar o App React Native para ouvir os links do domínio.

### Instruções para o Agente de Frontend:
1. Abra o arquivo `app.json` do projeto Expo.
2. Adicione ou atualize o campo `scheme` para `finstreak`.
3. Adicione o domínio associado para iOS e Android:
   - No iOS (`ios.associatedDomains`): `applinks:finstreak.app.cassiano.cloud`.
   - No Android (`android.intentFilters`): Configure o `host` como `finstreak.app.cassiano.cloud` e `pathPrefix` como `/reset-password`.
4. Configure o `Linking` no `NavigationContainer` do React Navigation para mapear a URL `https://finstreak.app.cassiano.cloud/reset-password` para a tela de redefinição de senha, capturando o parâmetro `token`.

---

## 🚀 Comando de Verificação Final
Para validar se a infraestrutura está correta, o agente deve executar:
```bash
curl -I https://finstreak.app.cassiano.cloud/.well-known/assetlinks.json
```
**Resultado esperado:** HTTP 200 e `Content-Type: application/json`.
