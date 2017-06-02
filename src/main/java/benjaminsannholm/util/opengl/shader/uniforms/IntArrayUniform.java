package benjaminsannholm.util.opengl.shader.uniforms;

import java.nio.IntBuffer;
import java.util.Arrays;

import org.lwjgl.system.MemoryUtil;

import benjaminsannholm.util.opengl.GLAPI;
import benjaminsannholm.util.opengl.shader.ShaderProgram;
import benjaminsannholm.util.opengl.shader.Uniform;

public class IntArrayUniform extends Uniform<int[]>
{
    public IntArrayUniform(ShaderProgram parent, String name)
    {
        super(parent, name);
    }
    
    @Override
    protected boolean equals(int[] value1, int[] value2)
    {
        return Arrays.equals(value1, value2);
    }
    
    @Override
    protected int[] copyValue(int[] value)
    {
        return Arrays.copyOf(value, value.length);
    }
    
    @Override
    protected void upload(int[] value)
    {
        final IntBuffer buffer = MemoryUtil.memAllocInt(value.length);
        try
        {
            buffer.put(value);
            buffer.flip();
            GLAPI.setUniform1iv(getParent().getHandle(), getLocation(), buffer);
        }
        finally
        {
            MemoryUtil.memFree(buffer);
        }
    }
}