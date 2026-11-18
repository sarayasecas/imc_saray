"""Ilustraciones del IMC en estilo dibujo: contorno de tinta, sombras, pliegues y caras detalladas.
Samurái (hombre) y geisha (mujer) x 8 rangos OMS."""
import math, os, re, sys

out = sys.argv[1]
preview = sys.argv[2]
os.makedirs(out, exist_ok=True)

TINTA = "#2B2118"
SOMBRA = "#2E000000"      # negro semitransparente para sombras
SOMBRA_SUAVE = "#1A000000"
LUZ = "#33FFFFFF"

PIEL = "#F3C99A"
PIEL_OSC = "#D9A06A"
PIEL_G = "#FBF5EE"
PIEL_G_OSC = "#E6D6C6"
PELO = "#22232E"
PELO_BRILLO = "#5A5D78"
BLANCO = "#F6F2EA"
ORO = "#E2AE3A"
ORO_OSC = "#B9862A"
COLORETE = "#55E57373"

# Samurái
KIMONO_S = "#2F4A73"
KIMONO_S_OSC = "#22375A"
HAKAMA = "#7C7F8C"
HAKAMA_OSC = "#5C5F6B"
OBI_S = "#C9A66B"
TSUKA = "#EDE5D3"

# Geisha (como la referencia: kimono azul grisáceo, falda crema y obi dorado)
KIMONO_G = "#7E9CB8"
KIMONO_G_OSC = "#5F7E9C"
FALDA = "#F1EBDD"
FALDA_OSC = "#D9D0BD"
ERI = "#C8414B"
OBIJIME = "#C8414B"
LABIOS = "#C8303A"
FLOR = "#D23B3B"

SONRISA, RECTA, TRISTE = "sonrisa", "recta", "triste"
RANGOS = {
    "delgadez_severa":   dict(w=14, aw=7, lw=6, r=21, barriga=None, fondo="1B3A6B", boca=TRISTE, bx=6, mofletes=False, papada=False),
    "delgadez_moderada": dict(w=19, aw=8, lw=8, r=22, barriga=None, fondo="2E5C8A", boca=RECTA, bx=6, mofletes=False, papada=False),
    "delgadez_leve":     dict(w=26, aw=10, lw=11, r=23, barriga=None, fondo="5B8DB8", boca=SONRISA, bx=7, mofletes=False, papada=False),
    "normal":            dict(w=33, aw=12, lw=14, r=25, barriga=None, fondo="6A8D3A", boca=SONRISA, bx=8, mofletes=False, papada=False),
    "sobrepeso":         dict(w=46, aw=16, lw=20, r=28, barriga=(8, 42), fondo="E08A1E", boca=SONRISA, bx=8, mofletes=True, papada=False),
    "obesidad_1":        dict(w=58, aw=19, lw=25, r=31, barriga=(16, 52), fondo="D9472B", boca=SONRISA, bx=7, mofletes=True, papada=True),
    "obesidad_2":        dict(w=70, aw=23, lw=30, r=34, barriga=(24, 58), fondo="B22222", boca=SONRISA, bx=7, mofletes=True, papada=True),
    "obesidad_3":        dict(w=82, aw=27, lw=36, r=37, barriga=(32, 64), fondo="7B1E2B", boca=RECTA, bx=6, mofletes=True, papada=True),
}


# --------------------------------------------------------------------------- formas
def n(v):
    return f"{v:.2f}".rstrip("0").rstrip(".")


def circle(cx, cy, r):
    return f"M{n(cx - r)},{n(cy)} a{n(r)},{n(r)} 0 1,0 {n(2*r)},0 a{n(r)},{n(r)} 0 1,0 {n(-2*r)},0 Z"


def ellipse(cx, cy, rx, ry):
    return f"M{n(cx - rx)},{n(cy)} a{n(rx)},{n(ry)} 0 1,0 {n(2*rx)},0 a{n(rx)},{n(ry)} 0 1,0 {n(-2*rx)},0 Z"


