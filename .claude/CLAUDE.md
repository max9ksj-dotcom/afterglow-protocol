# 잔광 프로토콜 (Afterglow Protocol) — 작업 안내서 (공유용)

이 파일은 저장소에 올라가는 공유용 안내서다. 웹 Claude Code(claude.ai/code)와 PC의 Claude Code 모두 이 파일을 읽는다.
공개 저장소이므로 개인 정보(PC 경로, 이메일, 서명 키 위치·비밀번호, 채팅 링크)는 여기에 적지 않는다.

## 대화 방식
- 요청자는 한국어로 짧게, 맞춤법이 틀린 채로 요청한다. 답변도 한국어로, 쉬운 말로 한다(터미널·개발 용어는 풀어서 설명).
- 범위가 모호하면(예: "설명을 줄여라") 어느 화면의 무엇인지 먼저 확인한다. 전체 문구를 바꿨다가 롤백한 적이 있다.
- 밸런스 수치는 정한 값을 알려 주고 조정 의견을 받는다.

## 프로젝트 개요
- 톱뷰 생존 로그라이크. **게임이 두 벌이다**:
  - 웹판: 단일 HTML 파일 `afterglow_protocol.html`(HTML/CSS/JS 캔버스, 외부 이미지 없음, 모든 그림은 코드 안의 도트 데이터). 배경 음악 2곡은 `<audio id="bgmA"/"bgmB">`에 base64 WAV로 들어 있어 파일이 약 4MB다. 큰 파일이므로 통째로 읽지 말고 검색해서 필요한 부분만 본다.
  - 앱판: `android/` Kotlin 네이티브 게임(`com.afterglow.protocol`). Game.kt(로직) Render.kt(그리기) Ui.kt(메뉴·스킬트리) Gfx.kt(캔버스 래퍼) Data.kt(데이터·저장) Audio.kt(음악·효과음) Post.kt(심층·과부하 등 생존 이후 콘텐츠) Caps.kt(보급 캡슐) Debug.kt(개발용 자동 시험) MainActivity.kt.
- **게임 동작을 고치면 웹판과 앱판을 둘 다 고친다.** 같은 이름의 함수·수치를 맞춘다.
- 데이터(무기·스킬트리 노드·유물·도트 그림 `SPR`·`MON`·`ELD`·`BSP`·`HBP`·`WP` 등)는 `tools/export_game_data.js`가 HTML에서 뽑아 `android/app/src/main/assets/gamedata.json`으로 만든다(`tools/build_app.js`가 호출, gitignore 대상). 새 데이터 상수를 앱에서 쓰려면 export_game_data.js의 목록에 추가하고 Data.kt에서 읽는다.
- 앱 음악: `android/app/src/main/res/raw/bgm_game.wav`, `bgm_boss.wav`. 음악 생성기 `tools/make_music.py`(numpy, `python tools/make_music.py <출력폴더> 0.22` → 웹용 22kHz, 앱용 44kHz).

## 작업 규칙
- 새 게임을 만들지 말고 기존 파일을 수정한다. 웹판은 단일 HTML로 유지한다.
- 기존 저장 데이터(웹 localStorage, 앱 SharedPreferences)가 깨지지 않게 한다. 스킬트리 노드 id·효과·비용은 유지한다.
- 수정 후 항상 세 테스트를 돌린다(웹판만 검사, Node.js만 있으면 됨):
  - `node tests/regression.js`
  - `node tests/invest_relic.js`
  - `node tests/postgame.js`
  (HTML 안의 `<script>`를 꺼내 Node vm에서 가짜 캔버스로 실행한다.) BOSS(P1) quality 시험은 패턴이 무작위라 약 3% 확률로 우연히 실패한다 → 다시 돌려 본다.
- Kotlin 파일은 대부분 CRLF 줄바꿈이다. `sed -i`로 고치면 줄바꿈이 바뀔 수 있으니 Edit 도구나 줄바꿈을 보존하는 스크립트로 고친다.
- 웹판 HTML을 스크립트로 고칠 때도 원래 줄바꿈을 유지한다.

