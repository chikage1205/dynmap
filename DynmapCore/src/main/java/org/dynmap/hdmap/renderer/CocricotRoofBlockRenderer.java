package org.dynmap.hdmap.renderer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;

import org.dynmap.renderer.CustomRenderer;
import org.dynmap.renderer.DynmapBlockState;
import org.dynmap.renderer.MapDataContext;
import org.dynmap.renderer.RenderPatch;
import org.dynmap.renderer.RenderPatchFactory;
import org.dynmap.renderer.RenderPatchFactory.SideVisible;

public class CocricotRoofBlockRenderer extends CustomRenderer {
    private static final int TEX_INCLINE = 0;
    private static final int TEX_INCLINE1 = 1;
    private static final int TEX_TOP = 2;
    private static final int TEX_TOP_INCLINE = 3;
    private static final int TEX_TOP_OUTER = 4;
    private static final int TEX_TOP_OUTER1 = 5;
    private static BitSet stair_ids = new BitSet();

    private boolean extendedTexture;
        
    // Array of meshes for normal steps - index = (data value & 7)
    private RenderPatch[][] stepmeshes = new RenderPatch[8][];
    // Array of meshes for 3/4 steps - index = (data value & 7), with extra one clockwise from normal step
    private RenderPatch[][] step_3_4_meshes = new RenderPatch[8][];
    // Array of meshes for 1/4 steps - index = (data value & 7), with clockwise quarter clopped from normal step
    private RenderPatch[][] step_1_4_meshes = new RenderPatch[8][];
    
    private int textsetcnt = 0;
    private String textindex = null;
    private String[] tilefields = null;
    private String[] texturemap;
    
    private void setID(String bname) {
        DynmapBlockState bbs = DynmapBlockState.getBaseStateByName(bname);
        if (bbs.isNotAir()) {
            for (int i = 0; i < bbs.getStateCount(); i++) {
                stair_ids.set(bbs.getState(i).globalStateIndex);
            }
        }
    }

    @Override
    public boolean initializeRenderer(RenderPatchFactory rpf, String blkname, BitSet blockdatamask, Map<String,String> custparm) {
        if(!super.initializeRenderer(rpf, blkname, blockdatamask, custparm))
            return false;
        setID(blkname);   /* Mark block as a stair */

        String textureType = custparm.get("extendedtexture");
        if ((textureType != null) && (textureType.equals("true"))) {
            extendedTexture = true;
        } else {
            extendedTexture = false;
        }
        
        /* Build step meshes */
        for(int i = 0; i < 8; i++) {
            stepmeshes[i] = buildStepMeshes(rpf, i);   
            step_1_4_meshes[i] = buildCornerStepMeshes(rpf, i);   
            step_3_4_meshes[i] = buildIntCornerStepMeshes(rpf, i);   
        }
        textindex = custparm.get("textureindex");
        if(textindex != null) {
            String cnt = custparm.get("texturecnt");
            if(cnt != null) 
                textsetcnt = Integer.parseInt(cnt);
            else
                textsetcnt = 16;
            tilefields = new String[] { textindex };
            texturemap = new String[textsetcnt];
            for (int i = 0; i < textsetcnt; i++) {
                texturemap[i] = custparm.get("textmap" + i);
                if (texturemap[i] == null) {
                    texturemap[i] = Integer.toString(i);
                }
            }
        }
        return true;
    }

    @Override
    public int getMaximumTextureCount() {
        if(textsetcnt == 0){
            if (extendedTexture) {
                return 18;
            } else {
                return 6;
            }
        } else { 
            return textsetcnt;
        }
    }
    
    @Override
    public String[] getTileEntityFieldsNeeded() {
        return tilefields;
    }
    
