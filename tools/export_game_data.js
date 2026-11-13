// 게임 원본(afterglow_protocol.html)의 데이터(무기·스킬트리·유물·도트 그림 등)를 뽑아 안드로이드 앱용 JSON으로 저장한다.
const fs=require('fs'),path=require('path'),vm=require('vm');
const ROOT=path.join(__dirname,'..');
const html=fs.readFileSync(path.join(ROOT,'afterglow_protocol.html'),'utf8');
const js=html.slice(html.lastIndexOf('<script>')+8,html.lastIndexOf('</script>'));
const noop=()=>{};
const ctx=new Proxy({},{get:(t,k)=>k in t?t[k]:()=>({addColorStop:noop}),set:(t,k,v)=>{t[k]=v;return true}});
const el=()=>({getContext:()=>ctx,style:{},classList:{add:noop,remove:noop},width:16,height:16,textContent:'',innerHTML:'',className:'',setAttribute:noop,getBoundingClientRect:()=>({left:0,top:0,width:400,height:800})});
const els={};
const sb={console,Math,JSON,innerWidth:400,innerHeight:800,localStorage:{getItem:()=>null,setItem:noop},requestAnimationFrame:noop,setTimeout:noop,navigator:{maxTouchPoints:1},
 document:{getElementById:i=>els[i]||(els[i]=el()),createElement:el,addEventListener:noop,body:el()}};
sb.window=sb;vm.createContext(sb);
const dump=`;globalThis.__D=JSON.stringify({
WD:Object.keys(WD).map(k=>Object.assign({id:k},WD[k])),
NL:NL.map(n=>({id:n.id,t:n.t,x:n.x,y:n.y,c:n.c,nm:n.nm,e:Object.keys(n.e).map(k=>[k,n.e[k]]),rq:n.rq,ex:n.ex,rg:n.rg})),
LK,REL,UPS,OCN,MUT,AWK,ACH,LOG,PD:Object.keys(PD).map(k=>[k].concat(PD[k])),IC,ICP,PH,TOP,LG,
CHR:CHR.map(c=>({id:c.id,n:c.n,d:c.d,m:c.m,pal:c.pal,ex:c.ex,cd:c.cd})),
SPR:{CR,CH,SPL,SPT,TK,BS,BMB},ELD,GEMS:{G_DI,G_OR,G_DR,G_SQ,G_ST,G_CR},WSH,WV,WP});`;
vm.runInContext(js+dump,sb);
// 원본의 shd()는 짧은 색(#f6f)을 계산하지 못해 '#4707NaN' 같은 값을 만든다(브라우저는 무시). 여기서 바른 색으로 고친다.
const J=JSON.parse(sb.__D),full=c=>c.length==4?'#'+c[1]+c[1]+c[2]+c[2]+c[3]+c[3]:c;
const shade=(c,f)=>'#'+[1,3,5].map(i=>Math.min(255,Math.round(parseInt(full(c).slice(i,i+2),16)*f)).toString(16).padStart(2,'0')).join('');
for(const w of J.WD)if(w.gd){const v=J.WV[w.id];if(v&&!/^#[0-9a-f]{6}$/i.test(v[1].A))v[1].A=shade(w.c,.45)}
const bad=[];JSON.stringify(J,(k,v)=>{if(typeof v=='string'&&v[0]=='#'&&v.length>1&&!/^#([0-9a-f]{3}|[0-9a-f]{6})$/i.test(v)&&/NaN/.test(v))bad.push(v);return v});
if(bad.length)throw new Error('잘못된 색: '+bad.join(','));
sb.__D=JSON.stringify(J);
const out=path.join(ROOT,'android','app','src','main','assets');
fs.mkdirSync(out,{recursive:true});
fs.writeFileSync(path.join(out,'gamedata.json'),sb.__D);
const D=JSON.parse(sb.__D);
console.log('게임 데이터 추출 완료: 무기',D.WD.length,'노드',D.NL.length,'연결',D.LK.length,'유물',D.REL.length,'크기',sb.__D.length);
