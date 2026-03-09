# Configuração de Infraestrutura de E-mail e Deep Linking

Este documento descreve as configurações externas necessárias para que o sistema de recuperação de senha via **Resend** e **React Native** funcione corretamente utilizando o subdomínio dedicado `finstreak.app.cassiano.cloud`.

## 1. Variáveis de Ambiente (Backend)
Certifique-se de que o seu arquivo `.env` no servidor possui as seguintes chaves:

```env
RESEND_API_KEY=re_sua_chave_real_aqui
RESEND_FROM_EMAIL=no-reply@cassiano.cloud
FRONTEND_URL=https://finstreak.app.cassiano.cloud
```

---

## 2. Configuração no Painel da Resend
Para enviar e-mails para qualquer endereço:
1. Vá em **Resend Dashboard > Domains > Add Domain**.
2. Adicione `cassiano.cloud` (o domínio raiz é suficiente para enviar de qualquer subdomínio).
3. Adicione os registros DNS (MX e TXT) no seu provedor de DNS da Hostinger.

---

## 3. Deep Linking (Para o App abrir sozinho)
Os arquivos de validação devem estar hospedados no subdomínio do frontend:

### A. Android (Digital Asset Links)
Crie o arquivo em: `https://finstreak.app.cassiano.cloud/.well-known/assetlinks.json`
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
*Nota: A chave SHA256 é obtida no console do Google Play ou via `keytool` no ambiente de desenvolvimento.*

### B. iOS (Apple App Site Association)
Crie o arquivo em: `https://finstreak.app.cassiano.cloud/.well-known/apple-app-site-association` (sem extensão .json)
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

---

## 4. Configuração no Nginx Proxy Manager (NPM)
No Host de Proxy do subdomínio `finstreak.app.cassiano.cloud`, aba **Advanced**:

```nginx
location /.well-known/ {
    # Mapeie o volume no container do NPM para esta pasta
    alias /data/public/.well-known/;
    add_header Content-Type application/json;
}
```

---

## 5. Orientações para o Frontend (React Native)
O desenvolvedor frontend deve configurar o `associatedDomains`:
- **Expo:** Configurar `scheme: "finstreak"` e `associatedDomains: ["finstreak.app.cassiano.cloud"]` no `app.json`.
- **Nativo:** Adicionar `intent-filter` no `AndroidManifest.xml` (Android) e `Associated Domains` no Xcode (iOS).

O App deve ouvir a rota `/reset-password` e extrair o parâmetro `token` da URL para abrir a tela de nova senha.