def rrect(x1, y1, x2, y2, r):
    if x1 > x2:
        x1, x2 = x2, x1
    r = min(r, (x2 - x1) / 2, (y2 - y1) / 2)
    return (f"M{n(x1+r)},{n(y1)} L{n(x2-r)},{n(y1)} Q{n(x2)},{n(y1)} {n(x2)},{n(y1+r)} L{n(x2)},{n(y2-r)} "
            f"Q{n(x2)},{n(y2)} {n(x2-r)},{n(y2)} L{n(x1+r)},{n(y2)} Q{n(x1)},{n(y2)} {n(x1)},{n(y2-r)} "
            f"L{n(x1)},{n(y1+r)} Q{n(x1)},{n(y1)} {n(x1+r)},{n(y1)} Z")


def poly(*pts):
    return "M" + " L".join(f"{n(x)},{n(y)}" for x, y in pts) + " Z"


def fill(d, color):
    return f'    <path android:fillColor="{color}" android:pathData="{d}" />\n'


def fs(d, color, w=1.6):
    """Relleno con contorno de tinta."""
    return (f'    <path android:fillColor="{color}" android:strokeColor="{TINTA}" android:strokeWidth="{n(w)}" '
            f'android:strokeLineJoin="round" android:pathData="{d}" />\n')


def stroke(d, color=TINTA, width=1.4):
    return (f'    <path android:strokeColor="{color}" android:strokeWidth="{n(width)}" android:strokeLineCap="round" '
            f'android:strokeLineJoin="round" android:pathData="{d}" />\n')


def anchura_total(p):
    return p["w"] + (p["barriga"][0] if p["barriga"] else 0)


# --------------------------------------------------------------------------- fondo
def fondo(p):
    c = p["fondo"]
    # mancha de pincel grande y otra pequeña, como en una acuarela
    s = fill("M18,158 C14,100 64,70 108,76 C162,83 194,122 184,170 C176,218 138,252 92,248 "
             "C44,244 22,206 18,158 Z", "#40" + c)
    s += fill("M120,52 C150,40 182,62 176,94 C170,118 138,118 124,104 C110,90 102,60 120,52 Z", "#26" + c)
    s += stroke("M30,214 C60,236 120,242 168,214", "#55" + c, 3)
    return s


def sombra_suelo(ancho):
    return fill(ellipse(100, 280, ancho, 6), SOMBRA_SUAVE)


# --------------------------------------------------------------------------- cara
def ojo(cx, cy, geisha):
    s = stroke(f"M{n(cx - 4.6)},{n(cy)} Q{n(cx)},{n(cy - 3.8)} {n(cx + 4.6)},{n(cy)}", TINTA, 1.8)
    s += fill(circle(cx, cy + 0.4, 2.1), TINTA)
    s += fill(circle(cx + 0.7, cy - 0.3, 0.7), "#FFFFFF")
    if geisha:  # rabillo rojo, como el maquillaje de la geisha
        lado = 1 if cx > 100 else -1
        s += stroke(f"M{n(cx + lado*4.6)},{n(cy)} L{n(cx + lado*7)},{n(cy - 1.8)}", LABIOS, 1.2)
    return s


def boca(p, cy, geisha):
    y, bx = cy + 14, p["bx"]
    color = LABIOS if geisha else "#6B3A2A"
    if p["boca"] == SONRISA:
        d = f"M{n(100 - bx)},{n(y)} Q100,{n(y + 6)} {n(100 + bx)},{n(y)}"
    elif p["boca"] == TRISTE:
        d = f"M{n(100 - bx)},{n(y + 4)} Q100,{n(y - 2)} {n(100 + bx)},{n(y + 4)}"
    else:
        d = f"M{n(100 - bx)},{n(y + 1)} L{n(100 + bx)},{n(y + 1)}"
    s = stroke(d, color, 2.6 if geisha else 2)
    if geisha and p["boca"] != TRISTE:  # labio inferior pintado
        s += fill(ellipse(100, y + (3 if p["boca"] == SONRISA else 2.2), bx * 0.35, 1.6), LABIOS)
    return s


