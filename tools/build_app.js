// 잔광 프로토콜 → 설치 가능한 웹앱(PWA) 만들기
// 사용법: node tools/build_app.js   →  app/ 폴더에 index.html, manifest, sw.js, 아이콘이 생긴다
// 원본 게임은 afterglow_protocol.html 하나 그대로 유지하고, 앱용 파일은 여기서 자동으로 만든다.
const fs=require('fs'),path=require('path'),zlib=require('zlib');
const ROOT=path.join(__dirname,'..'),OUT=path.join(ROOT,'app');
fs.mkdirSync(OUT,{recursive:true});
const BUILD=new Date().toISOString().replace(/\D/g,'').slice(0,14);

// ---------- 아이콘 (도트 그림 → PNG)
const ICON=[ // 16x16: 어두운 배경 위 빛나는 잔광 구체와 꼬리
'................',
'................',
'..........oooo..',
'.........oyyyyo.',
'..c.....oywwwyyo',
'...cc..oywwwwwyo',
'.....cconwwwwwyo',
'..ccccccnywwwyyo',
'.....cconyyyyyo.',
'...cc..onnyyyo..',
'..c.....onnno...',
'.........ooo....',
'................',
'..a..a..a..a..a.',
'................',
'................'];
const PAL={o:[60,30,10],y:[255,179,71],w:[255,240,200],n:[230,120,40],c:[95,216,255],a:[95,216,255]};
const BG=[13,16,32];
function png(size){const S=Math.floor(size/16*0.8),off=Math.floor((size-S*16)/2),px=Buffer.alloc(size*size*4);
 for(let i=0;i<size*size;i++){px[i*4]=BG[0];px[i*4+1]=BG[1];px[i*4+2]=BG[2];px[i*4+3]=255}
 ICON.forEach((r,y)=>[...r].forEach((ch,x)=>{const c=PAL[ch];if(!c)return;for(let a=0;a<S;a++)for(let b=0;b<S;b++){const X=off+x*S+b,Y=off+y*S+a,i=(Y*size+X)*4;px[i]=c[0];px[i+1]=c[1];px[i+2]=c[2]}}));
 const raw=Buffer.alloc((size*4+1)*size);for(let y=0;y<size;y++){raw[y*(size*4+1)]=0;px.copy(raw,y*(size*4+1)+1,y*size*4,(y+1)*size*4)}
 const T=[];for(let n=0;n<256;n++){let c=n;for(let k=0;k<8;k++)c=c&1?0xedb88320^(c>>>1):c>>>1;T[n]=c>>>0}
 const crc=b=>{let c=0xffffffff;for(const x of b)c=T[(c^x)&255]^(c>>>8);return(c^0xffffffff)>>>0};
 const ch=(t,d)=>{const l=Buffer.alloc(4);l.writeUInt32BE(d.length);const td=Buffer.concat([Buffer.from(t),d]),c=Buffer.alloc(4);c.writeUInt32BE(crc(td));return Buffer.concat([l,td,c])};
 const ih=Buffer.alloc(13);ih.writeUInt32BE(size,0);ih.writeUInt32BE(size,4);ih[8]=8;ih[9]=6;
 return Buffer.concat([Buffer.from([137,80,78,71,13,10,26,10]),ch('IHDR',ih),ch('IDAT',zlib.deflateSync(raw)),ch('IEND',Buffer.alloc(0))])}
for(const s of [180,192,512])fs.writeFileSync(path.join(OUT,'icon-'+s+'.png'),png(s));

// ---------- 앱 정보(manifest)
fs.writeFileSync(path.join(OUT,'manifest.webmanifest'),JSON.stringify({
 name:'잔광 프로토콜',short_name:'잔광 프로토콜',description:'톱뷰 생존 로그라이크 체험판',lang:'ko',
 start_url:'./',scope:'./',display:'fullscreen',orientation:'any',background_color:'#0d1020',theme_color:'#0d1020',
 icons:[{src:'icon-192.png',sizes:'192x192',type:'image/png',purpose:'any maskable'},{src:'icon-512.png',sizes:'512x512',type:'image/png',purpose:'any maskable'}]},null,1));

// ---------- 오프라인 실행(서비스 워커): 인터넷이 되면 최신판, 안 되면 저장해 둔 판으로 실행
fs.writeFileSync(path.join(OUT,'sw.js'),`// 자동 생성 파일 (tools/build_app.js) — 빌드 ${BUILD}
const C='afterglow-${BUILD}',FILES=['./','./index.html','./manifest.webmanifest','./icon-192.png','./icon-512.png','./icon-180.png'];
self.addEventListener('install',e=>{e.waitUntil(caches.open(C).then(c=>c.addAll(FILES)));self.skipWaiting()});
self.addEventListener('activate',e=>{e.waitUntil(caches.keys().then(ks=>Promise.all(ks.filter(k=>k!=C).map(k=>caches.delete(k)))).then(()=>self.clients.claim()))});
self.addEventListener('fetch',e=>{if(e.request.method!='GET')return;e.respondWith(fetch(e.request).then(r=>{if(r&&r.ok){const cp=r.clone();caches.open(C).then(c=>c.put(e.request,cp))}return r}).catch(()=>caches.match(e.request,{ignoreSearch:true}).then(r=>r||caches.match('./index.html'))))});
`);

// ---------- index.html = 게임 원본 + 앱 설정 몇 줄
let h=fs.readFileSync(path.join(ROOT,'afterglow_protocol.html'),'utf8');
const head='<link rel="manifest" href="manifest.webmanifest"><meta name="theme-color" content="#0d1020"><meta name="mobile-web-app-capable" content="yes"><meta name="apple-mobile-web-app-capable" content="yes"><meta name="apple-mobile-web-app-status-bar-style" content="black-translucent"><meta name="apple-mobile-web-app-title" content="잔광 프로토콜"><link rel="apple-touch-icon" href="icon-180.png"><link rel="icon" href="icon-192.png">';
if(!h.includes('<meta charset="utf-8">'))throw new Error('head 위치를 찾을 수 없음');
h=h.replace('<meta charset="utf-8">','<meta charset="utf-8">'+head);
const reg='<script>if("serviceWorker" in navigator)addEventListener("load",()=>navigator.serviceWorker.register("sw.js").catch(()=>{}))</script>';
const i=h.lastIndexOf('</body>');h=i>=0?h.slice(0,i)+reg+h.slice(i):h+reg;
fs.writeFileSync(path.join(OUT,'index.html'),h);
console.log('앱 빌드 완료:',OUT,'('+Math.round(h.length/1024)+'KB, 빌드 '+BUILD+')');

// ---------- 안드로이드 앱(APK)용: 게임 원본을 assets에, 아이콘을 mipmap에 넣는다 (android/ 폴더에서 gradle로 빌드)
const AND=path.join(ROOT,'android','app','src','main');
if(fs.existsSync(AND)){
 const as=path.join(AND,'assets');require('./export_game_data.js');
 for(const [d,s] of [['mdpi',48],['hdpi',72],['xhdpi',96],['xxhdpi',144],['xxxhdpi',192]]){const dir=path.join(AND,'res','mipmap-'+d);fs.mkdirSync(dir,{recursive:true});fs.writeFileSync(path.join(dir,'ic_launcher.png'),png(s))}
 console.log('안드로이드 assets/아이콘 준비 완료:',as);
}
