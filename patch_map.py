import re
content = open('app/src/main/java/com/example/ui/screens/MapScreen.kt').read()

new_imports = """
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
"""

if "Manifest" not in content:
    content = content.replace("import androidx.compose.ui.Modifier", new_imports + "import androidx.compose.ui.Modifier")

body_replacement = """
    val context = LocalContext.current
    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context, 
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        context, 
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val mapProperties = MapProperties(
        mapType = MapType.SATELLITE,
        isMyLocationEnabled = hasLocationPermission
    )
    val uiSettings = MapUiSettings(
        zoomControlsEnabled = false,
        myLocationButtonEnabled = hasLocationPermission
    )
"""

content = re.sub(r'    val mapProperties = MapProperties\([^)]+\)\n    val uiSettings = MapUiSettings\([^)]+\)', body_replacement.strip(), content, flags=re.DOTALL)

open('app/src/main/java/com/example/ui/screens/MapScreen.kt', 'w').write(content)