def cara(p, piel_osc, geisha, cy=60):
    r = p["r"]
    ex = r * 0.4
    s = ojo(100 - ex, cy, geisha) + ojo(100 + ex, cy, geisha)
    # nariz
    s += stroke(f"M{n(100.5)},{n(cy + 4)} Q{n(98)},{n(cy + 8)} {n(101)},{n(cy + 9)}", piel_osc, 1.3)
    if p["mofletes"]:
        s += fill(ellipse(100 - r * 0.62, cy + 9, 6, 4), COLORETE) + fill(ellipse(100 + r * 0.62, cy + 9, 6, 4), COLORETE)
    elif geisha:
        s += fill(ellipse(100 - r * 0.6, cy + 8, 4.5, 3), "#33E57373") + fill(ellipse(100 + r * 0.6, cy + 8, 4.5, 3), "#33E57373")
    return s + boca(p, cy, geisha)


def cabeza(p, piel, piel_osc, cy=60):
    r = p["r"]
    s = ""
    if p["papada"]:
        s += fs(ellipse(100, cy + r - 2, r * 0.7, 9), piel_osc, 1.4)
    s += fs(circle(100, cy, r), piel)
    # sombra en el lado derecho de la cara
    s += fill(f"M{n(100 + r*0.55)},{n(cy - r*0.8)} Q{n(100 + r*1.02)},{n(cy)} {n(100 + r*0.45)},{n(cy + r*0.88)} "
              f"Q{n(100 + r*0.82)},{n(cy)} {n(100 + r*0.55)},{n(cy - r*0.8)} Z", SOMBRA_SUAVE)
    return s


