const fs = require('fs');
const zlib = require('zlib');

const W = 256;
const H = 256;
const CELL = 16;
const px = new Uint8Array(W * H * 4);

const materials = [
  ['INDUSTRIAL', 0x626c72],
  ['DARK_METAL', 0x252c31],
  ['TITANIUM', 0xaeb6bc],
  ['CHROME', 0xc3cace],
  ['GUNMETAL', 0x39434a],
  ['CARBON', 0x19191d],
  ['RUBBER', 0x25282a],
  ['GOLD', 0xd4a72c],
  ['BRONZE', 0xa9743a],
  ['COPPER', 0xb96b3f],
  ['BRASS', 0xb8912f],
  ['RUST', 0x6f3c1c],
  ['CONCRETE', 0x7f7c74],
  ['SMOKE', 0x60656a],
  ['OLIVE', 0x5a6038],
  ['MUSTARD', 0xb99722],
  ['YELLOW', 0xd9b51e],
  ['BLOOD_RED', 0x7e1418],
  ['NEON_RED', 0xe01f1f],
  ['NEON_ORANGE', 0xff7a18],
  ['NEON_YELLOW', 0xf7d417],
  ['ACID', 0x9ee81a],
  ['EMERALD', 0x16a85c],
  ['CIRCUIT', 0x22d45a],
  ['CYAN', 0x18d7e8],
  ['ELECTRIC_BLUE', 0x2f7bff],
  ['BLUE_STEEL', 0x3f6c8c],
  ['DARK_BLUE', 0x232f4a],
  ['NAVY', 0x182341],
  ['PURPLE', 0x9c2fe0],
  ['DARK_PURPLE', 0x2f1a45],
  ['VIOLET', 0x5a45d6],
  ['PINK', 0xe0608d],
  ['MEDICAL', 0xd9e0de],
  ['CERAMIC', 0xe8e6d8],
  ['BONE', 0xc9bd97],
  ['AMBER', 0xc98216],
  ['TOXIC', 0x74d414],
  ['GLASS', 0x9fe8f5]
];

function set(x, y, r, g, b, a = 255) {
  if (x < 0 || y < 0 || x >= W || y >= H) return;
  const i = (y * W + x) * 4;
  px[i] = r;
  px[i + 1] = g;
  px[i + 2] = b;
  px[i + 3] = a;
}

function clamp(v) {
  return Math.max(0, Math.min(255, Math.round(v)));
}

function mix(color, target, amount) {
  const r = (color >> 16) & 255;
  const g = (color >> 8) & 255;
  const b = color & 255;
  return [
    clamp(r + (target - r) * amount),
    clamp(g + (target - g) * amount),
    clamp(b + (target - b) * amount)
  ];
}

function jitter(value, amount, seed) {
  const n = Math.sin(seed * 12.9898) * 43758.5453;
  return clamp(value + (n - Math.floor(n) - 0.5) * 2 * amount);
}

function toneColor(base, tone) {
  if (tone === 0) return mix(base, 255, 0.32);
  if (tone === 2) return mix(base, 0, 0.45);
  return mix(base, 255, 0.02);
}

