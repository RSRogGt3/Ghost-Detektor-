import re

with open('app/src/main/java/com/example/ui/screens/ScannerScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

def extract_block(header, next_header):
    pattern = re.compile(rf"({header}.*?){next_header}", re.DOTALL)
    match = pattern.search(content)
    if match:
        return match.group(1)
    return None

dim_block = extract_block(r"            // SECTION: DIMENSIONEN & DÄMONEN-SIEGEL", r"            // SECTION: EMF, SPEKTRUM & MAGNETFELD")
emf_block = extract_block(r"            // SECTION: EMF, SPEKTRUM & MAGNETFELD", r"            // SECTION: SPIRIT-BOX & AUDIO")

if dim_block and emf_block:
    new_content = content.replace(dim_block + emf_block, emf_block + dim_block)
    with open('app/src/main/java/com/example/ui/screens/ScannerScreen.kt', 'w', encoding='utf-8') as f:
        f.write(new_content)
    print("Swapped!")
else:
    print("Could not find blocks.")
