const fs = require('fs');
const zlib = require('zlib');

const W = 32, H = 32;
const px = new Uint8Array(W * H * 4);
function set(x, y, r, g, b, a = 255) {
  if (x < 0 || y < 0 || x >= W || y >= H) return;
  const i = (y * W + x) * 4;
  px[i] = r; px[i + 1] = g; px[i + 2] = b; px[i + 3] = a;
}
function clamp(v) { return Math.max(0, Math.min(255, Math.round(v))); }
function jitter(base, amount, seed) {
  const n = Math.sin(seed * 12.9898) * 43758.5453;
  return clamp(base + (n - Math.floor(n) - 0.5) * 2 * amount);
}

// METAL: white ceramic shell, brushed warm
for (let y = 0; y < 16; y++) {
  for (let x = 0; x < 16; x++) {
    const streak = ((x * 7 + y * 3) % 5 === 0) ? 6 : 0;
    const v = jitter(232 + streak, 3, x * 31 + y * 17);
    set(x, y, clamp(v), clamp(v * 0.99), clamp(v * 0.93));
  }
}
for (let x = 0; x < 16; x++) { set(x, 0, 138, 136, 124); set(x, 15, 138, 136, 124); }
for (let y = 0; y < 16; y++) { set(0, y, 138, 136, 124); set(15, y, 138, 136, 124); }
for (const [rx, ry] of [[3, 3], [12, 3], [3, 12], [12, 12]]) {
  set(rx, ry, 96, 100, 102); set(rx + 1, ry, 126, 130, 130);
  set(rx, ry + 1, 126, 130, 130); set(rx + 1, ry + 1, 168, 170, 164);
}

// STEEL: bright silver, fine horizontal striations
for (let y = 0; y < 16; y++) {
  for (let x = 0; x < 16; x++) {
    const band = (y % 4 === 0) ? 16 : (y % 4 === 2 ? -8 : 0);
    const v = jitter(180 + band, 5, x * 13 + y * 29);
    set(16 + x, y, clamp(v * 0.99), clamp(v), clamp(v * 1.03));
  }
}
for (let x = 0; x < 16; x++) { set(16 + x, 0, 112, 118, 124); set(16 + x, 15, 112, 118, 124); }
for (let y = 0; y < 16; y++) { set(16, y, 112, 118, 124); set(31, y, 112, 118, 124); }
for (let x = 2; x < 14; x += 3) set(16 + x, 4, 238, 242, 246);
for (let x = 3; x < 15; x += 4) set(16 + x, 11, 104, 110, 118);

// RED: neon orange plates
for (let y = 0; y < 8; y++) {
  for (let x = 0; x < 16; x++) {
    const v = jitter(238, 6, x * 53 + y * 11);
    set(x, 16 + y, clamp(v), clamp(v * 0.46), clamp(v * 0.08));
  }
}
for (let x = 0; x < 16; x++) { set(x, 16, 255, 158, 74); set(x, 23, 120, 48, 8); }
for (let y = 0; y < 8; y++) { set(0, 16 + y, 120, 48, 8); set(15, 16 + y, 120, 48, 8); }
for (let x = 0; x < 16; x += 2) set(x, 19, 255, 140, 50);
for (let x = 1; x < 16; x += 4) set(x, 21, 160, 64, 12);

// DARK: gunmetal detail
for (let y = 0; y < 8; y++) {
  for (let x = 0; x < 16; x++) {
    const v = jitter(48, 3, x * 41 + y * 23);
    set(16 + x, 16 + y, clamp(v * 0.9), clamp(v), clamp(v * 1.12));
  }
}
for (let x = 0; x < 16; x++) { set(16 + x, 16, 72, 80, 88); set(31 - x, 23, 22, 26, 30); }
for (let y = 0; y < 8; y++) { set(16, 16 + y, 72, 80, 88); set(31, 16 + y, 22, 26, 30); }

// encode PNG
const raw = Buffer.alloc((W * 4 + 1) * H);
for (let y = 0; y < H; y++) {
  raw[y * (W * 4 + 1)] = 0;
  Buffer.from(px.buffer, y * W * 4, W * 4).copy(raw, y * (W * 4 + 1) + 1);
}
function chunk(type, data) {
  const len = Buffer.alloc(4); len.writeUInt32BE(data.length);
  const body = Buffer.concat([Buffer.from(type, 'ascii'), data]);
  const crc = Buffer.alloc(4); crc.writeUInt32BE(crc32(body) >>> 0);
  return Buffer.concat([len, body, crc]);
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
const ihdr = Buffer.alloc(13);
ihdr.writeUInt32BE(W, 0); ihdr.writeUInt32BE(H, 4);
ihdr[8] = 8; ihdr[9] = 6; ihdr[10] = 0; ihdr[11] = 0; ihdr[12] = 0;
const png = Buffer.concat([
  Buffer.from([0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]),
  chunk('IHDR', ihdr),
  chunk('IDAT', zlib.deflateSync(raw, { level: 9 })),
  chunk('IEND', Buffer.alloc(0)),
]);
const out = process.argv[2];
fs.writeFileSync(out, png);
console.log('wrote', out, png.length, 'bytes');
