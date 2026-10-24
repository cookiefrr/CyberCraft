# Gameplay e contenuti

Riferimento ai contenuti della versione `0.2.2` per Minecraft 1.21.1 / Fabric.

## Slot corporei

Un solo impianto per slot:

| Slot | ID |
|---|---|
| Sistema operativo | `operating_system` |
| Corteccia frontale | `frontal_cortex` |
| Oculare | `ocular` |
| Mani | `hands` |
| Braccia | `arms` |
| Sistema nervoso | `nervous_system` |
| Circolatorio | `circulatory` |
| Integumentario | `integumentary` |
| Scheletro | `skeleton` |
| Gambe | `legs` |

## Capacità e tolleranza

- **Capacità** = `min(60, 20 + 2 × livello XP)`, più 20 con il Compressore Cromato.
- Ogni impianto occupa capacità; se il totale supera il massimo l'installazione viene rifiutata.
- Se il livello XP scende, gli impianti restano installati: si blocca solo l'installazione di nuovi.
- **Tolleranza**: massimo 100, rigenera circa 3 al secondo. A zero scatta il sovraccarico (Nausea, danno periodico, impianti spenti, rigenerazione dimezzata); se ne esce a 25.
- **RAM**: massimo 100 per tutti. L'Ex-Disk aumenta solo la rigenerazione.

## Impianti (42)

