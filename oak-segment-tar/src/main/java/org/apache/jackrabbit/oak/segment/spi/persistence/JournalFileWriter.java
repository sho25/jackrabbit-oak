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
name|spi
operator|.
name|persistence
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|JournalFileWriter
extends|extends
name|Closeable
block|{
name|void
name|truncate
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|void
name|writeLine
parameter_list|(
name|String
name|line
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

