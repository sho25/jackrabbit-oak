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
name|spi
operator|.
name|commit
package|;
end_package

begin_interface
specifier|public
interface|interface
name|ConflictHandlerProvider
block|{
name|ConflictHandler
name|getConflictHandler
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

