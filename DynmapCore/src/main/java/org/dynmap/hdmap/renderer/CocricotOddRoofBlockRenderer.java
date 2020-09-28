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

public class CocricotOddRoofBlockRenderer extends CustomRenderer {
    private static final int TEX_DEF0 = 0;
    private static final int TEX_DEF1 = 1;
    private static final int TEX_DEF2 = 2;
    private static final int TEX_DEF3 = 3;
    private static final int TEX_COR0 = 4;
    private static final int TEX_COR1 = 5;
    private static final int TEX_COR2 = 6;
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

        int[] patchlist = {TEX_DEF0,TEX_DEF2,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF1,TEX_DEF0,TEX_DEF1,TEX_DEF0,TEX_DEF0,TEX_DEF1,TEX_DEF1,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF1,TEX_DEF3,TEX_DEF3,TEX_DEF3,TEX_DEF3,TEX_DEF0,TEX_DEF1,TEX_DEF2,TEX_DEF1,TEX_DEF0,TEX_DEF1,TEX_DEF2,TEX_DEF1,TEX_DEF1,TEX_DEF0,TEX_DEF0,TEX_DEF1,TEX_DEF3,TEX_DEF3,TEX_DEF3,TEX_DEF3,TEX_DEF0,TEX_DEF1,TEX_DEF0,TEX_DEF1,TEX_DEF2,TEX_DEF1,TEX_DEF0,TEX_DEF0,TEX_DEF1,TEX_DEF1,TEX_DEF0,TEX_DEF2,TEX_DEF1,TEX_DEF1,TEX_DEF2,TEX_DEF0,TEX_DEF0,TEX_DEF1};
        
