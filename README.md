# Calculadora IMC (Jetpack Compose)

Aplicación Android hecha con **Kotlin** y **Jetpack Compose** (Material Design 3). Calcula el **Índice de Masa Corporal (IMC)** de una persona adulta, con **estilo japonés**.

## Qué hace

1. **Formulario** con cinco datos:
   - **Nombre**: obligatorio, de 2 a 40 caracteres, solo letras, espacios y guiones.
   - **Edad**: número entero entre 18 y 90 años, porque es para personas adultas. No admite negativos (`-5`) ni decimales (`10,4`).
   - **Sexo**: Hombre o Mujer, con botones de opción (`RadioButton`).
   - **Peso** en kg: un número entre 20 y 300. Se puede escribir con coma o con punto (`70,5` o `70.5`).
   - **Estatura** en cm: un número entre 100 y 250.
2. Botón **«Calcular IMC»**. Si algún dato está mal, el campo se marca en rojo y debajo aparece el error. Esto ocurre a partir del primer intento y el aviso se actualiza mientras escribes.
3. **Pantalla de resultado**:
   - El nombre de la persona.
   - Su IMC como **número** (p. ej. `22,84`) y **por escrito** (p. ej. *Peso normal*).
   - Un **comentario personal** distinto para cada rango.
   - Una **barra de colores** con un tramo por rango y un marcador que señala dónde está la persona.
   - Una **ilustración** que depende del **sexo** y del **rango**: un **samurái** para los hombres y una **geisha** para las mujeres.
   - Debajo, la **tabla de rangos** del IMC con el rango de la persona resaltado.
   - Botón **«Volver»**. El botón «atrás» del sistema también vuelve. Los datos del formulario se mantienen.

## Fórmula y rangos (OMS, adultos)

```
IMC = peso (kg) / estatura (m)²
```

Clasificación internacional del IMC en adultos de la OMS:

| Rango             | IMC            | Comentario                                                                     |
|-------------------|----------------|--------------------------------------------------------------------------------|
| Delgadez severa   | < 16,00        | Estás en los huesos y esto ya preocupa: come bien y pide cita con tu médico.   |
| Delgadez moderada | 16,00 – 16,99  | Un día de viento sales volando: ponte un buen plato de lentejas.               |
| Delgadez leve     | 17,00 – 18,49  | Hazme un favor y cómete otro bocadillo más.                                    |
| Peso normal       | 18,50 – 24,99  | Estamos en línea, pero no te emociones y contrólate con las cervezas.          |
| Preobesidad       | 25,00 – 29,99  | Se acercan las Navidades: tienes prohibidos los polvorones, por bicho bola.    |
| Obesidad tipo I   | 30,00 – 34,99  | Haz un favor al mundo y a tu salud y deja de comer Bollycaos.                  |
| Obesidad tipo II  | 35,00 – 39,99  | El sofá ya tiene tu forma: levántate y sal a andar antes de que te adopte.     |
| Obesidad tipo III | ≥ 40,00        | Esto ya no es broma: pide cita con tu médico, que tu salud va primero.         |

El IMC se muestra con dos decimales y el rango se elige con ese mismo valor redondeado.

## Estilo japonés

- **Colores tradicionales**: fondo papel *washi*, texto tinta *sumi*, bermellón *shu* (como las puertas *torii*), índigo *ai* y dorado *kin*. Tiene modo claro y oscuro, y no usa el color dinámico del móvil.
- **Rangos**: azules índigo para la delgadez, verde *matcha* para el peso normal, caqui para la preobesidad y rojos cada vez más oscuros para la obesidad.
- **Letra**: los títulos y el número del IMC usan letra *serif*, que recuerda a la letra *Mincho* japonesa.
- **Barra superior** bermellón con el título en japonés debajo (体格指数, «índice de masa corporal», y 結果, «resultado»), y bajo ella una franja de olas *seigaiha* (青海波).
- **Formas**: esquinas poco redondeadas y tarjetas con un borde dorado fino.

## Estructura

```
app/src/main/java/com/sarayasecas/imc_saray/
├── MainActivity.kt         # App(): estado, navegación formulario ↔ resultado, TopAppBar
├── PantallaFormulario.kt   # Campos validados y botón Calcular IMC
├── PantallaResultado.kt    # Nombre, IMC, barra de nivel, ilustración, tabla de rangos y Volver
├── ui/theme/
│   ├── Color.kt / Theme.kt / Type.kt   # Colores, formas y letra japoneses
│   └── Decoracion.kt       # Franja de olas seigaiha
└── modelo/
    ├── Imc.kt              # Sexo, RangoImc, Persona y las listas inmutables
    └── Validacion.kt       # Funciones de validación de cada campo
app/src/main/res/drawable/
└── hombre_*.xml, mujer_*.xml   # 16 ilustraciones vectoriales (samurái y geisha × 8 rangos)
```

### Listas inmutables

- `RANGOS_IMC: List<RangoImc>` se crea con `listOf(...)`. Cada rango guarda su nombre, sus límites, su color y la ilustración de hombre y de mujer.
- `OPCIONES_SEXO: List<Sexo>` contiene las opciones que se muestran en el formulario.

Para saber el rango de una persona se busca en la lista con `RANGOS_IMC.first { it.contiene(imc) }`.

La barra de colores también sale de esta lista: cada rango es un tramo del mismo ancho y `posicionEnBarra(imc)` calcula dónde va el marcador dentro de su tramo.

### Cambiar las ilustraciones

Las imágenes son *vector drawables* en `res/drawable`, dibujadas con contorno de tinta, sombras y pliegues. Se generan con el script [`herramientas/generar_ilustraciones.py`](herramientas/generar_ilustraciones.py): cambiando sus colores o medidas y ejecutando `python herramientas/generar_ilustraciones.py app/src/main/res/drawable vista_previa.html` se vuelven a crear las 16 imágenes y una página para verlas todas. Para usar otras basta con poner un archivo con el **mismo nombre**, por ejemplo `mujer_obesidad_2.png` en lugar de `mujer_obesidad_2.xml`, y borrar el antiguo.

## Cómo ejecutarla

Abre la carpeta en Android Studio y pulsa ▶ Run. También se puede compilar desde la terminal:

```bash
./gradlew assembleDebug
```

Requisitos: minSdk 26 (Android 8.0) y targetSdk 37.
