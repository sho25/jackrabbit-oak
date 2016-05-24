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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A nodetype info mechanism.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeTypeInfo
block|{
comment|/**      * Check whether the nodetype exists.      *       * @return true if it exists      */
name|boolean
name|exists
parameter_list|()
function_decl|;
comment|/**      * Get the name of the nodetype.      *       * @return the fully qualified name      */
name|String
name|getNodeTypeName
parameter_list|()
function_decl|;
comment|/**      * Get the set of supertypes.      *       * @return the set      */
name|Set
argument_list|<
name|String
argument_list|>
name|getSuperTypes
parameter_list|()
function_decl|;
comment|/**      * Get the set of primary subtypes.      *       * @return the set      */
name|Set
argument_list|<
name|String
argument_list|>
name|getPrimarySubTypes
parameter_list|()
function_decl|;
comment|/**      * Get the set of mixin subtypes.      *       * @return the set      */
name|Set
argument_list|<
name|String
argument_list|>
name|getMixinSubTypes
parameter_list|()
function_decl|;
comment|/**      * Check whether this is a mixin.      *       * @return true if it is a mixin, false if it is a primary type      */
name|boolean
name|isMixin
parameter_list|()
function_decl|;
comment|/**      * Get the names of all single-valued properties.      *       * @return the names      */
name|Iterable
argument_list|<
name|String
argument_list|>
name|getNamesSingleValuesProperties
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

