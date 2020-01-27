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
name|segment
operator|.
name|azure
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

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

begin_comment
comment|/**  * Wrapper around the map that allows accessing the map with case-insensitive keys.  * For example, the keys 'hello' and 'Hello' access the same value.  *<p>  * If there is a conflicting key, any one of the keys and any one of the values is used. Because of  * the nature of  Hashmaps, the result is not deterministic.  */
end_comment

begin_class
specifier|public
class|class
name|CaseInsensitiveKeysMapAccess
block|{
comment|/**      * Wrapper around the map that allows accessing the map with case-insensitive keys.      *<p>      * Return an unmodifiable map to make it clear that changes are not reflected to the original map.      *      * @param map the map to convert      * @return an unmodifiable map with case-insensitive key access      */
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|convert
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|caseInsensitiveMap
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|String
operator|.
name|CASE_INSENSITIVE_ORDER
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|caseInsensitiveMap
operator|.
name|putAll
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
comment|// return an unmodifiable map to make it clear that changes are not reflected in the original map.
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|caseInsensitiveMap
argument_list|)
return|;
block|}
block|}
end_class

end_unit

