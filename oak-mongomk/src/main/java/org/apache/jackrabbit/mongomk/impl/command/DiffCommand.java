begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|command
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
name|mk
operator|.
name|model
operator|.
name|tree
operator|.
name|DiffBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
operator|.
name|tree
operator|.
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|api
operator|.
name|model
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|MongoNodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|action
operator|.
name|FetchCommitAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|action
operator|.
name|FetchHeadRevisionIdAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|model
operator|.
name|MongoCommit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|impl
operator|.
name|model
operator|.
name|tree
operator|.
name|SimpleMongoNodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|util
operator|.
name|MongoUtil
import|;
end_import

begin_comment
comment|/**  * A {@code Command} for {@code MongoMicroKernel#diff(String, String, String, int)}  */
end_comment

begin_class
specifier|public
class|class
name|DiffCommand
extends|extends
name|BaseCommand
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|final
name|String
name|fromRevision
decl_stmt|;
specifier|private
specifier|final
name|String
name|toRevision
decl_stmt|;
specifier|private
specifier|final
name|int
name|depth
decl_stmt|;
specifier|private
name|String
name|path
decl_stmt|;
comment|/**      * Constructs a {@code DiffCommandCommandMongo}      *      * @param nodeStore Node store.      * @param fromRevision From revision id.      * @param toRevision To revision id.      * @param path Path.      * @param depth Depth.      */
specifier|public
name|DiffCommand
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|,
name|String
name|fromRevision
parameter_list|,
name|String
name|toRevision
parameter_list|,
name|String
name|path
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
name|super
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|fromRevision
operator|=
name|fromRevision
expr_stmt|;
name|this
operator|.
name|toRevision
operator|=
name|toRevision
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|path
operator|=
name|MongoUtil
operator|.
name|adjustPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|checkDepth
argument_list|()
expr_stmt|;
name|long
name|fromRevisionId
decl_stmt|,
name|toRevisionId
decl_stmt|;
if|if
condition|(
name|fromRevision
operator|==
literal|null
operator|||
name|toRevision
operator|==
literal|null
condition|)
block|{
name|long
name|head
init|=
operator|new
name|FetchHeadRevisionIdAction
argument_list|(
name|nodeStore
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
name|fromRevisionId
operator|=
name|fromRevision
operator|==
literal|null
condition|?
name|head
else|:
name|MongoUtil
operator|.
name|toMongoRepresentation
argument_list|(
name|fromRevision
argument_list|)
expr_stmt|;
name|toRevisionId
operator|=
name|toRevision
operator|==
literal|null
condition|?
name|head
else|:
name|MongoUtil
operator|.
name|toMongoRepresentation
argument_list|(
name|toRevision
argument_list|)
expr_stmt|;
empty_stmt|;
block|}
else|else
block|{
name|fromRevisionId
operator|=
name|MongoUtil
operator|.
name|toMongoRepresentation
argument_list|(
name|fromRevision
argument_list|)
expr_stmt|;
name|toRevisionId
operator|=
name|MongoUtil
operator|.
name|toMongoRepresentation
argument_list|(
name|toRevision
argument_list|)
expr_stmt|;
empty_stmt|;
block|}
if|if
condition|(
name|fromRevisionId
operator|==
name|toRevisionId
condition|)
block|{
return|return
literal|""
return|;
block|}
if|if
condition|(
literal|"/"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|MongoCommit
name|toCommit
init|=
operator|new
name|FetchCommitAction
argument_list|(
name|nodeStore
argument_list|,
name|toRevisionId
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
if|if
condition|(
name|toCommit
operator|.
name|getBaseRevisionId
argument_list|()
operator|==
name|fromRevisionId
condition|)
block|{
comment|// Specified range spans a single commit:
comment|// use diff stored in commit instead of building it dynamically
return|return
name|toCommit
operator|.
name|getDiff
argument_list|()
return|;
block|}
block|}
name|NodeState
name|beforeState
init|=
name|MongoUtil
operator|.
name|wrap
argument_list|(
name|getNode
argument_list|(
name|path
argument_list|,
name|fromRevisionId
argument_list|)
argument_list|)
decl_stmt|;
name|NodeState
name|afterState
init|=
name|MongoUtil
operator|.
name|wrap
argument_list|(
name|getNode
argument_list|(
name|path
argument_list|,
name|toRevisionId
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|DiffBuilder
argument_list|(
name|beforeState
argument_list|,
name|afterState
argument_list|,
name|path
argument_list|,
name|depth
argument_list|,
operator|new
name|SimpleMongoNodeStore
argument_list|()
argument_list|,
name|path
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|void
name|checkDepth
parameter_list|()
block|{
if|if
condition|(
name|depth
operator|<
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"depth"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Node
name|getNode
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|revisionId
parameter_list|)
throws|throws
name|Exception
block|{
name|GetNodesCommandNew
name|command
init|=
operator|new
name|GetNodesCommandNew
argument_list|(
name|nodeStore
argument_list|,
name|path
argument_list|,
name|revisionId
argument_list|)
decl_stmt|;
return|return
name|command
operator|.
name|execute
argument_list|()
return|;
block|}
block|}
end_class

end_unit

