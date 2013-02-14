begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|prototype
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_class
specifier|public
class|class
name|Utils
block|{
specifier|static
name|int
name|pathDepth
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|path
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|?
literal|0
else|:
name|path
operator|.
name|replaceAll
argument_list|(
literal|"[^/]"
argument_list|,
literal|""
argument_list|)
operator|.
name|length
argument_list|()
return|;
block|}
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|newMap
parameter_list|()
block|{
return|return
operator|new
name|TreeMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
return|;
block|}
block|}
end_class

end_unit