| Impianto | ID | Slot | Attivazione | Tolleranza | Capacità | Tier | Effetto |
|---|---|---|---|---|---|---|---|
| Berserk | `berserk` | Sistema operativo | Attivo | 30 all'uso | 14 | 3 | Forza II, Resistenza II e Velocità I per 10 s. |
| Compressore Cromato | `chrome_compressor` | Sistema operativo | Passivo | — | 10 | 3 | +20 capacità cyberware. |
| Sandevistan | `sandevistan` | Sistema operativo | Attivo | 35 all'uso | 12 | 3 | Velocità III e Rapidità II per 10 s; mob entro 15 blocchi rallentati dell'85%. Ricarica 30 s. |
| Axolotl | `axolotl` | Corteccia frontale | Passivo | — | 5 | 2 | +2 cuori massimi; Sandevistan 2 s più lungo. |
| Ottimizzatore COX-2 | `cox2_optimizer` | Corteccia frontale | Passivo | — | 5 | 2 | Cortocircuito +25% danni; RAM +0,04/s. |
| Aggiornamento RAM Ex-Disk | `ex_disk_ram_upgrade` | Corteccia frontale | Passivo | — | 6 | 3 | RAM +0,10/s. Il massimo resta 100. |
| Potenziamento Memoria Newton | `newton_memory_boost` | Corteccia frontale | Passivo | — | 4 | 2 | RAM +0,08/s; ricarica Sandevistan −3 s. |
| Sintonizzatore Quantico | `quantum_tuner` | Corteccia frontale | Passivo | — | 6 | 3 | Ricarica Sandevistan −6 s. |
| Self-ICE | `self_ice` | Corteccia frontale | Passivo | — | 4 | 2 | +2 armatura. |
| Impronta Comportamentale | `behavioral_imprint` | Oculare | Passivo | — | 4 | 2 | Quickhack −10% RAM; +1 Fortuna. |
| Ottiche Kiroshi | `kiroshi_optics` | Oculare | Passivo | — | 5 | 2 | Visione notturna permanente. |
| Coprocessore Balistico | `ballistic_coprocessor` | Mani | Passivo | — | 5 | 2 | +1,5 danno e +0,5 velocità d'attacco. |
| Impugnatura Rinforzata | `handle_wrap` | Mani | Passivo | — | 3 | 1 | +0,5 velocità d'attacco. |
| Microgeneratore | `microgenerator` | Mani | Passivo | — | 6 | 2 | +1 danno; 15% di probabilità di +2,5 danni da fulmine a mani nude. |
| Collegamento Smart | `smart_link` | Mani | Passivo | — | 5 | 2 | +2 danno d'attacco. |
| Braccia di Gorilla | `gorilla_arms` | Braccia | Passivo | — | 8 | 2 | +3 danni a mani nude (2 Tolleranza) e +20% resistenza al contraccolpo. |
| Lame da Mantide | `mantis_blades` | Braccia | Al colpo | 3 all'uso | 8 | 2 | +6 danni a mani nude; ogni colpo costa 3 Tolleranza. |
| Sistema di Lancio Proiettili | `projectile_launch_system` | Braccia | Al colpo | 20 all'uso | 10 | 3 | Esplosione sui colpi a mani nude ogni 5 s; costa 20 Tolleranza. |
| Convertitore di Adrenalina | `adrenaline_converter` | Sistema nervoso | Passivo | — | 5 | 2 | Sotto il 30% di vita: Velocità II, Forza II e Resistenza I per 6 s. Ricarica 60 s. |
| Kerenzikov | `kerenzikov` | Sistema nervoso | Passivo | — | 6 | 2 | +2% velocità di movimento. |
| Sistema di Manovra | `maneuvering_system` | Sistema nervoso | Passivo | — | 5 | 2 | +0,08 salto; −15% danni da caduta. |
| Acceleratore Sinaptico Riflessi | `reflex_synaptic_accelerator` | Sistema nervoso | Passivo | — | 7 | 3 | +2% velocità e +1 velocità d'attacco. |
| Biomonitor | `biomonitor` | Circolatorio | Passivo | — | 6 | 2 | Sotto il 25% di vita: cura 6 e Assorbimento II. Ricarica 60 s. |
| Pompa Ematica | `blood_pump` | Circolatorio | Passivo | — | 5 | 2 | +1 cuore massimo; cura 1 per uccisione. |
| Guarigione su Uccisione | `heal_on_kill` | Circolatorio | Passivo | — | 5 | 2 | Cura 4 per uccisione. |
| Secondo Cuore | `second_heart` | Circolatorio | Passivo | — | 10 | 3 | Annulla un colpo letale ogni 60 s, curando al 30% con rigenerazione e assorbimento. |
| Polmone Sintetico Bioplastico | `syn_lungs_bioplastic` | Circolatorio | Passivo | — | 4 | 2 | Respirazione acquatica; +50% movimento in acqua. |
| Messa a Terra Ignifuga | `fireproof_grounding` | Integumentario | Passivo | — | 5 | 2 | Resistenza al fuoco permanente. |
| Mimetica Ottica | `optical_camo` | Integumentario | Passivo | — | 7 | 3 | Invisibilità accovacciati e fermi; +10% velocità furtiva. |
| Editor del Dolore | `pain_editor` | Integumentario | Passivo | — | 6 | 2 | +2 armatura e +2 robustezza. |
| Shock 'n Awe | `shock_n_awe` | Integumentario | Passivo | — | 6 | 2 | Chi attacca in mischia entro 4 blocchi subisce 2,5 danni da fulmine. |
| Corazza Sottocutanea | `subdermal_armor` | Integumentario | Passivo | — | 7 | 2 | +4 armatura. |
| Articolazioni Bioniche | `bionic_joints` | Scheletro | Passivo | — | 6 | 2 | +3% velocità di movimento. |
| Midollo Denso | `dense_marrow` | Scheletro | Passivo | — | 6 | 2 | +2 cuori massimi. |
| Scheletro Epimorfico | `epimorphic_skeleton` | Scheletro | Passivo | — | 8 | 3 | +3 cuori massimi; rigenerazione sotto metà vita. |
| Articolazioni a Molla | `spring_joints` | Scheletro | Passivo | — | 5 | 1 | +0,15 salto; −30% danni da caduta. |
| Ossa di Titanio | `titanium_bones` | Scheletro | Passivo | — | 6 | 2 | +4 robustezza. |
| Gambe Cibernetiche | `cyber_legs` | Gambe | Passivo | — | 6 | 1 | Salto Potenziato III; consuma 1 Tolleranza al secondo. |
| Caviglie Rinforzate | `fortified_ankles` | Gambe | Passivo | — | 6 | 2 | −50% danni da caduta. |
| Tendini di Jenkins | `jenkins_tendons` | Gambe | Passivo | — | 5 | 2 | +5% velocità di movimento. |
| Zampe di Lince | `lynx_paws` | Gambe | Passivo | — | 4 | 1 | +30% velocità furtiva. |
| Tendini Rinforzati | `reinforced_tendons` | Gambe | Al salto | — | 6 | 2 | Doppio salto. |

## Quickhack