# --------------------------------------------------------------------------- samurái
def samurai(p):
    w, aw, lw, r = p["w"], p["aw"], p["lw"], p["r"]
    a = anchura_total(p)
    t = max(w, a - 6)
    hb = max(t + 8, 2 * lw + 22)
    s = fondo(p) + sombra_suelo(hb + 6)

    # pies: zori y tabi
    for lado in (-1, 1):
        cx = 100 + lado * (lw + 4)
        s += fs(rrect(cx - lw - 3, 271, cx + lw + 3, 280, 3), "#7A4E35", 1.3)
        s += stroke(f"M{n(cx)},{n(266)} L{n(cx)},{n(272)}", "#B23A3A", 2)
        s += fs(rrect(cx - lw + 1, 260, cx + lw - 1, 274, 6), BLANCO, 1.3)

    # hakama con pliegues y sombra
    s += fs(poly((100 - t, 176), (100 + t, 176), (100 + hb, 266), (100 - hb, 266)), HAKAMA)
    s += fill(poly((100 + t * 0.3, 178), (100 + t, 177), (100 + hb, 265), (100 + hb * 0.35, 265)), SOMBRA)
    s += stroke("M100,210 L100,265", TINTA, 1.6)
    for k in (-0.7, -0.4, 0.4, 0.7):
        s += stroke(f"M{n(100 + k*t)},182 Q{n(100 + k*(t+hb)/2 + k*4)},224 {n(100 + k*hb)},264", HAKAMA_OSC, 1.3)

    # manos
    for lado in (-1, 1):
        s += fs(ellipse(100 + lado * (a + aw * 0.45), 168, max(5, aw * 0.36), max(6, aw * 0.42)), PIEL, 1.3)

    # mangas anchas con forro oscuro y pliegues
    for lado in (-1, 1):
        x_in, x_out = 100 + lado * (a - 8), 100 + lado * (a + aw + 4)
        s += fs(rrect(x_in, 96, x_out, 164, 8), KIMONO_S)
        s += fill(rrect(x_in, 150, x_out, 164, 6), KIMONO_S_OSC)
        mid = (x_in + x_out) / 2
        s += stroke(f"M{n(mid)},104 Q{n(mid + lado*3)},128 {n(mid - lado*1)},148", KIMONO_S_OSC, 1.3)

    # cuerpo del kimono
    s += fs(rrect(100 - t, 92, 100 + t, 184, 12), KIMONO_S)
    ow = t
    if p["barriga"]:
        ex, ry = p["barriga"]
        s += fs(ellipse(100, 156, w + ex, ry), KIMONO_S)
        s += fill(f"M{n(100 + (w+ex)*0.2)},{n(156 - ry*0.98)} Q{n(100 + (w+ex)*1.05)},156 {n(100 + (w+ex)*0.2)},{n(156 + ry*0.98)} "
                  f"Q{n(100 + (w+ex)*0.8)},156 {n(100 + (w+ex)*0.2)},{n(156 - ry*0.98)} Z", SOMBRA)
        s += fill(ellipse(100 - (w + ex) * 0.45, 140, (w + ex) * 0.18, ry * 0.25), LUZ)
        ow = max(t, (w + ex) * 0.88)
    else:
        s += fill(rrect(100 + t * 0.35, 93, 100 + t - 1, 183, 10), SOMBRA)
    # pliegues del pecho
    s += stroke(f"M{n(100 - t*0.5)},112 Q{n(100 - t*0.35)},140 {n(100 - t*0.55)},164", KIMONO_S_OSC, 1.2)

    # cuello cruzado en V (izquierda sobre derecha)
    s += fs(poly((86, 92), (114, 92), (100, 128)), BLANCO, 1.3)
    s += fs(poly((91, 92), (109, 92), (100, 116)), PIEL, 1.1)
    s += stroke("M114,92 L97,132", TINTA, 1.4)

    # obi y katana
    s += fs(rrect(100 - ow, 168, 100 + ow, 180, 3), OBI_S, 1.4)
    s += stroke(f"M{n(100 - ow + 3)},174 L{n(100 + ow - 3)},174", "#A88850", 1)
    x0 = 100 + ow * 0.35
    s += stroke(f"M{n(x0)},173 L{n(100 - ow - 22)},189", TINTA, 6.5)
    s += stroke(f"M{n(x0)},173 L{n(100 - ow - 22)},189", "#3A2A20", 4.5)
    s += stroke(f"M{n(x0 + 4)},171 L{n(x0 + 25)},164.5", TINTA, 6)
    s += stroke(f"M{n(x0 + 4)},171 L{n(x0 + 25)},164.5", TSUKA, 4)
    for k in range(3):
        xx = x0 + 9 + k * 6
        s += stroke(f"M{n(xx)},{n(171 - k*1.9 - 2)} L{n(xx + 2)},{n(171 - k*1.9 + 1)}", "#3A2A20", 1.2)
    s += fs(ellipse(x0 + 2, 172, 3.2, 5.2), ORO, 1.2)

    # cuello, orejas y cabeza
    s += fs(rrect(92 - lw * 0.2, 78, 108 + lw * 0.2, 96, 2), PIEL, 1.3)
    s += fill(rrect(92 - lw * 0.2, 86, 108 + lw * 0.2, 95, 2), SOMBRA_SUAVE)
    for lado in (-1, 1):
        s += fs(ellipse(100 + lado * r, 62, 4.5, 6.5), PIEL, 1.3)
    s += cabeza(p, PIEL, PIEL_OSC)

    # pelo con moño (chonmage) y brillos
    top = 60 - r
    s += fs(f"M{n(100 - r)},62 Q{n(100 - r)},{n(top - 2)} 100,{n(top - 2)} Q{n(100 + r)},{n(top - 2)} {n(100 + r)},62 "
            f"Q{n(100 + r*0.85)},{n(60 - r*0.4)} {n(100 + r*0.55)},{n(60 - r*0.5)} L{n(100 - r*0.55)},{n(60 - r*0.5)} "
            f"Q{n(100 - r*0.85)},{n(60 - r*0.4)} {n(100 - r)},62 Z", PELO)
    s += stroke(f"M{n(100 - r*0.6)},{n(top + 5)} Q100,{n(top + 1)} {n(100 + r*0.5)},{n(top + 5)}", PELO_BRILLO, 1.5)
    s += fs(rrect(96, top - 11, 104, top + 1, 3), PELO, 1.3)
    s += fs(rrect(95, top - 15, 118, top - 8, 3.5), PELO, 1.3)
    s += stroke(f"M{n(99)},{n(top - 12)} L{n(114)},{n(top - 12)}", PELO_BRILLO, 1.2)

    # cejas serias
    ex = r * 0.4
    s += stroke(f"M{n(100 - ex - 7)},49 L{n(100 - ex + 5)},53", TINTA, 2.8)
    s += stroke(f"M{n(100 + ex + 7)},49 L{n(100 + ex - 5)},53", TINTA, 2.8)
    return s + cara(p, PIEL_OSC, geisha=False)


