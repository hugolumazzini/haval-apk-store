# Haval APK Store

Loja de aplicativos para a **central multimídia do Haval H6**. Traz um catálogo
com os apps que a comunidade usa no carro — Impulse, AutoPanel, Haval Radio,
Haval Climate Control, Waze — e instala cada um direto na tela da central, sem
cabo e sem computador.

Nasceu para tapar um buraco: a aba "Instalar Apps" do Impulse foi descontinuada
e o catálogo dela está obsoleto. Este app **não substitui o Impulse** — anda ao
lado dele, e serve inclusive para instalar e atualizar o próprio Impulse. A
interface segue a linguagem visual do Impulse de propósito, para os dois não
destoarem na mesma tela.

> Deve funcionar em qualquer Android em paisagem (outras centrais, TV box), mas
> só é testado no H6.

## Instalar

Cole este endereço no "Instalar via URL" do Impulse, que já está na central:

```
https://tinyurl.com/havalapkstore
```

O link é fixo e sempre entrega a versão mais recente — não muda a cada release.
Depois da primeira instalação a Haval APK Store se atualiza sozinha, porque ela
também está no próprio catálogo.

Requer Android 6.0 ou mais novo. Os arquivos ficam em
[Releases](https://github.com/hugolumazzini/haval-apk-store/releases).

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
https://raw.githubusercontent.com/hugolumazzini/haval-apk-store/main/catalog.json
```

Esse é o endereço padrão, definido em
[`Prefs.kt`](app/src/main/java/br/com/hugolumazzini/havalapkstore/data/Prefs.kt) e trocável em tempo
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

O `apkUrl` precisa terminar entregando o arquivo. Redirecionamentos funcionam —
links `shorturl.at` foram testados e passam. O que **não** funciona é página de
download que exige um clique de confirmação (Google Drive, APKMirror): o app
receberia HTML no lugar do APK.

### Sobre o `sha256` das entradas com link encurtado

Waze e ReVanced Manager Plus usam encurtadores, que apontam para "a versão
atual" e não para um arquivo fixo. Quando essa versão mudar, o hash gravado aqui
deixa de bater e o app vai **recusar a instalação** — que é o comportamento
desejado: melhor falhar do que instalar um arquivo que não é o esperado.

Quando isso acontecer, baixe o novo arquivo e atualize o `sha256`, o
`versionCode` e o `versionName` desta entrada. As entradas do GitHub não têm
esse problema: apontam para uma versão fixa, que nunca muda.

## Publicar uma versão nova

O padrão, para o link curto continuar valendo:

1. Suba `versionCode` e `versionName` em `app/build.gradle.kts`. O
   `versionCode` **precisa** aumentar — é por ele que o Android e o catálogo
   sabem que há atualização.
2. `./gradlew assembleRelease` e copie o resultado para `haval-apk-store.apk`.
   **O nome do arquivo é sempre esse, sem número de versão** — é o que faz
   `releases/latest/download/haval-apk-store.apk` funcionar, e é para onde o
   `tinyurl.com/havalapkstore` aponta.
3. `gh release create vX.Y.Z haval-apk-store.apk` — tag em três números.
4. Atualize a entrada da própria loja no `catalog.json` (`versionCode`,
   `versionName`, `sizeBytes`, `sha256` e a URL fixada da tag) e dê push.

Nas notas do release: link curto, o que mudou, versão mínima do Android. **Sem
SHA-256** — ele não serve para ninguém ali. Quem confere o hash é o app, pelo
`catalog.json`, automaticamente.

O `raw.githubusercontent.com` guarda cópia por uns 5 minutos, então o catálogo
novo só aparece no app depois disso.

## Assinatura de release

Opcional. Crie um `keystore.properties` na raiz com `storeFile`,
`storePassword`, `keyAlias` e `keyPassword`. Sem ele, o build de release é
assinado com a chave de debug — instala normalmente num aparelho com fontes
desconhecidas liberadas.

Esse arquivo e os `.jks` estão no `.gitignore`: **nunca suba chaves.**
