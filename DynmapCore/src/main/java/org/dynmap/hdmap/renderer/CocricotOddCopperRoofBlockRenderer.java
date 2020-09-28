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

public class CocricotOddCopperRoofBlockRenderer extends CustomRenderer {
    private static final int TEX_DEF0 = 0;
    private static final int TEX_DEF1 = 1;
    private static final int TEX_COR0 = 2;
    private static final int TEX_COR1 = 3;
    private static final int TEX_COR2 = 4;
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

        int[] patchlist = {TEX_DEF0,TEX_DEF0,TEX_DEF1,TEX_DEF0,TEX_DEF0,TEX_DEF1,TEX_DEF0,TEX_DEF0};
        
        list.add(rpf.getPatch(-0.195761, -0.195761, -0.001250, -0.195761, -0.195761, 1.001250, 0.500278, 0.500278, -0.001250, 0.000000, 1.000000, 0.281250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.196317, -0.195761, 1.001250, 1.196317, -0.195761, -0.001250, 0.500278, 0.500278, 1.001250, 0.000000, 1.000000, 0.281250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.196317, -0.195761, 1.063750, 0.500278, 0.500278, 1.063750, 0.500278, -0.891800, 1.063750, 0.281250, 1.000000, 0.000000, 0.718750, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(-0.195761, -0.195761, 0.998750, -0.195761, -0.195761, 2.038750, 0.500278, 0.500278, 0.998750, 0.000000, 0.062500, 0.281250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.196317, -0.195761, 1.063750, 1.196317, -0.195761, 0.023750, 0.500278, 0.500278, 1.063750, 0.000000, 0.062500, 0.281250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(-0.195761, -0.195761, -0.063750, 0.500278, 0.500278, -0.063750, 0.500278, -0.891800, -0.063750, 0.281250, 1.000000, 0.000000, 0.718750, SideVisible.TOP, TEX_DEF1));
        list.add(rpf.getPatch(-0.195761, -0.195761, -0.063750, -0.195761, -0.195761, 0.976250, 0.500278, 0.500278, -0.063750, 0.000000, 0.062500, 0.281250, 1.000000, SideVisible.TOP, TEX_DEF0));
        list.add(rpf.getPatch(1.196317, -0.195761, 0.001250, 1.196317, -0.195761, -1.038750, 0.500278, 0.500278, 0.001250, 0.000000, 0.062500, 0.281250, 1.000000, SideVisible.TOP, TEX_DEF0));

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