function drawSwatch(name, base, tone, index) {
  const ox = (index % 16) * CELL;
  const oy = Math.floor(index / 16) * CELL;
  const [r0, g0, b0] = toneColor(base, tone);
  const alpha = name === 'GLASS' ? (tone === 0 ? 160 : tone === 1 ? 145 : 125) : 255;
  for (let y = 0; y < CELL; y++) {
    for (let x = 0; x < CELL; x++) {
      let r = jitter(r0, 5, index * 71 + x * 17 + y * 31);
      let g = jitter(g0, 5, index * 37 + x * 53 + y * 11);
      let b = jitter(b0, 5, index * 91 + x * 29 + y * 7);
      let a = alpha;

      if (['CHROME', 'TITANIUM', 'BLUE_STEEL', 'GOLD', 'BRONZE', 'COPPER', 'BRASS', 'AMBER'].includes(name)) {
        if (x % 4 === 0) { r += 12; g += 12; b += 12; }
        if (y % 5 === 0) { r -= 8; g -= 8; b -= 8; }
      }
      if (['INDUSTRIAL', 'GUNMETAL', 'DARK_METAL', 'SMOKE'].includes(name)) {
        if (y % 4 === 0) { r += 9; g += 9; b += 9; }
        if (x % 7 === 0) { r -= 5; g -= 5; b -= 5; }
      }
      if (name === 'CARBON') {
        if (((x >> 1) + (y >> 1)) % 2 === 0) { r += 10; g += 10; b += 12; }
      }
      if (name === 'CIRCUIT') {
        const trace = (y === 3 || y === 8 || y === 12) && x > 1 && x < 14;
        const pad = (x === 2 || x === 13) && y >= 2 && y <= 13;
        if (trace || pad) { r = 130; g = 255; b = 160; }
        else if (tone === 1) { r = 18; g = 120; b = 50; }
        if ((x === 5 || x === 10) && y > 4 && y < 11) { r = 30; g = 210; b = 80; }
      }
      if (['NEON_RED', 'NEON_ORANGE', 'NEON_YELLOW', 'CYAN', 'ELECTRIC_BLUE', 'PURPLE', 'VIOLET', 'TOXIC', 'ACID', 'EMERALD'].includes(name)) {
        if ((x * 5 + y * 3) % 37 === 0) { r = 255; g = 255; b = 235; }
      }
      if (['ACID', 'TOXIC', 'EMERALD'].includes(name) && (x + y) % 5 === 0) {
        r = clamp(r * 0.8); g = clamp(g * 0.85); b = clamp(b * 0.8);
      }
      if (name === 'GLASS') {
        if (x === y || x === y + 1) { r = 240; g = 255; b = 255; }
        a = x === 0 || y === 0 || x === 15 || y === 15 ? 215 : alpha;
      }
      if (['RUST', 'CONCRETE', 'BONE'].includes(name) && (x * 3 + y * 7) % 19 === 0) {
        r -= 18; g -= 18; b -= 18;
      }

      if (x === 0 || y === 0 || x === 15 || y === 15) {
        r = clamp(r * 0.62);
        g = clamp(g * 0.62);
        b = clamp(b * 0.62);
        if (name === 'GLASS') a = 220;
      }
      set(ox + x, oy + y, clamp(r), clamp(g), clamp(b), a);
    }
  }
}

let index = 0;
for (const [name, base] of materials) {
  for (let tone = 0; tone < 3; tone++) {
    drawSwatch(name, base, tone, index++);
  }
}

for (let y = 0; y < H; y++) {
  for (let x = 0; x < W; x++) {
    const i = (y * W + x) * 4;
    if (px[i + 3] === 0) {
      px[i] = 12; px[i + 1] = 14; px[i + 2] = 18; px[i + 3] = 255;
    }
  }
}

const raw = Buffer.alloc((W * 4 + 1) * H);
for (let y = 0; y < H; y++) {
  raw[y * (W * 4 + 1)] = 0;
  Buffer.from(px.buffer, y * W * 4, W * 4).copy(raw, y * (W * 4 + 1) + 1);
}

let table = null;
function crc32(buf) {
  if (!table) {
    table = new Uint32Array(256);
    for (let n = 0; n < 256; n++) {
      let c = n;
      for (let k = 0; k < 8; k++) c = c & 1 ? 0xedb88320 ^ (c >>> 1) : c >>> 1;
      table[n] = c >>> 0;
    }
  }
  let c = 0xffffffff;
  for (const b of buf) c = table[(c ^ b) & 0xff] ^ (c >>> 8);
  return (c ^ 0xffffffff) >>> 0;
}

function chunk(type, data) {
  const len = Buffer.alloc(4);
  len.writeUInt32BE(data.length);
  const body = Buffer.concat([Buffer.from(type, 'ascii'), data]);
  const crc = Buffer.alloc(4);
  crc.writeUInt32BE(crc32(body) >>> 0);
  return Buffer.concat([len, body, crc]);
}

const ihdr = Buffer.alloc(13);
ihdr.writeUInt32BE(W, 0);
ihdr.writeUInt32BE(H, 4);
ihdr[8] = 8;
ihdr[9] = 6;
const png = Buffer.concat([
  Buffer.from([0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]),
  chunk('IHDR', ihdr),
  chunk('IDAT', zlib.deflateSync(raw, { level: 9 })),
  chunk('IEND', Buffer.alloc(0))
]);

const out = process.argv[2];
if (!out) throw new Error('usage: node gen-cyberware-palette.js <output.png>');
fs.writeFileSync(out, png);
console.log('wrote', out, png.length, 'bytes', index, 'swatches');
