package benjaminsannholm.util.opengl.texture;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;

import com.google.common.base.Preconditions;

import benjaminsannholm.util.opengl.GLAPI;
import benjaminsannholm.util.opengl.GLAPIEnum;
import benjaminsannholm.util.opengl.GraphicsObject;

public abstract class Texture extends GraphicsObject
{
    private final Type type;
    private final Format format;
    private final int levels;
    
    private final MinificationFilter minFilter;
    private final MagnificationFilter magFilter;
    
    protected Texture(Type type, Format format, int levels, MinificationFilter minFilter, MagnificationFilter magFilter)
    {
        this.type = Preconditions.checkNotNull(type, "type");
        this.format = Preconditions.checkNotNull(format, "format");
        Preconditions.checkArgument(levels > 0, "Levels cannot be <= 0");
        this.levels = levels;
        this.minFilter = Preconditions.checkNotNull(minFilter, "minFilter");
        this.magFilter = Preconditions.checkNotNull(magFilter, "magFilter");
    }
    
    @Override
    protected void create()
    {
        setHandle(GLAPI.createTexture(type.getEnum()));
    }
    
    @Override
    protected void destroy()
    {
        GLAPI.deleteTexture(getHandle());
    }
    
    public void bind(int unit)
    {
        GLAPI.bindTexture(getType().getEnum(), unit, getHandle());
    }
    
    public void bindImage(int unit, Access access, Format format)
    {
        GLAPI.bindImageTexture(unit, getHandle(), 0, false, 0, access.getEnum(), format.getEnum());
    }
    
    protected void uploadParameters()
    {
        GLAPI.setTextureParameteri(getHandle(), getType().getEnum(), GL11.GL_TEXTURE_MIN_FILTER, getMinFilter().getEnum());
        GLAPI.setTextureParameteri(getHandle(), getType().getEnum(), GL11.GL_TEXTURE_MAG_FILTER, getMagFilter().getEnum());
        GLAPI.setTextureParameteri(getHandle(), getType().getEnum(), GL14.GL_TEXTURE_COMPARE_MODE, GL11.GL_NONE);
    }
    
    public abstract void upload(ByteBuffer buffer, int format, int type);
    
    protected void upload(int x, int y, int z, int width, int height, int depth, ByteBuffer buffer, int format, int type)
    {
        GLAPI.uploadTextureImage(getHandle(), getType().getEnum(), 0, x, y, z, width, height, depth, format, type, buffer);
    }
    
    public void generateMipmaps()
    {
        GLAPI.generateTextureMipmaps(getHandle(), getType().getEnum());
    }

    public void clear(int level)
    {
        GLAPI.clearTextureImage(getHandle(), getType().getEnum(), level, GL11.GL_RGBA, GL11.GL_FLOAT, null);
    }
    
    public Type getType()
    {
        return type;
    }
    
    public Format getFormat()
    {
        return format;
    }
    
    public int getLevels()
    {
        return levels;
    }
    
    public MinificationFilter getMinFilter()
    {
        return minFilter;
    }
    
    public MagnificationFilter getMagFilter()
    {
        return magFilter;
    }
    
    public static abstract class Builder<T extends Texture, B extends Builder<T, B>>
    {
        protected Format format = Format.RGBA8;
        protected int levels = 1;
        
        protected MinificationFilter minFilter = MinificationFilter.LINEAR;
        protected MagnificationFilter magFilter = MagnificationFilter.LINEAR;
        
        public B format(Format format)
        {
            this.format = format;
            return (B)this;
        }
        
        public B levels(int levels)
        {
            this.levels = levels;
            return (B)this;
        }
        
        public B minFilter(MinificationFilter filter)
        {
            minFilter = filter;
            return (B)this;
        }
        
        public B magFilter(MagnificationFilter filter)
        {
            magFilter = filter;
            return (B)this;
        }
        
        public abstract T build();
    }
    
    public static enum Type implements GLAPIEnum
    {
        _1D(GL11.GL_TEXTURE_1D),
        _2D(GL11.GL_TEXTURE_2D),
        _3D(GL12.GL_TEXTURE_3D);
        
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
    
    public static enum Format implements GLAPIEnum
    {
        RGBA8(GL11.GL_RGBA8),
        RGBA32F(GL30.GL_RGBA32F),
        SRGB8_ALPHA8(GL21.GL_SRGB8_ALPHA8),
        RGB32F(GL30.GL_RGB32F),
        RGB16F(GL30.GL_RGB16F),
        RG16(GL30.GL_RG16),
        RG8(GL30.GL_RG8),
        R8(GL30.GL_R8),
        DEPTH24(GL14.GL_DEPTH_COMPONENT24);
        
        private final int glEnum;
        
        private Format(int glEnum)
        {
            this.glEnum = glEnum;
        }
        
        @Override
        public int getEnum()
        {
            return glEnum;
        }
    }
    
    public static enum MagnificationFilter implements GLAPIEnum
    {
        NEAREST(GL11.GL_NEAREST),
        LINEAR(GL11.GL_LINEAR);
        
        private final int glEnum;
        
        private MagnificationFilter(int glEnum)
        {
            this.glEnum = glEnum;
        }
        
        @Override
        public int getEnum()
        {
            return glEnum;
        }
    }
    
    public static enum MinificationFilter implements GLAPIEnum
    {
        NEAREST(GL11.GL_NEAREST),
        LINEAR(GL11.GL_LINEAR),
        NEAREST_MIPMAP(GL11.GL_NEAREST_MIPMAP_LINEAR),
        LINEAR_MIPMAP(GL11.GL_LINEAR_MIPMAP_LINEAR);
        
        private final int glEnum;
        
        private MinificationFilter(int glEnum)
        {
            this.glEnum = glEnum;
        }
        
        @Override
        public int getEnum()
        {
            return glEnum;
        }
    }
    
    public static enum Wrap implements GLAPIEnum
    {
        REPEAT(GL11.GL_REPEAT),
        CLAMP(GL12.GL_CLAMP_TO_EDGE);
        
        private final int glEnum;
        
        private Wrap(int glEnum)
        {
            this.glEnum = glEnum;
        }
        
        @Override
        public int getEnum()
        {
            return glEnum;
        }
    }
    
    public static enum Access implements GLAPIEnum
    {
        READ(GL15.GL_READ_ONLY),
        WRITE(GL15.GL_WRITE_ONLY),
        READ_WRITE(GL15.GL_READ_WRITE);
        
        private final int glEnum;
        
        private Access(int glEnum)
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