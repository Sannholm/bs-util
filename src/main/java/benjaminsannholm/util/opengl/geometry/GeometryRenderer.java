package benjaminsannholm.util.opengl.geometry;

import java.util.List;

import benjaminsannholm.util.opengl.BufferObject;
import benjaminsannholm.util.opengl.GLAPI;
import benjaminsannholm.util.opengl.geometry.VertexFormat.VertexAttribute;

public final class GeometryRenderer
{
    // TODO: Don't use a global VAO
    private static VertexArrayObject GLOBAL_VAO;
    
    public static void render(RenderMode mode, VertexFormat format, BufferObject vbo, int start, int count)
    {
        if (GLOBAL_VAO == null)
            GLOBAL_VAO = new VertexArrayObject();
        
        GLOBAL_VAO.bind();
        vbo.bind();
        
        final List<VertexAttribute> attributes = format.getAttributes();
        for (int i = 0; i < attributes.size(); i++)
        {
            final VertexAttribute attribute = attributes.get(i);
            
            GLAPI.enableVertexArrayAttrib(GLOBAL_VAO.getHandle(), i);
            GLAPI.setVertexAttribPointer(GLOBAL_VAO.getHandle(), i, attribute.getCount(), attribute.getDataType().getEnum(), attribute.shouldNormalize(), format.getBytesPerVertex(), format.getOffset(i));
        }
        
        GLAPI.drawArrays(mode.getEnum(), start, count);
        
        for (int i = 0; i < attributes.size(); i++)
            GLAPI.disableVertexArrayAttrib(GLOBAL_VAO.getHandle(), i);
    }
}