# --------------------------------------------------------------------------- geisha
def geisha(p):
    w, aw, lw, r = p["w"], p["aw"], p["lw"], p["r"]
    a = anchura_total(p)
    top = max(w * 0.9, a - 6)
    hem = a + 16
    s = fondo(p) + sombra_suelo(hem + 4)

    # geta y tabi (asoman bajo la falda)
    for lado in (-1, 1):
        cx = 100 + lado * (lw * 0.8 + 3)
        s += fs(rrect(cx - lw * 0.8 - 2, 273, cx + lw * 0.8 + 2, 280, 2), "#C8414B", 1.2)
        s += fs(rrect(cx - lw * 0.8, 264, cx + lw * 0.8, 275, 5), BLANCO, 1.2)

    # falda crema larga con vuelo, pliegues y flores
    s += fs(f"M{n(100 - top)},150 L{n(100 + top)},150 Q{n(100 + hem*0.95)},220 {n(100 + hem + 6)},268 "
            f"Q100,274 {n(100 - hem - 6)},268 Q{n(100 - hem*0.95)},220 {n(100 - top)},150 Z", FALDA)
    s += fill(f"M{n(100 + top*0.2)},152 L{n(100 + top)},151 Q{n(100 + hem*0.95)},220 {n(100 + hem + 5)},267 "
              f"Q{n(100 + hem*0.6)},270 {n(100 + hem*0.25)},271 Z", SOMBRA_SUAVE)
    for k in (-0.65, -0.3, 0.05, 0.4, 0.75):
        s += stroke(f"M{n(100 + k*top)},170 Q{n(100 + k*(top+hem)/2 - 3)},220 {n(100 + k*hem)},266", FALDA_OSC, 1.1)
    s += stroke(f"M{n(100 + top*0.15)},150 Q{n(100 + top*0.05)},210 {n(100 - hem*0.1)},270", TINTA, 1.2)
    for fx, fy, sz in ((-0.45, 236, 4.2), (-0.25, 250, 3.2), (-0.6, 254, 2.8)):
        cx = 100 + fx * hem
        for i in range(5):
            ang = math.radians(90 + i * 72)
            px, py = cx + sz * 0.9 * math.cos(ang), fy - sz * 0.9 * math.sin(ang)
            s += fill(ellipse(px, py, sz * 0.55, sz * 0.55), FLOR)
        s += fill(circle(cx, fy, sz * 0.3), ORO)
    s += stroke(f"M{n(100 - hem*0.3)},228 L{n(100 - hem*0.26)},222", "#7E9CB8", 1.3)
    s += stroke(f"M{n(100 - hem*0.7)},244 L{n(100 - hem*0.66)},239", "#7E9CB8", 1.3)

    # mangas largas (furisode) con forro y pliegues
    for lado in (-1, 1):
        x_in, x_out = 100 + lado * (a - 10), 100 + lado * (a + aw + 8)
        xa, xb = min(x_in, x_out), max(x_in, x_out)
        s += fs(f"M{n(xa + 6)},98 L{n(xb - 6)},98 Q{n(xb)},98 {n(xb)},106 L{n(xb + lado*2)},196 "
                f"Q{n((xa+xb)/2)},{n(214)} {n(xa - lado*2)},200 L{n(xa)},106 Q{n(xa)},98 {n(xa + 6)},98 Z", KIMONO_G)
        s += fill(f"M{n(xa + 2)},186 Q{n((xa+xb)/2)},{n(196)} {n(xb - 2)},184 L{n(xb + lado*2)},196 "
                  f"Q{n((xa+xb)/2)},{n(214)} {n(xa - lado*2)},200 Z", KIMONO_G_OSC)
        mid = (xa + xb) / 2
        s += stroke(f"M{n(mid + lado*2)},108 Q{n(mid - lado*3)},150 {n(mid + lado*1)},190", KIMONO_G_OSC, 1.2)

    # parte de arriba del kimono (cuerpo)
    s += fs(f"M{n(100 - top - 4)},106 Q{n(100 - top - 4)},90 {n(100 - top + 8)},90 L{n(100 + top - 8)},90 "
            f"Q{n(100 + top + 4)},90 {n(100 + top + 4)},106 L{n(100 + top + 2)},160 L{n(100 - top - 2)},160 Z", KIMONO_G)
    ow = top
    if p["barriga"]:
        ex, ry = p["barriga"]
        s += fs(ellipse(100, 156, w + ex, ry * 0.85), KIMONO_G)
        s += fill(f"M{n(100 + (w+ex)*0.2)},{n(156 - ry*0.83)} Q{n(100 + (w+ex)*1.05)},156 {n(100 + (w+ex)*0.2)},{n(156 + ry*0.83)} "
                  f"Q{n(100 + (w+ex)*0.8)},156 {n(100 + (w+ex)*0.2)},{n(156 - ry*0.83)} Z", SOMBRA)
        ow = max(top, (w + ex) * 0.95)
    else:
        s += fill(f"M{n(100 + top*0.35)},92 L{n(100 + top - 4)},92 L{n(100 + top + 1)},158 L{n(100 + top*0.35)},158 Z", SOMBRA)

    # cuello en V: blanco, rojo y piel
    s += fs(poly((84, 91), (116, 91), (100, 132)), BLANCO, 1.3)
    s += fs(poly((89, 91), (111, 91), (100, 124)), ERI, 1.1)
    s += fs(poly((93, 91), (107, 91), (100, 114)), PIEL_G, 1)
    s += stroke("M116,91 L96,136", TINTA, 1.4)

    # obi ancho dorado con pliegues y cordón
    s += fs(rrect(100 - ow, 132, 100 + ow, 166, 4), ORO, 1.5)
    s += fill(rrect(100 + ow * 0.35, 133, 100 + ow - 1, 165, 3), "#22000000")
    s += stroke(f"M{n(100 - ow + 3)},140 Q100,143 {n(100 + ow - 3)},140", ORO_OSC, 1.1)
    s += stroke(f"M{n(100 - ow + 3)},158 Q100,155 {n(100 + ow - 3)},158", ORO_OSC, 1.1)
    s += stroke(f"M{n(100 - ow + 2)},149 L{n(100 + ow - 2)},149", OBIJIME, 2.4)

    # manos juntas sobre el obi, como en la referencia
    hr = min(7, max(3.5, ow * 0.32))
    s += fs(ellipse(100 - hr * 0.7, 150, hr, hr * 0.7), PIEL_G, 1.1)
    s += fs(ellipse(100 + hr * 0.7, 152, hr, hr * 0.7), PIEL_G, 1.1)

    # pelo de atrás, cuello y cabeza
    s += fs(ellipse(100, 54, r + 11, r * 0.86), PELO)
    s += fs(rrect(93 - lw * 0.2, 78, 107 + lw * 0.2, 94, 2), PIEL_G, 1.2)
    s += fill(rrect(93 - lw * 0.2, 86, 107 + lw * 0.2, 93, 2), SOMBRA_SUAVE)
    s += cabeza(p, PIEL_G, PIEL_G_OSC)

    # moño alto, flequillo y mechones
    s += fs(ellipse(100, 60 - r - 7, r * 0.8, r * 0.46), PELO)
    s += stroke(f"M{n(100 - r*0.55)},{n(60 - r - 9)} Q100,{n(60 - r - 17)} {n(100 + r*0.5)},{n(60 - r - 10)}", PELO_BRILLO, 1.5)
    s += fs(f"M{n(100 - r)},60 Q{n(100 - r)},{n(60 - r - 2)} 100,{n(60 - r - 2)} Q{n(100 + r)},{n(60 - r - 2)} {n(100 + r)},60 "
            f"Q{n(100 + r*0.7)},{n(60 - r*0.55)} 100,{n(60 - r*0.35)} Q{n(100 - r*0.7)},{n(60 - r*0.55)} {n(100 - r)},60 Z", PELO, 1.4)
    s += stroke(f"M{n(100 - r*0.75)},{n(60 - r*0.6)} Q{n(100 - r*0.4)},{n(60 - r*0.95)} {n(100 - r*0.05)},{n(60 - r*0.9)}", PELO_BRILLO, 1.3)
    for lado in (-1, 1):  # mechones a los lados de la cara
        s += stroke(f"M{n(100 + lado*r*0.95)},{n(62)} Q{n(100 + lado*r*1.05)},{n(76)} {n(100 + lado*r*0.85)},{n(84)}", PELO, 2.2)

    # kanzashi: peineta dorada y flor roja con colgantes
    s += fs(rrect(100 - r * 0.5, 60 - r - 5, 100 + r * 0.5, 60 - r + 1, 2), ORO, 1.2)
    fx, fy = 100 + r * 0.85, 60 - r * 0.78
    for i in range(5):
        ang = math.radians(90 + i * 72)
        s += fs(circle(fx + 4.2 * math.cos(ang), fy - 4.2 * math.sin(ang), 3.3), FLOR, 0.9)
    s += fill(circle(fx, fy, 2.1), ORO)
    for dx in (-2, 2):
        s += stroke(f"M{n(fx + dx)},{n(fy + 5)} L{n(fx + dx * 1.5)},{n(fy + 17)}", ORO_OSC, 1.1)
        s += fill(circle(fx + dx * 1.5, fy + 18, 1.7), FLOR)

    # cejas finas
    ex = r * 0.4
    s += stroke(f"M{n(100 - ex - 5)},50 Q{n(100 - ex)},47 {n(100 - ex + 5)},50", TINTA, 1.3)
    s += stroke(f"M{n(100 + ex - 5)},50 Q{n(100 + ex)},47 {n(100 + ex + 5)},50", TINTA, 1.3)
    return s + cara(p, PIEL_G_OSC, geisha=True)


