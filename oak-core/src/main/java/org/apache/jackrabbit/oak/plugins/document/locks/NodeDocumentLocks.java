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
name|document
operator|.
name|locks
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_interface
specifier|public
interface|interface
name|NodeDocumentLocks
block|{
comment|/**      * Acquires a log for the given key.      *      * @param key a key.      * @return the acquired lock for the given key.      */
name|Lock
name|acquire
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