## 웹 Claude Code에서 작업할 때
- 웹 환경에는 안드로이드 에뮬레이터가 없다. 앱판(Kotlin)이 컴파일되는지는 **풀 리퀘스트를 만들면 GitHub Actions가 자동으로 확인**한다(테스트 3종 + APK 빌드, 이때는 공개 릴리스를 만들지 않음). PR의 검사 결과(체크)가 초록색인지 확인하고 결과를 요청자에게 알려 준다.
- 작업은 브랜치에서 하고 PR을 만든다. **main에 병합되면** GitHub Actions가 앱 릴리스(`v1.0.N`, afterglow-protocol.apk)와 웹사이트(GitHub Pages) 게시를 자동으로 한다. 병합은 요청자가 하거나, 요청자가 맡기면 한다.
- 커밋 메시지는 한국어로, 무엇을 바꿨는지 쉽게 적는다.
- 작업을 끝내면 이 파일의 "최근 상태"에 바뀐 점(함수 이름·수치)을 짧게 덧붙인다.

## 주소
- GitHub: https://github.com/max9ksj-dotcom/afterglow-protocol
- 웹사이트: https://max9ksj-dotcom.github.io/afterglow-protocol/
- 앱 다운로드: https://github.com/max9ksj-dotcom/afterglow-protocol/releases/latest/download/afterglow-protocol.apk

## 게임 구조 요약
- 무기 여러 종 + 레벨업 선택, 5레벨 무기 2개 합체(28종, 3단 합체 없음). 무기 그림 `WSH`.
- 몬스터 7종(기생충 처음부터, 지렁이 1분, 분열체 2분, 돌격수 4분, 폭탄귀 5분30초, 독침 사수 7분, 갑각 거신 8분) + 엘리트 7종(`ELD`, `eliteAI`, 처치 시 유물 3택1).
- 게임 시간 5분 시작(스킬트리로 연장), 보스 9분 등장. 보스 처치 시 2페이지(버그 컨셉 글리치, 공중 무적 보스 ERROR.exe, 3분 버티기) → 3페이지(캐릭터 하나로 변한 라이벌 ERROR_CLONE). 3페이지 클리어 후 심층(PAGE 4, 층당 45초, 변이 8종, 5층마다 수호자, 10층마다 거울 층).
- 스킬트리 371노드/736연결, 영구 능력치 투자 `UPS` 10종, 유물 15종(5종 영구), 각성 8종, 도전 과제 25개, 과부하 0~10등급, 보스 러시, 기록실.
- 증강(PD): 합체 무기는 합체 전 무기 둘 중 하나라도 해당되는 증강 효과를 받는다(`WD[k].req`). rld/dur/prj/rng 최대 5레벨.
- 보급 캡슐(`capDrop` 등, 앱 Caps.kt): 수리/자석/정화.
- 앱 개발용 빌드는 `am start ... --es jump soak|boss|glitch|p2|p3|lv|fitelite|fitcap|...`로 특정 장면을 띄운다(PC에서만).

