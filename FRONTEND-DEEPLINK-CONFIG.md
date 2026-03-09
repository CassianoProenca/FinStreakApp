# Guia de Configuração Frontend: Deep Linking e Recuperação de Senha

Este documento é direcionado à equipe de Frontend (React Native / Expo). Ele detalha os passos necessários para capturar o link de recuperação de senha enviado por e-mail e abrir o App diretamente na tela correta.

## 🎯 O Cenário
O backend envia um e-mail de redefinição de senha com o seguinte formato de link:
`https://finstreak.app.cassiano.cloud/reset-password?token=SEU_TOKEN_AQUI`

O objetivo é que, ao clicar neste link no celular, o sistema operacional (iOS/Android) **não abra o navegador**, mas sim o aplicativo FinStreak, enviando o `token` para a tela de redefinição de senha.

---

## 🛠️ Passo 1: Configuração do Expo (`app.json`)
Você precisa informar ao Expo que o seu aplicativo é "dono" do domínio `finstreak.app.cassiano.cloud`.

No seu arquivo `app.json` (ou `app.config.js`), adicione as configurações de `scheme` e `associatedDomains`:

```json
{
  "expo": {
    "name": "FinStreak",
    "scheme": "finstreak",
    "ios": {
      "bundleIdentifier": "com.financial.app",
      "associatedDomains": [
        "applinks:finstreak.app.cassiano.cloud"
      ]
    },
    "android": {
      "package": "com.financial.app",
      "intentFilters": [
        {
          "action": "VIEW",
          "autoVerify": true,
          "data": [
            {
              "scheme": "https",
              "host": "finstreak.app.cassiano.cloud",
              "pathPrefix": "/reset-password"
            }
          ],
          "category": [
            "BROWSABLE",
            "DEFAULT"
          ]
        }
      ]
    }
  }
}
```
*Atenção: Substitua `com.financial.app` pelo seu `bundleIdentifier` / `package` real se for diferente.*

---

## 🚀 Passo 2: Configuração de Navegação (React Navigation)
No seu arquivo de rotas (onde você configura o `NavigationContainer`), você precisa mapear a URL para a sua tela.

```javascript
import { NavigationContainer } from '@react-navigation/native';
import * as Linking from 'expo-linking';

// Prefixo principal do Deep Link
const prefix = Linking.createURL('/');

const linking = {
  // Lista dos prefixos aceitos
  prefixes: [prefix, 'https://finstreak.app.cassiano.cloud', 'finstreak://'],
  
  // Mapeamento das telas
  config: {
    screens: {
      // O nome "ResetPasswordScreen" deve ser o mesmo nome da sua rota no Stack Navigator
      ResetPasswordScreen: 'reset-password',
    },
  },
};

export default function App() {
  return (
    <NavigationContainer linking={linking}>
      {/* Suas rotas aqui */}
    </NavigationContainer>
  );
}
```

---

## 🧩 Passo 3: Capturando o Token na Tela
Quando o app abrir, a rota enviará o `token` da URL como parâmetro de navegação para a sua tela.

No seu componente de tela de redefinição de senha (`ResetPasswordScreen`), você recupera o token assim:

```javascript
import React from 'react';
import { View, Text, Button } from 'react-native';

export default function ResetPasswordScreen({ route }) {
  // O token virá dos parâmetros (extraído automaticamente da URL ?token=...)
  const { token } = route.params || {};

  const handleSaveNewPassword = () => {
    // Chame a sua API passando a nova senha e o token
    // POST /api/auth/reset-password { token, newPassword }
    console.log("Token recebido do email:", token);
  };

  return (
    <View style={{ padding: 20 }}>
      <Text>Redefinir Senha</Text>
      <Text>Token Válido: {token ? "Sim" : "Não"}</Text>
      {/* Seus inputs de nova senha */}
    </View>
  );
}
```

---

## 🧪 Como Testar Localmente
Antes do App estar publicado nas lojas, você pode testar o fluxo de Deep Linking usando o CLI do Expo.

Com o servidor do Expo rodando, execute no terminal:
```bash
# Para testar no Android Emulator:
npx uri-scheme open exp://127.0.0.1:8081/--/reset-password?token=teste123 --android

# Para testar no iOS Simulator:
npx uri-scheme open exp://127.0.0.1:8081/--/reset-password?token=teste123 --ios
```
Se a sua tela de Reset Password abrir e conseguir imprimir `teste123`, a integração frontend está pronta!

---

**Nota Final:** Para que o App abra no celular físico (produção/TestFlight), o backend hospedará dois arquivos JSON (`assetlinks.json` e `apple-app-site-association`). O frontend não precisa se preocupar com esses arquivos, apenas com as configurações acima.
