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

## O catálogo

A lista de apps vive em [`catalog.json`](catalog.json), na raiz deste
repositório, e é servida em:

```
https://raw.githubusercontent.com/hugolumazzini/apkbox/main/catalog.json
```

Esse é o endereço padrão, definido em
[`Prefs.kt`](app/src/main/java/br/com/apkbox/data/Prefs.kt) e trocável em tempo
de execução na aba **Ajustes**. Uma cópia do mesmo arquivo fica em
`app/src/main/assets/` como último recurso, para a central sem internet.

**Para adicionar um app:** edite o `catalog.json` e faça push. O app pega a
versão nova no próximo "Recarregar" — não precisa recompilar nada.

Só `packageName`, `name` e `apkUrl` são obrigatórios. Os demais campos:

| Campo | Para que serve |
|---|---|
| `versionCode` | Compara com a versão instalada para avisar que há atualização. Sem ele, o app nunca aparece como *atualizável*. |
| `sha256` | Confere o arquivo baixado antes de instalar. Sem ele, o download não é verificado. |
| `versionName`, `sizeBytes`, `category` | Só exibição. |
| `iconUrl` | Ícone na grade. Sem ele, mostra a inicial do nome. |

Para levantar os valores de um APK:

```bash
aapt2 dump badging app.apk | head -1   # packageName, versionCode, versionName
shasum -a 256 app.apk                  # sha256
stat -f%z app.apk                      # sizeBytes
```

O `apkUrl` precisa apontar **direto** para o arquivo. Links encurtados e páginas
de download com confirmação não funcionam: o app receberia HTML no lugar do APK.

## Assinatura de release

Opcional. Crie um `keystore.properties` na raiz com `storeFile`,
`storePassword`, `keyAlias` e `keyPassword`. Sem ele, o build de release é
assinado com a chave de debug — instala normalmente num aparelho com fontes
desconhecidas liberadas.

Esse arquivo e os `.jks` estão no `.gitignore`: **nunca suba chaves.**