## 최근 상태 (2026-11-14 기준, v1.0.18)
- 기본 몬스터 도트 `MON`(왼쪽 절반 줄 r, 전용 색 p, 걷기 frB 인자 a). 갑각 거신 18칸 배율 2(엘리트 거신 +2). 지렁이 `WPG`/`WPC` 14×5.
- 시간 난이도: `tmf` 1페이지 t/30×(1+t/600)(상한 45), 심층 14+층×2.5+층²×0.1+`ftR()`×(4+층×0.3). `dmgMul`·`eSp`·`eHp`에 시간/층 상승 포함(2·3페이지 제외). 수치는 의견 대기.
- 배경 음악은 Claude가 직접 작곡·합성(게임 A단조 120BPM, 보스 E단조 150BPM, 각 32초 반복).
- 공격 속도: `stat().iv`를 빼기 대신 속도 합산으로 계산 `1/(1+.05×acc+tb('iv')+relX.iv)`(풀 스킬트리에서 음수가 되어 공속 표시가 깨지던 문제 수정). 궤도형 무기 타격 간격 `OB().iv`도 공격 속도를 받음(최소 0.15초).
- 웹판 3페이지 라이벌 캐릭터 id를 `S.rid`에 저장(전에는 잔광 회수 대기시간 `S.rc`와 겹쳐서 라이벌이 항상 첫 캐릭터, 3페이지·심층에서 잔광 회수 증강이 안 됐음). 앱은 원래 `S.rivalId`로 분리돼 있었음.
- 줌(2026-11-16): 게임 화면 오른쪽 가장자리 줌 막대를 끌어 `ZM`(0.6~1.5, 기본 1)을 바꾼다. 웹 `zmBar/zmHit/zmSet/drawZoom`(PC는 마우스 휠), 앱 Render.kt의 같은 이름 함수 + MainActivity 터치(`pz`). 저장 키 `zm`. 넓게 볼 때(ZM<1)는 출현 거리·삭제 거리·`HW/HH`를 1/ZM로 넓힌다.
- 배속(2026-11-16): `spE()`가 실제 적용 배속(2페이지는 1 고정, 버튼 `spT`/`spLab`). 루프는 실제 흐른 시간(최대 0.1초)×배속을 0.034초 이하 조각으로 나눠 `upd`를 돌린다 → 게임 속 시간이 정확히 배속만큼 간다. 2배속 이상에서는 타격 멈춤(`S.hs`)이 시간을 늦추지 않는다.
- 신전·상자·책(2026-11-21, 요청자가 고른 "메가봉크 느낌" 두 가지): 처치 시 금화 `S.gd`(일반 1·엘리트 15·보스 50). 1페이지·심층에만(보스 러시 제외, `woOn`) 맵에 충전 신전 2개·보급 상자 2개(`S.wo`, `woUpd/drawWo/drawWoEdge`, 앱 Loot.kt). 신전: 반경 `SHR`70 안에 `SHT`4초 → 축복 3택1(`shrineUse`, `SHV`×등급), 사용 후 30초 뒤 재생성. 상자: 값 `chCost()`=12×1.4^연 횟수, 3택1(`chestOpen`, `opts(1)`=책 희귀 이상). 능력치 책 `BK` 9종(레벨업 선택지, 3칸 `BKS`, 각 10레벨 `BKX`, 등급 배율 `BKM` 1/1.5/2/3, 확률은 유물 `RG.w`+행운의 책). 효과 합계 `S.bkv`/`bv(k)`를 `stat()`·`gainXP`·수집 범위·금화에 더함. 선택 화면 `choose()`. 테스트 postgame.js LOOT/CHEST/SHRINE/BOOK.
- 신전·상자 도트(2026-11-21): `WOS`(sh 신전 16×23, ch/cho 닫힌·열린 상자 16×11, coin, arr 화살표, shi 작은 신전)+`WOP` 색, `wos(k,v)` 캐시(v: d 꺼짐, b 빛남, y 금색 화살표). export_game_data.js가 WOS/WOP를 내보내고 Data.kt가 읽음. 책 아이콘 `IC.b_*` 9종(ICP에 m 분홍·d 갈색 추가). 신전 충전은 점 24개 고리, 연 상자·다 쓴 신전은 `o.ot`초 동안 남아 사라짐. 금화는 HUD 첫 줄 오른쪽 `drawGold`.
- 몬스터·보스 도트 다시 그림(2026-11-21): 기본 몬스터 `MON` 6종을 16칸 폭(왼쪽 절반 8칸)으로, 엘리트 `ELD` 6종도 16칸 폭 + 전용 색 `q`(있으면 `mkS(r.map(MR),q)`로 그림, 앱 Eld.q). 걷기 인자 `a`도 새 줄 번호. 보스 `BS2`·`HB2`는 크기 그대로 하이라이트만 추가. 책 레벨 기록은 `S.bks`(2페이지 전환 `nextPage`의 `S.bk=0`과 겹쳐 책이 초기화되던 버그 수정, 앱도 `bks`).
- 의견 대기 중인 수치: 투자 비용, 영구 유물 선정, 궤도 무기 4초/2.5초, 심층·과부하 배율, 증강 수치, 보급 캡슐, 엘리트 패턴, 시간 난이도, 금화·상자 값·신전·책 수치.
