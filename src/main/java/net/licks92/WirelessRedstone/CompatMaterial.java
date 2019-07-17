package net.licks92.WirelessRedstone;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CompatMaterial {
    REDSTONE_TORCH("REDSTONE_TORCH", "REDSTONE_TORCH_ON"),
    REDSTONE_WALL_TORCH("REDSTONE_WALL_TORCH", "REDSTONE_TORCH_ON"),
    SIGN(new String[]{"OAK_SIGN", "SIGN"}, "SIGN_POST"),
    WALL_SIGN(new String[]{"OAK_WALL_SIGN", "WALL_SIGN"}, "WALL_SIGN"),
    COMPARATOR("COMPARATOR", "REDSTONE_COMPARATOR"),
    COMPARATOR_ON("COMPARATOR", "REDSTONE_COMPARATOR_ON"),
    COMPARATOR_OFF("COMPARATOR", "REDSTONE_COMPARATOR_OFF"),
    REPEATER("REPEATER", "DIODE"),
    REPEATER_ON("REPEATER", "DIODE_BLOCK_ON"),
    REPEATER_OFF("REPEATER", "DIODE_BLOCK_OFF"),
    IS_PREASURE_PLATE("_PLATE");

    private Material material;
    private List<Material> materials;

    CompatMaterial(String[] newVersions, String oldVersion) {
        HashMap<String, Material> materialMap = new HashMap<>();
        for (Material material : Material.values()) {
            materialMap.put(material.toString(), material);
        }

        if (Utils.isNewMaterialSystem()) {
            for (String newVersion : newVersions) {
                this.material = materialMap.entrySet()
                        .stream()
                        .filter( e -> e.getKey().equalsIgnoreCase(newVersion))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse(null);

                if (this.material != null)
                    break;
            }
        } else {
            this.material = materialMap.get(oldVersion);
        }
    }

    CompatMaterial(String newVersion, String oldVersion) {
        HashMap<String, Material> materialMap = new HashMap<>();
        for (Material material : Material.values()) {
            materialMap.put(material.toString(), material);
        }

        if (Utils.isNewMaterialSystem()) {
            this.material = materialMap.get(newVersion);
        } else {
            this.material = materialMap.get(oldVersion);
        }
    }

    CompatMaterial(String regex) {
        this.materials = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.toString().contains(regex)){
                this.materials.add(material);
            }
        }
    }

    /**
     * Get a Material instance which is backwards compatible with MC <1.13
     *
     * @return Material
     */
    public Material getMaterial() {
        if (material != null) {
            return material;
        } else if (materials != null) {
            return materials.get(0);
        } else {
            throw new IllegalStateException("Material " + this.name() + " couldn't be find. Is this the latest version?");
        }
    }

    /**
     * Check if a Material exists in a Material list.
     *
     * @param material Material to check if it exists in the list
     * @return Boolean; exists or doesn't exists
     */
    public boolean isMaterial(Material material) {
        if (materials == null) {
            return this.material == material;
        } else {
            return materials.contains(material);
        }
    }

}
