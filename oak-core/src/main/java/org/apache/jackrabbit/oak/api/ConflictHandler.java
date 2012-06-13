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
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * A {@code ConflictHandler} is responsible for handling conflicts which happen  * on {@link Root#rebase(ConflictHandler)} and on the implicit rebase operation which  * takes part on {@link Root#commit(ConflictHandler)}.  *  * This interface contains one method per type of conflict which might occur.  * Each of these methods must return a {@link Resolution} for the current conflict.  * The resolution indicates to use the changes in the current {@code Root} instance  * ({@link Resolution#OURS}) or to use the changes from the underlying persistence  * store ({@link Resolution#THEIRS}). Alternatively the resolution can also indicate  * that the changes have been successfully merged by this {@code ConflictHandler}  * instance ({@link Resolution#MERGED}).  */
end_comment

begin_interface
specifier|public
interface|interface
name|ConflictHandler
block|{
comment|/**      * Resolutions for conflicts      */
enum|enum
name|Resolution
block|{
comment|/**          * Use the changes from the current {@link Root} instance          */
name|OURS
block|,
comment|/**          * Use the changes from the underlying persistence store          */
name|THEIRS
block|,
comment|/**          * Indicated changes have been merged by this {@code ConflictHandler} instance.          */
name|MERGED
block|}
comment|/**      * The property {@code ours} has been added to {@code parent} which conflicts      * with property {@code theirs} which has been added in the persistence store.      *      * @param parent  root of the conflict      * @param ours  our version of the property      * @param theirs  their version of the property      * @return  {@link Resolution} of the conflict      */
name|Resolution
name|addExistingProperty
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
function_decl|;
comment|/**      * The property {@code ours} has been changed in {@code parent} while it was      * removed in the persistence store.      *      * @param parent  root of the conflict      * @param ours  our version of the property      * @return  {@link Resolution} of the conflict      */
name|Resolution
name|changeDeletedProperty
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|)
function_decl|;
comment|/**      * The property {@code ours} has been changed in {@code parent} while it was      * also changed to a different value ({@code theirs}) in the persistence store.      *      * @param parent  root of the conflict      * @param ours  our version of the property      * @param theirs  their version of the property      * @return  {@link Resolution} of the conflict      */
name|Resolution
name|changeChangedProperty
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|ours
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
function_decl|;
comment|/**      * The property {@code theirs} changed in the persistence store while it has been      * deleted locally.      *      * @param parent  root of the conflict      * @param theirs  their version of the property      * @return  {@link Resolution} of the conflict      */
name|Resolution
name|deleteChangedProperty
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|theirs
parameter_list|)
function_decl|;
comment|/**      * The node {@code ours} has been added to {@code parent} which conflicts      * with node {@code theirs} which has been added in the persistence store.      *      * @param parent  root of the conflict      * @param name  name of the node      * @param ours  our version of the node      * @param theirs  their version of the node      * @return  {@link Resolution} of the conflict      */
name|Resolution
name|addExistingNode
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|ours
parameter_list|,
name|NodeState
name|theirs
parameter_list|)
function_decl|;
comment|/**      * The node {@code ours} has been changed in {@code parent} while it was      * removed in the persistence store.      *      * @param parent  root of the conflict      * @param name  name of the node      * @param ours  our version of the node      * @return  {@link Resolution} of the conflict      */
name|Resolution
name|changeDeletedNode
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|ours
parameter_list|)
function_decl|;
comment|/**      * The node {@code theirs} changed in the persistence store while it has been      * deleted locally.      *      * @param parent  root of the conflict      * @param name  name of the node      * @param theirs      * @param theirs  their version of the node      * @return  {@link Resolution} of the conflict      */
name|Resolution
name|deleteChangedNode
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|theirs
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

