# Sviluppo

## Ambiente

- **JDK 21** (obbligatorio).
- Gradle Wrapper 8.10.2 incluso: non serve un'installazione globale.
- Il progetto usa Loom 1.7.4 e mapping Mojang ufficiali.
- Le versioni di Minecraft, loader, API e mod sono in `gradle.properties`.

## Build

```bash
./gradlew build
```

In assenza di rete:

```bash
./gradlew build --offline
```

Output: `build/libs/cybercraft-<versione>.jar` e relativo sources jar.

La versione si cambia in `gradle.properties` (`mod_version`); `fabric.mod.json` espande `${version}` durante `processResources`.

## Test server

```bash
./gradlew runServer
```

Per un test rapido non interattivo:

```bash
timeout -k 10 ./gradlew runServer --offline
```

Il codice di uscita `124` è atteso quando il timeout interrompe il server: controllare nel log la presenza di `Done`, del messaggio di caricamento della mod e l'assenza di eccezioni. I log finiscono in `run/`.

Checklist minima dopo una modifica:

1. `gradlew build --offline` termina con `BUILD SUCCESSFUL`.
2. Il server carica la versione corretta e arriva a `Done`.
3. Nessun errore di datapack, ricette, worldgen o registrazione.
4. JSON e lang restano validi e con parità EN/IT.
5. Il jar contiene le classi e gli asset nuovi.

## Verifica asset

- Ogni file JSON in `src/main/resources` deve essere parsabile.
- `en_us.json` e `it_it.json` devono avere lo stesso set di chiavi.
- Ogni item deve avere modello e texture; ogni blocco blockstate, modello, loot table e tag.
- Le texture restano PNG 16×16 segnaposto finché non vengono sostituite.

## Installazione in un'istanza

1. Chiudere Minecraft e il launcher: su Windows un jar in uso è bloccato (`Device or resource busy`).
2. Eliminare la versione precedente dalla cartella `mods`.
3. Copiare il nuovo jar.
4. Verificare che ci sia una sola versione di CyberCraft.
5. Avviare con Fabric Loader e Fabric API corretti.

## Branch

| Branch | Uso |
|---|---|
| `main` | Versioni stabili e rilasciate; nessun lavoro in corso. |
| `indev` | Sviluppo attivo; build e test possono essere incompleti. |

Flusso consigliato:

```bash
git switch indev
# sviluppo e commit
./gradlew build --offline
# test server
git switch main
git merge --no-ff indev
git push origin main indev
```

Le modifiche che richiedono più passaggi restano su `indev` finché build e test server non sono puliti.

## Rilascio

1. Aggiornare `mod_version` (es. `0.2.2`).
2. Aggiornare `docs/changelog.md`, `docs/roadmap.md` e `README.md`.
3. Build e test server.
4. Commit su `indev`, merge in `main`, push di entrambi i branch.
5. Allegare il jar alla release GitHub.

## Problemi noti dell'ambiente

- L'avvio client in ambiente di sviluppo può richiedere `downloadAssets` o partire con asset vanilla incompleti: è un limite dell'ambiente, non della mod.
- Il test gameplay lato client (apertura schermate, HUD, effetti) va eseguito manualmente in un mondo.
- Su Windows, Modrinth App o `javaw.exe` possono bloccare i jar nella cartella `mods` finché il gioco è aperto.