# --------------------------------------------------------------------------- salida
def ancho_lienzo(p):
    return max(200, int(2 * (anchura_total(p) + p["aw"]) + 30))


def header(W):
    return ('<?xml version="1.0" encoding="utf-8"?>\n'
            '<!-- Ilustración del resultado del IMC (samurái / geisha + rango) -->\n'
            '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
            f'    android:width="{W}dp" android:height="290dp"\n'
            f'    android:viewportWidth="{W}" android:viewportHeight="290">\n')


def to_svg(body, W):
    def argb(c):  # #AARRGGBB -> #RRGGBBAA
        return "#" + c[3:] + c[1:3] if len(c) == 9 else c
    body = re.sub(r'#[0-9A-Fa-f]{8}\b', lambda m: argb(m.group(0)), body)
    body = (body.replace("android:fillColor", "fill").replace("android:pathData", "d")
                .replace("android:strokeColor", "stroke").replace("android:strokeWidth", "stroke-width")
                .replace("android:strokeLineCap", "stroke-linecap").replace("android:strokeLineJoin", "stroke-linejoin"))
    body = re.sub(r'<path (stroke=)', r'<path fill="none" \1', body)
    return f'<svg viewBox="0 0 {W} 290" height="290"><g transform="translate({(W-200)/2},0)">{body}</g></svg>'


html = ['<html><body style="font-family:sans-serif;background:#FFFBF2"><table><tr>']
for sexo, fn in (("hombre", samurai), ("mujer", geisha)):
    for nombre, p in RANGOS.items():
        body = fn(p)
        W = ancho_lienzo(p)
        xml = body if W == 200 else (f'    <group android:translateX="{(W - 200) / 2}">\n'
                                     + body.replace("    <path", "        <path") + "    </group>\n")
        with open(os.path.join(out, f"{sexo}_{nombre}.xml"), "w", encoding="utf-8") as f:
            f.write(header(W) + xml + "</vector>\n")
        html.append(f"<td align=center valign=bottom>{to_svg(body, W)}<br>{sexo}_{nombre}</td>")
    html.append("</tr><tr>")
html.append("</tr></table></body></html>")
with open(preview, "w", encoding="utf-8") as f:
    f.write("".join(html))
print("ok")
