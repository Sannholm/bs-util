package benjaminsannholm.util.opengl.geometry;

import java.nio.ByteBuffer;

import benjaminsannholm.util.opengl.BufferObject;
import benjaminsannholm.util.opengl.BufferObject.Type;
import benjaminsannholm.util.opengl.BufferObject.Usage;
import benjaminsannholm.util.opengl.geometry.VertexFormat.DataType;

public final class FullscreenQuadRenderer
{
    private static final VertexFormat FULLSCREEN_QUAD_VERTEX_FORMAT = VertexFormat.builder()
            .attribute(DataType.BYTE, 4, false) // 2D Position
            .build();

    private static BufferObject fullscreenQuadVbo;

    public static void render()
    {
        if (fullscreenQuadVbo == null)
        {
            fullscreenQuadVbo = new BufferObject(Type.ARRAY, FULLSCREEN_QUAD_VERTEX_FORMAT.getBytesPerVertex() * 4, Usage.STATIC_DRAW);
            final ByteBuffer dataBuffer = fullscreenQuadVbo.map();

            dataBuffer.put((byte)-1).put((byte)-1).put((byte)0).put((byte)0);
            dataBuffer.put((byte)1).put((byte)-1).put((byte)0).put((byte)0);
            dataBuffer.put((byte)-1).put((byte)1).put((byte)0).put((byte)0);
            dataBuffer.put((byte)1).put((byte)1).put((byte)0).put((byte)0);

            fullscreenQuadVbo.unmap();
        }

        GeometryRenderer.render(RenderMode.TRIANGLE_STRIP, FULLSCREEN_QUAD_VERTEX_FORMAT, fullscreenQuadVbo, 0, 4);
    }
}