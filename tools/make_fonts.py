# 원본 게임의 글꼴(Noto Sans KR)을 앱에 넣기 위해 게임에 쓰는 글자만 남겨 작게 만든다.
# 쓰는 법: pip install fonttools 후 python tools/make_fonts.py
# 게임 문구에 새 한자·기호를 넣었을 때만 다시 실행하면 된다. 한글 2350자와 영문은 항상 들어간다.
import glob
import os
import sys

from fontTools import subset
from fontTools.ttLib import TTFont
from fontTools.varLib import instancer

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = sys.argv[1] if len(sys.argv) > 1 else r"C:\Windows\Fonts\NotoSansKR-VF.ttf"
OUT = os.path.join(ROOT, "android", "app", "src", "main", "assets", "fonts")

chars = set(chr(c) for c in range(0x20, 0x7F))
for a in range(0xB0, 0xC9):  # KS X 1001 한글 2350자
    for b in range(0xA1, 0xFF):
        try:
            chars.add(bytes([a, b]).decode("euc-kr"))
        except UnicodeDecodeError:
            pass
files = [os.path.join(ROOT, "afterglow_protocol.html")]
files += glob.glob(os.path.join(ROOT, "android", "app", "src", "main", "java", "**", "*.kt"), recursive=True)
for f in files:
    with open(f, encoding="utf-8") as h:
        text = h.read()
    # 음악 base64는 글자 수에 영향이 없다(영문뿐). 그대로 넣어도 된다.
    chars.update(c for c in text if ord(c) >= 0x80)

os.makedirs(OUT, exist_ok=True)
for name, wght in (("ag_regular.ttf", 400), ("ag_bold.ttf", 700)):
    f = instancer.instantiateVariableFont(TTFont(SRC), {"wght": wght})
    opt = subset.Options()
    opt.layout_features = ["*"]
    opt.name_IDs = ["*"]
    opt.notdef_outline = True
    s = subset.Subsetter(opt)
    s.populate(unicodes=[ord(c) for c in chars])
    s.subset(f)
    p = os.path.join(OUT, name)
    f.save(p)
    print(name, os.path.getsize(p))
