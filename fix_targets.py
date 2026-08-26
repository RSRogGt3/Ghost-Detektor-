import re

content = open('app/src/main/java/com/example/data/GhostRewardModels.kt').read()

# I want the base target counts to be roughly 50, 100, 250, 500
# Let's map IDs to new target counts
replacements = {
    'ghost_5': 50,
    'ghost_10': 100,
    'ghost_25': 250,
    'ghost_50': 500,
    
    'demon_1': 10,
    'demon_5': 50,
    'demon_15': 150,
    'demon_30': 300,
    
    'vampire_1': 10,
    'vampire_5': 50,
    'vampire_15': 150,
    'vampire_30': 300,
    
    'portal_1': 10,
    'portal_5': 50,
    'portal_15': 150,
    
    'evp_1': 10,
    'evp_5': 50,
    'evp_20': 200,
    
    'special_all_25': 250,
    'special_all_100': 1000
}

for id, target in replacements.items():
    # Find the block for this id and replace targetCount = \d+,
    pattern = r'(id\s*=\s*"' + id + r'".*?targetCount\s*=\s*)\d+,'
    content = re.sub(pattern, r'\g<1>' + str(target) + ',', content, flags=re.DOTALL)

# Add the new upgrade
new_upgrade = """        GhostShopUpgrade(
            id = "upgrade_coin_multiplier",
            title = "Ecto-Coin Multiplikator",
            subtitle = "Mehr Coins pro Individuum (+2 pro Level)",
            iconEmoji = "🪙",
            basePrice = 100,
            priceMultiplier = 1.8f,
            maxLevel = 50,
            colorHex = 0xFFFFD700,
            getLevelDescription = { lvl ->
                if (lvl == 0) "Stufe 0: 2 Coins pro Individuum"
                else "Stufe $lvl: ${2 + lvl * 2} Coins pro Individuum"
            }
        ),
"""
if "upgrade_coin_multiplier" not in content:
    content = content.replace('val allShopUpgrades: List<GhostShopUpgrade> = listOf(', 'val allShopUpgrades: List<GhostShopUpgrade> = listOf(\n' + new_upgrade)

open('app/src/main/java/com/example/data/GhostRewardModels.kt', 'w').write(content)
