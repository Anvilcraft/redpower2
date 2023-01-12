package com.eloraam.redpower.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.oredict.OreDictionary;

public class CoreLib {
    private static TreeMap<ItemStack, String> oreMap
        = new TreeMap<>(CoreLib::compareItemStack);
    public static String[] rawColorNames
        = new String[] { "white", "orange", "magenta", "lightBlue", "yellow", "lime",
                         "pink",  "gray",   "silver",  "cyan",      "purple", "blue",
                         "brown", "green",  "red",     "black" };
    public static String[] enColorNames
        = new String[] { "White", "Orange", "Magenta",    "Light Blue", "Yellow", "Lime",
                         "Pink",  "Gray",   "Light Gray", "Cyan",       "Purple", "Blue",
                         "Brown", "Green",  "Red",        "Black" };
    public static int[] paintColors
        = new int[] { 16777215, 16744448, 16711935, 7110911, 16776960, 65280,
                      16737408, 5460819,  9671571,  65535,   8388863,  255,
                      5187328,  32768,    16711680, 2039583 };
    public static final Material materialRedpower = new Material(MapColor.woodColor);
    public static final GameProfile REDPOWER_PROFILE = new GameProfile(
        UUID.fromString("d90e51a0-41af-4a37-9fd0-f2fdc15a181b"), "[RedPower]"
    );

    public static FakePlayer getRedpowerPlayer(
        World world, int x, int y, int z, int rotation, GameProfile profile
    ) {
        MinecraftServer server = ((WorldServer) world).func_73046_m();
        FakePlayer player = FakePlayerFactory.get((WorldServer) world, profile);
        double dx = (double) x + 0.5;
        double dy = (double) y - 1.1;
        double dz = (double) z + 0.5;
        float pitch;
        float yaw;
        switch (rotation ^ 1) {
            case 0:
                pitch = 90.0F;
                yaw = 0.0F;
                dy -= 0.51;
                break;
            case 1:
                pitch = -90.0F;
                yaw = 0.0F;
                dy += 0.51;
                break;
            case 2:
                pitch = 0.0F;
                yaw = 180.0F;
                dz -= 0.51;
                break;
            case 3:
                pitch = 0.0F;
                yaw = 0.0F;
                dz += 0.51;
                break;
            case 4:
                pitch = 0.0F;
                yaw = 90.0F;
                dx -= 0.51;
                break;
            default:
                pitch = 0.0F;
                yaw = 270.0F;
                dx += 0.51;
        }

        player.setLocationAndAngles(dx, dy, dz, yaw, pitch);
        return player;
    }

    public static boolean hasBreakPermission(EntityPlayerMP player, int x, int y, int z) {
        return hasEditPermission(player, x, y, z)
            && !ForgeHooks
                    .onBlockBreakEvent(
                        player.worldObj,
                        player.theItemInWorldManager.getGameType(),
                        player,
                        x,
                        y,
                        z
                    )
                    .isCanceled();
    }

    public static boolean hasEditPermission(EntityPlayerMP player, int x, int y, int z) {
        return player.canPlayerEdit(
                   x, y, z, player.worldObj.getBlockMetadata(x, y, z), null
               )
            && !MinecraftServer.getServer().isBlockProtected(
                player.worldObj, x, y, z, player
            );
    }

    public static void updateAllLightTypes(World world, int x, int y, int z) {
        world.updateLightByType(EnumSkyBlock.Block, x, y, z);
        world.updateLightByType(EnumSkyBlock.Sky, x, y, z);
    }

    @Deprecated
    void initModule(String name) {
        Class<?> cl;
        try {
            cl = Class.forName(name);
        } catch (ClassNotFoundException var7) {
            return;
        }

        Method mth;
        try {
            mth = cl.getDeclaredMethod("initialize");
        } catch (NoSuchMethodException var6) {
            return;
        }

        try {
            mth.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException var5) {}
    }

    public static <T>
        T getTileEntity(IBlockAccess iba, int x, int y, int z, Class<T> type) {
        TileEntity tile = iba.getTileEntity(x, y, z);
        return (T) (tile != null && type.isAssignableFrom(tile.getClass()) ? tile : null);
    }

    public static <T> T getTileEntity(IBlockAccess iba, WorldCoord wc, Class<T> type) {
        TileEntity tile = iba.getTileEntity(wc.x, wc.y, wc.z);
        return (T) (tile != null && type.isAssignableFrom(tile.getClass()) ? tile : null);
    }

    public static <T extends TileEntity>
        T getGuiTileEntity(World world, int x, int y, int z, Class<T> cl) {
        if (!world.isRemote) {
            TileEntity tr = world.getTileEntity(x, y, z);
            return (T) (!cl.isInstance(tr) ? null : tr);
        } else {
            try {
                T t = (T) cl.newInstance();
                t.setWorldObj(world);
                return t;
            } catch (IllegalAccessException | InstantiationException var6) {
                return null;
            }
        }
    }

    public static void markBlockDirty(World world, int i, int j, int k) {
        if (world.blockExists(i, j, k)) {
            world.getChunkFromBlockCoords(i, k).setChunkModified();
        }
    }

    public static int compareItemStack(ItemStack a, ItemStack b) {
        return Item.getIdFromItem(a.getItem()) != Item.getIdFromItem(b.getItem())
            ? Item.getIdFromItem(a.getItem()) - Item.getIdFromItem(b.getItem())
            : (a.getItemDamage() == b.getItemDamage()
                   ? 0
                   : (a.getItem().getHasSubtypes() ? a.getItemDamage() - b.getItemDamage()
                                                   : 0));
    }