| Quickhack | RAM | Effetto |
|---|---:|---|
| Cortocircuito | 4 | 6 danni elettrici e fuoco per 4 s; fulmine solo grafico. |
| Riavvio Ottico | 3 | Cecità per 10 s; il mob perde il bersaglio. |
| Contagio | 5 | Veleno II e Debolezza per 10 s. |

Lancio con tasto destro mirando qualsiasi entità vivente in linea di vista, entro `min(render distance × 16, 64)` blocchi; il server valida fino a 64. Maiusc + tasto destro cambia hack; la selezione si scorre solo con Z/X, la rotella resta alla hotbar.

### Scanner e scheda bersaglio

- Con lo scanner attivo le entità viventi visibili nel campo visivo ricevono un outline rosso; il limite è 48 entità per aggiornamento, a 4 Hz.
- La scheda statistiche compare solo quando il mirino è sopra un bersaglio evidenziato: nome, tipo, fazione, salute, armatura/robustezza, effetti attivi e barra di upload.
- Quando il mirino lascia il bersaglio la scheda sfuma in 120 ms.
- Bersagli validi: mob, animali, villager, giocatori e armor stand; esclusi sé stessi, morti e spettatori.

## Materiali e componenti

| Contenuto | ID | Uso |
|---|---|---|
| Titanio grezzo / lingotto | `raw_titanium` / `titanium_ingot` | Tier 2, scheletri e arti. |
| Neodimio grezzo / lingotto | `raw_neodymium` / `neodymium_ingot` | Tier 3 e impianti avanzati. |
| Litio grezzo / lingotto | `raw_lithium` / `lithium_ingot` | Gel biologico e componenti. |
| Scheda Circuito | `circuit_board` | Componente base di impianti e banchi. |
| Servomotore | `servo_motor` | Braccia, gambe e articolazioni. |
| Gel Biologico | `bio_gel` | Impianti circolatori. |
| Lente Ottica | `optics_lens` | Impianti oculari e puntamento. |

Fusione e blasting per ogni lingotto (200 / 100 tick, 0,7 XP). I blocchi di stoccaggio si craftano 3×3 con 9 unità e si scompattano in 9.

## Worldgen

| Minerale | Altezza | Filoni per chunk | Dimensione vena |
|---|---|---:|---:|
| Titanio | Y −64…32 | 10 | 6 |
| Neodimio | Y −64…16 | 7 | 5 |
| Litio | Y −32…64 | 12 | 7 |

Ogni filone sostituisce pietra e ardesia profonda.

## Ricette

- **Tier 1**: nucleo ferro, secondario rame, struttura ferro.
- **Tier 2**: nucleo titanio, secondario oro, struttura titanio.
- **Tier 3**: nucleo neodimio, secondario diamante, struttura titanio, materiale pesante neodimio.
- Comuni: scheda circuito, servomotore, gel biologico, lente ottica, vetro, redstone.
- **Nessuna ricetta impianto usa legno.**

Totale: 70 ricette (42 impianti, 4 strumenti, 4 componenti, 2 banchi, 6 blocchi, 12 fusione/scompattamento).

## Strumenti e banchi

| Contenuto | ID | Uso |
|---|---|---|
| Neural Link | `neural_link` | Tasto destro: installa l'impianto nell'altra mano; a mani vuote apre la schermata Cyberware. Mostra TOL/RAM/CAP. |
| Cyberdeck | `cyberdeck` | Lancia il quickhack selezionato. |
| Edgerunner's Book | `edgerunners_book` | Manuale con descrizione, icona e ricetta di ogni contenuto. |
| Monocorda | `monowire` | 7 danni, colpi lenti, 2 blocchi di portata extra. |
| Banco Impianti | `implant_forge` | Griglia 3×3 per craftare gli impianti. |
| Tavolo di Installazione | `installation_table` | Installa l'impianto tenuto in mano o apre la schermata Cyberware. |

## Comandi

| Azione | Tasto |
|---|---|
| Sandevistan / Berserk | `V` |
| Schermata Cyberware | `C` |
| Scanner | `R` |
| Esegui quickhack | `G` |
| Quickhack precedente / successivo | `X` / `Z` |
| Naviga quickhack | Rotella |
| Installa impianto / apri Cyberware | Tasto destro con Neural Link |
| Cambia quickhack | Maiusc + tasto destro con Cyberdeck |

Le keybind sono rimappabili in **Opzioni → Comandi → CyberCraft**.
