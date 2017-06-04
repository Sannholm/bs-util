package benjaminsannholm.util.opengl;

import java.nio.ByteBuffer;
import java.util.Set;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL44;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class BufferObject extends GraphicsObject
{
    private final Type type;
    private final int size;
    private ByteBuffer data;
    private final Set<Flag> flags;
    
    private BufferObject(Type type, int size, ByteBuffer data, Iterable<Flag> flags)
    {
        this.type = Preconditions.checkNotNull(type, "type");
        Preconditions.checkArgument(size > 0, "Size cannot be <= 0");
        this.size = size;
        this.data = data;
        this.flags = Sets.immutableEnumSet(Preconditions.checkNotNull(flags, "flags"));
        
        create();
    }

    public BufferObject(Type type, int size, Iterable<Flag> flags)
    {
        this(type, size, null, flags);
    }
    
    public BufferObject(Type type, int size)
    {
        this(type, size, ImmutableSet.of());
    }

    public BufferObject(Type type, ByteBuffer data, Iterable<Flag> flags)
    {
        this(type, data.remaining(), data, flags);
    }

    public BufferObject(Type type, ByteBuffer data)
    {
        this(type, data, ImmutableSet.of());
    }

    @Override
    protected void create()
    {
        setHandle(GLAPI.createBuffer());

        int flags = 0;
        for (Flag flag : getFlags())
            flags |= flag.getEnum();
        GLAPI.initBufferData(getHandle(), getType().getEnum(), getSize(), data, flags);
        data = null;
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

    public Set<Flag> getFlags()
    {
        return flags;
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

    public static enum Flag implements GLAPIEnum
    {
        MAP_READ(GL30.GL_MAP_READ_BIT),
        MAP_WRITE(GL30.GL_MAP_WRITE_BIT),
        MAP_PERSISTENT(GL44.GL_MAP_PERSISTENT_BIT),
        MAP_COHERENT(GL44.GL_MAP_COHERENT_BIT),
        DYNAMIC_STORAGE(GL44.GL_DYNAMIC_STORAGE_BIT),
        CLIENT_STORAGE(GL44.GL_CLIENT_STORAGE_BIT);

        private final int glEnum;

        private Flag(int glEnum)
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