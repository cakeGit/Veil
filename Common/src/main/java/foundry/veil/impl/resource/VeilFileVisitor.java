package foundry.veil.impl.resource;

import foundry.veil.Veil;
import foundry.veil.ext.PackResourcesExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.Nullable;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class VeilFileVisitor extends SimpleFileVisitor<Path> {

    private final PackType packType;
    private final Path root;
    private final Path build;
    private final PackResourcesExtension.PackResourceConsumer consumer;

    public VeilFileVisitor(PackType packType, Path root, PackResourcesExtension.PackResourceConsumer consumer, boolean checkBuildPath) {
        this.packType = packType;
        this.root = root;
        this.build = checkBuildPath && Veil.platform().isDevelopmentEnvironment() ? root.getParent() : null;
        this.consumer = consumer;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (file.startsWith(this.root) && Files.isRegularFile(file)) {
            Path localPath = this.root.relativize(file);

            // Attempt to find actual resources, not gradle copy
            if (this.build != null && file.startsWith(this.build) && file.getNameCount() > 3) {
                try {
                    String sourceRoot = this.build.relativize(file).getName(0).toString();
                    Path projectRoot = this.build.getParent().getParent().getParent();

                    // This sucks, but we have to scan all roots :/
                    try (Stream<Path> walk = Files.walk(projectRoot, 1)) {
                        for (Path possibleRoot : walk.toList()) {
                            Path resourcesPath = possibleRoot.resolve("src").resolve(sourceRoot).resolve("resources").resolve(localPath);
                            if (Files.exists(resourcesPath)) {
                                this.accept(localPath, resourcesPath, file);
                                return FileVisitResult.CONTINUE;
                            }
                        }
                    }
                } catch (Exception e) {
                    Veil.LOGGER.error("Failed to find IDE source root", e);
                }
            }

            this.accept(localPath, file, null);
        }
        return FileVisitResult.CONTINUE;
    }

    public void accept(ResourceLocation localPath, Path file) {
    }

    private void accept(Path localPath, Path file, @Nullable Path modResourcePath) {
        String[] name = localPath.toString().replace(localPath.getFileSystem().getSeparator(), "/").split("/", 2);
        if (name.length == 2) {
            ResourceLocation id = ResourceLocation.tryBuild(name[0], name[1]);
            if (id != null) {
                this.consumer.accept(this.packType, id, file, modResourcePath);
            }
        }
    }
}
