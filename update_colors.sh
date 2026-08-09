find app/src/main/java -name "*.kt" -exec sed -i 's/InfraGreenPrimary/MaterialTheme.colorScheme.primary/g' {} +
find app/src/main/java -name "*.kt" -exec sed -i 's/InfraGreenSurfaceVariant/MaterialTheme.colorScheme.surfaceVariant/g' {} +
find app/src/main/java -name "*.kt" -exec sed -i 's/InfraGreenSurface/MaterialTheme.colorScheme.surface/g' {} +
find app/src/main/java -name "*.kt" -exec sed -i 's/InfraGreenTextPrimary/MaterialTheme.colorScheme.onSurface/g' {} +
find app/src/main/java -name "*.kt" -exec sed -i 's/InfraGreenTextMuted/MaterialTheme.colorScheme.onSurfaceVariant/g' {} +
find app/src/main/java -name "*.kt" -exec sed -i 's/InfraGreenBorder/MaterialTheme.colorScheme.outline/g' {} +