        list.add(rpf.getPatch(0.204833, -0.237109, 0.000000, -0.102605, 0.070329, 0.000000, 0.721249, 0.279307, 0.000000, 0.000000, 0.718750, 0.031250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.721249, 0.279307, 0.000000, 0.721249, 0.279307, 1.000000, 0.220971, -0.220971, 0.000000, 0.000000, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF2));
        list.add(rpf.getPatch(0.000000, 0.000000, 1.000000, 0.307438, -0.307438, 1.000000, 0.500278, 0.500278, 1.000000, 0.000000, 0.718750, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(-0.500278, -0.500278, 0.000000, -0.500278, -0.500278, 1.000000, 0.500278, 0.500278, 0.000000, 0.000000, 1.000000, 0.500000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.510431, -0.509874, 1.000000, 1.510431, -0.509874, 0.000000, 0.500278, 0.500278, 1.000000, 0.000000, 1.000000, 0.781250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.707107, -0.707107, 1.000000, 0.000000, 0.000000, 1.000000, 0.707107, -0.707107, 0.000000, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(-0.411890, -0.588666, 1.313125, -0.411890, -0.588666, 0.311125, 0.588666, 0.411890, 1.313125, 0.000000, 0.312500, 0.500000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(-0.743346, -0.257210, 1.313125, -0.036239, -0.964317, 1.313125, 0.257210, 0.743346, 1.313125, 0.343750, 0.468750, 0.500000, 1.000000, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(-0.500278, -0.500278, 1.000000, -0.500278, -0.500278, 2.002000, 0.500278, 0.500278, 1.000000, 0.000000, 0.312500, 0.500000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.443087, -0.442531, 2.002000, 1.443087, -0.442531, 1.000000, 0.500278, 0.500278, 2.002000, 0.687500, 1.000000, 0.906250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.707107, -0.707107, 2.002000, 0.000000, 0.000000, 2.002000, 0.707107, -0.707107, 1.000000, 0.875000, 1.000000, 0.687500, 1.000000, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(-0.168822, -0.831734, -0.312500, -0.875929, -0.124628, -0.312500, 0.831734, 0.168822, -0.312500, 0.343750, 0.468750, 0.500000, 1.000000, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(-0.411890, -0.588666, 0.687500, -0.411890, -0.588666, -0.312500, 0.588666, 0.411890, 0.687500, 0.687500, 1.000000, 0.500000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(-0.500278, -0.500278, -1.000000, -0.500278, -0.500278, 0.000000, 0.500278, 0.500278, -1.000000, 0.687500, 1.000000, 0.500000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.443087, -0.442531, 0.000000, 1.443087, -0.442531, -1.000000, 0.500278, 0.500278, 0.000000, 0.000000, 0.312500, 0.906250, 1.000000, SideVisible.TOP, TEX_DEF0)); 
        list.add(rpf.getPatch(0.707107, -0.707107, 0.000000, 0.000000, 0.000000, 0.000000, 0.707107, -0.707107, -1.000000, 0.875000, 1.000000, 0.000000, 0.312500, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(1.606372, -0.213915, 0.881250, 0.910143, -0.910143, 0.881250, 0.600709, 0.791748, 0.881250, 0.593750, 1.000000, 0.406250, 0.687500, SideVisible.TOP, TEX_DEF3));
        list.add(rpf.getPatch(0.784435, -0.218750, 1.883750, -0.724059, -1.727244, 1.883750, 0.784435, -0.218750, 0.881250, 0.000000, 0.187500, 0.750000, 1.000000, SideVisible.TOP, TEX_DEF3));
        list.add(rpf.getPatch(-0.603186, -0.213915, 1.131875, 0.093042, -0.910143, 1.131875, 0.402477, 0.791748, 1.131875, 0.593750, 1.000000, 0.406250, 0.687500, SideVisible.TOP, TEX_DEF3));
        list.add(rpf.getPatch(0.218750, -0.218750, -0.204792, 1.041565, -1.041565, -0.204792, 0.218750, -0.218750, 1.131875, 0.000000, 0.343750, 0.812500, 1.000000, SideVisible.TOP, TEX_DEF3));
        list.add(rpf.getPatch(1.409290, -0.586001, 1.802000, 1.409290, -0.586001, 1.000000, 0.414627, 0.408663, 1.802000, 0.687500, 1.000000, 0.500000, 0.781250, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.914168, -1.018956, 1.250625, 1.621275, -0.311849, 1.250625, -0.080495, -0.024293, 1.250625, 0.468750, 0.656250, 0.468750, 0.750000, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(-0.215287, 0.773412, 1.250625, -0.215287, 0.773412, 0.359514, 0.779376, -0.221251, 1.250625, 0.000000, 0.281250, 0.718750, 1.000000, SideVisible.TOP, TEX_DEF2));
        list.add(rpf.getPatch(0.293240, -0.707387, 0.248125, 1.000347, -0.000281, 0.248125, 0.293240, -0.707387, 1.250625, 0.687500, 0.875000, 0.750000, 1.000000, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(1.497679, -0.497612, 1.000000, 1.497679, -0.497612, 0.000000, 0.503015, 0.497051, 1.000000, 0.000000, 1.000000, 0.500000, 0.781250, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.536308, -0.464319, 1.000000, 1.243415, 0.242787, 1.000000, 0.256559, -0.184570, 1.000000, 0.343750, 0.656250, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(-0.115821, 0.673946, 1.000000, -0.115821, 0.673946, 0.000000, 0.779376, -0.221251, 1.000000, 0.000000, 1.000000, 0.687500, 1.000000, SideVisible.TOP, TEX_DEF2));
        list.add(rpf.getPatch(0.293240, -0.707387, 0.000000, 1.000347, -0.000281, 0.000000, 0.293240, -0.707387, 1.000000, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(-0.250926, 0.588080, -0.312500, 0.456181, 1.295187, -0.312500, 0.764044, -0.426890, -0.312500, 0.343750, 0.468750, 0.500000, 0.906250, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(1.507832, -0.507766, 0.000000, 1.507832, -0.507766, -1.000000, 0.492862, 0.507205, 0.000000, 0.000000, 0.312500, 0.500000, 0.906250, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.419444, -0.596154, -0.312500, 1.419444, -0.596154, 0.687500, 0.404474, 0.418816, -0.312500, 0.000000, 0.312500, 0.500000, 0.906250, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.293240, -0.707387, -0.312500, 1.000347, -0.000281, -0.312500, 0.293240, -0.707387, 0.687500, 0.875000, 1.000000, 0.000000, 0.312500, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(1.606369, -0.213915, -0.131250, 0.910141, -0.910144, -0.131250, 0.600707, 0.791748, -0.131250, 0.593750, 1.000000, 0.406250, 0.687500, SideVisible.TOP, TEX_DEF3));
        list.add(rpf.getPatch(0.784433, -0.218750, 0.871250, -0.724061, -1.727245, 0.871250, 0.784433, -0.218750, -0.131250, 0.000000, 0.187500, 0.750000, 1.000000, SideVisible.TOP, TEX_DEF3));
        list.add(rpf.getPatch(-0.603188, -0.213915, 0.119375, 0.093040, -0.910144, 0.119375, 0.402475, 0.791748, 0.119375, 0.593750, 1.000000, 0.406250, 0.687500, SideVisible.TOP, TEX_DEF3));
        list.add(rpf.getPatch(0.218748, -0.218750, -1.217292, 2.028941, -2.028944, -1.217292, 0.218748, -0.218750, 0.119375, 0.000000, 0.156250, 0.812500, 1.000000, SideVisible.TOP, TEX_DEF3));
        list.add(rpf.getPatch(1.507832, -0.507766, 2.002000, 1.507832, -0.507766, 1.000000, 0.492862, 0.507205, 2.002000, 0.687500, 1.000000, 0.500000, 0.906250, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.176376, -0.839222, 1.313125, 1.883483, -0.132115, 1.313125, 0.161406, 0.175748, 1.313125, 0.343750, 0.468750, 0.500000, 0.906250, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(1.419444, -0.596154, 0.311125, 1.419444, -0.596154, 1.313125, 0.404474, 0.418816, 0.311125, 0.687500, 1.000000, 0.500000, 0.906250, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.293240, -0.707387, 0.311125, 1.000347, -0.000281, 0.311125, 0.293240, -0.707387, 1.313125, 0.875000, 1.000000, 0.687500, 1.000000, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(0.220971, -0.220971, 1.250625, 0.220971, -0.220971, 0.448625, 0.632861, 0.190919, 1.250625, 0.000000, 0.312500, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF2));
        list.add(rpf.getPatch(-0.750009, -0.263873, 1.250625, -0.042902, -0.970980, 1.250625, 0.263873, 0.750009, 1.250625, 0.468750, 0.656250, 0.500000, 0.906250, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(-0.323501, -0.500278, 1.000000, -0.323501, -0.500278, 1.802000, 0.500278, 0.323501, 1.000000, 0.000000, 0.312500, 0.500000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.106370, -0.282590, 1.802000, 1.106370, -0.282590, 1.000000, 0.500278, 0.323501, 1.802000, 0.687500, 1.000000, 0.781250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.707107, -0.707107, 2.002500, 0.000000, 0.000000, 2.002500, 0.707107, -0.707107, 1.000000, 0.687500, 0.875000, 0.750000, 1.000000, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(1.709664, -0.223461, -0.250000, 1.002557, -0.930568, -0.250000, 0.715000, 0.771202, -0.250000, 0.468750, 0.656250, 0.468750, 0.750000, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(1.409290, -0.586001, 0.000000, 1.409290, -0.586001, -0.800000, 0.414627, 0.408663, 0.000000, 0.000000, 0.312500, 0.500000, 0.781250, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(-0.215287, 0.773412, 0.000000, -0.215287, 0.773412, -0.888889, 0.779376, -0.221251, 0.000000, 0.000000, 0.281250, 0.718750, 1.000000, SideVisible.TOP, TEX_DEF2));
        list.add(rpf.getPatch(0.293240, -0.707387, -0.250000, 1.000347, -0.000281, -0.250000, 0.293240, -0.707387, 0.750000, 0.687500, 0.875000, 0.000000, 0.250000, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(0.045486, -1.059368, -0.250000, -0.661621, -0.352262, -0.250000, 1.059368, -0.045486, -0.250000, 0.468750, 0.656250, 0.500000, 0.906250, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(0.220971, -0.220971, 0.550000, 0.220971, -0.220971, -0.250000, 0.632861, 0.190919, 0.550000, 0.687500, 1.000000, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF2));
        list.add(rpf.getPatch(-0.323501, -0.500278, -0.800000, -0.323501, -0.500278, 0.000000, 0.500278, 0.323501, -0.800000, 0.687500, 1.000000, 0.500000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.106370, -0.282590, 0.550000, 1.106370, -0.282590, -0.250000, 0.500278, 0.323501, 0.550000, 0.687500, 1.000000, 0.781250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.707107, -0.707107, 0.000000, 0.000000, 0.000000, 0.000000, 0.707107, -0.707107, -1.000000, 0.687500, 0.875000, 0.000000, 0.250000, SideVisible.TOP, TEX_DEF1));


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

        int[] patchlist = {TEX_COR0,TEX_COR1,TEX_COR2,TEX_COR0,TEX_COR1};
        list.add(rpf.getPatch(1.000000, 0.000347, 0.002782, 0.000000, 0.000347, 0.002782, 1.000000, 1.000903, 1.003338, 0.000000, 1.000000, 0.000000, 0.500000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 1.000000, 1.000556, 1.000556, 0.000000, 0.000000, 1.000000, 0.000000, 0.500000, SideVisible.TOP, TEX_COR1));
        list.add(rpf.getPatch(-1.461538, 0.000000, -0.692308, 1.000000, 0.000000, -0.692308, -1.461538, 0.000000, 1.769231, 0.593750, 1.000000, 0.281250, 0.687500, SideVisible.TOP, TEX_COR2));
        list.add(rpf.getPatch(1.000044, 0.000044, 1.000000, 1.000044, 0.000044, 0.000000, -0.000512, 1.000600, 1.000000, 0.000000, 0.500000, 0.000000, 0.500000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(1.500000, 0.500000, 0.499982, 0.500000, 0.500000, 0.499982, 1.500000, -0.441700, 1.441682, 0.500000, 1.000000, 0.000000, 0.531250, SideVisible.TOP, TEX_COR1));
        
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
        
        int[] patchlist = {TEX_COR0,TEX_COR1,TEX_COR2,TEX_COR0,TEX_COR1};
        list.add(rpf.getPatch(1.000000, 0.000347, 0.002782, 0.000000, 0.000347, 0.002782, 1.000000, 1.000903, 1.003338, 0.000000, 1.000000, 0.000000, 0.500000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 1.000000, 1.000556, 1.000556, 0.000000, 0.000000, 1.000000, 0.000000, 0.500000, SideVisible.TOP, TEX_COR1));
        list.add(rpf.getPatch(-1.461538, 0.000000, -0.692308, 1.000000, 0.000000, -0.692308, -1.461538, 0.000000, 1.769231, 0.593750, 1.000000, 0.281250, 0.687500, SideVisible.TOP, TEX_COR2));
        list.add(rpf.getPatch(1.000044, 0.000044, 1.000000, 1.000044, 0.000044, 0.000000, -0.000512, 1.000600, 1.000000, 0.000000, 0.500000, 0.000000, 0.500000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(1.500000, 0.500000, 0.499982, 0.500000, 0.500000, 0.499982, 1.500000, -0.441700, 1.441682, 0.500000, 1.000000, 0.000000, 0.531250, SideVisible.TOP, TEX_COR1));
        
        switch(rotate) {
            case 0:
                yrot = 90;
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
                yrot = 0;
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
