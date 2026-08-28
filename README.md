# APK Box

Loja de aplicativos própria para Android, feita para telas em paisagem
(centrais multimídia de carro, TV boxes). Lê um catálogo em JSON hospedado
por você e instala os APKs direto no aparelho.

## O que ele faz

- **Catálogo** — grade de apps vinda de um `catalog.json`, mostrando se cada um
  está *não instalado*, *instalado* ou *com atualização disponível*.
- **Por URL** — cola o endereço de um `.apk` e instala.
- **Instalados** — lista os apps do aparelho, abre e desinstala.
- **Ajustes** — troca a URL do catálogo e libera a permissão de "fontes desconhecidas".

Detalhes de robustez:

- O catálogo tem três níveis de queda: URL remota → última cópia baixada →
  lista embutida no app. A grade nunca fica vazia por falta de internet.
- A instalação tenta o `PackageInstaller` (que devolve sucesso/erro); se a ROM
  recusar, cai para o diálogo clássico do sistema.
- Se o catálogo informar um `sha256`, o APK baixado é conferido antes de instalar.

## Como compilar

Precisa do Android Studio instalado (ele traz o Java necessário).

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug
```

O APK sai em `app/build/outputs/apk/debug/app-debug.apk`.

Para instalar num aparelho conectado por USB:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Configurando o catálogo

A URL padrão fica em [`Prefs.kt`](app/src/main/java/br/com/apkbox/data/Prefs.kt)
e pode ser trocada em tempo de execução na aba **Ajustes**.

O formato do JSON está em [`catalog-exemplo.json`](catalog-exemplo.json).
Só `packageName`, `name` e `apkUrl` são obrigatórios.

## Assinatura de release

Opcional. Crie um `keystore.properties` na raiz com `storeFile`,
`storePassword`, `keyAlias` e `keyPassword`. Sem ele, o build de release é
assinado com a chave de debug — instala normalmente num aparelho com fontes
desconhecidas liberadas.

Esse arquivo e os `.jks` estão no `.gitignore`: **nunca suba chaves.**