    static void registerOre(String name, ItemStack ore) {
        oreMap.put(ore, name);
    }

    public static void readOres() {
        for (String st : OreDictionary.getOreNames()) {
            for (ItemStack ist : OreDictionary.getOres(st)) {
                registerOre(st, ist);
            }
        }
    }

    public static String getOreClass(ItemStack ist) {
        String st = (String) oreMap.get(ist);
        if (st != null) {
            return st;
        } else {
            ist = new ItemStack(ist.getItem(), 1, -1);
            return (String) oreMap.get(ist);
        }
    }

    public static boolean matchItemStackOre(ItemStack a, ItemStack b) {
        String s1 = getOreClass(a);
        String s2 = getOreClass(b);
        return (ItemStack.areItemStacksEqual(a, b)
                || s1 != null && s2 != null && s1.equals(s2))
            && ItemStack.areItemStackTagsEqual(a, b);
    }

    public static void dropItem(World world, int i, int j, int k, ItemStack ist) {
        if (!world.isRemote) {
            double d = 0.7;
            double x = (double) world.rand.nextFloat() * d + (1.0 - d) * 0.5;
            double y = (double) world.rand.nextFloat() * d + (1.0 - d) * 0.5;
            double z = (double) world.rand.nextFloat() * d + (1.0 - d) * 0.5;
            EntityItem item = new EntityItem(
                world, (double) i + x, (double) j + y, (double) k + z, ist
            );
            item.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(item);
        }
    }

    public static ItemStack copyStack(ItemStack ist, int n) {
        return new ItemStack(ist.getItem(), n, ist.getItemDamage());
    }

    public static int rotToSide(int r) {
        switch (r) {
            case 0:
                return 5;
            case 1:
                return 3;
            case 2:
                return 4;
            default:
                return 2;
        }
    }

    public static int getFacing(int side) {
        switch (side) {
            case 0:
                return 2;
            case 1:
                return 5;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 1;
            case 5:
                return 0;
            default:
                return 0;
        }
    }

    public static MovingObjectPosition
    retraceBlock(World world, EntityLivingBase ent, int x, int y, int z) {
        Vec3 org = Vec3.createVectorHelper(
            ent.posX, ent.posY + 1.62 - (double) ent.yOffset, ent.posZ
        );
        Vec3 vec = ent.getLook(1.0F);
        Vec3 end = org.addVector(vec.xCoord * 5.0, vec.yCoord * 5.0, vec.zCoord * 5.0);
        Block bl = world.getBlock(x, y, z);
        return bl == null ? null : bl.collisionRayTrace(world, x, y, z, org, end);
    }

    public static MovingObjectPosition traceBlock(EntityPlayer player) {
        Vec3 org = Vec3.createVectorHelper(
            player.posX, player.posY + 1.62 - (double) player.yOffset, player.posZ
        );
        Vec3 vec = player.getLook(1.0F);
        Vec3 end = org.addVector(vec.xCoord * 5.0, vec.yCoord * 5.0, vec.zCoord * 5.0);
        return player.worldObj.rayTraceBlocks(org, end);
    }

    public static void placeNoise(World world, int i, int j, int k, Block block) {
        world.playSoundEffect(
            (double) ((float) i + 0.5F),
            (double) ((float) j + 0.5F),
            (double) ((float) k + 0.5F),
            block.stepSound.func_150496_b(),
            (block.stepSound.getVolume() + 1.0F) / 2.0F,
            block.stepSound.getPitch() * 0.8F
        );
    }

    public static int getBurnTime(ItemStack ist) {
        return TileEntityFurnace.getItemBurnTime(ist);
    }

    public static double getAverageEdgeLength(AxisAlignedBB aabb) {
        double d = aabb.maxX - aabb.minX;
        double d1 = aabb.maxY - aabb.minY;
        double d2 = aabb.maxZ - aabb.minZ;
        return (d + d1 + d2) / 3.0;
    }

    public static void writeChat(EntityPlayer pl, String str) {
        if (pl instanceof EntityPlayerMP) {
            EntityPlayerMP emp = (EntityPlayerMP) pl;
            emp.addChatComponentMessage(new ChatComponentText(str));
        }
    }

    public static void updateBlock(World world, int x, int y, int z) {
        if (!(world.getTileEntity(x, y, z) instanceof TileExtended)) {
            world.func_147479_m(x, y, z);
        }
    }

    public static int[] toIntArray(List<Integer> integerList) {
        int[] intArray = new int[integerList.size()];

        for (int i = 0; i < integerList.size(); ++i) {
            intArray[i] = integerList.get(i);
        }

        return intArray;
    }

    public static <T, E> void setFinalValue(
        Class<? super T> classToAccess, T instance, E value, String... fieldNames
    ) {
        try {
            findField(classToAccess, fieldNames).set(instance, value);
        } catch (Exception var5) {
            throw new UnableToAccessFieldException(fieldNames, var5);
        }
    }

    public static Field findField(Class<?> clazz, String... fieldNames) {
        Exception failed = null;

        for (String fieldName : fieldNames) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & -17);
                return field;
            } catch (Exception var9) {
                failed = var9;
            }
        }

        throw new UnableToFindFieldException(fieldNames, failed);
    }
}
