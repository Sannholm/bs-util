package benjaminsannholm.util.opengl;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import com.google.common.base.Preconditions;

public class BufferObject extends GraphicsObject
{
    private final Type type;
    private final int size;
    private final Usage usage;

    public BufferObject(Type type, int size, Usage usage)
    {
        this.type = Preconditions.checkNotNull(type, "type");
        Preconditions.checkArgument(size > 0, "Size cannot be <= 0");
        this.size = size;
        this.usage = Preconditions.checkNotNull(usage, "usage");

        create();
    }

    @Override
    protected void create()
    {
        setHandle(GLAPI.createBuffer());
        GLAPI.initBufferData(getHandle(), getType().getEnum(), getSize(), getUsage().getEnum());
    }

    @Override
    protected void destroy()
    {
        GLAPI.deleteBuffer(getHandle());
    }

    public void bind()
    {
        GLAPI.bindBuffer(getType().getEnum(), getHandle());
    }

    public void bindIndexed(int index, int offset, int length)
    {
        GLAPI.bindBufferIndexed(getHandle(), getType().getEnum(), index, offset, length);
    }

    public void bindIndexed(int index)
    {
        bindIndexed(index, 0, getSize());
    }

    public ByteBuffer map(int offset, int length, int flags)
    {
        return GLAPI.mapBuffer(getHandle(), getType().getEnum(), flags, offset, length);
    }

    public ByteBuffer map(int flags)
    {
        return map(0, getSize(), flags);
    }

    public ByteBuffer map()
    {
        return map(GL30.GL_MAP_WRITE_BIT | GL30.GL_MAP_INVALIDATE_BUFFER_BIT | GL30.GL_MAP_INVALIDATE_RANGE_BIT | GL30.GL_MAP_UNSYNCHRONIZED_BIT);
    }

    public void unmap()
    {
        GLAPI.unmapBuffer(getHandle(), getType().getEnum());
    }

    public Type getType()
    {
        return type;
    }

    public int getSize()
    {
        return size;
    }

    public Usage getUsage()
    {
        return usage;
    }
    
    public static enum Type implements GLAPIEnum
    {
        ARRAY(GL15.GL_ARRAY_BUFFER),
        SHADER_STORAGE(GL43.GL_SHADER_STORAGE_BUFFER);

        private final int glEnum;

        private Type(int glEnum)
        {
            this.glEnum = glEnum;
        }

        @Override
        public int getEnum()
        {
            return glEnum;
        }
    }

    public static enum Usage implements GLAPIEnum
    {
        STATIC_DRAW(GL15.GL_STATIC_DRAW),
        DYNAMIC_DRAW(GL15.GL_DYNAMIC_DRAW),
        STREAM_DRAW(GL15.GL_STREAM_DRAW);

        private final int glEnum;

        private Usage(int glEnum)
        {
            this.glEnum = glEnum;
        }

        @Override
        public int getEnum()
        {
            return glEnum;
        }
    }
}