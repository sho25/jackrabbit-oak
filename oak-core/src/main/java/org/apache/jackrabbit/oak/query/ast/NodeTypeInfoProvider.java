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
name|query
operator|.
name|ast
package|;
end_package

begin_comment
comment|/**  * A nodetype info mechanism.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeTypeInfoProvider
block|{
comment|/**      * Verify that the given nodetype exists.      *       * @param nodeTypeName the fully qualified nodetype name      * @return the information      */
name|NodeTypeInfo
name|getNodeTypeInfo
parameter_list|(
name|String
name|nodeTypeName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

