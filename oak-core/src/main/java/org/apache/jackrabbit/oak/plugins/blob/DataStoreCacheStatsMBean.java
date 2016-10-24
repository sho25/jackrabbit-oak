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
name|blob
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
name|jmx
operator|.
name|CacheStatsMBean
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_interface
specifier|public
interface|interface
name|DataStoreCacheStatsMBean
extends|extends
name|CacheStatsMBean
block|{
comment|/**      * Total weight of the in-memory cache      * @return to weight of the cache      */
comment|//Computing weight is costly hence its an operation
name|long
name|estimateCurrentMemoryWeight
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

