import re

content = open('app/src/main/java/com/example/ui/viewmodel/GhostViewModel.kt').read()

content = content.replace('com.example.ui.components.EntityCategory.DEMON -> val c = getCoinBonusPerEntity(); awardCoins(c, "Höllendämon gefangen! (+$c 🪙)")', 'com.example.ui.components.EntityCategory.DEMON -> { val c = getCoinBonusPerEntity(); awardCoins(c, "Höllendämon gefangen! (+$c 🪙)") }')
content = content.replace('com.example.ui.components.EntityCategory.VAMPIRE -> val c = getCoinBonusPerEntity(); awardCoins(c, "Astral-Vampir gebannt! (+$c 🪙)")', 'com.example.ui.components.EntityCategory.VAMPIRE -> { val c = getCoinBonusPerEntity(); awardCoins(c, "Astral-Vampir gebannt! (+$c 🪙)") }')
content = content.replace('else -> val c = getCoinBonusPerEntity(); awardCoins(c, "Geist gefangen! (+$c 🪙)")', 'else -> { val c = getCoinBonusPerEntity(); awardCoins(c, "Geist gefangen! (+$c 🪙)") }')

open('app/src/main/java/com/example/ui/viewmodel/GhostViewModel.kt', 'w').write(content)
