package foundry.veil.impl.resource.tree;

import foundry.veil.api.resource.VeilResource;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import org.jetbrains.annotations.Nullable;

/**
 * A resource folder for a tree-structure
 */
public class VeilResourceFolder {

    private final String name;
    private final Object2ObjectMap<String, VeilResourceFolder> subFolders = new Object2ObjectArrayMap<>(8);
    private final Object2ObjectMap<String, VeilResource<?>> resources = new Object2ObjectArrayMap<>(8);

    public VeilResourceFolder(String name) {
        this.name = name;
    }

    /**
     * Adds a resource to this folder, creating sub-folders if necessary
     *
     * @param path     The path of the resource
     * @param resource The resource to add
     */
    public void addResource(String path, VeilResource<?> resource) {
        // If the path contains a slash, we need to create a sub-folder
        if (path.contains("/")) {
            String[] parts = path.split("/", 2);
            VeilResourceFolder folder = this.subFolders.computeIfAbsent(parts[0], VeilResourceFolder::new);
            folder.addResource(parts[1], resource);
            return;
        }

        this.resources.put(path, resource);
    }

    /**
     * Adds a folder to this folder
     *
     * @param folder The folder to add, with a pre-known name
     */
    public void addFolder(VeilResourceFolder folder) {
        this.subFolders.put(folder.name, folder);
    }

    /**
     * @return An iterable collection of all folders contained within this folder
     */
    public ObjectCollection<VeilResourceFolder> getSubFolders() {
        return this.subFolders.values();
    }

    /**
     * @return An iterable collection of all resources contained within this folder
     */
    public Iterable<VeilResource<?>> getResources() {
        return this.resources.values();
    }

    public String getName() {
        return this.name;
    }

    public @Nullable VeilResourceFolder getFolder(String name) {
        return this.subFolders.get(name);
    }

    public @Nullable VeilResource<?> getResource(String name) {
        return this.resources.get(name);
    }
}
