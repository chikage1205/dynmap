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

public class CocricotMonoStringerBlockRenderer extends CustomRenderer {
    private static final int TEX_DEF0 = 0;
    private static final int TEX_COR0 = 1;
    private static final int TEX_ICOR0 = 2;
    private static final int TEX_ICOR1 = 3;
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

        int[] patchlist = {TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0,TEX_DEF0};
        
        list.add(rpf.getPatch(1.531250, 1.225000, 0.000000, 1.531250, 0.425000, 0.000000, 0.531250, 1.225000, 0.000000, 0.281250, 0.437500, 0.531250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.000000, 0.562500, 0.000000, 1.000000, 1.562500, 0.000000, 1.000000, 0.562500, 1.000000, 0.312500, 0.437500, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.000000, 1.225000, 1.000000, 0.000000, 0.425000, 1.000000, 1.000000, 1.225000, 1.000000, 0.281250, 0.437500, 0.531250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.531250, 0.562500, 1.000000, 0.531250, 1.562500, 1.000000, 0.531250, 0.562500, 0.000000, 0.312500, 0.437500, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.531250, 1.000000, 1.000000, 1.531250, 1.000000, 1.000000, 0.531250, 1.000000, 0.000000, 0.000000, 0.468750, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.531250, 0.875000, 0.000000, 1.531250, 0.875000, 0.000000, 0.531250, 0.875000, 1.000000, 0.000000, 0.468750, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.491882, 1.508350, 0.406250, 1.198989, 0.801244, 0.406250, -0.508232, 0.508236, 0.406250, 0.718750, 0.968750, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.176892, 0.823341, -0.343750, 1.176892, 0.823341, 0.656250, 0.176778, -0.176773, -0.343750, 0.750000, 0.937500, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.685125, 0.315108, 0.593750, 0.978018, 1.022214, 0.593750, 0.685011, -0.685006, 0.593750, 0.718750, 0.968750, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.000001, 0.000003, -0.343750, 0.000001, 0.000003, 0.656250, 1.000115, 1.000117, -0.343750, 0.750000, 0.937500, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.942924, 0.057308, 1.343750, 1.942924, 0.057308, 0.343750, 1.000115, 1.000117, 1.343750, 0.750000, 0.937500, 0.812500, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.000001, 0.000003, 1.343750, 0.000001, 0.000003, 0.343750, 0.942810, -0.942806, 1.343750, 0.750000, 0.937500, 0.000000, 0.187500, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.741667, 0.537500, 0.000000, 0.741667, -0.262500, 0.000000, -0.125000, 0.537500, 0.000000, 0.281250, 0.437500, 0.531250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.281250, -0.125000, 0.000000, 0.281250, 0.875000, 0.000000, 0.281250, -0.125000, 1.000000, 0.312500, 0.437500, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(-0.585417, 0.537500, 1.000000, -0.585417, -0.262500, 1.000000, 0.281250, 0.537500, 1.000000, 0.281250, 0.437500, 0.531250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(-0.125000, -0.125000, 1.000000, -0.125000, 0.875000, 1.000000, -0.125000, -0.125000, 0.000000, 0.312500, 0.437500, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(-0.125000, 0.312500, 1.000000, 0.741667, 0.312500, 1.000000, -0.125000, 0.312500, 0.000000, 0.000000, 0.468750, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(-0.125000, 0.187500, 0.000000, 0.741667, 0.187500, 0.000000, -0.125000, 0.187500, 1.000000, 0.000000, 0.468750, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.187500, 0.881250, 0.000000, 1.187500, 0.081250, 0.000000, 0.187500, 0.881250, 0.000000, 0.281250, 0.437500, 0.531250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.656250, 0.218750, 0.000000, 0.656250, 1.218750, 0.000000, 0.656250, 0.218750, 1.000000, 0.312500, 0.437500, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(-0.343750, 0.881250, 1.000000, -0.343750, 0.081250, 1.000000, 0.656250, 0.881250, 1.000000, 0.281250, 0.437500, 0.531250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.187500, 0.218750, 1.000000, 0.187500, 1.218750, 1.000000, 0.187500, 0.218750, 0.000000, 0.312500, 0.437500, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.187500, 0.656250, 1.000000, 1.187500, 0.656250, 1.000000, 0.187500, 0.656250, 0.000000, 0.000000, 0.468750, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(0.187500, 0.531250, 0.000000, 1.187500, 0.531250, 0.000000, 0.187500, 0.531250, 1.000000, 0.000000, 0.468750, 0.000000, 1.000000, SideVisible.TOP, TEX_DEF0));


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

        int[] patchlist = {TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0,TEX_COR0};
        list.add(rpf.getPatch(1.000000, 0.875000, 0.531250, 1.681394, 0.875000, 1.212644, 0.531542, 0.875000, 0.999708, 0.000000, 0.343750, 0.000000, 1.000000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(0.531542, 0.999375, 0.999708, 1.212936, 0.999375, 1.681102, 1.000000, 0.999375, 0.531250, 0.000000, 0.343750, 0.000000, 1.000000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(1.000000, 1.870000, 0.531250, 1.681394, 1.870000, 1.212644, 1.000000, 0.875000, 0.531250, 0.000000, 0.343750, 0.875000, 1.000000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(0.531542, 0.875000, 0.999708, 1.212936, 0.875000, 1.681102, 0.531542, 1.870000, 0.999708, 0.000000, 0.343750, 0.000000, 0.125000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(0.531542, 0.875000, 0.999708, 0.531542, 1.870000, 0.999708, 1.000000, 0.875000, 0.531250, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(1.234229, 0.875000, 0.765479, 1.234229, 1.870000, 0.765479, 0.765771, 0.875000, 1.233937, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(1.000000, 0.531250, 0.187500, 1.681394, 0.531250, 0.868894, 0.186827, 0.531250, 1.000673, 0.000000, 0.343750, 0.000000, 1.000000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(0.186827, 0.655625, 1.000673, 0.868221, 0.655625, 1.682067, 1.000000, 0.655625, 0.187500, 0.000000, 0.343750, 0.000000, 1.000000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(1.000000, 1.526250, 0.187500, 1.681394, 1.526250, 0.868894, 1.000000, 0.531250, 0.187500, 0.000000, 0.343750, 0.875000, 1.000000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(0.186827, 0.531250, 1.000673, 0.868221, 0.531250, 1.682067, 0.186827, 1.526250, 1.000673, 0.000000, 0.343750, 0.000000, 0.125000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(0.186827, 0.531250, 1.000673, 0.186827, 1.526250, 1.000673, 1.000000, 0.531250, 0.187500, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(1.234229, 0.531250, 0.421729, 1.234229, 1.526250, 0.421729, 0.421056, 0.531250, 1.234902, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(1.000000, 0.187500, -0.125000, 1.681394, 0.187500, 0.556394, -0.125626, 0.187500, 1.000626, 0.000000, 0.343750, 0.000000, 1.000000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(-0.125626, 0.311875, 1.000626, 0.555768, 0.311875, 1.682019, 1.000000, 0.311875, -0.125000, 0.000000, 0.343750, 0.000000, 1.000000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(1.000000, 1.182500, -0.125000, 1.681394, 1.182500, 0.556394, 1.000000, 0.187500, -0.125000, 0.000000, 0.343750, 0.875000, 1.000000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(-0.125626, 0.187500, 1.000626, 0.555768, 0.187500, 1.682019, -0.125626, 1.182500, 1.000626, 0.000000, 0.343750, 0.000000, 0.125000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(-0.125626, 0.187500, 1.000626, -0.125626, 1.182500, 1.000626, 1.000000, 0.187500, -0.125000, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_COR0));
        list.add(rpf.getPatch(1.234229, 0.187500, 0.109229, 1.234229, 1.182500, 0.109229, 0.108604, 0.187500, 1.234855, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_COR0));
        
        switch(rotate) {
            case 0:
                if (inverted) {
                    xrot = 180;
                }else{
                    yrot = 270;
                }
                break;
            case 1:
                if (inverted) {
                    xrot = 180;
                    yrot = 270;
                }else{
                    yrot = 180;
                }
                break;
            case 2:
                if (inverted) {
                    xrot = 180;
                    yrot = 180;
                }else{
                    yrot = 90;
                }
                break;
            case 3:
                if (inverted) {
                    xrot = 180;
                    yrot = 90;
                }else{
                    
                }
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
        
        int[] patchlist = {TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR0,TEX_ICOR1,TEX_ICOR1,TEX_ICOR1,TEX_ICOR1,TEX_ICOR1};
        list.add(rpf.getPatch(0.765625, 0.875000, -0.234396, 1.447019, 0.875000, 0.446998, -0.234489, 0.875000, 0.765718, 0.000000, 0.343750, 0.000000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(-0.234489, 0.998750, 0.765718, 0.446905, 0.998750, 1.447112, 0.765625, 0.998750, -0.234396, 0.000000, 0.343750, 0.000000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(0.765625, 1.865000, -0.234396, 1.447019, 1.865000, 0.446998, 0.765625, 0.875000, -0.234396, 0.000000, 0.343750, 0.875000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(-0.234489, 0.875000, 0.765718, 0.446905, 0.875000, 1.447112, -0.234489, 1.865000, 0.765718, 0.000000, 0.343750, 0.000000, 0.125000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(-0.234489, 0.875000, 0.765718, -0.234489, 1.865000, 0.765718, 0.765625, 0.875000, -0.234396, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(0.999854, 0.875000, -0.000167, 0.999854, 1.865000, -0.000167, -0.000260, 0.875000, 0.999948, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(0.421875, 0.531250, -0.233250, 1.103269, 0.531250, 0.448144, -0.234408, 0.531250, 0.423033, 0.000000, 0.343750, 0.000000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(-0.234408, 0.655000, 0.423033, 0.446985, 0.655000, 1.104427, 0.421875, 0.655000, -0.233250, 0.000000, 0.343750, 0.000000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(0.421875, 1.521250, -0.233250, 1.103269, 1.521250, 0.448144, 0.421875, 0.531250, -0.233250, 0.000000, 0.343750, 0.875000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(-0.234408, 0.531250, 0.423033, 0.446985, 0.531250, 1.104427, -0.234408, 1.521250, 0.423033, 0.000000, 0.343750, 0.000000, 0.125000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(-0.234408, 0.531250, 0.423033, -0.234408, 1.521250, 0.423033, 0.421875, 0.531250, -0.233250, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(0.656104, 0.531250, 0.000979, 0.656104, 1.521250, 0.000979, -0.000179, 0.531250, 0.657263, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(0.112500, 0.187500, -0.237500, 0.793894, 0.187500, 0.443894, -0.233540, 0.187500, 0.108540, 0.000000, 0.343750, 0.000000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(-0.233540, 0.311250, 0.108540, 0.447853, 0.311250, 0.789934, 0.112500, 0.311250, -0.237500, 0.000000, 0.343750, 0.000000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(0.112500, 1.177500, -0.237500, 0.793894, 1.177500, 0.443894, 0.112500, 0.187500, -0.237500, 0.000000, 0.343750, 0.875000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(-0.233540, 0.187500, 0.108540, 0.447853, 0.187500, 0.789934, -0.233540, 1.177500, 0.108540, 0.000000, 0.343750, 0.000000, 0.125000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(-0.233540, 0.187500, 0.108540, -0.233540, 1.177500, 0.108540, 0.112500, 0.187500, -0.237500, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(0.346729, 0.187500, -0.003271, 0.346729, 1.177500, -0.003271, 0.000689, 0.187500, 0.342770, 0.000000, 0.125000, 0.000000, 1.000000, SideVisible.TOP, TEX_ICOR0));
        list.add(rpf.getPatch(0.000000, 0.875000, 1.000000, 1.000114, 0.875000, -0.000114, 0.707107, 0.875000, 1.707107, 0.000000, 1.000000, 0.000000, 0.718750, SideVisible.TOP, TEX_ICOR1));
        list.add(rpf.getPatch(0.707107, 0.998750, 1.707107, 1.707221, 0.998750, 0.706993, 0.000000, 0.998750, 1.000000, 0.000000, 1.000000, 0.281250, 1.000000, SideVisible.TOP, TEX_ICOR1));
        list.add(rpf.getPatch(0.508233, 0.008750, 1.508233, 1.508347, 0.008750, 0.508119, 0.508233, 0.998750, 1.508233, 0.000000, 1.000000, 0.875000, 1.000000, SideVisible.TOP, TEX_ICOR1));
        list.add(rpf.getPatch(0.000000, 0.008750, 1.000000, 0.707107, 0.008750, 1.707107, 0.000000, 0.998750, 1.000000, 0.000000, 0.718750, 0.875000, 1.000000, SideVisible.TOP, TEX_ICOR1));
        list.add(rpf.getPatch(1.707221, 0.008750, 0.706993, 1.000114, 0.008750, -0.000114, 1.707221, 0.998750, 0.706993, 0.281250, 1.000000, 0.875000, 1.000000, SideVisible.TOP, TEX_ICOR1));
        
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
