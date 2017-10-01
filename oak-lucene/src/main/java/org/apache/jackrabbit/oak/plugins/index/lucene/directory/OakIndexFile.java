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
name|index
operator|.
name|lucene
operator|.
name|directory
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

begin_interface
specifier|public
interface|interface
name|OakIndexFile
block|{
comment|/**      * @return name of the index being accessed      */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * @return length of index file      */
name|long
name|length
parameter_list|()
function_decl|;
name|boolean
name|isClosed
parameter_list|()
function_decl|;
name|void
name|close
parameter_list|()
function_decl|;
comment|/**      * @return current location of access      */
name|long
name|position
parameter_list|()
function_decl|;
comment|/**      * Seek current location of access to {@code pos}      * @param pos      * @throws IOException      */
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Duplicates this instance to be used by a different consumer/thread.      * State of the cloned instance is same as original. Once cloned, the states      * would change separately according to how are they accessed.      *      * @return cloned instance      */
name|OakIndexFile
name|clone
parameter_list|()
function_decl|;
comment|/**      * Read {@code len} number of bytes from underlying storage and copy them      * into byte array {@code b} starting at {@code offset}      * @param b byte array to copy contents read from storage      * @param offset index into {@code b} where the copy begins      * @param len numeber of bytes to be read from storage      * @throws IOException      */
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Writes {@code len} number of bytes from byte array {@code b}      * starting at {@code offset} into the underlying storage      * @param b byte array to copy contents into the storage      * @param offset index into {@code b} where the copy begins      * @param len numeber of bytes to be written to storage      * @throws IOException      */
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Flushes the content into storage. Before calling this method, written      * content only exist in memory      * @throws IOException      */
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

