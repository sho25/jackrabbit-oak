begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Scalar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|ScalarImpl
implements|implements
name|Scalar
block|{
specifier|private
specifier|final
name|int
name|type
decl_stmt|;
specifier|public
specifier|static
name|Scalar
name|numberScalar
parameter_list|(
name|String
name|value
parameter_list|)
block|{
comment|// todo improve
try|try
block|{
return|return
name|longScalar
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
return|return
name|doubleScalar
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|Scalar
name|booleanScalar
parameter_list|(
specifier|final
name|boolean
name|value
parameter_list|)
block|{
return|return
name|value
condition|?
name|TRUE_SCALAR
else|:
name|FALSE_SCALAR
return|;
block|}
specifier|public
specifier|static
name|Scalar
name|longScalar
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
return|return
operator|new
name|LongScalar
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Scalar
name|nullScalar
parameter_list|()
block|{
return|return
name|NULL_SCALAR
return|;
block|}
specifier|public
specifier|static
name|Scalar
name|doubleScalar
parameter_list|(
specifier|final
name|double
name|value
parameter_list|)
block|{
return|return
operator|new
name|DoubleScalar
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Scalar
name|stringScalar
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Value must not be null"
argument_list|)
throw|;
block|}
return|return
operator|new
name|StringScalar
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Scalar
name|binaryScalar
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Value must not be null"
argument_list|)
throw|;
block|}
return|return
operator|new
name|SmallBinaryScalar
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Scalar
name|binaryScalar
parameter_list|(
specifier|final
name|Callable
argument_list|<
name|InputStream
argument_list|>
name|valueProvider
parameter_list|)
block|{
if|if
condition|(
name|valueProvider
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Value must not be null"
argument_list|)
throw|;
block|}
return|return
operator|new
name|BinaryScalar
argument_list|(
name|valueProvider
argument_list|)
return|;
block|}
specifier|private
name|ScalarImpl
parameter_list|(
name|int
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getBoolean
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
name|getString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLong
parameter_list|()
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|getString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getDouble
parameter_list|()
block|{
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|getString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|getString
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|// todo handle UnsupportedEncodingException
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getString
argument_list|()
operator|+
literal|": "
operator|+
name|Scalar
operator|.
name|typeNames
index|[
name|type
index|]
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
specifier|static
specifier|final
name|BooleanScalar
name|TRUE_SCALAR
init|=
operator|new
name|BooleanScalar
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|BooleanScalar
name|FALSE_SCALAR
init|=
operator|new
name|BooleanScalar
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
class|class
name|BooleanScalar
extends|extends
name|ScalarImpl
block|{
specifier|private
specifier|final
name|boolean
name|value
decl_stmt|;
specifier|public
name|BooleanScalar
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|Scalar
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getBoolean
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|other
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|value
operator|==
operator|(
operator|(
name|BooleanScalar
operator|)
name|other
operator|)
operator|.
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|value
condition|?
literal|1
else|:
literal|0
operator|)
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
name|NullScalar
name|NULL_SCALAR
init|=
operator|new
name|NullScalar
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
class|class
name|NullScalar
extends|extends
name|ScalarImpl
block|{
specifier|protected
name|NullScalar
parameter_list|()
block|{
name|super
argument_list|(
name|Scalar
operator|.
name|NULL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
literal|"null"
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|this
operator|==
name|other
operator|||
name|other
operator|!=
literal|null
operator|&&
name|getClass
argument_list|()
operator|==
name|other
operator|.
name|getClass
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|42
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|LongScalar
extends|extends
name|ScalarImpl
block|{
specifier|private
specifier|final
name|long
name|value
decl_stmt|;
specifier|public
name|LongScalar
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|Scalar
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLong
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|other
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|value
operator|==
operator|(
operator|(
name|LongScalar
operator|)
name|other
operator|)
operator|.
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|value
operator|^
operator|(
name|value
operator|>>>
literal|32
operator|)
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|DoubleScalar
extends|extends
name|ScalarImpl
block|{
specifier|private
specifier|final
name|double
name|value
decl_stmt|;
specifier|public
name|DoubleScalar
parameter_list|(
name|double
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|Scalar
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getDouble
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
name|Double
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|other
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|Double
operator|.
name|compare
argument_list|(
operator|(
operator|(
name|DoubleScalar
operator|)
name|other
operator|)
operator|.
name|value
argument_list|,
name|value
argument_list|)
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|long
name|h
init|=
name|value
operator|!=
literal|0.0d
condition|?
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|value
argument_list|)
else|:
literal|0L
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|h
operator|^
operator|(
name|h
operator|>>>
literal|32
operator|)
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|StringScalar
extends|extends
name|ScalarImpl
block|{
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
specifier|public
name|StringScalar
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|Scalar
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|value
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|StringScalar
operator|)
name|o
operator|)
operator|.
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|value
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|SmallBinaryScalar
extends|extends
name|ScalarImpl
block|{
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
specifier|public
name|SmallBinaryScalar
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|Scalar
operator|.
name|BINARY
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|other
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|value
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|SmallBinaryScalar
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|value
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|BinaryScalar
extends|extends
name|ScalarImpl
block|{
specifier|private
specifier|final
name|Callable
argument_list|<
name|InputStream
argument_list|>
name|valueProvider
decl_stmt|;
specifier|public
name|BinaryScalar
parameter_list|(
name|Callable
argument_list|<
name|InputStream
argument_list|>
name|valueProvider
parameter_list|)
block|{
name|super
argument_list|(
name|Scalar
operator|.
name|BINARY
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueProvider
operator|=
name|valueProvider
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
block|{
try|try
block|{
return|return
name|valueProvider
operator|.
name|call
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// todo handle Exception
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
literal|""
return|;
comment|// todo implement getString
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|other
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|getString
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|BinaryScalar
operator|)
name|other
operator|)
operator|.
name|getString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getString
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