    private RenderPatch[] buildStepMeshes(RenderPatchFactory rpf, int dat) {
        ArrayList<RenderPatch> list = new ArrayList<RenderPatch>();
        ArrayList<RenderPatch> rotatedList = new ArrayList<RenderPatch>();
        boolean inverted = (dat & 0x4) != 0;
        int rotate = dat & 0x3;
        int xrot = 0;
        int yrot = 0;
        int zrot = 0;

        int[] patchlist = {TEX_INCLINE1,TEX_TOP,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_TOP,TEX_TOP_INCLINE,TEX_TOP,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_TOP,TEX_TOP,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_INCLINE,TEX_INCLINE1,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE1,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE1,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_INCLINE1,TEX_TOP_INCLINE,TEX_TOP_INCLINE};
        
        list.add(rpf.getPatch(1.220018, 0.779585, 0.000000, 1.220018, 0.779585, 1.000000, 0.219904, -0.220529, 0.000000, 0.000000, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE1));
        list.add(rpf.getPatch(-0.000625, 0.000000, 0.000000, -0.000625, 0.000000, 1.000000, 0.999489, 1.000114, 0.000000, 0.000000, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP));
        list.add(rpf.getPatch(0.514326, 1.485278, 1.000000, 1.220018, 0.779585, 1.000000, 0.514326, 1.485278, 0.000000, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.705068, -0.705693, 1.000000, -0.000625, -0.000000, 1.000000, 0.705068, -0.705693, 0.000000, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.087321, -0.087946, 1.313125, 0.087321, -0.087946, 0.311125, 1.087436, 0.912168, 1.313125, 0.000000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP));
        list.add(rpf.getPatch(-0.000625, 0.000000, 1.313125, 0.702946, -0.703571, 1.313125, 0.999489, 1.000114, 1.313125, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(-0.000625, 0.000000, 1.000000, -0.000625, 0.000000, 2.002000, 0.999489, 1.000114, 1.000000, 0.000000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP));
        list.add(rpf.getPatch(0.383864, 1.615739, 2.002000, 1.087436, 0.912168, 2.002000, 0.383864, 1.615739, 1.000000, 0.875000, 1.000000, 0.687500, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.702946, -0.703571, 2.002000, -0.000625, -0.000000, 2.002000, 0.702946, -0.703571, 1.000000, 0.875000, 1.000000, 0.687500, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.087321, -0.087946, -0.312500, -0.612714, 0.612089, -0.312500, 1.087436, 0.912168, -0.312500, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.087321, -0.087946, 0.687500, 0.087321, -0.087946, -0.312500, 1.087436, 0.912168, 0.687500, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP));
        list.add(rpf.getPatch(-0.000183, -0.000442, -1.000000, -0.000183, -0.000442, 0.000000, 0.999931, 0.999672, -1.000000, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP));
        list.add(rpf.getPatch(0.527407, 1.472196, 0.000000, 1.087436, 0.912168, 0.000000, 0.527407, 1.472196, -1.000000, 0.843750, 1.000000, 0.000000, 0.312500, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.699853, -0.700478, 0.000000, -0.000183, -0.000442, 0.000000, 0.699853, -0.700478, -1.000000, 0.875000, 1.000000, 0.000000, 0.312500, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(1.217538, 0.781806, -0.124375, 1.924645, 0.074699, -0.124375, 0.216982, -0.218750, -0.124375, 0.000000, 0.400000, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(1.500381, 0.498963, -0.124375, 1.500381, 0.498963, 1.208958, 0.499825, -0.501593, -0.124375, 0.000000, 0.187500, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE1));
        list.add(rpf.getPatch(0.216982, -0.218750, 0.125625, 0.924089, -0.925857, 0.125625, 1.217538, 0.781806, 0.125625, 0.000000, 0.400000, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(1.217538, 0.781806, 1.204529, 1.924645, 0.074699, 1.204529, 1.217538, 0.781806, -0.124375, 0.000000, 0.400000, 0.811875, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(0.216982, -0.218750, -1.207708, 0.924089, -0.925857, -1.207708, 0.216982, -0.218750, 0.125625, 0.000000, 0.400000, 0.812500, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(1.000000, 0.593750, 1.000000, 1.000000, -0.523897, 1.000000, 1.000000, 0.593750, 0.000000, 0.000000, 0.531250, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(0.437500, 0.000000, 0.000000, 1.437500, 0.000000, 0.000000, 0.437500, 0.000000, 1.000000, 0.000000, 0.562500, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(1.217538, 0.781806, 0.875000, 1.924645, 0.074699, 0.875000, 0.216982, -0.218750, 0.875000, 0.000000, 0.400000, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(1.500381, 0.498963, 0.875000, 1.500381, 0.498963, 2.211667, 0.499825, -0.501593, 0.875000, 0.000000, 0.187500, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE1));
        list.add(rpf.getPatch(0.216982, -0.218750, 1.125625, 0.924089, -0.925857, 1.125625, 1.217538, 0.781806, 1.125625, 0.000000, 0.400000, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(1.217538, 0.781806, 2.207226, 1.924645, 0.074699, 2.207226, 1.217538, 0.781806, 0.875000, 0.000000, 0.400000, 0.811875, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(0.216982, -0.218750, -0.206601, 0.924089, -0.925857, -0.206601, 0.216982, -0.218750, 1.125625, 0.000000, 0.400000, 0.811875, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(0.218315, -0.220083, 1.250625, 0.218315, -0.220083, 0.448625, 1.218871, 0.780474, 1.250625, 0.000000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE1));
        list.add(rpf.getPatch(-0.002656, 0.000888, 1.250625, 0.704451, -0.706218, 1.250625, 0.997900, 1.001444, 1.250625, 0.125000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.600152, 1.399192, 1.802000, 1.307259, 0.692085, 1.802000, 0.600152, 1.399192, 1.000000, 0.687500, 0.875000, 0.687500, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.704451, -0.706218, 1.802000, -0.002656, 0.000888, 1.802000, 0.704451, -0.706218, 1.000000, 0.687500, 0.875000, 0.687500, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.306703, -0.308471, -0.250000, -0.400404, 0.398636, -0.250000, 1.307259, 0.692085, -0.250000, 0.125000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.218315, -0.220083, 0.550000, 0.218315, -0.220083, -0.250000, 1.218871, 0.780474, 0.550000, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE1));
        list.add(rpf.getPatch(0.600152, 1.399192, 0.000000, 1.307259, 0.692085, 0.000000, 0.600152, 1.399192, -0.800000, 0.687500, 0.875000, 0.000000, 0.312500, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.704451, -0.706218, 0.000000, -0.002656, 0.000888, 0.000000, 0.704451, -0.706218, -0.800000, 0.687500, 0.875000, 0.000000, 0.312500, SideVisible.TOP, TEX_TOP_INCLINE));


        switch(rotate) {
            case 0:
                yrot = 0;
                if (inverted) {
                    zrot = 180;
                }
                break;
            case 1:
                if (inverted) {
                    zrot = 180;
                }
                yrot = 180;
                break;
            case 2:
                if (inverted) {
                    xrot = 180;
                }
                yrot = 90;
                break;
            case 3:
                if (inverted) {
                    xrot = 180;
                }
                yrot = 270;
                break;
        }

        for (int i=0; i<list.size(); i++) {
            rotatedList.add(rpf.getRotatedPatch(list.get(i), xrot, yrot, zrot, patchlist[i]));
        } 

        return rotatedList.toArray(new RenderPatch[rotatedList.size()]);
    }

    private RenderPatch[] buildCornerStepMeshes(RenderPatchFactory rpf, int dat) {
        ArrayList<RenderPatch> list = new ArrayList<RenderPatch>();
        ArrayList<RenderPatch> rotatedList = new ArrayList<RenderPatch>();
        boolean inverted = (dat & 0x4) != 0;
        int rotate = dat & 0x3;
        int xrot = 0;
        int yrot = 0;
        int zrot = 0;

        int[] patchlist = {TEX_INCLINE,TEX_TOP_OUTER,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_TOP_OUTER1,TEX_INCLINE,TEX_INCLINE,TEX_TOP_OUTER1,TEX_TOP_OUTER};
        list.add(rpf.getPatch(0.000000, 0.029515, 0.471390, 0.000000, -0.770930, -0.329054, 1.000000, 0.029515, 0.471390, 0.000000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(1.000000, 0.000347, 0.000281, 0.000000, 0.000347, 0.000281, 1.000000, 1.000903, 1.000837, 0.000000, 1.000000, 0.000000, 0.250000, SideVisible.TOP, TEX_TOP_OUTER));
        list.add(rpf.getPatch(0.000000, -0.706760, 0.707387, 0.000000, 0.000347, 0.000281, 1.000000, -0.706760, 0.707387, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.707107, -0.707107, 1.000000, 0.000000, 0.000000, 1.000000, 0.707107, -0.707107, 0.000000, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 1.000000, 1.000556, 1.000556, 0.000000, 0.000000, 1.000000, 0.000000, 0.250000, SideVisible.TOP, TEX_TOP_OUTER1));
        list.add(rpf.getPatch(0.471110, 0.029168, 1.000000, -0.329335, -0.771277, 1.000000, 0.471110, 0.029168, 0.000000, 0.000000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(-0.384615, 0.000000, 0.048077, 1.000000, 0.000000, 0.048077, -0.384615, 0.000000, 1.432692, 0.593750, 1.000000, 0.281250, 0.687500, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(-0.000139, -0.000139, 0.000000, -0.000139, -0.000139, 1.000000, 1.000417, 1.000417, 0.000000, 0.000000, 1.000000, 0.250000, 1.000000, SideVisible.TOP, TEX_TOP_OUTER1));
        list.add(rpf.getPatch(1.000000, 0.000307, -0.000585, 0.000000, 0.000307, -0.000585, 1.000000, 1.000863, 0.999971, 0.000000, 1.000000, 0.250000, 1.000000, SideVisible.TOP, TEX_TOP_OUTER));
        
        switch(rotate) {
            case 0:
                yrot = 270;
                if (inverted) {
                    zrot = 180;
                }
                break;
            case 1:
                if (inverted) {
                    zrot = 180;
                }
                yrot = 180;
                break;
            case 2:
                if (inverted) {
                    xrot = 180;
                }
                yrot = 90;
                break;
            case 3:
                if (inverted) {
                    xrot = 180;
                }
                yrot = 0;
                break;
        }

        for (int i=0; i<list.size(); i++) {
            rotatedList.add(rpf.getRotatedPatch(list.get(i), xrot, yrot, zrot, patchlist[i]));
        } 

        return rotatedList.toArray(new RenderPatch[rotatedList.size()]);
    }

    private RenderPatch[] buildIntCornerStepMeshes(RenderPatchFactory rpf, int dat) {
        ArrayList<RenderPatch> list = new ArrayList<RenderPatch>();
        ArrayList<RenderPatch> rotatedList = new ArrayList<RenderPatch>();
        boolean inverted = (dat & 0x4) != 0;
        int rotate = dat & 0x3;
        int xrot = 0;
        int yrot = 0;
        int zrot = 0;
        
        int[] patchlist = {TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_INCLINE,TEX_TOP,TEX_INCLINE1,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_TOP,TEX_TOP,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE1,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_INCLINE1,TEX_INCLINE1,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE,TEX_INCLINE1,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_TOP,TEX_TOP,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_INCLINE1,TEX_TOP,TEX_TOP_INCLINE,TEX_TOP_INCLINE,TEX_INCLINE};
        
        list.add(rpf.getPatch(0.706483, -0.707107, 1.000000, -0.000624, 0.000000, 1.000000, 0.706483, -0.707107, 0.000000, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.513796, 1.486692, 1.000000, 1.220903, 0.779585, 1.000000, 0.513796, 1.486692, 0.000000, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(-0.000624, -0.000000, 1.000000, 0.706483, -0.707107, 1.000000, 0.999932, 1.000556, 1.000000, 0.000000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(-0.000624, -0.000000, 0.000000, -0.000624, -0.000000, 1.000000, 0.999932, 1.000556, 0.000000, 0.000000, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP));
        list.add(rpf.getPatch(1.220903, 0.779585, 0.000000, 1.220903, 0.779585, 1.000000, 0.220347, -0.220971, 0.000000, 0.000000, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE1));
        list.add(rpf.getPatch(0.706483, -0.707107, 0.689500, -0.000624, 0.000000, 0.689500, 0.706483, -0.707107, -0.312500, 0.875000, 1.000000, 0.687500, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.381213, 1.619275, 0.689500, 1.088320, 0.912168, 0.689500, 0.381213, 1.619275, -0.312500, 0.875000, 1.000000, 0.687500, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.087764, -0.088388, -0.312500, -0.619343, 0.618718, -0.312500, 1.088320, 0.912168, -0.312500, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(-0.000624, -0.000000, -0.312500, -0.000624, -0.000000, 0.689500, 0.999932, 1.000556, -0.312500, 0.000000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP));
        list.add(rpf.getPatch(0.087764, -0.088388, 0.000625, 0.087764, -0.088388, -1.001375, 1.088320, 0.912168, 0.000625, 0.000000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP));
        list.add(rpf.getPatch(-0.499687, 0.000000, -1.667083, 2.168646, 0.000000, -1.667083, -0.499687, 0.000000, 1.001250, 0.187500, 0.562500, 0.625000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(0.000625, 0.593750, 1.001250, 0.000625, -0.461806, 1.001250, 1.001250, 0.593750, 1.001250, 0.000000, 0.562500, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(1.001250, 0.593750, 1.001250, 1.001250, -0.461806, 1.001250, 1.001250, 0.593750, 0.000625, 0.000000, 0.562500, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(0.217500, -0.218752, -1.206601, 0.924607, -0.925859, -1.206601, 0.217500, -0.218752, 0.125625, 0.000000, 0.400000, 0.811875, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(1.218057, 0.781804, 1.207226, 1.925163, 0.074697, 1.207226, 1.218057, 0.781804, -0.125000, 0.000000, 0.400000, 0.811875, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(1.218057, 0.781804, -0.125000, 1.925163, 0.074697, -0.125000, 0.217500, -0.218752, -0.125000, 0.000000, 0.400000, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(0.217500, -0.218752, 0.125625, 0.924607, -0.925859, 0.125625, 1.218057, 0.781804, 0.125625, 0.000000, 0.400000, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(1.500899, 0.498961, -0.125000, 1.500899, 0.498961, 1.211667, 0.500343, -0.501595, -0.125000, 0.000000, 0.187500, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE1));
        list.add(rpf.getPatch(0.704967, -0.706218, 0.552000, -0.002140, 0.000888, 0.552000, 0.704967, -0.706218, -0.250000, 0.687500, 0.875000, 0.687500, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.600668, 1.399192, 0.552000, 1.307775, 0.692085, 0.552000, 0.600668, 1.399192, -0.250000, 0.687500, 0.875000, 0.687500, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.307219, -0.308471, -0.250000, -0.399888, 0.398636, -0.250000, 1.307775, 0.692085, -0.250000, 0.125000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.218831, -0.220083, 0.000625, 0.218831, -0.220083, -0.801375, 1.219387, 0.780474, 0.000625, 0.000000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE1));
        list.add(rpf.getPatch(-0.125000, -0.499376, 0.500414, 1.208333, -0.499376, 0.500414, -0.125000, 0.501180, 1.500970, 0.000000, 0.187500, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE1));
        list.add(rpf.getPatch(-1.203904, -0.499376, 0.500414, -1.203904, 0.207731, -0.206693, 0.125000, -0.499376, 0.500414, 0.000000, 0.400000, 0.811875, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(-1.208333, 0.784023, 1.218127, -1.208333, 0.076916, 1.925234, 0.125000, 0.784023, 1.218127, 0.000000, 0.400000, 0.812500, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(-0.125000, -0.216533, 0.217571, -0.125000, -0.923640, 0.924678, -0.125000, 0.784023, 1.218127, 0.000000, 0.400000, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(0.125000, 0.784023, 1.218127, 0.125000, 0.076916, 1.925234, 0.125000, -0.216533, 0.217571, 0.000000, 0.400000, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));
        list.add(rpf.getPatch(0.550625, 0.781809, 1.218680, -0.249375, 0.781809, 1.218680, 0.550625, -0.218747, 0.218124, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE1));
        list.add(rpf.getPatch(-0.249375, -0.704883, 0.704260, -0.249375, 0.002224, -0.002847, 0.550625, -0.704883, 0.704260, 0.687500, 0.875000, 0.000000, 0.312500, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.000625, 0.295673, 1.704816, 0.000625, 1.002780, 0.997709, -0.799375, 0.295673, 1.704816, 0.687500, 0.875000, 0.000000, 0.312500, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(-0.249375, 0.693421, 1.307068, -0.249375, 1.400527, 0.599961, -0.249375, -0.307135, 0.306512, 0.125000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.688125, 0.913057, 1.087432, -0.311875, 0.913057, 1.087432, 0.688125, -0.087499, 0.086876, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP));
        list.add(rpf.getPatch(-0.999375, 1.001445, 0.999044, 0.000625, 1.001445, 0.999044, -0.999375, 0.000889, -0.001513, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP));
        list.add(rpf.getPatch(-0.311875, -0.564796, 0.564173, -0.311875, 0.000889, -0.001513, 0.688125, -0.564796, 0.564173, 0.843750, 1.000000, 0.000000, 0.312500, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.000625, 0.294338, 1.706150, 0.000625, 1.001445, 0.999044, -0.999375, 0.294338, 1.706150, 0.875000, 1.000000, 0.000000, 0.312500, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(-0.311875, 0.913057, 1.087432, -0.311875, 1.620164, 0.380325, -0.311875, -0.087499, 0.086876, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(0.000625, -0.221527, 0.219790, 1.000625, -0.221527, 0.219790, 0.000625, 0.779029, 1.220346, 0.000000, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE1));
        list.add(rpf.getPatch(0.000625, 1.000000, 0.999375, 1.000625, 1.000000, 0.999375, 0.000625, -0.000556, -0.001181, 0.000000, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP));
        list.add(rpf.getPatch(0.000625, -0.707663, 0.705926, 0.000625, -0.000556, -0.001181, 1.000625, -0.707663, 0.705926, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(1.000625, 0.292893, 1.706482, 1.000625, 1.000000, 0.999375, 0.000625, 0.292893, 1.706482, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_TOP_INCLINE));
        list.add(rpf.getPatch(1.000625, 1.000000, 0.999375, 1.000625, 0.292893, 1.706482, 1.000625, -0.000556, -0.001181, 0.000000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_INCLINE));

        switch(rotate) {
            case 0:
                yrot = 270;
                if (inverted) {
                    zrot = 180;
                }
                break;
            case 1:
                if (inverted) {
                    zrot = 180;
                }
                yrot = 0;
                break;
            case 2:
                if (inverted) {
                    xrot = 180;
                }
                yrot = 180;
                break;
            case 3:
                if (inverted) {
                    xrot = 180;
                }
                yrot = 90;
                break;
        }

        for (int i=0; i<list.size(); i++) {
            rotatedList.add(rpf.getRotatedPatch(list.get(i), xrot, yrot, zrot, patchlist[i]));
        }

        return rotatedList.toArray(new RenderPatch[rotatedList.size()]);
    }

    //  Steps
    // 0 = up to east
    // 1 = up to west
    // 2 = up to south
    // 3 = up to north
    //  Corners
    // 0 = NE
    // 1 = NW
    // 2 = SW
    // 3 = SE
    //  Interior Corners
    // 0 = open to SW
    // 1 = open to NW
    // 2 = open to SE
    // 3 = open to NE
    private static final int off_x[] = { 1, -1, 0, 0, 1, -1, 0, 0 };
    private static final int off_z[] = { 0, 0, 1, -1, 0, 0, 1, -1 };
    private static final int match1[] = { 2, 3, 0, 1, 6, 7, 4, 5 };
    private static final int corner1[] = { 3, 1, 3, 1, 7, 5, 7, 5 };
    private static final int icorner1[] = { 1, 2, 1, 2, 5, 6, 5, 6 };
    private static final int match2[] = { 3, 2, 1, 0, 7, 6, 5, 4 };
    private static final int corner2[] = { 0, 2, 2, 0, 4, 6, 6, 4 };
    private static final int icorner2[] = { 0, 3, 3, 0, 4, 7, 7, 4 };
    
    @Override
    public RenderPatch[] getRenderPatchList(MapDataContext ctx) {
        RenderPatch[] rp = getBaseRenderPatchList(ctx);
        if(textindex != null) {
            int idx = 0;
            Object o = ctx.getBlockTileEntityField(textindex);
            if(o instanceof Number) {
                idx = ((Number)o).intValue();
            }
            else if (o instanceof String) {
                String os = (String) o;
                for (int i = 0; i < texturemap.length; i++) {
                    if (os.equals(texturemap[i])) {
                        idx = i;
                        break;
                    }
                }
            }
            if((idx < 0) || (idx >= textsetcnt)) {
                idx = 0;
            }
            RenderPatch[] rp2 = new RenderPatch[rp.length];
            for(int i = 0; i < rp.length; i++) {
                rp2[i] = ctx.getPatchFactory().getRotatedPatch(rp[i], 0, 0, 0, idx);
            }
            return rp2;
        }
        else {
            return rp;
        }
    }
    
    private RenderPatch[] getBaseRenderPatchList(MapDataContext ctx) {
        int data = ctx.getBlockType().stateIndex & 0x07;   /* Get block data */
        /* Check block behind stair */
        DynmapBlockState corner = ctx.getBlockTypeAt(off_x[data], 0, off_z[data]);
        if (stair_ids.get(corner.globalStateIndex)) {   /* If it is a stair */
            int cornerdat = corner.stateIndex & 0x07;
            if(cornerdat == match1[data]) {    /* If right orientation */
                /* Make sure we don't have matching stair to side */
                DynmapBlockState side = ctx.getBlockTypeAt(-off_x[cornerdat], 0, -off_z[cornerdat]);
                if((!stair_ids.get(side.globalStateIndex)) || ((side.stateIndex & 0x07) != data)) {
                    return step_1_4_meshes[corner1[data]];
                }
            }
            else if(cornerdat == match2[data]) {   /* If other orientation */
                /* Make sure we don't have matching stair to side */
                DynmapBlockState side = ctx.getBlockTypeAt(-off_x[cornerdat], 0, -off_z[cornerdat]);
                if((!stair_ids.get(side.globalStateIndex)) || ((side.stateIndex & 0x07) != data)) {
                    return step_1_4_meshes[corner2[data]];
                }
            }
        }
        /* Check block in front of stair */
        corner = ctx.getBlockTypeAt(-off_x[data], 0, -off_z[data]);
        if(stair_ids.get(corner.globalStateIndex)) {   /* If it is a stair */
            int cornerdat = corner.stateIndex & 0x07;
            if(cornerdat == match1[data]) {    /* If right orientation */
                /* Make sure we don't have matching stair to side */
                DynmapBlockState side = ctx.getBlockTypeAt(off_x[cornerdat], 0, off_z[cornerdat]);
                if((!stair_ids.get(side.globalStateIndex)) || ((side.stateIndex & 0x07) != data)) {
                    return step_3_4_meshes[icorner1[data]];
                }
            }
            else if(cornerdat == match2[data]) {   /* If other orientation */
                /* Make sure we don't have matching stair to side */
                DynmapBlockState side = ctx.getBlockTypeAt(off_x[cornerdat], 0, off_z[cornerdat]);
                if((!stair_ids.get(side.globalStateIndex)) || ((side.stateIndex & 0x07) != data)) {
                    return step_3_4_meshes[icorner2[data]];
                }
            }
        }
        
        return stepmeshes[data];
    }    
}
