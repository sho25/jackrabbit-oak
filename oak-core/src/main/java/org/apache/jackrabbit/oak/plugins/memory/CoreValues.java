begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|memory
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|ByteStreams
import|;
end_import

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
name|Blob
import|;
end_import

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
name|CoreValue
import|;
end_import

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
name|PropertyState
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|BINARIES
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|BINARY
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|BOOLEAN
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|BOOLEANS
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|DECIMAL
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|DECIMALS
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|DOUBLE
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|DOUBLES
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|LONG
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|LONGS
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|STRING
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|STRINGS
import|;
end_import

begin_comment
comment|/**  * The methods of this class adapt a {@code PropertyState} to a {@code CoreValue}.  * TODO this is a temporary solution while resolving OAK-350  */
end_comment

begin_class
specifier|public
class|class
name|CoreValues
block|{
specifier|private
name|CoreValues
parameter_list|()
block|{}
comment|/**      * Value of the {@code property}      * @param property      * @return The single value of {@code property}.      * @throws IllegalStateException if {@code property.isArray()} is {@code true}.      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|CoreValue
name|getValue
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
if|if
condition|(
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Not a single valued property"
argument_list|)
throw|;
block|}
name|int
name|type
init|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
return|return
operator|new
name|StringValue
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
return|return
operator|new
name|LongValue
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
operator|new
name|DoubleValue
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|DOUBLE
argument_list|)
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
name|property
operator|.
name|getValue
argument_list|(
name|BOOLEAN
argument_list|)
condition|?
name|BooleanValue
operator|.
name|TRUE
else|:
name|BooleanValue
operator|.
name|FALSE
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
operator|new
name|DecimalValue
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|DECIMAL
argument_list|)
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
name|binaryValue
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|BINARY
argument_list|)
argument_list|)
return|;
default|default:
return|return
operator|new
name|GenericValue
argument_list|(
name|type
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**      * Values of  {@code property}. The returned list is immutable and contains      * all the values of the property. If {@code property} is a single-valued property,      * then the returned list will simply contain a single value.      * @param property      * @return immutable list of the values of this property      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|List
argument_list|<
name|CoreValue
argument_list|>
name|getValues
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
name|List
argument_list|<
name|CoreValue
argument_list|>
name|cvs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|int
name|type
init|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
for|for
control|(
name|String
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
control|)
block|{
name|cvs
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|PropertyType
operator|.
name|LONG
case|:
for|for
control|(
name|long
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|LONGS
argument_list|)
control|)
block|{
name|cvs
operator|.
name|add
argument_list|(
operator|new
name|LongValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
for|for
control|(
name|double
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|DOUBLES
argument_list|)
control|)
block|{
name|cvs
operator|.
name|add
argument_list|(
operator|new
name|DoubleValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
for|for
control|(
name|boolean
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|BOOLEANS
argument_list|)
control|)
block|{
name|cvs
operator|.
name|add
argument_list|(
name|value
condition|?
name|BooleanValue
operator|.
name|TRUE
else|:
name|BooleanValue
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
for|for
control|(
name|BigDecimal
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|DECIMALS
argument_list|)
control|)
block|{
name|cvs
operator|.
name|add
argument_list|(
operator|new
name|DecimalValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
for|for
control|(
name|Blob
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|BINARIES
argument_list|)
control|)
block|{
name|cvs
operator|.
name|add
argument_list|(
name|binaryValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
for|for
control|(
name|String
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
control|)
block|{
name|cvs
operator|.
name|add
argument_list|(
operator|new
name|GenericValue
argument_list|(
name|type
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|cvs
return|;
block|}
specifier|private
specifier|static
name|CoreValue
name|binaryValue
parameter_list|(
name|Blob
name|blob
parameter_list|)
block|{
name|InputStream
name|in
init|=
name|blob
operator|.
name|getNewStream
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|BinaryValue
argument_list|(
name|ByteStreams
operator|.
name|toByteArray
argument_list|(
name|in
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO better return a stream which defers this exception until accessed
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

