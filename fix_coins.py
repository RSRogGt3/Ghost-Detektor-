import re

content = open('app/src/main/java/com/example/ui/viewmodel/GhostViewModel.kt').read()

# Add getCoinBonusPerEntity
bonus_func = """    fun getCoinBonusPerEntity(): Int {
        val coinUpgradeLvl = getUpgradeLevel("upgrade_coin_multiplier")
        return 2 + (coinUpgradeLvl * 2)
    }
"""

if "getCoinBonusPerEntity" not in content:
    content = content.replace("fun getCoinMultiplier(): Float {", bonus_func + "\n    fun getCoinMultiplier(): Float {")

# Replace entity captures/frees
content = re.sub(r'awardCoins\(35, "Höllendämon gefangen! \(\+35 🪙\)"\)', r'val c = getCoinBonusPerEntity(); awardCoins(c, "Höllendämon gefangen! (+$c 🪙)")', content)
content = re.sub(r'awardCoins\(35, "Astral-Vampir gebannt! \(\+35 🪙\)"\)', r'val c = getCoinBonusPerEntity(); awardCoins(c, "Astral-Vampir gebannt! (+$c 🪙)")', content)
content = re.sub(r'awardCoins\(20, "Geist gefangen! \(\+20 🪙\)"\)', r'val c = getCoinBonusPerEntity(); awardCoins(c, "Geist gefangen! (+$c 🪙)")', content)
content = re.sub(r'awardCoins\(20, "Geist ins Licht erlöst \(\+20 🪙\)"\)', r'val c = getCoinBonusPerEntity(); awardCoins(c, "Geist ins Licht erlöst (+$c 🪙)")', content)

# Also fix Seelen-Harmonisierung
content = re.sub(r'awardCoins\(coinsToAward, "Seelen-Harmonisierung \(\+\$\{coinsToAward\} 🪙\)"\)', r'val c = getCoinBonusPerEntity(); awardCoins(c, "Seelen-Harmonisierung (+$c 🪙)")', content)

open('app/src/main/java/com/example/ui/viewmodel/GhostViewModel.kt', 'w').write(content)
