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
name|api
package|;
end_package

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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_interface
specifier|public
interface|interface
name|Blob
block|{
comment|/**      * Returns a new stream for this value object.      * @return a new stream for this value based on an internal conversion.      */
annotation|@
name|Nonnull
name|InputStream
name|getNewStream
parameter_list|()
function_decl|;
comment|/**      * Returns the length of this blob.      *      * @return the length of this bloc.      */
name|long
name|length
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

