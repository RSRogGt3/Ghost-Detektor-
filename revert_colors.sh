find app/src/main/java -name "*.kt" -exec sed -i 's/MaterialTheme.colorScheme.primary/InfraGreenPrimary/g' {} +
find app/src/main/java -name "*.kt" -exec sed -i 's/MaterialTheme.colorScheme.surfaceVariant/InfraGreenSurfaceVariant/g' {} +
find app/src/main/java -name "*.kt" -exec sed -i 's/MaterialTheme.colorScheme.surface/InfraGreenSurface/g' {} +
find app/src/main/java -name "*.kt" -exec sed -i 's/MaterialTheme.colorScheme.onSurface/InfraGreenTextPrimary/g' {} +
find app/src/main/java -name "*.kt" -exec sed -i 's/MaterialTheme.colorScheme.onSurfaceVariant/InfraGreenTextMuted/g' {} +
find app/src/main/java -name "*.kt" -exec sed -i 's/MaterialTheme.colorScheme.outline/InfraGreenBorder/g' {} +
