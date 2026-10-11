package dev.sxmurxy.mre.utils;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.sxmurxy.mre.providers.ResourceProvider;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;

public final class CRenderPipelines {

    public static final BindGroupLayout PARAMS = BindGroupLayout.builder()
        .withUniform("MreParams", UniformType.UNIFORM_BUFFER).build();

    private static RenderPipeline create(String name, VertexFormat format, boolean sampler) {
        RenderPipeline.Builder b = RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withBindGroupLayout(PARAMS)
            .withLocation(ResourceProvider.id("pipeline/" + name))
            .withVertexShader(ResourceProvider.getShaderIdentifier(name))
            .withFragmentShader(ResourceProvider.getShaderIdentifier(name))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withVertexBinding(0, format)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withCull(false);
        if (sampler) {
            b.withBindGroupLayout(BindGroupLayouts.SAMPLER0);
        }

        return b.build();
    }

    public static final RenderPipeline RECTANGLE_PIPLINE = create("rectangle", DefaultVertexFormat.POSITION_COLOR, false);
    public static final RenderPipeline BORDER_PIPLINE = create("border", DefaultVertexFormat.POSITION_COLOR, false);
    public static final RenderPipeline TEXTURE_PIPLINE = create("texture", DefaultVertexFormat.POSITION_TEX_COLOR, true);
    public static final RenderPipeline MSDF_FONT_PIPLINE = create("msdf_font", DefaultVertexFormat.POSITION_TEX_COLOR, true);
    public static final RenderPipeline BLUR_PIPLINE = create("blur", DefaultVertexFormat.POSITION_COLOR, true);
    public static final RenderPipeline LIQUID_GLASS_PIPLINE = create("liquidglass", DefaultVertexFormat.POSITION_COLOR, true);

}
